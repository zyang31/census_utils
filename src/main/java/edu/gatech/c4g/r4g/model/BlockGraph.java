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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

//import com.linuxense.javadbf.DBFException;
//import com.linuxense.javadbf.DBFField;
//import com.linuxense.javadbf.DBFReader;
//import com.linuxense.javadbf.DBFWriter;

/**
 * Copyright (C) 2009
 * 
 * @author Joshua Justice This program is free software: you can redistribute it
 *         and/or modify it under the terms of the GNU General Public License as
 *         published by the Free Software Foundation, either version 2 of the
 *         License, or (at your option) any later version. This program is
 *         distributed in the hope that it will be useful, but WITHOUT ANY
 *         WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *         FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *         for more details. You should have received a copy of the GNU General
 *         Public License along with this program. If not, see
 *         <http://www.gnu.org/licenses/>.
 * 
 *         The JavaDBF library is licensed under the GNU Lesser General Public
 *         License. A copy of the GNU LGPL should be included in the merge-xbase
 *         folder. If it is not, it can be found on
 *         http://www.gnu.org/licenses/lgpl-3.0.txt . For details regarding
 *         JavaDBF, see http://javadbf.sarovar.org/
 * 
 * 
 */
public class BlockGraph {
	// used to know where to look for population data
	public static final int TYPE_US = 0;
	public static final int TYPE_AUS = 1;

	// public ArrayList<Block> blockList;
	// a hash table is much faster!
	Hashtable<Integer, Block> blockTable;
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
		blockTable.put(new Integer(b.recordNo), b);
	}

	public void removeBlock(Block b) {
		blockTable.remove(b);
	}

	public void load(File dbfFile, int type) {
//		try {
//			InputStream inputStream = new FileInputStream(dbfFile);
//			DBFReader reader = new DBFReader(inputStream);
//
//			int numberOfFields = reader.getFieldCount();
//			int popfield = -1;
//			int areafield = -1;
//
//			// search for population and area fields
//			for (int i = 0; i < numberOfFields; i++) {
//				DBFField field = reader.getField(i);
//
//				switch (type) {
//				case TYPE_US: {
//					if (field.getName().equals("POP100")) {
//						popfield = i;
//					}
//					if (field.getName().equals("AREALAND")) {
//						areafield = i;
//					}
//				}
//					break;
//				case TYPE_AUS: {
//					// 2006 is the year of the census
//					if (field.getName().equals("TURPOP2006")) {
//						popfield = i;
//					}
//					if (field.getName().equals("AREA")) {
//						areafield = i;
//					}
//				}
//					break;
//				default: {
//					System.err.println("Cannot recognize type!");
//					System.exit(1);
//				}
//				}
//			}
//
//			if (popfield == -1) {
//				System.err.println("Could not find population data!");
//				System.exit(1);
//			}
//			if (areafield == -1) {
//				System.err.println("Could not find area data!");
//				System.exit(1);
//			}
//
//			Object[] rowObjects;
//
//			int recordNum = 0;
//
//			while ((rowObjects = reader.nextRecord()) != null) {
//				recordNum++;
//				int pop = -1;
//				int area = -1;
//
//				if (type == TYPE_US) {
//					// recordNum = Integer.parseInt((String) rowObjects[0]);
//					pop = Integer.parseInt(((String) rowObjects[popfield])
//							.trim());
//					area = Integer.parseInt(((String) rowObjects[areafield])
//							.trim());
//				} else if (type == TYPE_AUS) {
//					// recordNum = (Integer) rowObjects[0];
//					pop = (Integer) rowObjects[popfield];
//					area = (Integer) rowObjects[areafield];
//				}
//
//				// System.out.println("Adding new block (" + recordNum + "," +
//				// pop + "," + area + ")");
//				Block b = new Block(recordNum, pop, area);
//				this.addBlock(b);
//			}
//			inputStream.close();
//		} catch (DBFException e) {
//
//			System.out.println(e.getMessage());
//		} catch (IOException e) {
//
//			System.out.println(e.getMessage());
//		}
//
//		// Read GAL file
//		String filename = dbfFile.getAbsolutePath().substring(0,
//				dbfFile.getAbsolutePath().length() - 3)
//				+ "GAL";
//		parseGal(filename);
//
//	}
//
//	public void parseGal(String filename) {
//		File galFile = new File(filename);
//
//		try {
//			FileInputStream fstream = new FileInputStream(galFile);
//			DataInputStream in = new DataInputStream(fstream);
//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
//
//			int n_blocks = Integer.parseInt(br.readLine());
//
//			String currentline;
//			int line_num = 2;
//
//			for (int i = 0; i < n_blocks; i++, line_num++) {
//				// read current block id and number of neighbors
//				currentline = br.readLine();
//
//				if (currentline == null) {
//					System.err.println("Error in GAL file at line " + line_num);
//					System.exit(1);
//				}
//
//				StringTokenizer block_st = new StringTokenizer(currentline);
//				int current_block_id = -1;
//				int num_neighbors = -1;
//
//				try {
//					current_block_id = Integer.parseInt(block_st.nextToken());
//					num_neighbors = Integer.parseInt(block_st.nextToken());
//				} catch (NoSuchElementException e) {
//					System.err.println("Error in GAL file at line " + line_num);
//					System.exit(1);
//				}
//
//				Block currentBlock = blockTable.get(new Integer(
//						current_block_id));
//
//				// now read neighbors ids
//				currentline = br.readLine();
//
//				if (currentline == null) {
//					System.err.println("Error in GAL file at line " + line_num);
//					System.exit(1);
//				}
//
//				StringTokenizer neighbor_st = new StringTokenizer(currentline);
//
//				for (int j = 0; j < num_neighbors; j++) {
//					int neighbor_id = -1;
//					try {
//						neighbor_id = Integer.parseInt(neighbor_st.nextToken());
//					} catch (NoSuchElementException e) {
//						System.err.println("Error in GAL file at line "
//								+ line_num);
//						System.exit(1);
//					}
//
//					// System.out.println("Adding " + neighbor_id +
//					// " to the neighbors of " + current_block_id);
//					currentBlock.neighbors.add(blockTable.get(new Integer(
//							neighbor_id)));
//				}
//			}
//
//			in.close();
//		} catch (Exception e) {// Catch exception if any
//			System.err.println("Error: " + e.getMessage());
//			e.printStackTrace();
//		}
//	}
//
//	public void save(String outputFile) {
//		// part 1 - add district to dbf
//		saveDBF(new File(outputFile + ".dbf"), new File(outputFile + "_"
//				+ distList.size() + ".dbf"));
//
//		// part 2 - write which blocks are in which district
//		saveDST(new File(outputFile + ".dst"));
//	}
//
//	private void saveDBF(File original, File outFile) {
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
//			//write to file
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
				out.write(b.recordNo + " " + getBlockDistrictNo(b) + "\n");
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
