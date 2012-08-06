
ifeq ($(shell whoami)@$(shell uname -n),pvienne@tin)
	JDK_HOME=/usr/java/jdk1.7.0_05/
	jvs_sign_key=mykey
	user_tmp_nav=~/.tmp
endif
project_home=${PWD}/..
java_src=$(shell find src -name '*.java')
BIN_DIR=bin
SRC_DIR=src
LIB_DIR=lib
jar_libs:=$(shell find $(LIB_DIR) -name '*.jar')

clean:
	@echo "We clean a little ..."
	@rm -f polyfilewriter.jar;

$(BIN_DIR): clean $(java_src) $(jar_libs)
	@rm -rf $(BIN_DIR);
	@mkdir $(BIN_DIR);
	@echo "Extract libs to the bin folder ..."
	@unzip -qo $(jar_libs) -d $(BIN_DIR)
	@rm -rf $(BIN_DIR)/META_INF
	@echo "Extract is ended"
	@echo "Compile ..."
	@${JDK_HOME}/bin/javac -d $(BIN_DIR) -classpath $(BIN_DIR) $(java_src)
	@echo "Compile is ended"

polyfilewriter.jar: $(BIN_DIR)
	@echo "Create the JAR Archive ..."
	@${JDK_HOME}/bin/jar cf polyfilewriter.jar -C $(BIN_DIR) .
	@echo "Jar created"

signedjar: polyfilewriter.jar
	@echo "Signing the JAR"
	@${JDK_HOME}/bin/jarsigner polyfilewriter.jar ${jvs_sign_key} -storepass ${KEYTOOL_PASSWORD}
	@echo "Copy the signed JAR to the web directory"
	@cp polyfilewriter.jar ../PolyFileWriter-JQuery  > /dev/null
	@echo "All is down"

test: clean signedjar
	@echo "Start the test"
	@chromium-browser --user-data-dir=${user_tmp_nav} "file://${project_home}/PolyFileWriter-JQuery/index.html" > /dev/null

