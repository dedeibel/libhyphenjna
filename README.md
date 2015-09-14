
# README

## JNA based Java API for hyphen

This package contains the JNA based Java bindings for the hyphen, see:
http://sourceforge.net/projects/hunspell/files/Hyphen/
for details.

This java wrapper lives at:
https://github.com/dedeibel/libhyphenjna

See this page for dictionaries:
http://wiki.services.openoffice.org/wiki/Dictionaries


### Building the native binaries

Before using the java API you must build some native binaries to include
in the jar, to make things easy I've included the binaries from the platforms
that I care about in native-lib.

The binaries in native-lib were built on the various platforms using the
`native-build.pl` script, the source for the native libs is in native-src and
consists of an unmodified hunspell source tar ball and any needed diffs.

I've included a script called `native-build.pl` which might help to build them
on at least Linux and Mac and 32 and 64 bit, for windows you
can take it as a hint of what to do.

When building the sources on your own, make sure you have `gawk` installed if
you run into related errors.


### Building the Java API

To build hyphen.jar simply run `ant` this will produce
`build/jar/hyphen.jar` which contains everything a standalone application
could want (aside from jna.jar which can be found on the JNA page or in lib)


### API

See `HyphenMain.java` for a very simple example of how to use the API, it
boils down to:

```java
Hyphen.Dictionary dictionary = Hyphen.getInstance().getDictionary(dictPath);
String hyphenated = dictionary.hyphenate(word);
```

`Hyphen.getInstance()` caches the loaded hunspell library, so there is no
overhead in calling it more than once.

`getDictionary()` is also internally cached, so it costs no more than a hash
lookup when calling the second time.


### Thanks

To Flemming Frandsen who build HunspellJNA which was mostly copied here,
https://github.com/dren-dk/HunspellJNA

Andrzej Zydron figured out how to build libhunspell for 64 bit OSX.


### Misc

At the moment the package contains copies of:

- jna.jar version 3.4.0 
- hyphen version 2.8.3
- junit 4
- hamcrest 1.3

Benjamin Peter <benjaminpeter@arcor.de>

