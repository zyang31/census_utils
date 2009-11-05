package edu.gatech.c4g.r4g.redistricting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;
import edu.gatech.c4g.r4g.util.Loader;

public abstract class RedistrictingAlgorithm {

	public static final double MAX_DEVIATION = 0.1;

	double idealPopulation;
	double minPopulation;
	double maxPopulation;

	BlockGraph bg;
	ArrayList<HashSet<Block>> islands;
	Loader loader;

	public RedistrictingAlgorithm(Loader loader,
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {
		bg = new BlockGraph();
		this.loader = loader;
		bg = loader.load(source, galFile);
		islands = bg.toIslands();
	}

	/**
	 * 
	 * @param ndis
	 *            number of districts to create
	 */
	public void redistrict(int ndis) {
		// calculate ideal population
		idealPopulation = bg.getTotPopulation() / ndis;
		minPopulation = idealPopulation - idealPopulation * MAX_DEVIATION;
		maxPopulation = idealPopulation + idealPopulation * MAX_DEVIATION;

		// stage1

		// stage2

		// stage3
	}
}
