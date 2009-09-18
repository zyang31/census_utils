#include "UTlist.h";
#define DBL_TOLERANCE 0.000000000001 //TODO: get this exact value

UTList * neighbors; //TODO: malloc an array of x pointers to linked lists where x = block count
//TODO: the above should be in the main code not in a lower level function (make it global)

bool CheckCont(SHPObject *a, SHPObject *b)
{

   if(neighbors.exist(a)==b) //TODO: fix this
   {
      return true;
   }
   for(int i=0;i<a.nVertices-1;i++) //TODO: account for multiple polygons
   {
      for(int j=0;j<b.nVertices-1;j++)
      {
    	if (CheckOverlap(a.panPartStart[i], a.panPartStart[i+1], b.panPartStart[j], b. panPartStart[j+1])){
   			neighbors.append(a); //TODO: fix this
		}
      }
   }
}

bool CheckOverlap(double xa1, double ya1, double xa2, double ya2, double xb1, double yb1, double xb2, double yb2)
{
	delXa=(xa2-xa1);
	delYa=(ya2-ya1);
	delXb=(xb2-xb1);
	delYb=(yb2-yb1);
	ma=delYa/delXa;
	mb=delYb/delXb;
	overlap=
	if(abs(ma-mb)<=DBL_TOLERANCE){ // slopes are equal enough
		if(){ // both x and y values are outside the values for the other block
			return false;
		}else if (){ // y=mx+b within tolerance
			return true;
		}else{
			return false;
		}
	}
	else{
		return false;
	}
}