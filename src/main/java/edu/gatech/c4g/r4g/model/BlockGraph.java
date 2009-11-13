package edu.gatech.c4g.r4g.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class BlockGraph extends Graph {
	private ArrayList<District> distList;

	public BlockGraph(FeatureSource<SimpleFeatureType, SimpleFeature> source) {
		this.blocks = new Hashtable<Integer, Block>();
		this.distList = new ArrayList<District>();

		try {
			FeatureCollection<SimpleFeatureType, SimpleFeature> collection = source
					.getFeatures();

			Iterator<SimpleFeature> iterator = collection.iterator();
			try {
				for (Iterator<SimpleFeature> i = collection.iterator(); i
						.hasNext();) {
					SimpleFeature feature = i.next();

					Block b = new Block(feature);
					addBlock(b);
				}
			} finally {
				collection.close(iterator);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void removeBlock(Block b) {
		for (Block neighbor : b.neighbors) {
			neighbor.neighbors.remove(b);
		}

		blocks.remove(b.getId());
	}

	public void addDistrict(District d) {
		distList.add(d);
	}

	public int getDistrictCount() {
		return distList.size();
	}

	public ArrayList<District> getDistList() {
		return distList;
	}

	/**
	 * Returns a list containing all the islands in the graph sorted by number
	 * of blocks.
	 */
	public ArrayList<Island> toIslands() {
		ArrayList<Block> allBlocks = new ArrayList<Block>(blocks.values());
		ArrayList<Island> islands = new ArrayList<Island>();

		while (!allBlocks.isEmpty()) {
			int start = (int) (Math.random() * Integer.MAX_VALUE)
					% allBlocks.size();

			Block firstBlock = allBlocks.get(start);
			HashSet<Block> islandBlocks = new HashSet<Block>(); 
			addToIsland(islandBlocks, firstBlock);
			Island island = new Island(islandBlocks);

			islands.add(island);

			allBlocks.removeAll(islandBlocks);
		}

		Collections.sort(islands, new Comparator<Island>() {

			public int compare(Island o1, Island o2) {
				if (o1.getAllBlocks().size() > o2.getAllBlocks().size()) {
					return -1;
				} else if (o1.getAllBlocks().size() < o2.getAllBlocks().size()) {
					return 1;
				}
				return 0;
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

	// private HashSet<Block> findIsland(Block startingBlock) {
	// HashSet<Block> toAdd = new HashSet<Block>();
	// toAdd.add(startingBlock);
	//
	// int oldSize = 0;
	//
	// while (oldSize < toAdd.size()) {
	// oldSize = toAdd.size();
	//
	// HashSet<Block> newToAdd = new HashSet<Block>();
	//
	// for (Block b : toAdd) {
	// newToAdd.addAll(b.neighbors);
	// }
	//
	// toAdd.addAll(newToAdd);
	// }
	//
	// return toAdd;
	// }

}
