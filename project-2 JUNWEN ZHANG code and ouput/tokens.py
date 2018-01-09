#encoding: utf-8

import json
import nltk
import re
from collections import Counter
import collections, itertools
import string
from nltk.corpus import stopwords
#from sklearn.naive_bayes import MultinomialNB, BernoulliNB
#from sklearn.svm import LinearSVC
#from sklearn.svm import NuSVC

import preprocess
from nltk.classify import NaiveBayesClassifier
from nltk.classify.util import accuracy
from nltk.corpus import subjectivity
from nltk.sentiment import SentimentAnalyzer
from nltk.sentiment.util import *
from nltk.stem.snowball import SnowballStemmer


import collections, itertools
import nltk.classify.util, nltk.metrics
from nltk.classify import NaiveBayesClassifier
from nltk.corpus import movie_reviews, stopwords
from nltk.collocations import BigramCollocationFinder
from nltk.metrics import BigramAssocMeasures
from nltk.probability import FreqDist, ConditionalFreqDist
from nltk.metrics import precision, recall, f_measure

trainingfile = open('info-features.txt', 'r')

def get_num_features(file,num):
    words = []
    for i in range(num):
        for line in file:
            content = nltk.word_tokenize(line)
            for word in content:
                words.append(word)
                break
            break
#    print (words)
    return words

bestwords = get_num_features(trainingfile,1000)


def word_feats(words):
    return dict([(word, True) for word in words ])

def best_feats(words,bestwords):
    return dict([(word, True) for word in words if word in bestwords])


def bigram_word_feats(words, score_fn=nltk.BigramAssocMeasures.chi_sq, n=200):
    bigram_finder = nltk.BigramCollocationFinder.from_words(words)
    bigrams = bigram_finder.nbest(score_fn, n)
    d = dict([(bigram, True) for bigram in bigrams])
    return d

if __name__ == "__main__":
    tweet = {}
    with open('train-tweets.txt', 'r') as f:
        print ('preprocess the text')
        count_all = Counter()
        stemmer = nltk.stem.PorterStemmer()
        lemmatizer = nltk.WordNetLemmatizer()

        train_data = []
        all_tweets = []
        for line in f:
            temp = line.strip("\n")
            temp = temp.strip("\t").split("\t")
            id = temp[0]
            text = temp[1]
            tweet[id] = text

            # preprocess
            text = preprocess.processAll(text)

            words = [word if (word[0:2] == '__') else word.lower() for word in text.split() if len(word) >= 3]
            words = [word for word in words if word[0:2] != '__']

            # remmoving stop words
            punctuation = list(string.punctuation)
            stop = stopwords.words('english') + punctuation + ['rt', 'via']

            words = [stemmer.stem(w) for w in words]  
            words = [lemmatizer.lemmatize(w) for w in words]
            all_tweets.append((words))

            tokens = nltk.word_tokenize(text)
            # tweet[id] = text
            # print(tokens)


            # count term frequency
            terms_all = [term for term in words if term not in stop]
            word_tokens = word_feats(terms_all)
            bi_tokens = bigram_word_feats(terms_all)
            best_tokens = best_feats(terms_all,bestwords)
            count_all.update(terms_all)
            sentence = " ".join(str(term) for term in terms_all)
            #print ('label the features')
            with open('train-labels.txt','r') as f2:
                for line2 in f2:
                    line2 = line2.strip('\n')
                    if id == line2.split('\t')[0]:
                        train_data.append([word_tokens, line2.split('\t')[1]])

        print ('train the model')
        classifier = NaiveBayesClassifier.train(train_data)
#       classifier.show_most_informative_features(20)
    with open("dev-tweets.txt",'r')as dev:
        print ('preprocess the dev ')
        test_data = []
        for line in dev:
            line = line.strip('\n')
            id2 = line.split('\t')[0]
            text = line.split('\t')[1]
            text = preprocess.processAll(text)

            words = [word if (word[0:2] == '__') else word.lower() for word in text.split() if len(word) >= 3]
            words = [word for word in words if word[0:2] != '__']
            words = [stemmer.stem(w) for w in words]  
            words = [lemmatizer.lemmatize(w) for w in words]

            punctuation = list(string.punctuation)
            stop = stopwords.words('english') + punctuation + ['rt', 'via']

            # count term frequency
            terms_all = [term for term in words if term not in stop]
            word_tokens = word_feats(terms_all)
            bi_tokens = bigram_word_feats(terms_all)
            best_tokens = best_feats(terms_all,bestwords)
            sentence = " ".join(str(term) for term in terms_all)
            with open('dev-labels.txt', 'r') as dev_label:
                for line2 in dev_label:
                    line2 = line2.strip('\n')
                    if line2.split('\t')[0] == id2:
                        test_data.append([word_tokens, line2.split('\t')[1]])


        #print(str(model.show_most_informative_features(400)))

        print("NaiveBayes:")
        refsets = collections.defaultdict(set)
        testsets = collections.defaultdict(set)
        #print(nltk.classify.accuracy(model, test_data))
        for i, (feats, label) in enumerate(test_data):
            refsets[label].add(i)
            observed = classifier.classify(feats)
            testsets[observed].add(i)
 
        print 'accuracy:', nltk.classify.util.accuracy(classifier, test_data)
        print 'pos precision:', precision(refsets['positive'], testsets['positive'])
        print 'pos recall:', recall(refsets['positive'], testsets['positive'])
        print 'neg precision:', precision(refsets['negative'], testsets['negative'])
        print 'neg recall:', recall(refsets['negative'], testsets['negative'])
        print 'neu precision:', precision(refsets['neutral'], testsets['neutral'])
        print 'neu recall:', recall(refsets['neutral'], testsets['neutral'])
#        classifier.show_most_informative_features(1000)



