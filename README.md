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

## Usage
See the Wiki
