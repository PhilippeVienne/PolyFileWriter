
clean:
	rm -rf build;

build: clean
	mkdir build;
	javac -d build src/org/javascool/polyfilewriter/Gateway.java\
	                src/org/javascool/tools/FileManager.java;

polyfilewriter.jar: build
	rm polyfilewriter.jar
	jar cvf0 polyfilewriter.jar -C build .

signedjar: polyfilewriter.jar
	/usr/java/jdk1.7.0_05/bin/jarsigner polyfilewriter.jar mykey
	cp polyfilewriter.jar ../PolyFileWriter-JQuery

