
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <ctype.h>

#include "hyphen.h"

#define BUFSIZE 512

int main(int argc, char** argv) {
  if (argc != 3) {
    printf("%s: <dic> <word>\n", argv[0]);
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

  char hword[BUFSIZE];
  hword[0] = '\0';
  char *hyphens = (char *)malloc(word_length + 1);

  // Are these correct?
  char **rep = (char **)malloc(word_length * sizeof(char*));
  int * pos  = (int*)malloc(word_length * sizeof(int));
  int * cut  = (int*)malloc(word_length * sizeof(int));
  memset(rep, 0, word_length * sizeof(char*));
  memset(pos, 0, word_length * sizeof(int));
  memset(cut, 0, word_length * sizeof(int));

  if (hnj_hyphen_hyphenate2(dict, word, word_length, hyphens, hword, &rep, &pos, &cut)) {
    free(hyphens);
    free(word);
    fprintf(stderr, "hyphenation error\n");
    exit(1);
  }

  printf("Hyphens: ");
  for (int i = 0; i < word_length + 1; ++i) {
    if (hyphens[i] & 1) {
       printf(".-");
    }
    else {
       printf(".");
    }
  }
  printf("\n");
  printf("Hyphenated word: %s\n", hword);

  // rep and cut etc not set, why? - Schifffart from the header doku used ...
  printf("rep: ");
  for (int i = 0; i < word_length; ++i) {
    if (rep[i] != NULL) {
       printf("%s", rep[i]);
    }
    else {
       printf("_");
    }
  }
  printf("\n");

  printf("pos: ");
  for (int i = 0; i < word_length; ++i) {
    printf("%d", pos[i]);
  }
  printf("\n");

  printf("cut: ");
  for (int i = 0; i < word_length; ++i) {
    printf("%d", cut[i]);
  }
  printf("\n");

  free(hyphens);
  free(word);
  hnj_hyphen_free(dict);

  return 0;
}
