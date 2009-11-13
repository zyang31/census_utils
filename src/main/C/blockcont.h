#include "shapefil.h"

int checkCont(SHPObject *a, SHPObject *b);
int checkOverlap(double xa1, double ya1, double xa2, double ya2, double xb1, double yb1, double xb2, double yb2);
void callOverlapTestCode();
void callBlockTestCode();
int queen_Contig(SHPObject *,SHPObject *);
