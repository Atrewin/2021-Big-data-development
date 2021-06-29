import re
import nltk
import pandas as pd
import numpy as np
english_stemmer=nltk.stem.SnowballStemmer('english')
from sklearn.feature_selection.univariate_selection import SelectKBest, chi2, f_classif
from sklearn.model_selection import train_test_split
from sklearn.feature_extraction.text import TfidfVectorizer
import random
from keras.preprocessing import sequence
from keras.utils import np_utils
from keras.preprocessing.text import Tokenizer
import seaborn as sns
from models import *

# TODO Data Preprocessing
def review_to_wordlist( review, remove_stopwords=True):
    # Function to convert a document to a sequence of words,
    # optionally removing stop words.  Returns a list of words.
    #
    # 1. Remove HTML
    review_text = BeautifulSoup(review).get_text()

    #
    # 2. Remove non-letters
    review_text = re.sub("[^a-zA-Z]"," ", review)
    #
    # 3. Convert words to lower case and split them
    words = review_text.lower().split()
    #
    # 4. Optionally remove stop words (True by default)
    if remove_stopwords:
        stops = set(stopwords.words("english"))
        words = [w for w in words if not w in stops]

    b=[]
    stemmer = english_stemmer #PorterStemmer()
    for word in words:
        b.append(stemmer.stem(word))

    # 5. Return a list of words
    return(b)

def loader_raw_data():
    # TODO get data
    data_file = '../dataset/Amazon_Unlocked_Mobile.csv'

    n = 413000
    s = 20000
    skip = sorted(random.sample(range(1, n), n - s))
    data = pd.read_csv(data_file, delimiter=",", skiprows=skip)
    print(data.shape)

    data = data[data['Reviews'].isnull() == False]
    train, test = train_test_split(data, test_size=0.3)
    sns.countplot(data['Rating'])

    return train, test

def get_features_data(train, test):
    # train, test = loader_raw_data()

    # TODO data clean
    clean_train_reviews = []
    for review in train['Reviews']:
        clean_train_reviews.append(" ".join(review_to_wordlist(review)))

    clean_test_reviews = []
    for review in test['Reviews']:
        clean_test_reviews.append(" ".join(review_to_wordlist(review)))

    # TODO build feature
    # TFidf
    vectorizer = TfidfVectorizer(min_df=2, max_df=0.95, max_features=200000, ngram_range=(1, 4),
                                 sublinear_tf=True)
    vectorizer = vectorizer.fit(clean_train_reviews)
    train_features = vectorizer.transform(clean_train_reviews)

    test_features = vectorizer.transform(clean_test_reviews)

    fselect = SelectKBest(chi2, k=10000)
    train_features = fselect.fit_transform(train_features, train["Rating"])
    test_features = fselect.transform(test_features)

    # TODO format dataset
    # word2vecter
    vectorizer = TfidfVectorizer(min_df=2, max_df=0.95, max_features=1000, ngram_range=(1, 3),
                                 sublinear_tf=True)
    vectorizer = vectorizer.fit(clean_train_reviews)
    train_features = vectorizer.transform(clean_train_reviews)

    test_features = vectorizer.transform(clean_test_reviews)
    X_train = train_features.toarray()
    X_test = test_features.toarray()

    print('X_train shape:', X_train.shape)
    print('X_test shape:', X_test.shape)
    y_train = np.array(train['Rating'] - 1)
    y_test = np.array(test['Rating'] - 1)
    batch_size = 32
    nb_classes = 5
    Y_train = np_utils.to_categorical(y_train, nb_classes)
    Y_test = np_utils.to_categorical(y_test, nb_classes)

    # pre-processing: divide by max and substract mean
    scale = np.max(X_train)
    X_train /= scale
    X_test /= scale

    mean = np.mean(X_train)
    X_train -= mean
    X_test -= mean

    return X_train, X_test, Y_train, Y_test

def get_lstm_data(train, test):

    # TODO format to lstm
    max_features = 20000
    maxlen = 80

    nb_classes = 5
    # vectorize the text samples into a 2D integer tensor
    tokenizer = Tokenizer(nb_words=max_features)
    tokenizer.fit_on_texts(train['Reviews'])
    sequences_train = tokenizer.texts_to_sequences(train['Reviews'])
    sequences_test = tokenizer.texts_to_sequences(test['Reviews'])

    y_train = np.array(train['Rating'] - 1)
    y_test = np.array(test['Rating'] - 1)

    X_train = sequence.pad_sequences(sequences_train, maxlen=maxlen)
    X_test = sequence.pad_sequences(sequences_test, maxlen=maxlen)

    Y_train = np_utils.to_categorical(y_train, nb_classes)
    Y_test = np_utils.to_categorical(y_test, nb_classes)

    return X_train, X_test, Y_train, Y_test