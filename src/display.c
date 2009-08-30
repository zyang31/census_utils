#include <stdio.h>
#include <stdlib.h>
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
int main(){
  int entityCount;
  int shapeType;
  double padfMinBound[4];
  double padfMaxBound[4];
  int i;
  SHPHandle handle = SHPOpen("/home/josh/Desktop/FultonCoData/Fultoncombinednd.shp", "rb");
  SHPGetInfo(handle, &entityCount, &shapeType, padfMinBound, padfMaxBound);
  printf("There are %d entities, of type %d\n", entityCount, shapeType);
  
  printf("Allocating %ld bytes of memory\n", entityCount*sizeof(SHPObject *));
  SHPObject **shapeList = malloc(entityCount*sizeof(SHPObject *));
  
  for(i=0; i<5; i++){
    shapeList[i] = SHPReadObject(handle,i);
    printf("Shape %d has the following attributes:\n",i);
    printf("\tID Number: %d\n", shapeList[i]->nShapeId);
    printf("\tVertex count: %d\n", shapeList[i]->nVertices);
  }
  for(i=0; i<5; i++){
    SHPDestroyObject(shapeList[i]);
  }


  SHPClose(handle);
}
