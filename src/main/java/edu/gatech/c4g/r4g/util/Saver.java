package edu.gatech.c4g.r4g.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FileDataStore;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileWriter;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.sun.rowset.internal.Row;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;

public class Saver {

	public static void save(BlockGraph bg, String outputFile) {
		// part 1 - add district to dbf
		System.out.println("Writing new DBF file to " + outputFile + "_"
				+ bg.getDistrictCount() + ".dbf");
		saveDBF(bg, new File(outputFile + ".dbf"), new File(outputFile + "_"
				+ bg.getDistrictCount() + ".dbf"));

		// part 2 - write which blocks are in which district
		System.out.println("Saving districting data to " + outputFile + ".dst");
		saveDST(bg, new File(outputFile + ".dst"));
	}

	private static void saveDBF(BlockGraph bg, File original, File outFile) {
		ArrayList<Block> blocks = new ArrayList<Block>(bg.getAllBlocks());
		Collections.sort(blocks, new Comparator<Block>() {

			public int compare(Block o1, Block o2) {
				return o1.getId() - o2.getId();
			}

		});

		FileChannel in;
		FileChannel out;
		try {
			// read the original header
			in = new FileInputStream(original).getChannel();
			DbaseFileReader r = new DbaseFileReader(in, false, Charset
					.defaultCharset());
			DbaseFileHeader header = r.getHeader();

			DbaseFileHeader newHeader = new DbaseFileHeader();

			for (int i = 0; i < header.getNumFields(); i++) {
				newHeader.addColumn(header.getFieldName(i), header
						.getFieldType(i), header.getFieldLength(i), header
						.getFieldDecimalCount(i));
			}

			newHeader.addColumn("DISTRICT", 'N', 4, 0);

			out = new FileOutputStream(outFile).getChannel();
			DbaseFileWriter w = new DbaseFileWriter(newHeader, out);

			int currentBlock = 0;
			while (r.hasNext()) {
				DbaseFileReader.Row row = r.readRow();

				for (int i = 0; i < header.getNumFields(); i++) { // do stuff
					row.read(i);
				}

				Object[] record = r.readEntry();
				Object[] newRecord = new Object[record.length + 1];
				System.arraycopy(record, 0, newRecord, 0, record.length);
				if (blocks.get(currentBlock).getId() == currentBlock) {
					newRecord[record.length] = blocks.get(currentBlock)
							.getDistNo();
					currentBlock++;
				} else {
					newRecord[record.length] = Block.UNASSIGNED;
				}
				w.write(newRecord);

			}

			r.close();
			w.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveDST(BlockGraph bg, File dstFile) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(dstFile);
			BufferedWriter out = new BufferedWriter(fstream);

			// write block count and district count
			out.write(bg.getAllBlocks().size() + " " + bg.getDistrictCount()
					+ "\n");

			for (Block b : bg.getAllBlocks()) {
				out.write(b.getId() + " " + b.getDistNo() + "\n");
			}

			out.close();

			System.out.println("DST info written to "
					+ dstFile.getAbsolutePath());
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
