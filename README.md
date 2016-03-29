# PeARS-evaluation
A repo for the evaluation of the PeARS search engine.

##Requirements
To use the code in this repo, you will need the following. 

### A semantic space
The semantic space of Baroni et al (2014): Don't count, predict! A systematic comparison of context-counting vs. context-predicting semantic vectors Proceedings of ACL 2014 (52nd Annual Meeting of the Association for Computational Linguistics), East Stroudsburg PA: ACL, 238-247.

**Get the semantic space with:**

`wget http://clic.cimec.unitn.it/composes/materials/EN-wform.w.5.cbow.neg10.400.subsmpl.txt.gz`

`gunzip EN-wform.w.5.cbow.neg10.400.subsmpl.txt.gz`

`mv EN-wform.w.5.cbow.neg10.400.subsmpl.txt DS/ukwac.predict.dm`

### A clean version of Wikipedia

We provide a clean dump of Wikipedia, processed by the excellent *wikiextractor* (https://github.com/attardi/wikiextractor). The dump can be downloaded from http://clic.cimec.unitn.it/~aurelie.herbelot/enwiki-20150304-clean.tar.gz (3.8GB, dump dated March 2015). Warning: uncompressed, the directory contains 103 files totalling 11GB of data.

### Some users

We simulate network users by utilising the public log of Wikipedia edits, regularly made available at https://dumps.wikimedia.org/enwiki/latest/ (look for the https://dumps.wikimedia.org/enwiki/latest/enwiki-latest-pages-logging.xml.gz file). In the data/ directory of this repo, you will find a script, getUserData.py, to extract the relevant user info from the log. For convenience, we are also making an already processed version available at http://clic.cimec.unitn.it/~aurelie.herbelot/user-data.tar.gz (log of March 2016).
