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

#include <stdio.h>
#include <stdlib.h>
#include <stddef.h>
#include "shapefil.h"
#include "uthash.h"

#define TRUNCATE_UPTO 	100000
#define TOLERANCE 	0.0005

#define TRUNCATE(var, convert)  {                                                    \
     int temp = var*TRUNCATE_UPTO;                                                   \
     convert = (double)temp/TRUNCATE_UPTO;                                           \
}  

#define ADD_TO_BUCKET_LIST(present, traverse, new_entry, object) {                   \
     new_entry = malloc(sizeof(bucket_list));			                     \
     memset(new_entry, 0, sizeof(bucket_list));			                     \
     new_entry->block = object;					                     \
     traverse = (bucket_list *)present->head_of_list.next_block;                     \
     if( traverse == NULL)					                     \
        present->head_of_list.next_block = (struct bucket_list *)new_entry;          \
     else							                     \
     {								                     \
       while( traverse->next_block != NULL)			                     \
         traverse = (bucket_list *)traverse->next_block;	                     \
       traverse->next_block = (struct bucket_list *)new_entry;	                     \
     }								                     \
}

#define ADD_TO_HASH_TABLE(new_block, padfX, padfY, object, x_inc, y_inc) {           \
     new_block = malloc(sizeof(HT_Struct_For_Block));                                \
     memset(new_block, 0, sizeof(HT_Struct_For_Block));                              \
     new_block->head_of_list.block = object;                                         \
     new_block->head_of_list.next_block = NULL;                                      \
     new_block->padfX = padfX + x_inc;                                               \
     new_block->padfY = padfY + y_inc;                                               \
     HASH_ADD(hh, HT_Blocks, padfX, keylen, new_block);                              \
}     

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
    bucket_list head_of_list;
    UT_hash_handle hh;
} HT_Struct_For_Block;

HT_Struct_For_Block *HT_Blocks = NULL;
SHPObject **block_list = NULL;
int block_count;

void Add_block_to_HT();

void Add_Blocks_to_HT(SHPHandle handle)
{
  int i;
  SHPGetInfo(handle, &block_count, NULL, NULL, NULL);
  printf("\nTotal number of blocks identified from SHPGetInfo = %d", block_count);
  block_list = malloc(block_count*sizeof(SHPObject *));
  for(i=0; i<block_count; i++)
  {
  block_list[i] = SHPReadObject(handle, i);
  Add_block_to_HT(block_list[i]);
  //printf("\n added %f %f", block_list[i]->padfX[0], block_list[i]->padfY[0]);
  }

  /* This code was used for testing. Believe it is better to have this for debugging purposes in the future. 
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

/* check_for_entry_in_HT checks if there is an entry already present in the HT with the same key. (hash collision).
 * If there is a hash collision we add it to the bucket_list */

HT_Struct_For_Block * check_for_entry_in_HT(double padfX, double padfY)
{
  HT_Struct_For_Block *present = NULL;
  int keylen = offsetof(HT_Struct_For_Block, padfY) + sizeof(double) - offsetof(HT_Struct_For_Block, padfX);

  lookup_key *key = malloc(sizeof(lookup_key));
  memset(key, 0, sizeof(*key));
  key->padfX = padfX;
  key->padfY = padfY;

  HASH_FIND(hh, HT_Blocks, &key->padfX, keylen, present);
  free(key);
  if(present != NULL)
      return present;

  return NULL;
}

void Add_block_to_HT(SHPObject *object)
{
  HT_Struct_For_Block *new_block1, *new_block2, *new_block3, *new_block4, *present;
  double padfX, padfY;
  int keylen = offsetof(HT_Struct_For_Block, padfY) + sizeof(double) - offsetof(HT_Struct_For_Block, padfX);
  TRUNCATE(object->padfX[0], padfX);
  TRUNCATE(object->padfY[0], padfY);
  bucket_list *traverse, *new_entry;

  if ((present = check_for_entry_in_HT(padfX, padfY)) != NULL) { //add to the bucket_list
     ADD_TO_BUCKET_LIST(present, traverse, new_entry, object); }
  else
     ADD_TO_HASH_TABLE(new_block1, padfX, padfY, object, 0, 0);
  
  if ((present = check_for_entry_in_HT(padfX + TOLERANCE, padfY)) != NULL) { //add to the bucket_list
     ADD_TO_BUCKET_LIST(present, traverse, new_entry, object); }
  else
     ADD_TO_HASH_TABLE(new_block2, padfX, padfY, object, TOLERANCE, 0);
  
  if ((present = check_for_entry_in_HT(padfX, padfY + TOLERANCE)) != NULL) { //add to the bucket_list
     ADD_TO_BUCKET_LIST(present, traverse, new_entry, object); }
  else
     ADD_TO_HASH_TABLE(new_block3, padfX, padfY, object, 0, TOLERANCE);
  
  if ((present = check_for_entry_in_HT(padfX + TOLERANCE, padfY + TOLERANCE)) != NULL) { //add to the bucket_list
     ADD_TO_BUCKET_LIST(present, traverse, new_entry, object); }
  else
     ADD_TO_HASH_TABLE(new_block4, padfX, padfY, object, TOLERANCE, TOLERANCE);
}
  
void print_table()
{
  HT_Struct_For_Block *s;
  bucket_list *temp = NULL;
  for(s=HT_Blocks; s != NULL; s=s->hh.next)
  {
    temp = (bucket_list *)s->head_of_list.next_block;
    printf("\npadfX = %f, padfY = %f \n", s->padfX, s->padfY); 
    while(temp != NULL)
    {
       printf("\nin the bucket_list");
       temp = (bucket_list *)temp->next_block;
    }
  }
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
  print_table();

  lookup_key *key1 = malloc(sizeof(lookup_key));
  memset(key1, 0, sizeof(*key1));
  TRUNCATE(-84.383048, key1->padfX);
  TRUNCATE(33.786532, key1->padfY);
  printf("\n trying to find %f %f\n", key1->padfX, key1->padfY);
  HASH_FIND(hh, HT_Blocks, &key1->padfX, keylen, test2); 
  
  if(test2 != NULL)
     printf("\n found!! %f %f\n", test2->padfX, test2->padfY);
  else
     printf("\n not found!!\n");
  free(test1);
  free(key1);
}
 
int main(){
  int i;
  SHPHandle handle = SHPOpen("/home/sumanth/Documents/eDemocracy/Files/Fultoncombinednd.shp", "rb");
  Add_Blocks_to_HT(handle);

  printf("\nTotal number of slots in the block HT = %d\n", HASH_COUNT(HT_Blocks));
  //test_hashing();

  //free all the items in the HT
  HT_Struct_For_Block *current;
  bucket_list *temp = NULL, *temp_next = NULL;
  while (HT_Blocks)
  {
     current = HT_Blocks;
     temp = (bucket_list *)current->head_of_list.next_block;
     /* delete the bucket_list too if present */
     while(temp != NULL)
     {
         temp_next = (bucket_list *)temp->next_block;
         free(temp);
         temp = temp_next;
     }
     HASH_DEL(HT_Blocks, current);
     free(current);
  }  

  for(i=0; i<block_count; i++)
  SHPDestroyObject(block_list[i]);
  return 0;
}
