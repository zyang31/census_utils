CFLAGS = -c -Wall -g -O0
#remove -g -O0 when not debugging

#note that shpopen.o comes from shapelib folder

default: contig display blockcont centroid

contig: contig.o shpopen.o
	$(CC) contig.o shpopen.o -o contig

contig.o: contig.c
	$(CC) $(CFLAGS) contig.c -o contig.o

display: display.o shpopen.o centroid.o
	$(CC) display.o shpopen.o centroid.o -o display

display.o: display.c
	$(CC) $(CFLAGS) display.c -o display.o

blockcont: blockcont.o
	$(CC) blockcont.o -o blockcont

blockcont.o: blockcont.c
	$(CC) $(CFLAGS) blockcont.c -o blockcont.o

centroid.o: centroid.c

clean: 
	rm -f contig contig.o display display.o blockcont blockcont.o centroid.o
