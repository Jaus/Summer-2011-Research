To compile:
javac -cp lib/wikixmlj-r43.jar:lib/Jama-1.0.2.jar:lib/stanford-parser.jar:lib/jsimlib.jar -sourcepath src -d classes src/ExtendWikipedia.java

To run:
java -cp classes:lib/ant.jar:lib/wikixmlj-r43.jar:lib/Jama-1.0.2.jar:lib/stanford-parser.jar:lib/jsimlib.jar ExtendWikipedia data/enwiki-latest-pages-articles.xml.bz2
