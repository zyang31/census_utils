package edu.gatech.c4g.r4g.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

public abstract class Graph {

	protected int population = 0;
	protected Hashtable<Integer,Block> blocks;
	
	public Graph(){
		blocks = new Hashtable<Integer, Block>();
	}
	
	public void addBlock(Block b) {
		blocks.put(b.getId(),b);
		population += b.getPopulation();
	}

	public void removeBlock(Block b) {
		blocks.remove(b.getId());
		population -= b.getPopulation();
	}
	
	public Block getBlock(int index){
		return blocks.get(index);
	}
	
	public Collection<Block> getAllBlocks(){
		return blocks.values();
	}

	public void addAllBlocks(Collection<Block> c) {
		for (Block b : c) {
			addBlock(b);
		}
	}
	
	public void removeAllBlocks(Collection<Block> c){
		for (Block b : c){
			removeBlock(b);
		}
	}
	
	public int getPopulation(){
		return population;
	}
	
	public boolean hasBlock(Block b){
		return blocks.contains(b);
	}
}
