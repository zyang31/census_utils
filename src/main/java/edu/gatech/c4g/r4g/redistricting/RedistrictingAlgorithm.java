package edu.gatech.c4g.r4g.redistricting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;
import edu.gatech.c4g.r4g.model.District;
import edu.gatech.c4g.r4g.model.Island;
import edu.gatech.c4g.r4g.util.Loader;

//import com.vividsolutions.jts.geom.Geometry;

public abstract class RedistrictingAlgorithm {

	int ndis;
	double idealPopulation;
	double minPopulation;
	double maxPopulation;
	
	//this array keeps track of whether or not changes have been made during stage 3. If changes have been made, then we want to reloop through stage 3
	//since the population density distribution may not be correct after only one loop.
	ArrayList<Integer> numbers = new ArrayList<Integer>();
	public BlockGraph bg;
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
	
	// for (District d : bg.getAllDistricts()) {
	// System.out.println("District " + d.getDistrictNo()
	// + " compactness: " + d.getCompactness());
	// }
	
	// --------------------------------------
	// stage3
	
		populationBalancing();
		
		System.out.println("\n=============\n" + "After Stage 3.2\n"
		+ "=============\n");
		System.out.println(bg.districtStatistics());
		System.out.println("minPopulation:");
		System.out.println(minPopulation);
		System.out.println("maxPopulation:");
		System.out.println(maxPopulation);
		checkConnectivity();
		//getCompactnesses();
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
				for(Block b:blocksToAdd){
					if(dist.hull == null){
						dist.hull = b.hull;
					}
					else{
						dist.hull.union(b.hull);
					}
				}
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
						bg.getDistrict(district).hull.union(current.hull);
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
		numbers.add(1);
		while(!numbers.isEmpty()){
			finalizeDistricts();
		}
		System.out.println("\n=============\n" + "After Stage 3.1\n"
				+ "=============\n");
		System.out.println(bg.districtStatistics());
		if (numbers.isEmpty()){
			for(District d : bg.getAllDistricts()){
				if(d.getPopulation()<minPopulation || d.getPopulation()>maxPopulation){
					numbers.add(1);
					while(!numbers.isEmpty()){
						distantNeighborTransfers(d);
					}
				}
			}
		}
	}
	
	//this method takes care of the case where a district is under/over populated and its neighbors are unable to swap blocks with it.
	
	
	// First part to stage 3 or stage 2.5 or whatever you want to call it
	protected void finalizeDistricts() {
		numbers.clear();
	// System.out.println(bg.districtStatistics());
	
	//Get the over-apportioned Districts overAppDists
		ArrayList<District> overAppDists = new ArrayList<District>();
		for (District e : bg.getAllDistricts()){
			if (e.getPopulation() > maxPopulation){
				overAppDists.add(e);
			}
		}
		Collections.sort(overAppDists, new districtCompactnessComparator());
		
		// System.out.println(1);
	
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
				//Geometry currentHull = noDist.getShape();
				//find out which neighboring districts have few people and get the bordering blocks to those districts one at a time
				if (noDist.getPopulation() < minPopulation){
					while((e.getPopulation() > maxPopulation) && (noDist.getPopulation() < minPopulation)) {
						Hashtable<Integer, Block> bBlocks = noDist.getBorderingBlocks(e);
						while ((!bBlocks.isEmpty())) {
							Block b;
							Enumeration<Integer> enume = bBlocks.keys();
							if (enume.hasMoreElements()){
								numbers.add(1);
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
	
							Hashtable<Integer, Block> bBlocks = noDist.getBorderingBlocks(e);
							while(!bBlocks.isEmpty()){
	
								Block b;
								Enumeration<Integer> enume = bBlocks.keys();
								if (enume.hasMoreElements()){
									b = bBlocks.remove(enume.nextElement());
									bg.getDistrict(b.getDistNo()).removeBlock(b);
									b.setDistNo(noDist.getDistrictNo());
									noDist.addBlock(b);
									if (noDist.getPopulation() > maxPopulation){
										//now that noDist has an extra block added, check to see if its population is now greater than the max.
	
										bg.getDistrict(b.getDistNo()).removeBlock(b);
										b.setDistNo(e.getDistrictNo());
										e.addBlock(b);
										e.hull.union(b.hull);
										noDist.hull.difference(b.hull);
										break search1;
									}
									else{
										numbers.add(1);
									}
								}
								else{
									break search1;
								}
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
			//Geometry currentHull = d.getShape();
			ArrayList<Integer> n = d.getNeighboringDistricts();
			ArrayList<District> nDists = new ArrayList<District>();
			for (District t : bg.getAllDistricts()) {
				for (int i : n) {
					if (t.getDistrictNo() == i) {
						nDists.add(t);
						/*System.out.println("underPopulated District:" + d.getDistrictNo());
						System.out.println("District's neighbors:" + t.getDistrictNo());*/
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
						Hashtable<Integer, Block> bBlocks = d.getBorderingBlocks(nDist); //TO HERE
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
								numbers.add(1);
							}
						}
					}
				}
				//find out which neighboring districts have an acceptable range but transfer blocks anyway and see if they
				//fall below acceptable limits
				else{
					search2:
						while ((d.getPopulation() < minPopulation) && (nDist.getPopulation() > minPopulation)){
							Hashtable<Integer, Block> bBlocks = d.getBorderingBlocks(nDist);
							Block b;
							Enumeration<Integer> enume = bBlocks.keys();
							if (enume.hasMoreElements()){
								b = bBlocks.remove(enume.nextElement());
								bg.getDistrict(b.getDistNo()).removeBlock(b);
								if (nDist.getPopulation() >= minPopulation){
									d.addBlock(b);
									b.setDistNo(d.getDistrictNo());
									numbers.add(1);
								}
								else{
									nDist.addBlock(b);
									b.setDistNo(nDist.getDistrictNo());
									nDist.hull.union(b.hull);
									d.hull.difference(b.hull);
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
	
	protected void distantNeighborTransfers(District d){
		numbers.clear();
		//neighbors is the current set of neighbors to check on
		ArrayList<District> neighbors = new ArrayList<District>();
		//flags indicate which districts have already have their neighbors checked
		ArrayList<District> flags = new ArrayList<District>();
		//this is for under populated districts
		ArrayList<Integer> m = d.getNeighboringDistricts();
		neighbors = getNeighbors(m);
		flags.add(d);
		loop1:
			while(!neighbors.isEmpty()){
				for (int i=0; i<neighbors.size(); i++){
					District z = neighbors.get(i);
					flags.add(z);
					neighbors.remove(z);
					ArrayList<Integer> n = z.getNeighboringDistricts();
					ArrayList<District> newNeighbors = getNeighbors(n);
					for (District y : newNeighbors){
						if (!flags.contains(y)){
							neighbors.add(y);
							if(z.getPopulation()-idealPopulation>0){
								//Geometry currentHull = y.getShape();
								Block b;
								loop2:
									while((z.getPopulation() > minPopulation) && (y.getPopulation() < maxPopulation)) {
										Hashtable<Integer, Block> bBlocks = y.getBorderingBlocks(z);
										while(!bBlocks.isEmpty()){
											Enumeration<Integer> enume = bBlocks.keys();
											if (enume.hasMoreElements()){
												b = bBlocks.remove(enume.nextElement());
												bg.getDistrict(b.getDistNo()).removeBlock(b);
												b.setDistNo(y.getDistrictNo());
												y.addBlock(b);
												if(y.getPopulation()>maxPopulation || z.getPopulation()<minPopulation){
													bg.getDistrict(b.getDistNo()).removeBlock(b);
													b.setDistNo(z.getDistrictNo());
													z.addBlock(b);
													z.hull.union(b.hull);
													y.hull.difference(b.hull);
													break loop2;
												}
												else{
													numbers.add(1);
												}
											}
										}
									}
							}
							else{
								//Geometry currentHull = z.getShape();
								Block b;
								loop3:
									while((z.getPopulation() < maxPopulation) && (y.getPopulation() > minPopulation)) {
										Hashtable<Integer, Block> bBlocks = z.getBorderingBlocks(y);
										while((!bBlocks.isEmpty())){
											Enumeration<Integer> enume = bBlocks.keys();
											if (enume.hasMoreElements()){
												b = bBlocks.remove(enume.nextElement());
												bg.getDistrict(b.getDistNo()).removeBlock(b);
												b.setDistNo(z.getDistrictNo());
												z.addBlock(b);
												if(y.getPopulation()<minPopulation || z.getPopulation() > maxPopulation){
													bg.getDistrict(b.getDistNo()).removeBlock(b);
													b.setDistNo(y.getDistrictNo());
													y.addBlock(b);
													y.hull.union(b.hull);
													z.hull.difference(b.hull);
													break loop3;
												}
												else{
													numbers.add(1);
												}
											}
										}
									}
							}
							while(!numbers.isEmpty()){
								finalizeDistricts();
							}
							if (d.getPopulation()>=minPopulation && d.getPopulation()<=maxPopulation){
								break loop1;
							}
						}
				}
			}
		}
	}
	protected void getCompactnesses(){
		for(District d : bg.getAllDistricts()){
			System.out.println("District: "+ d + "'s compactness:" + d.getCompactness());
		}
	}
	protected void checkConnectivity(){
		for (District d : bg.getAllDistricts()){
			ArrayList<Block> allBlocks = new ArrayList<Block>();
			Hashtable<Integer, Block> temp = new Hashtable<Integer, Block>();
			Hashtable<Integer, Block> neighbors = new Hashtable<Integer, Block>();
			int count = 1;
			allBlocks.addAll(d.getAllBlocks());
			Collections.sort(allBlocks, new BlockDensityComparator());
			Block a = allBlocks.get(0);
			neighbors.put(0, a);
			Iterator<Block> h = a.neighbors.iterator();
			while(h.hasNext()){
				Block b = h.next();
				if(b.getDistNo()== a.getDistNo()){
					neighbors.put(count, b);
					temp.put(count, b);
					count = count + 1;
				}
			}
			while(!temp.isEmpty()){
				Enumeration<Integer> enume = temp.keys();
				if(enume.hasMoreElements()){
					Block c = temp.remove(enume.nextElement());
					Iterator<Block> i = c.neighbors.iterator();
					while(i.hasNext()){
						Block e = i.next();
						if((e.getDistNo()==a.getDistNo()) && !neighbors.containsValue(e)){
							neighbors.put(count, e);
							temp.put(count, e);
							count = count + 1;
						}
					}
				}
			}
			if(neighbors.size()==d.size()){
				System.out.println("District:"+ d.getDistrictNo() + " possesses connectivity");
				System.out.println("District's supposed size:" + d.size() + "tree's size:" + neighbors.size());
			}
			else{
				System.out.println("District:" + d.getDistrictNo() + " does not possess connectivity");
				System.out.println("District's supposed size:" + d.size() + "tree's size:" + neighbors.size());
			}
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
	public ArrayList<District> getNeighbors(ArrayList<Integer> m) {
	
		ArrayList<District> neighbors = new ArrayList<District>();
		for (District u : bg.getAllDistricts()){
			for (int j : m){
				if (u.getDistrictNo() == j){
					neighbors.add(u);
				}
			}
		}
		return neighbors;
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
	
	protected class districtCompactnessComparator implements Comparator<District>{

		public int compare(District d0, District d1) {
			// TODO Auto-generated method stub
			if(d0.getCompactness() > d1.getCompactness()){
				return 1;
			}
			else if(d0.getCompactness() < d1.getCompactness()){
				return -1;
			}
			return 0;
		}
		
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

