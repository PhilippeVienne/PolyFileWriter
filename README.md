# PolyFileWriter
This library is an HTML5 Polyfills to support FileAPI on all browsers.

## Code
The code has got tow sides, one part is written in Java and the other as a jQuery plugin.

For the part in Java, there is two class :

* org.javascool.tools.FileManager: A file access class written for Java's Cool 4
* org.javascool.polyfilewriter: An applet class to talk with JavaScript

For the JS part, it's a simple JQuery extension

## Security Warning
Open the user file system to the web can cause many security problem, so this library is originaly written to run on Java's Cool environment (http://www.javascool.fr) and make stuff to check if it's into.
However, you can fork the project, rewrite security checks into the Applet class, recompile and sign (With your own certificate) the final jar.

## Compile
To create the jar for the library, use the Makefile (use Cygwin on Windows).
You have to put some environment varaibles:

* JDK_HOME : The root of your JDK instalation (not bin folder, the root)
* jvs_sign_key: The keystore's key used to sign the applet
* project_home: The path to this GIT repo (set empty if it's PWD)
* user_tmp_nav: Used to create a profile for chromium (test enterpoint)

To build the jar and sign it, execute :
``make signedjar``

## Usage
See the Wiki
