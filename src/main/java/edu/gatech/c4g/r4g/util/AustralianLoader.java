package edu.gatech.c4g.r4g.util;

import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;
//import edu.gatech.c4g.r4g.model.Island;

public class AustralianLoader extends Loader {

	//private static final String NATURAL_BORDER_WATER = "Water";

	@Override
	public BlockGraph load(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {

		BlockGraph bg = super.load(source, galFile);

		return removeNaturalBorders(bg);
	}

	private BlockGraph removeNaturalBorders(BlockGraph bg) {
		ArrayList<Block> toRemove = new ArrayList<Block>();
		
		for (Block b : bg.getAllBlocks()) {
			SimpleFeature f = b.getFeature();
			String cat = (String) f.getProperty("CATEGORY").getValue();
			if (cat.equals(Block.CATEGORY_WATER)
					|| cat.equals(Block.CATEGORY_SHIPPING)) {
				toRemove.add(b);
			}
		}
		
		bg.removeAllBlocks(toRemove);

		return bg;
	}

}
