#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "shapefil.h"

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

  The Shapelib library is licensed under the GNU Lesser General Public License.
  A copy of the GNU LGPL can be found on http://www.gnu.org/licenses/lgpl-3.0.txt .
  For information on Shapelib, see http://shapelib.maptools.org/ .
*/

void svg_header(FILE *svg){
  fprintf(svg, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
  fprintf(svg, "<svg\n
    xmlns:svg=\"http://www.w3.org/2000/svg\"\n
    xmlns=\"http://www.w3.org/2000/svg\"\n
    version=\"1.0\"\n
    width=\"360\"\n
    height=\"180\"\n
    id=\"svg2\">\n");
}

void svg_footer(FILE *svg){
  fprintf(svg, "</svg>");
}

int main(){
  int entityCount;
  int shapeType;
  double padfMinBound[4];
  double padfMaxBound[4];
  int i;
  //For now, we'll use this. Later on, this will change.
  char sf_name[] = "/home/josh/Desktop/FultonCoData/Fultoncombinednd.shp";
  SHPHandle handle = SHPOpen(sf_name, "rb");


  int fn_len = strlen(sf_name);
  char svg_filename[fn_len];
  FILE *svg;
  strcpy(svg_filename, sf_name);
  svg_filename[fn_len-2] = 'v';
  svg_filename[fn_len-1] = 'g';

  SHPGetInfo(handle, &entityCount, &shapeType, padfMinBound, padfMaxBound);
  printf("There are %d entities, of type %d\n", entityCount, shapeType);
  printf("Filename is: %s \n", svg_filename);
  
  printf("Allocating %ld bytes of memory\n", entityCount*sizeof(SHPObject *));
  SHPObject **shapeList = malloc(entityCount*sizeof(SHPObject *));
  
  //populate the shapeList
  for(i=0; i<entityCount; i++){
    shapeList[i] = SHPReadObject(handle,i);
  }
  //set up the SVG file
  svg = fopen(svg_filename, "rw");

  //fprintf header
  svg_header(svg);

  for(i=0; i<entityCount; i++){
    //fprintf individual blocks
  }
 
  //fprintf footer
  svg_footer(svg);

  for(i=0; i<entityCount; i++){
    SHPDestroyObject(shapeList[i]);
  }
  SHPClose(handle);
  fclose(svg);
}

