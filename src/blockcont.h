#include "uthash.h"
#include "shapefil.h"
#include "utlist.h"

bool CheckCont(SHPObject *a, SHPObject *b);
bool CheckOverlap(double xa1, double ya1, double xa2, double ya2, double xb1, double yb1, double xb2, double yb2);
int findVerticesLim(SHPObject * a);