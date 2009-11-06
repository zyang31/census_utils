package edu.gatech.c4g.r4g.model;

import java.util.Collection;
import java.util.HashSet;

public abstract class Graph {

	int population = 0;
	Collection<Block> blocks;
	
	public void addBlock(Block b) {
		blocks.add(b);
		population += b.getPopulation();
	}

	public void removeBlock(Block b) {
		blocks.remove(b);
		population -= b.getPopulation();
	}
	
	public Collection<Block> getAllBlocks(){
		return blocks;
	}

	public void addAllBlocks(Collection<Block> c) {
		for (Block b : c) {
			addBlock(b);
		}
	}
	
	public int getPopulation(){
		return population;
	}
	
	public boolean hasBlock(Block b){
		return blocks.contains(b);
	}
}
