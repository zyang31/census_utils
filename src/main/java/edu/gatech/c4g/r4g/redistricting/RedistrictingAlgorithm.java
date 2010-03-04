package edu.gatech.c4g.r4g.redistricting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
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

	int ndis;
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

	}

	public void redistrict(int ndis, double maxDeviation) {
		// calculate ideal population
		this.ndis = ndis;
		idealPopulation = bg.getPopulation() / ndis;
		minPopulation = idealPopulation - idealPopulation * maxDeviation;
		maxPopulation = idealPopulation + idealPopulation * maxDeviation;

		// stage 1
		initialExpansion();

		// LOG INFO
		double totPop = bg.getPopulation();

		int usedblocks = 0;

		// System.out.println("\n=============\n" + "After Stage 1\n"
		// + "=============\n");
		// System.out.println(bg.districtStatistics());

		// --------------------------------------
		// stage2
		secondaryExpansion();

		// LOG INFO
		usedblocks = 0;

		System.out.println("\n=============\n" + "After Stage 2\n"
				+ "=============\n");
		System.out.println(bg.districtStatistics());

//		for (District d : bg.getAllDistricts()) {
//			System.out.println("District " + d.getDistrictNo()
//					+ " compactness: " + d.getCompactness());
//		}

		// --------------------------------------
		// stage3
		 populationBalancing();
		 System.out.println("\n=============\n" + "After Stage 3\n"
					+ "=============\n");
		 System.out.println(bg.districtStatistics());
	}

	protected void initialExpansion() {
		ArrayList<Block> allBlocks = new ArrayList<Block>();
		allBlocks.addAll(bg.getAllBlocks());
		// sort the blocks by density
		Collections.sort(allBlocks, new BlockDensityComparator());
		//the blocks are being added by population here
		for (int currentDistNo = 1; currentDistNo <= ndis; currentDistNo++) {
			System.out.println("Building district " + currentDistNo);

			Block firstBlock = findFirstUnassignedBlock(allBlocks);

			District dist = new District(currentDistNo);
			// add the most populated block
			ArrayList<Block> expandFrom = new ArrayList<Block>();
			dist.addBlock(firstBlock);
			expandFrom.add(firstBlock);

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

	}

	protected void secondaryExpansion() {
		ArrayList<Block> unassigned = bg.getUnassigned();

		// Argument: a SortedSet of unassigned blocks
		boolean ignorePopulation = false;
		for (int i = 0; i < 2; i++) {
			int oldsize = 0;
			int newsize = unassigned.size();

			while (oldsize != newsize) {
				for (Block current : unassigned) {
					int district = Block.UNASSIGNED;

					if (current.neighbors.isEmpty()) {
						System.out.println("Block " + current.getId()
								+ " has no neighbors!!");
					}

					for (Block b : current.neighbors) {
						if (b.getDistNo() != Block.UNASSIGNED) {
							District d = bg.getDistrict(b.getDistNo());
							int pop = d.getPopulation();
							if (pop <= idealPopulation || ignorePopulation) {
								if (district == Block.UNASSIGNED) {
									district = b.getDistNo();
								} else {
									District currentD = bg
											.getDistrict(district);
									int newPop = currentD.getPopulation();
									district = pop < newPop ? b.getDistNo()
											: district;
								}
							}
						}
					}

					if (district != Block.UNASSIGNED) {
						bg.getDistrict(district).addBlock(current);

						// System.out.println(unassigned.size());
					}
				}
				oldsize = newsize;
				unassigned = bg.getUnassigned();
				newsize = unassigned.size();
			}
			ignorePopulation = true;
		}

	}//end second expansion

	protected void populationBalancing() {
				finalizeDistricts();
	
	}

	// First part to stage 3 or stage 2.5 or whatever you want to call it
	protected void finalizeDistricts() {

		//Get the over-apportioned Districts overAppDists
		ArrayList<District> overAppDists = new ArrayList<District>();
		for (District e : bg.getAllDistricts()){
			if (e.getPopulation() > maxPopulation){
				overAppDists.add(e);
			}
		}
		
		//Get the neighboring districts call them noDists
		
		for (District e : overAppDists){
			ArrayList<Integer> m = e.getNeighboringDistricts();
			ArrayList<District> noDists = new ArrayList<District>();
			for (District u : bg.getAllDistricts()){
				for (int j : m){
					if (u.getDistrictNo() == j){
						noDists.add(u);
					}
				}
			}
			for (int i = 0; i<noDists.size(); i++){
				District noDist = noDists.get(i);
				//find out which neighboring districts have few people and get the bordering blocks to those districts one at a time
				if (noDists.get(i).getPopulation() < minPopulation){
					while((e.getPopulation() > maxPopulation) && (noDists.get(i).getPopulation() < minPopulation)) {
						Hashtable<Integer, Block> bBlocks = noDist.getBorderingBlocks(e.getDistrictNo());
						while ((!bBlocks.isEmpty())) {
							Block b;
							Enumeration<Integer> enume = bBlocks.keys();
							if (enume.hasMoreElements()){
								b = bBlocks.remove(enume.nextElement());
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								b.setDistNo(noDist.getDistrictNo());
								noDist.addBlock(b);
							}
						}
					}
				}
				//find out if adding a bordering block to a neighboring district that is not over the maxPopulation 
				//will push it over the threshold and add blocks to them one at a time
				else{
					search1:
					while ((noDist.getPopulation() < maxPopulation) && (e.getPopulation() > maxPopulation)){
						Hashtable<Integer, Block> bBlocks = noDist.getBorderingBlocks(e.getDistrictNo());
						Block b;
						Enumeration<Integer> enume = bBlocks.keys();
						if (enume.hasMoreElements()){
							b = bBlocks.remove(enume.nextElement());
							bg.getDistrict(b.getDistNo()).removeBlock(b);
							b.setDistNo(noDist.getDistrictNo());
							noDist.addBlock(b);
							if (noDist.getPopulation() > maxPopulation){//now that noDist has an extra block added, check to see if its population is now greater than the max.
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								b.setDistNo(e.getDistrictNo());
								e.addBlock(b);
								break search1;
								/*b = bBlocks.remove(enume.nextElement());
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								if (nDist.getPopulation() >= minPopulation){
									d.addBlock(b);
									b.setDistNo(d.getDistrictNo());
								}
								else{
									nDist.addBlock(b);
									b.setDistNo(nDist.getDistrictNo()); */
							}
						else{
							break search1;
						}
					}
				}
			}
		}
		// Get the under-apportioned Districts undAppDists
		ArrayList<District> undAppDists = new ArrayList<District>();
		for (District d : bg.getAllDistricts()) {
			if (d.getPopulation() < minPopulation) {
				undAppDists.add(d);
			}
		}
		// Get their neighboring districts nDists
		for (District d : undAppDists) {
			ArrayList<Integer> n = d.getNeighboringDistricts();
			ArrayList<District> nDists = new ArrayList<District>();
			for (District t : bg.getAllDistricts()) {
				for (int i : n) {
					if (t.getDistrictNo() == i) {
						nDists.add(t);
					}
				}
			}
			// find out which neighboring districts have too many people and get the bordering blocks from those districts one at a time.
			for (int i = 0; i < nDists.size(); i++) {
				District nDist = nDists.get(i);
				if (nDist.getPopulation() > maxPopulation){
					while ((d.getPopulation() < minPopulation)
						&& (nDists.get(i).getPopulation() > maxPopulation)) {
						// The bordering blocks to be moved over. 
						Hashtable<Integer, Block> bBlocks = d.getBorderingBlocks(nDist.getDistrictNo());  //TO HERE
						while ((!bBlocks.isEmpty())) {
						// get Enumeration and remove value based off of the
						// enumerated values
							Block b;
							Enumeration<Integer> enume = bBlocks.keys();
							if (enume.hasMoreElements()) {
								b = bBlocks.remove(enume.nextElement());
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								b.setDistNo(d.getDistrictNo());
								d.addBlock(b);
							
							}
						}
					}
				}
				//find out which neighboring districts have an acceptable range but transfer blocks anyway and see if they
				//fall below acceptable limits
					else{
						search2:
						while ((d.getPopulation() < minPopulation) && (nDist.getPopulation() > minPopulation)){
							Hashtable<Integer, Block> bBlocks = d.getBorderingBlocks(nDist.getDistrictNo());
							Block b;
							Enumeration<Integer> enume = bBlocks.keys();
							if (enume.hasMoreElements()){
								b = bBlocks.remove(enume.nextElement());
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								if (nDist.getPopulation() >= minPopulation){
									d.addBlock(b);
									b.setDistNo(d.getDistrictNo());
								}
								else{
									nDist.addBlock(b);
									b.setDistNo(nDist.getDistrictNo());
									break search2;
								}
							}
							else{
								break search2;
							}
						}
					}
				}
			}
		}
		/*search3:
		for (District e : bg.getAllDistricts()){
			if (e.getPopulation() > maxPopulation || e.getPopulation() < minPopulation){
				finalizeDistricts();
				System.out.println("b");
			}
			else{
				System.out.println("a");
				break search3;
			}
		}*/
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
				int countAssigned = 0;
				for (Block n : b.neighbors) {
					if (n.getDistNo() != Block.UNASSIGNED) {
						countAssigned++;
					}
				}

				if (countAssigned < b.neighbors.size()) {
					return b;
				}
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
