#include <stdio.h>
#include <stdlib.h>
#include "shapefil.h"
#include "uthash.h"

/*
 *     Contiguity list generator for Census shapefiles
 *     Copyright (C) <2009>  <Joshua Justice>
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *     The Shapelib library is licensed under the GNU Lesser General Public License.
 *     A copy of the GNU LGPL can be found on http://www.gnu.org/licenses/lgpl-3.0.txt .
 *     For information on Shapelib, see http://shapelib.maptools.org/ .
 *                                                                              */

/*The key for the hash table for blocks is the product of the x and y coordinates of the first vertix of the outermost ring of
 *  * the block. This is done so that all the blocks with a common point would be in the common bucket */
typedef struct
{
    long double key;
    SHPObject *block;
    UT_hash_handle hh;
} HT_Struct_For_Block;

HT_Struct_For_Block *HT_Blocks = NULL;
SHPObject **block_list = NULL;
int block_count;

/* We do not require ShapeType, padfMinBound and padfMaxBound as of now. Hence passing NULL to SHPGetInfo. */
//int ShapeType;
////double padfMinBound[4], padfMaxBound[4]; 

void Add_block_to_HT();

void Add_Blocks_to_HT(SHPHandle handle)
{
  int i;
  SHPGetInfo(handle, &block_count, NULL, NULL, NULL);
  /* SHPObject **block_list = malloc(block_count*sizeof(SHPObject *));*/
  printf("\nTotal number of blocks identified from SHPGetInfo = %d", block_count);
  block_list = malloc(block_count*sizeof(SHPObject *));
  for(i=0; i<block_count; i++)
  {
  block_list[i] = SHPReadObject(handle, i);
  Add_block_to_HT(block_list[i]);
  }
}
 
void Add_block_to_HT(SHPObject *object)
{
  HT_Struct_For_Block *new_block1, *new_block2, *new_block3, *new_block4;

  new_block1 = malloc(sizeof(HT_Struct_For_Block));
  new_block1->key = object->padfX[0] * object->padfY[0];
  HASH_ADD(hh, HT_Blocks, key, sizeof(long double), new_block1);
  
  new_block2 = malloc(sizeof(HT_Struct_For_Block));
  new_block2->key = (object->padfX[0]+.0005) * (object->padfY[0]);
  HASH_ADD(hh, HT_Blocks, key, sizeof(long double), new_block2);
  
  new_block3 = malloc(sizeof(HT_Struct_For_Block));
  new_block3->key = (object->padfX[0]) * (object->padfY[0]+.0005);
  HASH_ADD(hh, HT_Blocks, key, sizeof(long double), new_block3);
  
  new_block4 = malloc(sizeof(HT_Struct_For_Block));
  new_block4->key = (object->padfX[0]+.0005) * (object->padfY[0]+.0005);
  HASH_ADD(hh, HT_Blocks, key, sizeof(long double), new_block4);
}
  
//This function can be called to test on the hash table
void test_hashing()
{
}
  
int main(){
  int i;
  SHPHandle handle = SHPOpen("/home/sumanth/Documents/eDemocracy/Files/Fultoncombinednd.shp", "rb");
  Add_Blocks_to_HT(handle);

  printf("\nTotal number of blocks in the block HT = %d\n", HASH_COUNT(HT_Blocks));
  //test_hashing();
  
  //free the block list
  for(i=0; i<block_count; i++)
  SHPDestroyObject(block_list[i]);
  return 0;
}
