# PolyFileWriter
This library is an HTML5 Polyfills to support FileAPI on all browsers.



## Compile


## Usage
To implement PolyFileWriter on your own site, you have to do many things:

1. Edit the security checker in `org.javascool.polyfilewriter.Gateway`
   In the function `init()` change the code to :
   ```Java
if(!getCodeBase().getHost().equals("example.com")){
            appletLocked=true;
}
   ```
   Don't forget to replace `example.com` by your own website;
2. Compile the jar (An big step):
    To create the jar for the library, use the Makefile (use Cygwin on Windows).
    You have to put some environment varaibles:

        * JDK_HOME : The root of your JDK instalation (not bin folder, the root)
        * jvs_sign_key: The keystore's key used to sign the applet
        * project_home: The path to this GIT repo (set empty if it's PWD)
        * user_tmp_nav: Used to create a profile for chromium (test enterpoint)

    To build the jar and sign it, execute :
    ```make signedjar```
be sure that polyfilewriter.jar is in PolyFileWriter-JQuery.
3. Add libs to your HTML page :
    ```html
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script>
<script src="jquery.polyfilewriter.js"></script>
```
    Copy jquery.polyfilewriter.js and polyfilewriter.jar beside your html file
4. Run the plugin :
```js
$(document).ready(function(){
    $.polyfilewriter({
            jar:"polyfilewriter.jar", // Location of the JAR (If you don't write it the default location will be polyfilewriter.jar)
            id:"PolyFileWriter" // The applet id (If you don't write it the default location will be a random UUID)
    });
});
```

## Code
The code has got tow sides, one part is written in Java and the other as a jQuery plugin.

For the part in Java, there is two class :

* org.javascool.tools.FileManager: A file access class written for Java's Cool 4
* org.javascool.polyfilewriter: An applet class to talk with JavaScript

For the JS part, it's a simple JQuery extension

## Security Warning
Open the user file system to the web can cause many security problem, so this library is originaly written to run on Java's Cool environment (http://www.javascool.fr) and make stuff to check if it's into.
However, you can fork the project, rewrite security checks into the Applet class, recompile and sign (With your own certificate) the final jar.