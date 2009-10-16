CFLAGS = -c -Wall -g -O0
#remove -g -O0 when not debugging

#note that shpopen.o comes from shapelib folder

default: contig display

contig: contig.o shpopen.o blockcont.o
	$(CC) contig.o shpopen.o blockcont.o -o contig

contig.o: contig.c
	$(CC) $(CFLAGS) contig.c -o contig.o

display: display.o shpopen.o
	$(CC) display.o shpopen.o -o display

display.o: display.c
	$(CC) $(CFLAGS) display.c -o display.o

blockcont.o: blockcont.c
	$(CC) -c blockcont.c

clean: 
	rm -f contig contig.o display display.o blockcont blockcont.o
