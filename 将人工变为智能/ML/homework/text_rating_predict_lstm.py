import re
import nltk
from models import *
from dataloader import get_lstm_data, loader_raw_data
english_stemmer=nltk.stem.SnowballStemmer('english')

train, test = loader_raw_data()
X_train, X_test, Y_train, Y_test = get_lstm_data(train, test)
print('X_train shape:', X_train.shape)
print('X_test shape:', X_test.shape)

# TODO build models
max_features = 20000
EMBEDDING_DIM = 100
VALIDATION_SPLIT = 0.2
maxlen = 80
batch_size = 32
nb_classes = 5
model  = get_LSTM_model(max_features,nb_classes)

# TODO set loss function
# we'll use categorical xent for the loss, and RMSprop as the optimizer
model.compile(loss='categorical_crossentropy', optimizer='rmsprop')

# TODO training
model.compile(loss='binary_crossentropy',
              optimizer='adam',
              metrics=['accuracy'])

print('Train...')
model.fit(X_train, Y_train, batch_size=batch_size, nb_epoch=1,
          validation_data=(X_test, Y_test))

# TODO testing
score, acc = model.evaluate(X_test, Y_test,
                            batch_size=batch_size)
print('Test score:', score)
print('Test accuracy:', acc)


print("Generating test predictions...")
preds = model.predict_classes(X_test, verbose=0)

print('prediction 7 accuracy: ', accuracy_score(test['Rating'], preds+1))

# save result
test["predict_rating"] = preds

test.to_csv("result_lstm.csv", index=False)

print("save result to result_lstm.csv")


