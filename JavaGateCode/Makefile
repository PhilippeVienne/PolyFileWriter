
ifeq ($(shell whoami)@$(shell uname -n),pvienne@tin)
	JDK_HOME=/usr/java/jdk1.7.0_05/
	jvs_sign_key=mykey
	user_tmp_nav=~/.tmp
endif
project_home=${PWD}/..
java_src=$(shell find src -name *.java)

clean:
	rm -rf build;
	rm -f polyfilewriter.jar;

build: clean
	mkdir build;
	${JDK_HOME}/bin/javac -nowarn -d build $(shell find src -name '*.java')

polyfilewriter.jar: build
	${JDK_HOME}/bin/jar cf polyfilewriter.jar -C build .

signedjar: polyfilewriter.jar
	${JDK_HOME}/bin/jarsigner polyfilewriter.jar ${jvs_sign_key} -storepass ${keytool_pass}
	cp polyfilewriter.jar ../PolyFileWriter-JQuery  > /dev/null

test: clean signedjar
	chromium-browser --user-data-dir=${user_tmp_nav} "file://${project_home}/PolyFileWriter-JQuery/index.html"

