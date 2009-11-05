package edu.gatech.c4g.r4g.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;

public class Saver {
	public static void save(BlockGraph bg, String outputFile) {
		// part 1 - add district to dbf
		saveDBF(bg, new File(outputFile + ".dbf"), new File(outputFile + "_"
				+ bg.getDistrictCount() + ".dbf"));

		// part 2 - write which blocks are in which district
		saveDST(bg, new File(outputFile + ".dst"));
	}

	private static void saveDBF(BlockGraph bg, File original, File outFile) {
		// ArrayList<DBFField> originalFields;
		//
		// FileInputStream fis;
		// try {
		// fis = new FileInputStream(original);
		// DBFReader reader = new DBFReader(fis);
		//
		// // read existing fields
		// int fcount = reader.getFieldCount();
		// originalFields = new ArrayList<DBFField>(fcount);
		//
		// for (int i = 0; i < fcount; i++) {
		// originalFields.add(reader.getField(i));
		// }
		//
		// // create new district field
		// DBFField distField = new DBFField();
		// distField.setName("DISTRICT");
		// distField.setDataType(DBFField.FIELD_TYPE_N);
		// distField.setFieldLength(4);
		//
		// // initialize writer
		// DBFWriter writer = new DBFWriter();
		//
		// // create and write records
		// Object[] record;
		// int currentBlock = 1;
		// while ((record = reader.nextRecord()) != null) {
		// Object[] newRecord = new Object[record.length + 1];
		// System.arraycopy(record, 0, newRecord, 0, record.length);
		// Block block = blockTable.get(new Integer(currentBlock));
		// newRecord[record.length] = getBlockDistrictNo(block);
		// writer.addRecord(newRecord);
		// currentBlock++;
		// }
		//
		// // write to file
		// FileOutputStream fos = new FileOutputStream(outFile);
		// writer.write(fos);
		//
		// fos.close();
		// fis.close();
		//
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (DBFException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}

	private static void saveDST(BlockGraph bg, File dstFile) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(dstFile);
			BufferedWriter out = new BufferedWriter(fstream);

			// write block count and district count
			out.write(bg.getAllBlocks().size() + " " + bg.getDistrictCount() + "\n");

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
