
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>

#include "hyphen.h"

#define BUFSIZE 512

int main(int argc, char** argv) {
  if (argc != 3) {
    printf("%s: <dic> <word>", argv[0]);
    exit(1);
  }

  HyphenDict *dict;
  /* load the hyphenation dictionary */  
  if ((dict = hnj_hyphen_load(argv[1])) == NULL) {
       fprintf(stderr, "Couldn't find file %s\n", argv[1]);
       fflush(stderr);
       exit(1);
  }

  int word_length = strnlen(argv[2], 1024);
  char * word = (char *)malloc(word_length + 1);
  strncpy(word, argv[2], word_length + 1);

  printf("So I shall hyphenate this: %s\n", word);

  char hword[BUFSIZE * 2];
  char *hyphens = (char *)malloc(word_length + 5);
  char ** rep;
  int * pos;
  int * cut;

  hword[0] = '\0';
  rep = NULL;
  pos = NULL;
  cut = NULL;

  if (hnj_hyphen_hyphenate2(dict, word, word_length, hyphens, hword, &rep, &pos, &cut)) {
    free(hyphens);
    free(word);
    fprintf(stderr, "hyphenation error\n");
    exit(1);
  }

  printf("Hyphens: ");
  for (int i = 0; i < word_length; ++i) {
    if (hyphens[i] & 1) {
       printf(".-");
    }
    else {
       printf(".");
    }
  }
  printf("\n");
  printf("Hyphenated word: %s\n", hword);

  // TODO rep and cut etc not set, why? stackoverflow?

  free(hyphens);
  free(word);
  hnj_hyphen_free(dict);

  return 0;
}
