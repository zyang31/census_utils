default: contig display blockcont

contig: contig.o shpopen.o
	$(CC) contig.o shpopen.o -o contig

contig.o: contig.c
	$(CC) -c contig.c -o contig.o

display: display.o shpopen.o
	$(CC) display.o shpopen.o -o display

display.o: display.c
	$(CC) -c display.c -o display.o

blockcont: blockcont.o
	$(CC) blockcont.o -o blockcont

blockcont.o: blockcont.c
	$(CC) -c blockcont.c -o blockcont.o

clean: 
	rm -f contig contig.o display display.o blockcont blockcont.o
