package edu.gatech.c4g.r4g.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;

public class BlockGraph {
	// public ArrayList<Block> blockList;
	// a hash table is much faster!
	public Hashtable<Integer, Block> blockTable;
	public ArrayList<District> distList;

	public BlockGraph() {
		this.blockTable = new Hashtable<Integer, Block>();
	}

	public void addDistrict(District d) {
		distList.add(d);
	}

	public int getDistrictCount() {
		return distList.size();
	}

	public void addBlock(Block b) {
		// blockTable.put(new Integer(b.recordNo), b);
	}

	public void removeBlock(Block b) {
		blockTable.remove(b);
	}

	/**
	 * 
	 * @param source
	 *            FeatureSource obtained from the shapefile of the area to
	 *            redistrict
	 * @param galFile
	 *            GAL file containing contiguity information about the area to
	 *            redistrict
	 */
	public void load(FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {
		try {
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source
					.getFeatures();

			Iterator<SimpleFeature> iterator = collection.iterator();
			try {
				for (Iterator<SimpleFeature> i = collection.iterator(); i
						.hasNext();) {
					SimpleFeature feature = i.next();

					Block b = new Block(feature);
					blockTable.put(b.getId(), b);
				}
			} finally {
				collection.close(iterator);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Read GAL file
		parseGal(galFile);

	}

	public void parseGal(String filename) {
		File galFile = new File(filename);

		try {
			FileInputStream fstream = new FileInputStream(galFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			int n_blocks = Integer.parseInt(br.readLine());

			String currentline;
			int line_num = 2;

			for (int i = 0; i < n_blocks; i++, line_num++) {
				// read current block id and number of neighbors
				currentline = br.readLine();

				if (currentline == null) {
					System.err.println("Error in GAL file at line " + line_num);
					System.exit(1);
				}

				StringTokenizer block_st = new StringTokenizer(currentline);
				int current_block_id = -1;
				int num_neighbors = -1;

				try {
					current_block_id = Integer.parseInt(block_st.nextToken());
					num_neighbors = Integer.parseInt(block_st.nextToken());
				} catch (NoSuchElementException e) {
					System.err.println("Error in GAL file at line " + line_num);
					System.exit(1);
				}

				Block currentBlock = blockTable.get(new Integer(
						current_block_id));

				// now read neighbors ids
				currentline = br.readLine();

				if (currentline == null) {
					System.err.println("Error in GAL file at line " + line_num);
					System.exit(1);
				}

				StringTokenizer neighbor_st = new StringTokenizer(currentline);

				for (int j = 0; j < num_neighbors; j++) {
					int neighbor_id = -1;
					try {
						neighbor_id = Integer.parseInt(neighbor_st.nextToken());
					} catch (NoSuchElementException e) {
						System.err.println("Error in GAL file at line "
								+ line_num);
						System.exit(1);
					}

					// System.out.println("Adding " + neighbor_id +
					// " to the neighbors of " + current_block_id);
					currentBlock.neighbors.add(blockTable.get(new Integer(
							neighbor_id)));
				}
			}

			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void save(String outputFile) {
		// part 1 - add district to dbf
		saveDBF(new File(outputFile + ".dbf"), new File(outputFile + "_"
				+ distList.size() + ".dbf"));

		// part 2 - write which blocks are in which district
		saveDST(new File(outputFile + ".dst"));
	}

	private void saveDBF(File original, File outFile) {
//		ArrayList<DBFField> originalFields;
//
//		FileInputStream fis;
//		try {
//			fis = new FileInputStream(original);
//			DBFReader reader = new DBFReader(fis);
//
//			// read existing fields
//			int fcount = reader.getFieldCount();
//			originalFields = new ArrayList<DBFField>(fcount);
//
//			for (int i = 0; i < fcount; i++) {
//				originalFields.add(reader.getField(i));
//			}
//
//			// create new district field
//			DBFField distField = new DBFField();
//			distField.setName("DISTRICT");
//			distField.setDataType(DBFField.FIELD_TYPE_N);
//			distField.setFieldLength(4);
//
//			// initialize writer
//			DBFWriter writer = new DBFWriter();
//
//			// create and write records
//			Object[] record;
//			int currentBlock = 1;
//			while ((record = reader.nextRecord()) != null) {
//				Object[] newRecord = new Object[record.length + 1];
//				System.arraycopy(record, 0, newRecord, 0, record.length);
//				Block block = blockTable.get(new Integer(currentBlock));
//				newRecord[record.length] = getBlockDistrictNo(block);
//				writer.addRecord(newRecord);
//				currentBlock++;
//			}
//
//			// write to file
//			FileOutputStream fos = new FileOutputStream(outFile);
//			writer.write(fos);
//
//			fos.close();
//			fis.close();
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (DBFException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void saveDST(File dstFile) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(dstFile);
			BufferedWriter out = new BufferedWriter(fstream);

			// write block count and district count
			out.write(blockTable.size() + " " + distList.size() + "\n");

			for (Block b : blockTable.values()) {
				// out.write(b.recordNo + " " + getBlockDistrictNo(b) + "\n");
			}

			out.close();

			System.out.println("DST info written to "
					+ dstFile.getAbsolutePath());
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	private int getBlockDistrictNo(Block b) {
		for (District d : distList) {
			if (d.hasBlock(b)) {
				return d.getDistrictNo();
			}
		}

		return 0;
	}

}
