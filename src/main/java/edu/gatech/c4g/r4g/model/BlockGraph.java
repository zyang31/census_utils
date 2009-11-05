package edu.gatech.c4g.r4g.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

public class BlockGraph {
	public Hashtable<Integer, Block> blockTable;
	public ArrayList<District> distList;

	public BlockGraph() {
		this.blockTable = new Hashtable<Integer, Block>();
		this.distList = new ArrayList<District>();
	}

	public void addDistrict(District d) {
		distList.add(d);
	}

	public int getDistrictCount() {
		return distList.size();
	}

	public void removeBlock(Block b) {
		for (Block neighbor : b.neighbors) {
			neighbor.neighbors.remove(b);
		}

		blockTable.remove(b);
	}
	
	public int getTotPopulation(){
		int pop = 0;
		
		for (Block b: blockTable.values()){
			pop += b.getPopulation();
		}
		
		return pop;
	}

	/**
	 * Returns a list containing all the islands in the graph sorted by number
	 * of blocks.
	 */
	public ArrayList<HashSet<Block>> toIslands() {
		ArrayList<Block> allBlocks = new ArrayList<Block>(blockTable.values());
		ArrayList<HashSet<Block>> islands = new ArrayList<HashSet<Block>>();

		while (!allBlocks.isEmpty()) {
			int start = (int) (Math.random() * Integer.MAX_VALUE)
					% allBlocks.size();

			Block firstBlock = allBlocks.get(start);
			HashSet<Block> island = new HashSet<Block>();
			addToIsland(island, firstBlock);

			islands.add(island);

			allBlocks.removeAll(island);
		}

		Collections.sort(islands, new Comparator<HashSet<Block>>() {
			public int compare(HashSet<Block> o1, HashSet<Block> o2) {
				if (o1.size() > o2.size()) {
					return 1;
				} else if (o1.size() == o2.size()) {
					return 0;
				} else {
					return -1;
				}
			}
		});

		return islands;
	}

	private void addToIsland(HashSet<Block> island, Block b) {
		if (!island.contains(b)) {
			island.add(b);
			for (Block bl : b.neighbors) {
				if (!island.contains(bl)) {
					addToIsland(island, bl);
				}
			}
		}
	}

}
