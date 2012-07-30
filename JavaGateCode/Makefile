
clean:
	rm -rf build;

build: clean
	mkdir build;
	javac -d build src/org/javascool/polyfilewriter/Gateway.java\
	                src/org/javascool/tools/FileManager.java;

polyfilewriter.jar: build
	rm polyfilewriter.jar
	jar cvf0 polyfilewriter.jar -C build .

