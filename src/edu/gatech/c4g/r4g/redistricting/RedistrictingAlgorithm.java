package edu.gatech.c4g.r4g.redistricting;

import java.io.File;

import edu.gatech.c4g.r4g.model.BlockGraph;

public abstract class RedistrictingAlgorithm {

	String file;
	BlockGraph bg;
	
	public RedistrictingAlgorithm(String file){
		this.file = file;
		bg = new BlockGraph();
		bg.load(new File(file + ".dbf"), BlockGraph.TYPE_AUS);
	}
	
}
