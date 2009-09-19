#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include "shapefil.h"
#include "uthash.h"

#define TRUNCATE_UPTO 	100000
#define TOLERANCE 	0.0005

#define TRUNCATE(var, convert)  { int temp = var*TRUNCATE_UPTO; convert = (double)temp/TRUNCATE_UPTO; }  

/*
 *     Contiguity list generator for Census shapefiles
 *     Copyright (C) <2009>  <Joshua Justice, Sumanth Narendra>
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

/*     The key for the hash table for blocks is the product of the x and y coordinates of the first vertix of the outermost ring of
 *     the block. This is done so that all the blocks with a common point would be in the common bucket */

typedef struct
{
    double padfX;
    double padfY;
}lookup_key;

typedef struct
{
    SHPObject *block;
    struct bucket_list *next_block;
}bucket_list;

typedef struct
{
    double padfX;  /*padfX and padfY comprise the key. Both are passed onto the hash ADD */
    double padfY;
    //SHPObject *block_bucket; // make this a UTList of SHPObject *
    bucket_list list;
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
  printf("\n added %f %f", block_list[i]->padfX[0], block_list[i]->padfY[0]);
  }

  /* testing
  for(i=0; i<block_count; i++)
  {
      HT_Struct_For_Block *test2 = NULL;
      double padfX, padfY;
      int count = 0, keylen;
      lookup_key *key1 = malloc(sizeof(lookup_key));
      TRUNCATE(block_list[i]->padfX[0], padfX);
      TRUNCATE(block_list[i]->padfY[0], padfY);
      while (count < 4)
      {
      memset(key1, 0, sizeof(*key1));
      key1->padfX = padfX; key1->padfY = padfY;
      test2 = NULL;
      if(count == 1) { key1->padfX += TOLERANCE; }
      else if(count == 2) { key1->padfY += TOLERANCE; }
      else if(count == 3) { key1->padfX += TOLERANCE; key1->padfY += TOLERANCE; }
      keylen = offsetof(HT_Struct_For_Block, padfX) + sizeof(double) - offsetof(HT_Struct_For_Block, padfY);
      printf("\n to be found!! %f %f", key1->padfX, key1->padfY);
      HASH_FIND(hh, HT_Blocks, &key1->padfX, keylen, test2); 
  
      if(test2 != NULL)
         printf("\n found!!");
      else
         printf("\n not found!!\n");
      count++;
      }
      free(key1);
  }*/ 
}

void Add_block_to_HT(SHPObject *object)
{
  HT_Struct_For_Block *new_block1, *new_block2, *new_block3, *new_block4;
  double padfX, padfY;
  int keylen = offsetof(HT_Struct_For_Block, padfX) + sizeof(double) - offsetof(HT_Struct_For_Block, padfY);
  TRUNCATE(object->padfX[0], padfX);
  TRUNCATE(object->padfY[0], padfY);

  new_block1 = malloc(sizeof(HT_Struct_For_Block));
  memset(new_block1, 0, sizeof(HT_Struct_For_Block));
  new_block1->list.block = object;
  new_block1->padfX = padfX;
  new_block1->padfY = padfY;
  HASH_ADD(hh, HT_Blocks, padfX, keylen, new_block1);
  
  new_block2 = malloc(sizeof(HT_Struct_For_Block));
  memset(new_block2, 0, sizeof(HT_Struct_For_Block));
  new_block2->list.block = object;
  new_block2->padfX = padfX + TOLERANCE;
  new_block2->padfY = padfY;
  HASH_ADD(hh, HT_Blocks, padfX, keylen, new_block2);
  
  new_block3 = malloc(sizeof(HT_Struct_For_Block));
  memset(new_block3, 0, sizeof(HT_Struct_For_Block));
  new_block3->list.block = object;
  new_block3->padfX = padfX;
  new_block3->padfY = padfY + TOLERANCE;
  HASH_ADD(hh, HT_Blocks, padfX, keylen, new_block3);
  
  new_block4 = malloc(sizeof(HT_Struct_For_Block));
  memset(new_block4, 0, sizeof(HT_Struct_For_Block));
  new_block4->list.block = object;
  new_block4->padfX = padfX + TOLERANCE;
  new_block4->padfY = padfY + TOLERANCE;
  HASH_ADD(hh, HT_Blocks, padfX, keylen, new_block4);
}
  
void print_table()
{
  HT_Struct_For_Block *s;
  for(s=HT_Blocks; s != NULL; s=s->hh.next)
    printf("\npadfX = %f, padfY = %f \n", s->padfX, s->padfY); 
}
 
//This function can be called to test on the hash table
void test_hashing()
{
  HT_Struct_For_Block *test1, *test2 = NULL;
  int keylen = offsetof(HT_Struct_For_Block, padfY) + sizeof(double) - offsetof(HT_Struct_For_Block, padfX);
  double padfX1 = 37.45; 
  double padfY1 = 234.54;

  test1 = malloc(sizeof(HT_Struct_For_Block));
  memset(test1 , 0, sizeof(HT_Struct_For_Block));
  test1->padfX = padfX1;
  test1->padfY = padfY1;
  HASH_ADD(hh, HT_Blocks, padfX, keylen, test1);
  //print_table();

  lookup_key *key1 = malloc(sizeof(lookup_key));
  memset(key1, 0, sizeof(*key1));
  key1->padfX = 47.45;
  key1->padfY = 234.54;
  HASH_FIND(hh, HT_Blocks, &key1->padfX, keylen, test2); 
  
  if(test2 != NULL)
     printf("\n found!! %f %f\n", test2->padfX, test2->padfY);
  else
     printf("\n not found!!\n");
  free(key1);
}
 
int main(){
  int i;
  SHPHandle handle = SHPOpen("/home/sumanth/Documents/eDemocracy/Files/Fultoncombinednd.shp", "rb");
  Add_Blocks_to_HT(handle);

  printf("\nTotal number of blocks in the block HT = %d\n", HASH_COUNT(HT_Blocks));
  test_hashing();


  //TODO: Free the mallocs done for HT
  
  //free all the items in the HT
  HT_Struct_For_Block *current;
  while (HT_Blocks)
  {
     current = HT_Blocks;
     HASH_DEL(HT_Blocks, current);
     free(current);
  }  

  for(i=0; i<block_count; i++)
  SHPDestroyObject(block_list[i]);
  return 0;
}
