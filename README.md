# Watson
Attempts to emulate IBM Watson for Jeopardy using IR models.

#### Credits
The questions used for testing were extracted from j-archive.com, from shows that took place between 2013-01-01 and 2013-01-07.

#### TODO
* rule based re-ranking errors:
    * results case not sensitive should not be mentioned in the question - 2 + 2(I'm burning for you) + 1(the resident's) + 1(ucla celebrity)
    * handle 80's HITmakers category - 5
    * handle or result in answer - 1
    * african cities - add the word and check
    * golden globe winners -> add text golden globe winner
    * he played a guy named -> add movie actor
    * state of the art -> get the state from the musuem name - 5
    
* nlp processing:
    * use dependency parser to get subject object relations and boost the score for it.

* compare rankings
* handle subheadings correctly?
* How to re-rank?? Get some ideas?
