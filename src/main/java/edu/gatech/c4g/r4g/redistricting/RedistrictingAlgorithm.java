package edu.gatech.c4g.r4g.redistricting;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

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
		
		//islands = new ArrayList<Island>();
		islands = bg.toIslands();

		// TEST
		// for (Island i : islands){
		// System.out.println(i.getPopulation());
		// }
	}

	/**
	 * TODO THIS DOES NOT WORK!! (WHY?)
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

		int currentDistNo = 1;

		while (/* !mainlandBlocks.isEmpty() && */(currentDistNo <= ndis)) {
			System.out.println("Building district " + currentDistNo);// TODO
			// transform
			// to
			// LOG
			// (log4j?)
			
			// sort the blocks by density
			Collections.sort(mainlandBlocks, new BlockDensityComparator());

			District dist = new District(currentDistNo);
			// add the most populated block
			ArrayList<Block> expandFrom = new ArrayList<Block>(); 
			expandFrom.add(mainlandBlocks.get(0));
			mainlandBlocks.removeAll(expandFrom);// needed?

			while (!((dist.getPopulation() <= maxPopulation) && (dist
					.getPopulation() >= minPopulation))
					&& !expandFrom.isEmpty()) {

				ArrayList<Block> candidates = new ArrayList<Block>();

				for (Block b : expandFrom) {					
					for (Block n : b.neighbors) {
						if (n.getDistNo() == Block.UNASSIGNED) {
							candidates.add(n);
						}
					}
				}
				
				System.out.println(candidates.size() + " candidates");

				ArrayList<Block> blocksToAdd = chooseNeighbors(dist
						.getPopulation(), candidates);

				dist.addAllBlocks(blocksToAdd);
				// TEST
				System.out.println("District " + dist.getDistrictNo() + ": "
						+ dist.getPopulation());

				mainlandBlocks.removeAll(blocksToAdd);

				expandFrom = blocksToAdd;
			}

			System.out.println("District " + dist.getDistrictNo() + ": "
					+ dist.getPopulation() + " (" + dist.getAllBlocks().size()
					+ ")");

			bg.addDistrict(dist);

			currentDistNo++;
		}

		// TEST
		for (District d : bg.getDistList()) {
			System.out.println("District " + d.getDistrictNo() + ": "
					+ d.getPopulation());
		}

		System.out.println("Unassigned blocks: " + mainlandBlocks.size());

		// stage2

		// stage3
	}

	private ArrayList<Block> chooseNeighbors(int basePop,
			ArrayList<Block> blocks) {
		ArrayList<Block> blocksToTake = new ArrayList<Block>();

		int[] population = new int[blocks.size()];

		// generate random instance, items 1..N
		for (int n = 0; n < blocks.size(); n++) {
			population[n] = blocks.get(n).getPopulation();
		}

		// opt[n] = population obtained by taking blocks 1..n
		// sol[n] = does opt solution to pack items 1..n include item n?
		int[] opt = new int[blocks.size()];
		boolean[] sol = new boolean[blocks.size()];

		for (int n = 0; n < blocks.size(); n++) {
			// don't take block n
			int option1;
			// take item n
			int option2 = Integer.MIN_VALUE;
			;

			if (n > 0) {
				option1 = opt[n - 1];
				if (population[n] + population[n - 1] <= maxPopulation)
					option2 = population[n] + population[n - 1];
			} else {
				option1 = basePop;
				if (population[n] + basePop <= maxPopulation)
					option2 = population[n] + basePop;
			}

			// select better of two options
			opt[n] = Math.max(option1, option2);
			sol[n] = (option2 >= option1);
		}

		// determine which items to take
		System.out.println("Will take:");
		for (int n = blocks.size(); n > 0; n--) {
			if (sol[n]) {
				System.out.println("Block " + n);
				blocksToTake.add(blocks.get(n));
			}
		}

		return blocksToTake;
	}

	protected class BlockDensityComparator implements Comparator<Block> {

		public int compare(Block o1, Block o2) {
			if (o1.getDensity() > o2.getDensity()) {
				return -1;
			} else if (o1.getDensity() < o2.getDensity()) {
				return 1;
			}
			return 0;
		}

	}
}
