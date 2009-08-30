default: contig display

contig: contig.o shpopen.o
	$(CC) contig.o shpopen.o -o contig

contig.o: contig.c
	$(CC) -c contig.c -o contig.o

display: display.o shpopen.o
	$(CC) display.o shpopen.o -o display

display.o: display.c
	$(CC) -c display.c -o display.o

clean: 
	rm -f contig contig.o display display.o