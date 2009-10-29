package edu.gatech.c4g.r4g;

import java.io.File;

import edu.gatech.c4g.r4g.model.BlockGraph;

public class LoaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BlockGraph bg = new BlockGraph();
		bg.load(new File(args[0]), BlockGraph.TYPE_US);
 
	}

}
