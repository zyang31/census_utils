package edu.gatech.c4g.r4g.redistricting;

import java.io.File;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.BlockGraph;

public abstract class RedistrictingAlgorithm {

	BlockGraph bg;

	public RedistrictingAlgorithm(FeatureSource<SimpleFeatureType, SimpleFeature> source, String galFile){
		bg = new BlockGraph();
		bg.load(source, galFile);
	}
}
