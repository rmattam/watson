# Watson
Attempts to emulate IBM Watson for Jeopardy using IR models.

#### Credits
The questions used for testing were extracted from j-archive.com, from shows that took place between 2013-01-01 and 2013-01-07.

#### TODO
* Main tasks left
    * try tf-idf vs BM25 (default) = done - got exact same results for both
    * try lemmatization with whitespace analyzer vs stemming (default porter stemming)

* v1 performance = 15% Accuracy
* v2: rule based re-ranking errors:
    * results case not sensitive should not be mentioned in the question = improved 5% (1 left in the quotable keats) 
        * Exception: ranks & titles category - should ignore words being present in the question. = improved 1%
    * handle alternate possible result in answer = improved 2%
    
    * african cities - add the word and check
    
    * golden globe winners -> add text golden globe winner
    * he played a guy named -> add movie actor
    * handle 80's HITmakers category - 5 - handled in special manner - done = 5%
    * state of the art -> get the state from the musuem name - 5 - too hard
    
* v3: nlp processing: +6%
    * use dependency parser to get subject object relations and boost the score for it.
        * find the Question focus word, which is the head of the first noun or verb ignoring stop
            words, auxiliary verbs and copulative verbs.
    * count of words that occur in the same order as in question and in answer - did not help much
    * count of words in question that occur in the same sentence in the answer - did not help much
    * number of question words found in the answer context - helped a lot - improved 9%

* compare rankings
* handle subheadings correctly?
* How to re-rank?? Get some ideas?
