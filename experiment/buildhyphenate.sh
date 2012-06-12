echo "building hyphenate ..."
# If "-lhyphen" is missing, make sure "native-src" is build
gcc -std=gnu99 -Wall -o hyphenate hyphenate.c -I ../native-src/hyphen-2.8.3 -L ../native-src/hyphen-2.8.3/.libs -lhyphen -ggdb
echo "running hyphenate ..."
./hyphenate hyph_mini_de.dic danke
