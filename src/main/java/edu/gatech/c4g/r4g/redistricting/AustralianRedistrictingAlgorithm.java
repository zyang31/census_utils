package edu.gatech.c4g.r4g.redistricting;

import java.util.HashSet;

import org.geotools.data.FeatureSource;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.util.AustralianLoader;

public class AustralianRedistrictingAlgorithm extends RedistrictingAlgorithm {

	public AustralianRedistrictingAlgorithm(AustralianLoader loader,
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {
		super(loader, source, galFile);
		removeNaturalBorders();
	}

	public void redistrict() {

	}

	private void removeNaturalBorders() {
		for (HashSet<Block> island : islands) {
			for (Block b : island) {
				SimpleFeature f = b.getFeature();
				String cat = (String) f.getProperty("CATEGORY").getValue();
				if (cat.equals(Block.CATEGORY_WATER)
						|| cat.equals(Block.CATEGORY_SHIPPING)) {
					island.remove(b);
					bg.removeBlock(b);
				}
			}
		}
	}

}
