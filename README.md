# Watson
Attempts to emulate IBM Watson for Jeopardy using IR models.

#### Credits
The questions used for testing were extracted from j-archive.com, from shows that took place between 2013-01-01 and 2013-01-07.

#### TODO
* v1 performance = 15% Accuracy
* v2: rule based re-ranking errors:
    * results case not sensitive should not be mentioned in the question = improved 5% (1 left in the quotable keats) 
        * Exception: ranks & titles category - should ignore words being present in the question. = improved 1%
    * handle alternate possible result in answer = improved 2%
    * african cities - add the word and check
    * golden globe winners -> add text golden globe winner
    * he played a guy named -> add movie actor
    
    * handle 80's HITmakers category - 5
    * state of the art -> get the state from the musuem name - 5
    
* nlp processing:
    * use dependency parser to get subject object relations and boost the score for it.

* compare rankings
* handle subheadings correctly?
* How to re-rank?? Get some ideas?
