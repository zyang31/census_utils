package edu.gatech.c4g.r4g.redistricting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;
import edu.gatech.c4g.r4g.model.District;
import edu.gatech.c4g.r4g.model.Island;
import edu.gatech.c4g.r4g.util.Loader;

public abstract class RedistrictingAlgorithm {

	double idealPopulation;
	double minPopulation;
	double maxPopulation;

	BlockGraph bg;
	ArrayList<Island> islands;
	Loader loader;

	public RedistrictingAlgorithm(Loader loader,
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {
		this.loader = loader;
		bg = loader.load(source, galFile);
		islands = bg.toIslands();
	}

	/**
	 * 
	 * @param ndis
	 *            number of districts to create
	 */
	public void redistrict(int ndis, double maxDeviation) {
		// calculate ideal population
		idealPopulation = bg.getPopulation() / ndis;
		minPopulation = idealPopulation - idealPopulation * maxDeviation;
		maxPopulation = idealPopulation + idealPopulation * maxDeviation;

		// STAGE 1
		// redistrict mainland
		ArrayList<Block> mainlandBlocks = new ArrayList<Block>();
		mainlandBlocks.addAll(islands.get(0).getAllBlocks());

		// sort the blocks by density
		Collections.sort(mainlandBlocks, new BlockDensityComparator());

		int currentDistNo = 1;

		while (!mainlandBlocks.isEmpty() && (currentDistNo <= ndis)) {
			District dist = new District(currentDistNo);
			// add the most populated block
			Block firstBlock = mainlandBlocks.get(0);
			dist.addBlock(firstBlock);
			mainlandBlocks.remove(0);

			while (!((dist.getPopulation() <= maxPopulation) && (dist
					.getPopulation() >= minPopulation))) {

				Collections.sort(firstBlock.neighbors,
						new BlockDensityComparator());

				//TODO dynamic programming algorithm goes here				
//				int neighborsPop = 0;
//				
//				for (Block b : firstBlock.neighbors) {
//					neighborsPop += b.getPopulation();
//				}
//
//				if (dist.getPopulation() + neighborsPop <= maxPopulation) {
//					dist.addAllBlocks(firstBlock.neighbors);
//				} else {
//					
//				}

			}

			bg.addDistrict(dist);
		}

		// stage2

		// stage3
	}

	protected class BlockDensityComparator implements Comparator<Block> {

		public int compare(Block o1, Block o2) {
			if (o1.getDensity() > o2.getDensity()) {
				return 1;
			} else if (o1.getDensity() < o2.getDensity()) {
				return -1;
			}
			return 0;
		}

	}
}
