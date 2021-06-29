import re
import nltk

from dataloader import loader_raw_data, get_features_data
from models import *
english_stemmer=nltk.stem.SnowballStemmer('english')

# TODO getdata
train, test = loader_raw_data()
X_train, X_test, Y_train, Y_test = get_features_data(train, test)

# TODO build models


nb_classes = 5
input_dim = X_train.shape[1]

model  = get_MLP_model(input_dim,nb_classes)

# TODO set loss function
# we'll use categorical xent for the loss, and RMSprop as the optimizer
model.compile(loss='categorical_crossentropy', optimizer='rmsprop')

# TODO training
print("Training...")
model.fit(X_train, Y_train, nb_epoch=5, batch_size=16, validation_split=0.1)

# TODO testing
print("Generating test predictions...")
preds = model.predict_classes(X_test, verbose=0)
print('prediction 6 accuracy: ', accuracy_score(test['Rating'], preds+1))

# save result
test["predict_rating"] = preds
test.to_csv("result_basc.csv", index=False)
print("save result to result_basc.csv")


