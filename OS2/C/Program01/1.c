#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include <fnmatch.h>

bool isLast(int counter, int n, struct dirent **namelist, const char *dir);
bool isLink(char *d_name);
bool isFile(const char *path);
bool checkHidden(char *name);
bool matchPattern(char *name, char *path);
void checkArguments(const char *dir);
void listdir(const char *name, int indent);
void output(char *d_name, int indent, bool last);
void outputLink(char *path,char *d_name, int indent, bool last);
void recursion(const char *dir, int lvl);
//-a = stampa tutto incluso i file nascosti
//-L = max display depth
//-P = pattern file to take

struct Parameters {
   char *pattern;
   int Maxlvl;
   bool a;
   int *argc;
   int optind;
   char *progName;
   bool array[100];
   int TotFiles;
   int TotDirs;
};

struct Parameters Param;

int main(int argc, char *argv[])
{
  Param.progName = argv[0];
  Param.argc = &argc;
  Param.pattern = NULL;
  Param.Maxlvl = 100;
  Param.a = 0;

  int c;
  int i;
  opterr = 0;
  while ((c = getopt (argc, argv, "P:L:a")) != -1)
  switch (c)
    {
    case 'P':
      Param.pattern = optarg;
      break;
    case 'L':
      Param.Maxlvl = atoi(optarg);
      break;
    case 'a':
      Param.a = 1;
      break;
    case '?':
      fprintf (stderr, "Usage: %s  [-P pattern] [-L level] [-a] [dirs]\n", argv[0]);
      exit(20);
    default:
     abort();
  }

for (i = optind; i < argc; i++) {
  checkArguments(argv[i]);
  recursion(argv[i], 0);
}
if(Param.TotFiles == 1) printf("\n%i directories, %i file\n",Param.TotDirs,Param.TotFiles );
else printf("\n%i directories, %i files\n",Param.TotDirs,Param.TotFiles );
return 0;
}

void recursion(const char *dir, int lvl){
  Param.array[lvl] = 0;
  if (lvl == 0) printf("%s\n", dir);
  if (lvl == Param.Maxlvl)return;
  struct dirent **namelist;
  int n = scandir(dir, &namelist, 0, alphasort);
  int counter;
  for (counter = 0; counter < n; counter++){
    char path[1024];
    if (strcmp(namelist[counter]->d_name, ".") == 0 || strcmp(namelist[counter]->d_name, "..") == 0 ){
      free(namelist[counter]);
      continue;
    }
    if ( checkHidden(namelist[counter]->d_name) ){
      free(namelist[counter]);
      continue;
    }
    if (namelist[counter]->d_type == DT_DIR){
      Param.TotDirs++;
      sprintf(path, "%s/%s", dir, namelist[counter]->d_name);
      if( counter == n-1 || isLast(counter, n, namelist, dir) ) {
        Param.array[lvl] = 1;
        output(namelist[counter]->d_name, lvl, true);
      }
      else output(namelist[counter]->d_name, lvl, false);
      free(namelist[counter]);
      recursion(path, lvl+1);
    }
    else {
      char file[1024];
      sprintf(file, "%s/%s", dir, namelist[counter]->d_name);
      if( (counter == n-1 || isLast(counter, n, namelist, dir)) && matchPattern(namelist[counter]->d_name, file)){
        Param.TotFiles++;
        if( isLink(file) ) outputLink(file, namelist[counter]->d_name, lvl, true);
        else output(namelist[counter]->d_name, lvl, true);
      }
      else if(matchPattern(namelist[counter]->d_name, file)){
        Param.TotFiles++;
        if( isLink(file) ) outputLink(file, namelist[counter]->d_name, lvl, false);
        else output(namelist[counter]->d_name, lvl, false);
      }
    free(namelist[counter]);
    }
  }
free(namelist);
}

bool isLast(int counter, int n, struct dirent **namelist, const char *dir ){
  int x;
  int good = 0;
  for ( x = counter+1; x < n; x++) {
    char file[1024];
    sprintf(file, "%s/%s", dir, namelist[x]->d_name);

    if (namelist[x]->d_type == DT_DIR) return false;
    else {
      if ( matchPattern(namelist[x]->d_name, file) ) good++;
      else if ( isLink(file) && Param.pattern != NULL) continue;
      else if ( isLink(file) && Param.pattern == NULL) good++;
    }
  }
  return (good == 0 ) ? true :  false;
}

bool checkHidden(char *name){
  if(Param.a == 1) return 0;
  else return (name[0] == '.') ? true : false;
}

bool isLink(char *path){
  struct stat buf;
  lstat(path, &buf);
  return (S_ISLNK(buf.st_mode));
}

bool matchPattern(char *name, char *path){
  if (Param.pattern == NULL) return true;
  if ( fnmatch(Param.pattern, name, FNM_PATHNAME) != 0 ) return false;
  else return isLink(path) ? false : true ;
 }

void output(char *d_name, int indent, bool last){
  int i;
  for (i = 0; i < indent; i++) {
    if ( Param.array[i] == 0) printf("|%s", "   ");
    else printf("    ");
  }
  if( last ) printf("`-- %s\n", d_name);
  else printf("|-- %s\n", d_name);
}

void outputLink(char *path, char *d_name, int indent, bool last){
  int i;
  char *linkPath = malloc(256);
  struct stat buf;
  for (i = 0; i < indent; i++) {
    if ( Param.array[i] == 0) printf("|%s", "   ");
    else printf("    ");
  }
  lstat(path, &buf);
  int len = readlink(path, linkPath, sizeof(buf)-1);
  linkPath[len] = '\0';
  if( last ) printf("`-- %s -> %s\n", d_name,linkPath);
  else printf("|-- %s -> %s\n", d_name, linkPath);
  free(linkPath);
}

bool isFile(const char *path){
    struct stat path_stat;
    stat(path, &path_stat);
    return S_ISREG(path_stat.st_mode);
}


void checkArguments(const char *dir){
//check if a input argument is a file
  struct stat info;
  if( stat( dir, &info ) != 0 )
    printf( "cannot access %s\n", dir );
  else if( info.st_mode & S_IFDIR );
  else {
    printf( "%s [error opening dir because of being not a dir]\n", dir);
    printf("\n%i directories, %i files\n",Param.TotDirs,Param.TotFiles );
    exit (10); }
  }
