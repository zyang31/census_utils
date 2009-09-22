#include "blockcont.h";
#define DBL_TOLERANCE 0.000000000001 //TODO: get this exact value

//UTList * neighbors; //TODO: malloc an array of x pointers to linked lists where x = block count
//TODO: the above should be in the main code not in a lower level function (make it global)

typedef struct {
	SHPObject *block;
	struct neighbor *prev, *next;
}neighbor;

struct neighbor * neighborlist=(neighbor*) malloc (sizeof(SHPObject*)*block_count);

bool CheckCont(SHPObject *a, SHPObject *b)
{
	neightbor h1 = neightborlist[0];
	int count=0;
	while(h1->next != null)
	{
		if (h1->block==a)
		{
			if (h1->next->block==b)||(h1->prev->block==b)
				return true;
		}
		count++;
	}

	int verticesLimA=findVerticesLim(a);
	int verticesLimB=findVerticesLim(b);

   for(int i=0;i<verticesLimA;i++) //accounts for multiple polygons
   {
      for(int j=0;j<verticesLimB;j++)
      {
    	if (CheckOverlap(a->panPartStart[i], a->panPartStart[i+1], b->panPartStart[j], b->panPartStart[j+1]))
		{
   			neighborlist[count]->block=a;
			neighborlist[count]->next=b;
		}
		
      }
   }
}

bool CheckOverlap(double xa1, double ya1, double xa2, double ya2, double xb1, double yb1, double xb2, double yb2)
{
	double delXa=(xa2-xa1);
	double delYa=(ya2-ya1);
	double delXb=(xb2-xb1);
	double delYb=(yb2-yb1);
	double ma=delYa/delXa;
	double mb=delYb/delXb;
	double xAmin;
	double xBmin;
	double xAmax;
	double xBmax;
	double yAmin;
	double yBmin;
	double yBmax;
	double yAmax;
	if(xa1>xa2)
	{
		xAmax=xa1;
		xAmin=xa2;
	}
	else
	{
		xAmax=xa2;
		xAmin=xa1;
	}
	if(ya1>ya2)
	{
		yAmax=ya1;
		yAmin=ya2;
	}
	else
	{
		yAmax=ya2;
		yAmin=ya1;
	}
	if(yb1>yb2)
	{
		yBmax=yb1;
		yBmin=yb2;
	}
	else
	{
		yBmax=yb2;
		yBmin=yb1;
	}
	if(xb1>xb2)
	{
		xBmax=xb1;
		xBmin=xb2;
	}
	else
	{
		xBmax=xb2;
		xBmin=xb1;
	}

	double ytest1=yb1-ya1;
	double ytest2=yb2-ya1;
	double ytest3=yb1-ya2;
	double mx1=ma*(xb1-xa1);
	double mx2=ma*(xb2-xa1);
	double mx3=ma*(xb1-ya2);

	if(abs(ma-mb)<=DBL_TOLERANCE){ // slopes are equal enough
		if((xAmax<xBmin)||(yAmax<yBmin)){ // both x and y values are outside the values for the other block
			return false;
		}else if ((ytest1-mx1)<=DBL_TOLERANCE&&(ytest2-mx2)<=DBL_TOLERANCE&&(ytest3-mx3)<=DBL_TOLERANCE){ // y=mx+b within tolerance
			return true;
	}
	else{
		return false;
	}
}

	
int findVerticesLim(SHPObject * a)
{
	if !(a->nParts)
		return (a->nVertices-1);
	else 
	{
		int count=1;
		while(a->padfX[count]!=NULL)&&(a->padfY[count]!=NULL)
		{
			if(a->padfX[count]==a->padfX[0])&&(a->padfY[count]==a->padfY[0])
				return(count-1);
			count++;
		}
	}
}