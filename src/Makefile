default: contig

contig: contig.o shpopen.o
	$(CC) contig.o shpopen.o -o contig

contig.o: contig.c
	$(CC) -c contig.c -o contig.o

clean: 
	rm -f contig contig.o