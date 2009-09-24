#include <stdio.h>
#include <stdlib.h>
#include "shapefil.h"
//#include "blockcont.h";
#define DBL_TOLERANCE 0.000000000001 //TODO: get this exact value

//UTList * neighbors; //TODO: malloc an array of x pointers to linked lists where x = block count
//TODO: the above should be in the main code not in a lower level function (make it global)
/*
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
} */

int CheckOverlap(double xa1, double ya1, double xa2, double ya2, double xb1, double yb1, double xb2, double yb2)
{
	double delXa=(xa2-xa1);          //TODO: ACCOUNT FOR VERTICAL LINES
	double delYa=(ya2-ya1);
	double delXb=(xb2-xb1);
	double delYb=(yb2-yb1);
	double ma=delYa/delXa;
	double mb=delYb/delXb;

	if((abs(ma-mb)>DBL_TOLERANCE)||(delXa==0&&delXb!=0)||(delXa!=0&&delXb==0)){
		return 0;
	}

	double xAmin=xa1;
	double xBmin=xb1;
	double xAmax=xa2;
	double xBmax=xb2;
	double yAmin=ya1;
	double yBmin=yb1;
	double yBmax=yb2;
	double yAmax=ya2;
	if(xa1>xa2)
	{
		xAmax=xa1;
		xAmin=xa2;
	}
	if(ya1>ya2)
	{
		yAmax=ya1;
		yAmin=ya2;
	}
	if(yb1>yb2)
	{
		yBmax=yb1;
		yBmin=yb2;
	}
	if(xb1>xb2)
	{
		xBmax=xb1;
		xBmin=xb2;
	}

	double ytest1=yb1-ya1;
	double ytest2=yb2-ya1;
	double ytest3=yb1-ya2;
	double mx1=ma*(xb1-xa1);
	double mx2=ma*(xb2-xa1);
	double mx3=ma*(xb1-xa2);

	if((xAmax<xBmin)||(yAmax<yBmin)){ // both x and y values are outside the values for the other block
		return 0;
	}		
	else if(delXa==0&&delXb==0)
	{
		if(xa1==xb1){
			return 1;
		}
	}
	else if ((ytest1-mx1)<=DBL_TOLERANCE&&(ytest2-mx2)<=DBL_TOLERANCE&&(ytest3-mx3)<=DBL_TOLERANCE){ // y=mx+b within tolerance
		return 1;
	}
	else{
		return 0;
	}
}

/*	
int findVerticesLim(SHPObject * a)
{
	if (!(a->nParts))
		return (a->nVertices-1);
	else 
	{
		int count=1;
		while((a->padfX[count]!=NULL)&&(a->padfY[count]!=NULL))
		{
			if(a->padfX[count]==a->padfX[0])&&(a->padfY[count]==a->padfY[0])
				return(count-1);
			count++;
		}
	}
}*/


void callTestCode()
{
	int test1, test2, test3, test4, test5, test6;	


	test1 = CheckOverlap(3, 3.5, 2, 3, 1, 2.5, 4, 4);  //should return true

	test2 = CheckOverlap(1, 2.5, 2, 3, 3, 3.5, 4, 4);  //should return false

        test3 = CheckOverlap(1,2.5,3, 3.5, 2, 3, 4, 4);    //should return true

	test4 = CheckOverlap(4, 0, 10, 0, 4, 0, 10, 0);    //should return true

	test5 = CheckOverlap(0,4,0,10,0,5,0,11);	//should return true

	test6 = CheckOverlap(1,1,-1,-1,-1,1,1,-1);	//should return false

	printf("test1 should be true: %i\n",test1);
	printf("test2 should be false: %i\n",test2);
	printf("test3 should be true: %i\n",test3);
	printf("test4 should be true: %i\n",test4);
	printf("test5 should be true: %i\n",test5);
	printf("test6 should be false: %i\n",test6);



}

int main()
{
	callTestCode();
	//TODO: later on, change to a real main method
	return 0;
}
