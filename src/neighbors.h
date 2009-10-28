#ifndef _NEIGHBORSH
#define _NEIGHBORSH

typedef struct neighborList{
     int ID;
     struct neighborList *prev;
     struct neighborList *next;
} neighborList;

#endif
