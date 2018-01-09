# This Python file uses the following encoding: utf-8
import re
from nltk.corpus import stopwords
from nltk.stem.snowball import SnowballStemmer
from nltk.stem.wordnet import WordNetLemmatizer

#number
num_regex = re.compile(r'\d+')
def num_repl(match):
	return '_'

# Hashtags
hash_regex = re.compile(r"#(\w+)")
def hash_repl(match):
	return '_'

# Handels
hndl_regex = re.compile(r"@(\w+)")
def hndl_repl(match):
	return '_'

# URLs
url_regex = re.compile(r"(http|https|ftp)://[a-zA-Z0-9\./]+")

# Spliting by word boundaries
word_bound_regex = re.compile(r"\W+")

# Repeating words like hurrrryyyyyy
rpt_regex = re.compile(r"(.)\1{1,}", re.IGNORECASE);
def rpt_repl(match):
	return match.group(1)+match.group(1)

# Emoticons
emoticons = \
	[	('emotsmiley',	[':-)', ':)', '(:', '(-:', ] )	,\
		('emotlaugh',		[':-D', ':D', 'X-D', 'XD', 'xD', ] )	,\
		('emotlove',		['<3', ':\*', ] )	,\
		('emotwink',		[';-)', ';)', ';-D', ';D', '(;', '(-;', ] )	,\
		('emotfrown',		[':-(', ':(', '(:', '(-:', ] )	,\
		('emotcry',		[':,(', ':\'(', ':"(', ':(('] )	,\
	]

# Punctuations
punctuations = \
	[	#('',		['.', ] )	,\
		#('',		[',', ] )	,\
		#('',		['\'', '\"', ] )	,\
		('puncexcl',		['!', '¡', ] )	,\
		('puncques',		['?', '¿', ] )	,\
		('punchellp',		['...', '…', ] )	,\
		#FIXME : MORE? http://en.wikipedia.org/wiki/Punctuation
	]

#Printing functions for info
def print_config(cfg):
	for (x, arr) in cfg:
		print x, '\t',
		for a in arr:
			print a, '\t',
		print ''

def print_emoticons():
	print_config(emoticons)

def print_punctuations():
	print_config(punctuations)

#For emoticon regexes
def escape_paren(arr):
	return [text.replace(')', '[)}\]]').replace('(', '[({\[]') for text in arr]

def regex_union(arr):
	return '(' + '|'.join( arr ) + ')'

emoticons_regex = [ (repl, re.compile(regex_union(escape_paren(regx))) ) \
					for (repl, regx) in emoticons ]

#For punctuation replacement
def punctuations_repl(match):
	text = match.group(0)
	repl = []
	for (key, parr) in punctuations :
		for punc in parr :
			if punc in text:
				repl.append(key)
	if( len(repl)>0 ) :
		return ' '+' '.join(repl)+' '
	else :
		return ' '

def processHashtags( 	text, subject='', query=[]):
	return re.sub( hash_regex, hash_repl, text )

def processHandles( 	text, subject='', query=[]):
	return re.sub( hndl_regex, hndl_repl, text )

def processUrls( 		text, subject='', query=[]):
	return re.sub( url_regex, ' __URL ', text )

def processEmoticons( 	text, subject='', query=[]):
	for (repl, regx) in emoticons_regex :
		text = re.sub(regx, ' '+repl+' ', text)
	return text

def processPunctuations( text, subject='', query=[]):
	return re.sub( word_bound_regex , punctuations_repl, text )

def processRepeatings( 	text, subject='', query=[]):
	return re.sub( rpt_regex, rpt_repl, text )

def processQueryTerm( 	text, subject='', query=[]):
	query_regex = "|".join([ re.escape(q) for q in query])
	return re.sub( query_regex, '__QUER', text, flags=re.IGNORECASE )

def countHandles(text):
	return len( re.findall( hndl_regex, text) )
def countHashtags(text):
	return len( re.findall( hash_regex, text) )
def countUrls(text):
	return len( re.findall( url_regex, text) )
def countEmoticons(text):
	count = 0
	for (repl, regx) in emoticons_regex :
		count += len( re.findall( regx, text) )
	return count
 # remove stopword after tokens
def removeStopWord(tokens):
	filtered_word = []
	for word in tokens:
		if word not in stopwords.words('english'):
			filtered_word.append(word)
	return filtered_word

def removeStemm(tokens):
	filtered_word = []
	lmtzr = WordNetLemmatizer()
	for word in tokens:
		word = lmtzr.lemmatize(word)
		filtered_word.append(word)
	return filtered_word

def removeNum(text):
	filtered_word = []
	for word in text:
		word = re.sub( num_regex,num_repl,word)
		filtered_word.append(word)
	return filtered_word

def removePrt(text):
	filtered_word = []
	for word in text:
		word = re.sub( rpt_regex, rpt_repl, word )
		filtered_word.append(word)
	return filtered_word


#FIXME: preprocessing.preprocess()! wtf! will need to move.
#FIXME: use process functions inside
def processAll( text, subject='', query=[]):

#	if(len(query)>0):
#		query_regex = "|".join([ re.escape(q) for q in query])
#		text = re.sub( query_regex, '__QUER', text, flags=re.IGNORECASE )

# replace hashtag,number,@,url
	text = text.lower()
	text = re.sub( hash_regex, hash_repl, text )
	text = re.sub( hndl_regex, hndl_repl, text )
	text = re.sub( url_regex, '', text )
	text = re.sub( num_regex,num_repl,text)

 #replace emoticons
	for (repl, regx) in emoticons_regex :
		text = re.sub(regx, ' '+repl+' ', text)

# replace \
	text = text.replace('\'','')

	# FIXME: Jugad
# replace punctuations, repeated word
	text = re.sub( word_bound_regex , punctuations_repl, text )
#	text = re.sub( rpt_regex, rpt_repl, text )
#	text = removeStemm(text)

#	text = removeStopWord(text)
	return text

#from time import time
#import preprocessing, sanderstwitter02
#tweets = sanderstwitter02.getTweetsRawData('sentiment.csv')
#start = time()
#procTweets = [ (preprocessing.preprocess(t),s) for (t,s) in tweets]
#end = time()
#end - start

#uni = [ a if(a[0:2]=='__') else a.lower() for a in re.findall(r"\w+", text) ]
#bi  = nltk.bigrams(uni)
#tri = nltk.trigrams(uni)
