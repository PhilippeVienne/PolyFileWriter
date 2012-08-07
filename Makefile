sign_key=mykey
tmp=.tmp
project_home=${PWD}
java_src=$(shell find src -name '*.java')
BIN_DIR=bin
SRC_DIR=src
LIB_DIR=lib
output_jar=polyfilewriter.jar
jar_libs:=$(shell find $(LIB_DIR) -name '*.jar')
web_app=1

clean:
	@echo "We clean a little ..."
	@rm -f ${output_jar};
	@rm -rf $(BIN_DIR);

$(BIN_DIR): clean $(java_src) $(jar_libs)
	@rm -rf $(BIN_DIR);
	@mkdir $(BIN_DIR);
	@echo "Extract libs to the bin folder ..."
	@$(foreach var,$(jar_libs),unzip -qo $(var) -d $(BIN_DIR);)
	@rm -rf $(BIN_DIR)/META_INF
	@echo "Compile the code ..."
	@${JDK_BIN}javac -d $(BIN_DIR) -classpath $(BIN_DIR) $(java_src)

$(output_jar): $(BIN_DIR)
	@echo "Create the JAR Archive ..."
	@${JDK_BIN}jar cf ${output_jar} -C $(BIN_DIR) .
ifdef sign_key
	@echo "Signing the JAR with key $(sign_key)"
ifdef KEYTOOL_PASSWORD
	@${JDK_BIN}jarsigner ${output_jar} ${sign_key} -storepass ${KEYTOOL_PASSWORD} > /dev/null
else
	@${JDK_BIN}jarsigner ${output_jar} ${sign_key}
endif
endif
	@echo "Jar build"

ifeq ($(web_app),1)
web: clean $(output_jar)
	@echo "Start the test"
	@chromium-browser --user-data-dir=${user_tmp_nav} "file://${project_home}/index.html" > /dev/null
endif

is_git_repo=$(shell [ -d .git ] && echo 1 || echo 0)

ifeq ($(is_git_repo),1)
commit:
	@echo "Commiting ..."
	@echo "Pushing ..."
endif

