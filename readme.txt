I'll be updating this readme with more detailed information later.


** How to Run **

	I have included run and compile commands in the script/ directory. script/run takes
two arguments: the class name that you want to run and whether to skip named entities
or not (1 to skip, 0 not to). The programs print information to stderr that is not part
of the final output, so you should redirect stdout to a file to get clean output For
example, to run the hybrid method skipping named entities you would enter:

	> script/run HybridMethod /path/to/wiki/dump.xml 1 > /path/to/output/file.txt

	You will have to pass the path to the Wikipedia dump file to script/run. I have
not included the dump file in the project because it is too large; you need to download
it yourself from:

http://download.wikimedia.org/enwiki/latest/enwiki-latest-pages-articles.xml.bz2

	You can also edit the amount of memory reserved for the JVM using the -Xmx option. I
had to reduce it from the 3.5G it was using previously because my poor laptop only has 2G;
you may want to raise the memory limit back.

	If you change the code, compiling is easy. Simply invoke script/compile with the path
of the source file. E.g:

	> script/compile src/DummyMethod.java


** Relevant Source Files **

	There are several files in src/ that are not really relevant that I haven't removed
yet. The main files are WikipediaExtender.java, JiangMethod.java, NovelMethod.java, and
HybridMethod.java. All of the *Method classes can be invoked using script/run. I'll do
some garbage collection of the unused files soon :)
