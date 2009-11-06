package edu.gatech.c4g.r4g.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
	private Hashtable<Integer, Block> blocks;
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
					blocks.put(b.getId(), b);
				}
			} finally {
				collection.close(iterator);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void addBlock(Block b) {
		blocks.put(b.getId(), b);
		population += b.getPopulation();
	}

	public void removeBlock(Block b) {
		for (Block neighbor : b.neighbors) {
			neighbor.neighbors.remove(b);
		}

		blocks.remove(b);
	}

	public Block getBlock(int index) {
		return blocks.get(new Integer(index));
	}

	@Override
	public Collection<Block> getAllBlocks() {
		return blocks.values();
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
			Island island = new Island();
			addToIsland(island, firstBlock);

			islands.add(island);

			System.out.println("Island " + islands.indexOf(island)
					+ " done (size=" + island.getAllBlocks().size() + ")");

			allBlocks.removeAll(island.getAllBlocks());

			System.out.println(allBlocks.size());
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

	// private void addToIsland(Island island, Block b) {
	// if (!island.hasBlock(b)) {
	// System.out.println("Adding block " + b.getId());
	// island.addBlock(b);
	// for (Block bl : b.neighbors) {
	// if (!island.hasBlock(bl)) {
	// addToIsland(island, bl);
	// }
	// }
	// }
	// }

	private void addToIsland(Island island, Block b) {
		// System.out.println(island.getPopulation());
		island.addBlock(b);

		ArrayList<Block> toAdd = b.neighbors;
		toAdd.removeAll(island.getAllBlocks());

		for (Block bl : toAdd) {
			addToIsland(island, bl);
		}
	}

}
