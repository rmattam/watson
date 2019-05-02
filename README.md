# Watson
Attempts to emulate IBM Watson for Jeopardy using IR models.

### Instruction to run the evaluation

Follow the steps below to run the evaluation:

Step 1: Clone this repository.

Step 2: Download the compressed Lucene index lemma.tar.gz tar ball file from the Google Drive link: https://drive.google.com/file/d/1HnrAvDh8a9x3EDQBZBEtRelyeGreamMn/view?usp=sharing

or download from Dropbox : https://www.dropbox.com/s/x70pg7zcdmdfsph/lemma.tar.gz?dl=0

```
wget -O lemma.tar.gz https://www.dropbox.com/s/x70pg7zcdmdfsph/lemma.tar.gz?dl=0

```

Step 3: Extract the downloaded lucene index to a folder within the working directory of the watson source repository.

```
tar -xzvf lemma.tar.gz
```

Step 4: Run the following command in the terminal from the root of the source repository. Do not move the questions.txt file from resources. It should be present in the resources folder for the code to run.
```
  $ sbt "run -i lemma/"
```

The -i flag is used to refer to the location of the extracted lucene index file.

The evaluation output looks similar to the below extract:

```
...
...
Question: 82 is CORRECT!! expected: Hogan's Heroes retrieved: Hogan's Heroes
Question: 83 is WRONG!! expected: Calvin Coolidge retrieved: Edward L. Atkinson
Question: 84 is CORRECT!! expected: Martin Sheen retrieved: Martin Sheen
Question: 85 is CORRECT!! expected: Janet Jackson retrieved: Janet Jackson
Question: 86 is WRONG!! expected: Ottoman Empire retrieved: History of Armenia
Question: 87 is WRONG!! expected: Procter & Gamble retrieved: Nyctosaurus
Question: 88 is WRONG!! expected: Otto von Bismarck | Von Bismarck retrieved: Konrad Adenauer
Question: 89 is WRONG!! expected: William Wordsworth retrieved: Bliss Carman
Question: 90 is WRONG!! expected: Khmer language retrieved: Khmer Rouge
Question: 91 is WRONG!! expected: Rickshaw retrieved: Khmer Rouge
Question: 92 is CORRECT!! expected: Michael Jackson retrieved: Michael Jackson
Question: 93 is WRONG!! expected: JFK | John F. Kennedy retrieved: Ralph Nader
Question: 94 is WRONG!! expected: B'nai B'rith retrieved: Kabbalah
Question: 95 is CORRECT!! expected: Three's Company retrieved: Three's Company
Question: 96 is WRONG!! expected: The Six Day War retrieved: Israeli–Palestinian conflict
Question: 97 is CORRECT!! expected: Heather Locklear retrieved: Heather Locklear
Question: 98 is CORRECT!! expected: Souvlaki retrieved: Souvlaki
Question: 99 is WRONG!! expected: 3M retrieved: Lake Abitibi
Question: 100 is CORRECT!! expected: Robert Downey, Jr. retrieved: Robert Downey, Jr.
Performance Measurement Precision@1: 0.45
search done
```


#### Credits
The questions used for testing were extracted from j-archive.com, from shows that took place between 2013-01-01 and 2013-01-07.
