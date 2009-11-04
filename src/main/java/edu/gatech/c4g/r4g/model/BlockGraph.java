package edu.gatech.c4g.r4g.model;

import java.util.ArrayList;
import java.util.Hashtable;

public class BlockGraph {
	// public ArrayList<Block> blockList;
	// a hash table is much faster!
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
		for (Block neighbor : b.neighbors){
			neighbor.neighbors.remove(b);
		}
		
		blockTable.remove(b);
	}	

}
