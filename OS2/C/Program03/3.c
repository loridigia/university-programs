#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include <ctype.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <math.h>

int getLen(FILE *file);
int convertBinaryToDecimal(char byte[8]);
void writeCharOnFile(FILE *file, int decimal);
void readOutSed(char *argv[]);
void readBytesFromFile(char *file);
void executeSed(char *argv[]);
void error3(int b, char *argv[]);
void checkFormat(char *argv[]);
void decode();
void takeWord(unsigned char c, bool first);
void readFile(char *name);
void checkArg(int argc, char *argv[]);

struct Parameters {
  unsigned long fileLen;
  char *fileContent;
  char *fileDecoded;
  char wordDecoded[8];
  char word[16];
  char byte1[8];
  char wordEncoded[16];
  char wordHex[4];
};
struct Parameters Info;

int main(int argc, char *argv[]){
  checkArg(argc, argv);
  readFile(argv[1]);
  checkFormat(argv);
  int i;
  FILE *file = fopen("decoded.txt", "w");
  for ( i = 0; i < Info.fileLen; i++) {
    if ( i % 2 == 0 ) takeWord(Info.fileContent[i], true);
    else {
      takeWord(Info.fileContent[i], false);
      fprintf(file,"%c", convertBinaryToDecimal(Info.wordDecoded));
    }
  }
  fclose(file);
  executeSed(argv);
  readOutSed(argv);
  remove("decoded.txt");
  free(Info.fileContent);
  free(Info.fileDecoded);
  return 0;
}

void takeWord(unsigned char c, bool first){
    int i;
    if ( first ) {
      for( i = 0;i < 8; i++){
        if (c & (1<<i)) Info.word[7-i] = 1;
        else Info.word[7-i] = 0;
      }
    }
    else {
      for( i = 0;i < 8; i++){
        if (c & (1<<i)) Info.word[15-i] = 1;
        else Info.word[15-i] = 0;
      }
      decode();
    }
}

int convertBinaryToDecimal( char byte[8]){
  int i;
  int decimal = 0;
  for ( i = 0; i < 8; i++)
    decimal+= (int)byte[i] * pow(2, 7-i);
  return decimal;
}

void executeSed(char *argv[]){
  int status;
  int pid=fork();
  if(pid<0)
    printf("%s\n", "error");
  else if(pid==0){
    char command[30];
    sprintf(command,"sed -e '%s' decoded.txt > outputSed.txt",argv[3]);
    execl("/bin/sh", "/bin/sh", "-c", command, (char*)NULL);
  }
  else
    waitpid(pid,&status,0);
}

void decode(){
  int i;
  for (i = 4; i < 12; i++)
    Info.wordDecoded[i-4] = Info.word[i];
}


void readOutSed(char *argv[]){
  FILE *file = fopen("outputSed.txt", "r");
  FILE *outFile = fopen(argv[2], "wb");
  int c;
  while ((c = fgetc(file)) != EOF)
  {
    fprintf(outFile, "%c",  c >> 4);
    fprintf(outFile, "%c", c << 4);
  }
  fclose(outFile);
  fclose(file);
  remove("outputSed.txt");
}

void readFile(char *name){
  FILE *file;
  file = fopen(name, "rb");
  Info.fileLen = getLen(file);
  Info.fileContent = (char *)malloc(Info.fileLen+1);
  Info.fileDecoded = (char *)malloc(Info.fileLen+1);
  fread(Info.fileContent, Info.fileLen, 1, file);
  fclose(file);
}

int getLen(FILE *file){
  fseek(file, 0, SEEK_END);
  unsigned long lenFile= ftell(file);
  fseek(file, 0, SEEK_SET);
  return lenFile;
}

void error3(int b, char *argv[]){
  FILE *file = fopen(argv[2], "a");
  fclose(file);
  fprintf(stderr, "Wrong format for input binary file %s at byte %i\n", argv[1], b);
  free(Info.fileContent);
  free(Info.fileDecoded);
  exit (30);
}

void checkFormat(char *argv[]){
  int i;
  int k;
  for ( i = 0; i < Info.fileLen; i++) {
    if ( i % 2 == 0 ) takeWord(Info.fileContent[i], true);
    else {
      takeWord(Info.fileContent[i], false);
      for ( k = 0; k < 3; k++) if( Info.word[k] || Info.word[15-k]) error3(i, argv);
    }
  }
}

void checkArg(int argc, char *argv[]){
  if ( argc != 4 ) {
    fprintf(stderr, "Usage: %s file sed_script", argv[0]);
    exit (10);
  }
  if ( access(argv[1], R_OK) != 0 ){
    fprintf(stderr, "Unable to read from file %s because of %s", argv[1], strerror(errno));
    exit (20);
  }
  if ( access(argv[1], W_OK) != 0 ){
    fprintf(stderr, "Unable to write to file %s because of %s", argv[1], strerror(errno));
    exit (20);
  }
}
