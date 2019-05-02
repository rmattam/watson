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

#### Credits
The questions used for testing were extracted from j-archive.com, from shows that took place between 2013-01-01 and 2013-01-07.
