CFLAGS = -c -Wall -g -O0
#remove -g -O0 when not debugging

default: contig display

contig: contig.o shapelib-1.2.10/shpopen.o
	$(CC) contig.o blockcont.o shapelib-1.2.10/shpopen.o -o contig

contig.o: contig.c
	$(CC) $(CFLAGS) contig.c -o contig.o

display: display.o shapelib-1.2.10/shpopen.o
	$(CC) display.o shapelib-1.2.10/shpopen.o -o display

display.o: display.c
	$(CC) $(CFLAGS) display.c -o display.o

blockcont.o: blockcont.c
	$(CC) $(CFLAGS) blockcont.c -o blockcont.o
	
clean: 
	rm -f contig contig.o display display.o blockcont blockcont.o
