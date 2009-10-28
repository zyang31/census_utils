#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "shapefil.h"
#include "neighbors.h"

//global variables
double xCentList[entityCount];
double yCentList[entityCount];

/*
  Code to display Census shapefiles.
  Copyright (C) <2009>  <Joshua Justice>

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License along
  with this program; if not, write to the Free Software Foundation, Inc.,
  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
/*
  The Shapelib library is licensed under the GNU Lesser General Public License.
  A copy of the GNU LGPL can be found on http://www.gnu.org/licenses/lgpl-3.0.txt .
  For information on Shapelib, see http://shapelib.maptools.org/ .
*/
/*
  EULA: The Graphics Gems code is copyright-protected. 
  In other words, you cannot claim the text of the code as your own and resell it. 
  Using the code is permitted in any program, product, or library, non-commercial or commercial. 
  Giving credit is not required, though is a nice gesture. 
  The code comes as-is, and if there are any flaws or problems with any Gems code, nobody involved
  with Gems - authors, editors, publishers, or webmasters - are to be held responsible. 
  Basically, don't be a jerk, and remember that anything free comes with no guarantee. 
*/



/*  
    polyCentroid: Calculates the centroid (xCentroid, yCentroid) and area
    of a polygon, given its vertices (x[0], y[0]) ... (x[n-1], y[n-1]). It
    is assumed that the contour is closed, i.e., that the vertex following
    (x[n-1], y[n-1]) is (x[0], y[0]).  The algebraic sign of the area is
    positive for counterclockwise ordering of vertices in x-y plane;
    otherwise negative.

    Returned values:  0 for normal execution;  1 if the polygon is
    degenerate (number of vertices < 3);  and 2 if area = 0 (and the
    centroid is undefined).
*/

int polyCentroid(double x[], double y[], int n,
		 double *xCentroid, double *yCentroid, double *area){
     register int i, j;
     double ai, atmp = 0, xtmp = 0, ytmp = 0;
     if (n < 3) return 1;
     for (i = n-1, j = 0; j < n; i = j, j++){
          ai = x[i] * y[j] - x[j] * y[i];
          atmp += ai;
          xtmp += (x[j] + x[i]) * ai;
          ytmp += (y[j] + y[i]) * ai;
     }
     *area = atmp / 2;
     if (atmp != 0){
          *xCentroid =	xtmp / (3 * atmp);
          *yCentroid =	ytmp / (3 * atmp);
          return 0;
     }
     return 2;
} //end Graphics Gems code

void colorArrange(int* array, int n){
     unsigned int array_size = n+1;
     unsigned int max=0xffffff;
     int min=0x000000;
     unsigned int diff=(max-min)/array_size;
     int i;
     unsigned int current = max;
     //check array size here
     for(i=0; i<array_size; i++){
          array[i]=current;
          current=current-diff;
     }
     return;
}

void svg_header(FILE *svg){
     fputs("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n", svg);
     fputs("<svg\n\txmlns:svg=\"http://www.w3.org/2000/svg\"\n", svg);
     fputs("\txmlns=\"http://www.w3.org/2000/svg\"\n", svg);
     fputs("\tversion=\"1.0\"\n", svg);
     fputs("\twidth=\"360000\"\n", svg);
     fputs("\theight=\"180000\"\n", svg);
     fputs("\tid=\"svg2\">\n", svg);
     fputs("\t<defs\n\t\tid=\"defs1\" />\n", svg);
     fputs("\t<g\n\t\tid=\"layer1\">\n", svg);
     return;
}

void svg_polygon(SHPObject block, FILE *svg, int use_dist){
     int i,j,jLim;
     double x,y;
     fputs("\t\t<path\n\t\t\td=\"", svg);  
     for(i=0;i<block.nParts;i++){
          if(i==block.nParts-1){
               jLim=block.nVertices-1;
          }else{
               jLim=block.panPartStart[i+1]-2;
          }
          for(j=block.panPartStart[i];j<jLim;j++){
               //draw coordinates at padfX[j] etc.
               if(j==block.panPartStart[i]){
                    fputs("M ",svg); //not having the \n is deliberate
               }else{
                    fputs("L ",svg); //no \n is also deliberate here
               }
               x=(block.padfX[j]+180)*10000;
               y=(block.padfY[j]-90)*-10000; //SVG has y-down
               fprintf(svg, "%f %f ",x,y);
          }
     }
     fprintf(svg,"\"\n\t\t\tid=\"path%d\"\n",block.nShapeId);
     if(use_dist){
          //TODO: replace #ffffff with that district's color
          //use %X6 on that block's district's entry in colorarray
          //fprintf(svg,"\t\t\tstyle=\"fill:#%x6;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"/>");
     }else{
          fprintf(svg,"\t\t\tstyle=\"fill:#ffffff;fill-rule:evenodd;stroke:#000000;stroke-width:1px;stroke-linecap:butt;stroke-linejoin:miter;stroke-opacity:1\"/>");
     }
     return;
}

void svg_neighbors(SHPObject block, neighborList neighbors, FILE *svg){
     //TODO: write this function
     //The process is as follows:
     //for each neighbor to the block, print the path between the centroids
     
     neighborList current = neighbors;
     int currPos;
     double bx, by, nx, ny;
     bx = xCentList[block.ID - 1];
     by = yCentList[block.ID - 1];

     while(current!=NULL){
          currPos = current.ID - 1;
          nx = xCentList[currPos];
          ny = yCentList[currPos];
          



     }


     return;
}

void svg_footer(FILE *svg){
     fputs("\t</g>\n", svg);
     fputs("</svg>", svg);
}

int main(){
     int entityCount;
     int shapeType;
     double padfMinBound[4];
     double padfMaxBound[4];
     int i;
     int use_gal = 1;
     int use_dist = 0;  //file loading
     //make color array here, fill w/ 0xffffff
     //For desktop
     //char sf_name[] = "/home/josh/Desktop/FultonCoData/Fultoncombinednd.shp";
     //for clamps
     char sf_name[] = "/home/joshua/FultonCoData/Fultoncombinednd.shp";
     //Eventually, this won't be hardcoded

     SHPHandle handle = SHPOpen(sf_name, "rb");


     int fn_len = strlen(sf_name);
     char svg_filename[fn_len];
     char gal_filename[fn_len];
     FILE *svg;
     strcpy(svg_filename, sf_name);
     strcpy(gal_filename, sf_name);
     svg_filename[fn_len-2] = 'v';
     svg_filename[fn_len-1] = 'g';
     gal_filename[fn_len-3] = 'G';
     gal_filename[fn_len-2] = 'A';
     gal_filename[fn_len-1] = 'L';
     //I know, the above isn't really robust enough.
     //Should be improved upon when the file name is no longer hardcoded

     SHPGetInfo(handle, &entityCount, &shapeType, padfMinBound, padfMaxBound);
 
     SHPObject **shapeList = malloc(entityCount*sizeof(SHPObject *));
     neighborList neighbors[entityCount];
     double areaList[entityCount];
     //populate the shapeList
     for(i=0; i<entityCount; i++){
          shapeList[i] = SHPReadObject(handle,i);
     }
     printf("Shapelist populated.\n");
     //delete file if it exists
     remove(svg_filename);
     //set up the SVG file pointer
     svg = fopen(svg_filename, "a+");
     printf("SVG file opened for writing.\n");
 

     //write header
     svg_header(svg);
     printf("SVG header printed.\n");
 
     //write individual polygons
     for(i=0; i<entityCount; i++){
          svg_polygon(*shapeList[i], svg, use_dist);
     }
     printf("Polygons all printed.\n");
     if(use_gal){
          char line[1024];
          //int id;
          int nblocks;
          FILE *gal;
          printf("The name of the file is: %s\n", gal_filename);
          gal= fopen(gal_filename, "r");
          if(gal==NULL){
               printf("Error: Could not open GAL file.\n");
               return -1;
          }
          fgets(line, 1024, gal);
          nblocks = atoi(line);
          if(nblocks==entityCount){
               printf("GAL block count matches shapefile block count. Proceeding...\n");
               //TODO: load neighbors from GAL file





          }else{
               printf("Error: GAL block count does not match shapefile block count.\n");
               return -1;
          }
          //find centroids for every block
          for(i=0; i<entityCount; i++){
               int lastPoint;
               int status;
               SHPObject block = *shapeList[i];
               //Note that we're going to disregard holes, etc.
               if(block.nParts>1){
                    lastPoint = block.panPartStart[1]-1;
               }else{
                    lastPoint = block.nVertices-1;
               }
               status = polyCentroid(block.padfX, block.padfY, lastPoint, 
                                     xCentList+i, yCentList+i, areaList+i);
          }
          printf("Centroids calculated.\n");
          //write paths from centroid to centroid
          for(i=0; i<entityCount; i++){
               svg_neighbors(*shapeList[i], neighbors[i], svg);
          }  
          //printf("Contiguity paths drawn.\n");
          fclose(gal);
     }


  
     //write footer
     svg_footer(svg);
     printf("SVG footer printed.\n");
     for(i=0; i<entityCount; i++){
          SHPDestroyObject(shapeList[i]);
     }
     SHPClose(handle);
     fclose(svg);
     return 0;
}



//for testing
/*
  int main(){
  int ndists=10;
  int color_array[ndists+1];
  int i;
  colorArrange(color_array,ndists);
  for(i=0;i<=ndists;i++){
  printf("%X\n",color_array[i]);
  }
  return 0;
  }
*/

