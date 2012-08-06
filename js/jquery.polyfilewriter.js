/*
 *     Copyright (C) 2012  Philippe VIENNE
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

//You need an anonymous function to wrap around your function to avoid conflict
(function($){

    var methods = {
        jarLibUrl:"polyfilewriter.jar",
        appletId:"",
        applet:null,
        init : function( options ) {
            methods.jarLibUrl=options.jar||methods.jarLibUrl;
            methods.appletId=options.id||Math.uuidFast();
            $("body").append('<object id="'+methods.appletId+'" type="application/x-java-applet" height="1" width="1"><param name="archive" value="'+methods.jarLibUrl+'"/><param name="code" value="org.javascool.polyfilewriter.Gateway"/></object>');
            methods.applet=document.getElementById(methods.appletId);
        }
    };

    //Attach this new method to jQuery
    $.polyfilewriter=function( method ) {

        // Method calling logic
        if ( methods[method] ) {
            if((typeof methods[method]) === "function") {
                return methods[ method ].apply( this, Array.prototype.slice.call( arguments, 1 ));
            }
            return methods[method];
        } else if ( typeof method === 'object' || ! method ) {
            return methods.init.apply( this, arguments );
        } else {
            try{

                // Which arguments have we got ?
                var javaArgs=Array.prototype.slice.call( arguments, 1);
                var args="";
                for(var i=0;i<javaArgs.length;i++){
                    if(args!==""){
                        args+=",";
                    }
                    args+="javaArgs["+i+"]";
                }

                // Use EVAL to call on NPObject
                eval("var value=methods.applet[method]("+args+");");

                // Try to parse JSON
                try{
                    var json=$.parseJSON(value);
                    if(json!=null||typeof json==="Array"||typeof json==="Object")return json;
                }catch(e){}

                // Otherwise give back just the value
                return value;
            }catch(e){
                try{
                    var javaE=methods.applet.popException();
                    if(javaE!==null){
                        throw javaE;
                    }else{
                        throw e;
                    }
                }catch(E){
                    if(E.toString().indexOf("Error calling method on NPObject.")>=0){
                    throw "An error has happened in Java but we can't help to debug because we don't know it";
                    }else{
                    throw E;
                    }
                }
            }
        }

    };

})(jQuery);

/*
Math.uuid.js used to generate id
 */
(function(){var e="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".split("");Math.uuid=function(d,c){var b=[],c=c||e.length;if(d)for(var a=0;a<d;a++)b[a]=e[0|Math.random()*c];else{var f;b[8]=b[13]=b[18]=b[23]="-";b[14]="4";for(a=0;36>a;a++)b[a]||(f=0|16*Math.random(),b[a]=e[19==a?f&3|8:f])}return b.join("")};Math.uuidFast=function(){for(var d=Array(36),c=0,b,a=0;36>a;a++)8==a||13==a||18==a||23==a?d[a]="-":14==a?d[a]="4":(2>=c&&(c=33554432+16777216*Math.random()|0),b=c&15,c>>=4,d[a]=
    e[19==a?b&3|8:b]);return d.join("")};Math.uuidCompact=function(){return"xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g,function(d){var c=16*Math.random()|0;return("x"==d?c:c&3|8).toString(16)}).toUpperCase()}})();