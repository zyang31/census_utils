#ifndef _NEIGHBORSH
#define _NEIGHBORSH

typedef struct {
     int ID;
     struct neighbor *prev;
     struct neighbor *next;
} neighborlist;

#endif
