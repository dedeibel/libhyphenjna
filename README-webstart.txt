
UNTESTED, Flemming Frandsen from the hunspell jna lib writes:


* Building the Java API for use via webstart

If you plan on using hyphen.jar from a webstart application run:
"ant webstart".

This will produce two native jar files per supported platform (one for JNA
and one for hunspell) as well as pure java jna-jws.jar and hyphen-jws.jar
in build/jar.

Include jna-jws.jar and hyphen-jws.jar in the common resources section in
normal jar tags, the platform specific binaries must go into platform specific
resource sections as nativelib entries.


* Working around a bug in nativelib handling of webstart

I had great trouble getting nativelib with more than one jar file to work on
osx and linux (it worked fine on windows), there are two workarounds:

1) Use plain <jar/> tags in stead of <nativelib/> this will cause both JNA and
   hunspell to search the classpath for the needed binaries and they will then
   extract the binaries and load them as usual.
   This approach might leak a binary each run on windows because of
   the mandatory file locking used on that platform.

2) Consolidate all your nativelib files for each platform into one.
   This looks slightly less pretty, but it works and is slightly faster during
   download as well.


* Output

The output of "ant" is one large jar file containing binaries for all the
supported platforms, it's about 800k, so it's not very nice for folks who
have to download it though: 

hunspell.jar               : The Java API + all binaries

hunspell-jws.jar           : Just the Java API, for use in webstart.
hunspell-darwin-i386.jar   : Binaries for use as OS specific resoruces
hunspell-darwin-ppc.jar    : 
hunspell-linux-amd64.jar   :  
hunspell-linux-i386.jar    : 
hunspell-win32-x86.jar     : 

I've parted out the lib/jna.jar file in the same manner, also for use in
webstart: 
jna-jws.jar                : The Java API for JNA.
jna-darwin.jar             
jna-linux-amd64.jar
jna-linux-i386.jar
jna-win32-x86.jar

