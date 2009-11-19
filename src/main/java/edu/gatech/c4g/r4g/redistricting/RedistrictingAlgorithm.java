package edu.gatech.c4g.r4g.redistricting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.SortedSet;

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

		System.out.println("Loading files");
		bg = loader.load(source, galFile);

		// islands = new ArrayList<Island>();
		System.out.println("Finding islands");
		islands = bg.toIslands();
		System.out.println("Found " + islands.size() + " islands");

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
	public void initialExpansion(int ndis, double maxDeviation) {
		// calculate ideal population
		idealPopulation = bg.getPopulation() / ndis;
		minPopulation = idealPopulation - idealPopulation * maxDeviation;
		maxPopulation = idealPopulation + idealPopulation * maxDeviation;

		// STAGE 1
		// redistrict mainland
		Island mainland = islands.get(0);
		ArrayList<Block> mainlandBlocks = new ArrayList<Block>();
		mainlandBlocks.addAll(mainland.getAllBlocks());
		// sort the blocks by density
		Collections.sort(mainlandBlocks, new BlockDensityComparator());

		for (int currentDistNo = 1; currentDistNo <= ndis; currentDistNo++) {
			System.out.println("Building district " + currentDistNo);// TODO
			// log4j?

			Block firstBlock = findFirstUnassignedBlock(mainlandBlocks);

			District dist = new District(currentDistNo);
			// add the most populated block
			ArrayList<Block> expandFrom = new ArrayList<Block>();
			dist.addBlock(firstBlock);
			expandFrom.add(firstBlock);

			// the condition here must be fixed
			while (!expandFrom.isEmpty()
					&& dist.getPopulation() <= minPopulation * .8) {

				ArrayList<Block> neighborsList = new ArrayList<Block>();

				for (Block b : expandFrom) {
					for (Block n : b.neighbors) {
						if (n.getDistNo() == Block.UNASSIGNED) {
							if (!neighborsList.contains(n)) {
								neighborsList.add(n);
							}
						}
					}
				}

				ArrayList<Block> blocksToAdd = chooseNeighborsToAdd(dist
						.getPopulation(), minPopulation, neighborsList);
				dist.addAllBlocks(blocksToAdd);
				expandFrom = blocksToAdd;
			}

			bg.addDistrict(dist);
		}

		// TEST
		double totPop = bg.getPopulation();

		int usedblocks = 0;

		for (District d : bg.getAllDistricts()) {
			System.out.println("District " + d.getDistrictNo()
					+ ": population " + d.getPopulation() + "("
					+ (d.getPopulation() / totPop) * 100 + "%) ("
					+ d.getAllBlocks().size() + " blocks)");
			usedblocks += d.getAllBlocks().size();
		}

		System.out.println("Unassigned blocks: "
				+ (mainlandBlocks.size() - usedblocks));

		// stage2
		secondaryExpansion(mainland);

		usedblocks = 0;

		for (District d : bg.getAllDistricts()) {
			System.out.println("District " + d.getDistrictNo()
					+ ": population " + d.getPopulation() + "("
					+ (d.getPopulation() / totPop) * 100 + "%) ("
					+ d.getAllBlocks().size() + " blocks)");
			usedblocks += d.getAllBlocks().size();
		}

		System.out.println("Unassigned blocks: "
				+ (mainlandBlocks.size() - usedblocks));
		// stage3
	}

	public void secondaryExpansion(Island mainland) {		
		ArrayList<Block> unassigned = mainland.getUnassigned();
		
		// Argument: a SortedSet of unassigned blocks
		boolean ignorePopulation = false;
		for (int i = 0; i < 2; i++) {
			int oldsize = 0;
			int newsize = unassigned.size();
			
			while (oldsize != newsize) {
				for (Block current : unassigned){
					int district = Block.UNASSIGNED;

					if (current.neighbors.isEmpty()){
						System.out.println("Block " + current.getId() + " has no neighbors!!");
					}
					
					for (Block b : current.neighbors) {
						if (b.getDistNo() != Block.UNASSIGNED){
							District d = bg.getDistrict(b.getDistNo());
							int pop = d.getPopulation();
							if (pop <= maxPopulation || ignorePopulation){
								if (district == Block.UNASSIGNED){
									district = b.getDistNo();
								} else {
									District currentD = bg.getDistrict(district);
									int newPop = currentD.getPopulation();
									district = pop < newPop ? b.getDistNo() : district; 
								}
							}
						}
					}
					
					if (district != Block.UNASSIGNED) {
						bg.getDistrict(district).addBlock(current);
	
						//System.out.println(unassigned.size());
					}
				}
				oldsize = newsize;
				unassigned = mainland.getUnassigned();
				newsize = unassigned.size();
			}
			ignorePopulation = true;
		}

	}

	/**
	 * Returns the first unassigned block. The block list should be ordered by
	 * density using the {@link BlockDensityComparator}.
	 * 
	 * @return
	 */
	private Block findFirstUnassignedBlock(ArrayList<Block> list) {
		for (Block b : list) {
			if (b.getDistNo() == Block.UNASSIGNED) {
				return b;
			}
		}

		return null;
	}

	private ArrayList<Block> chooseNeighborsToAdd(int basePop,
			double upperBound, ArrayList<Block> blocks) {
		// HashSet<Block> blocksToTake = new HashSet<Block>();
		ArrayList<Block> returnList = new ArrayList<Block>();
		int[] population = new int[blocks.size()];
		int totalPop = basePop;

		// populate the population array
		for (int n = 0; n < blocks.size(); n++) {
			population[n] = blocks.get(n).getPopulation();
			totalPop += blocks.get(n).getPopulation();
		}

		if (totalPop <= upperBound) {
			// add all blocks
			return blocks;
		} else {
			Collections.sort(blocks);
			int position = blocks.size() - 1;
			totalPop = basePop;
			totalPop += blocks.get(position).getPopulation();

			while (totalPop <= upperBound) {
				returnList.add(blocks.get(position));
				position--;
				totalPop += blocks.get(position).getPopulation();
			}

			return returnList;
		}
	}

	public BlockGraph getBlockGraph() {
		return bg;
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
