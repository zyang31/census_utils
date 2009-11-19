package edu.gatech.c4g.r4g.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.activation.DataSource;

import org.geotools.data.DataAccess;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFactorySpi;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FileDataStore;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.dbf.DbaseFileWriter;
import org.geotools.factory.FactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.sun.rowset.internal.Row;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;

public class Saver {

	public static void save(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			BlockGraph bg, String outputFile) {
		// part 1 - add district to dbf
		System.out.println("Saving new Shapefile");
		saveShapefile(source, bg, new File(outputFile + "_"
				+ bg.getDistrictCount()));

		// part 2 - write which blocks are in which district
		System.out.println("Saving districting data to " + outputFile + ".dst");
		saveDST(bg, new File(outputFile + ".dst"));
	}

	private static void saveShapefile(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			BlockGraph bg, File outFile) {
		SimpleFeatureTypeBuilder sftb = new SimpleFeatureTypeBuilder();
		sftb.init(source.getSchema());
		sftb.add("DISTRICT", Integer.class);
		SimpleFeatureType withDistrict = sftb.buildFeatureType();

		SimpleFeatureBuilder sfb = new SimpleFeatureBuilder(withDistrict);

		FeatureCollection<SimpleFeatureType, SimpleFeature> newCollection = FeatureCollections
				.newCollection();

		for (Block b : bg.getAllBlocks()) {
			sfb.init(b.getFeature());
			sfb.set("DISTRICT", b.getDistNo());
			SimpleFeature sf = sfb.buildFeature(""+b.getId());
			newCollection.add(sf);
		}

		/*
		 * Get an output file name and create the new shapefile
		 */
		File newFile = getNewShapeFile(outFile);

		DataStoreFactorySpi dataStoreFactory = new ShapefileDataStoreFactory();

		Map<String, Serializable> params = new HashMap<String, Serializable>();
		try {
			params.put("url", newFile.toURI().toURL());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		params.put("create spatial index", Boolean.TRUE);

		ShapefileDataStore newDataStore;
		try {
			newDataStore = (ShapefileDataStore) dataStoreFactory
					.createNewDataStore(params);
			newDataStore.createSchema(withDistrict);
			newDataStore.forceSchemaCRS(source.getSchema()
					.getCoordinateReferenceSystem());

			/*
			 * Write the features to the shapefile
			 */
			Transaction transaction = new DefaultTransaction("create");

			String typeName = newDataStore.getTypeNames()[0];
			FeatureStore<SimpleFeatureType, SimpleFeature> featureStore;
			featureStore = (FeatureStore<SimpleFeatureType, SimpleFeature>) newDataStore
					.getFeatureSource(typeName);

			featureStore.setTransaction(transaction);
			try {
				featureStore.addFeatures(newCollection);
				transaction.commit();

			} catch (Exception problem) {
				problem.printStackTrace();
				transaction.rollback();

			} finally {
				transaction.close();
			}

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

	/**
	 * Prompt the user for the name and path to use for the output shapefile
	 */
	private static File getNewShapeFile(File f) {
		String path = f.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION) {
			// the user cancelled the dialog
			System.exit(0);
		}

		File newFile = chooser.getSelectedFile();
		if (newFile.equals(f)) {
			System.out.println("Error: cannot replace " + f);
			System.exit(0);
		}

		return newFile;
	}

}
