package edu.gatech.c4g.r4g.redistricting;

import java.io.File;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.BlockGraph;
import edu.gatech.c4g.r4g.util.Loader;

public abstract class RedistrictingAlgorithm {

	BlockGraph bg;
	Loader loader;

	public RedistrictingAlgorithm(Loader loader, FeatureSource<SimpleFeatureType, SimpleFeature> source, String galFile){
		bg = new BlockGraph();
		this.loader = loader;
		load(source,galFile);
	}
	
	private void load(FeatureSource<SimpleFeatureType, SimpleFeature> source, String galFile){
		bg = loader.load(source, galFile);
	}
	
	/**
	 * Finds the biggest subgraph in bg
	 */
	private void findMainland(){
		
	}
}
