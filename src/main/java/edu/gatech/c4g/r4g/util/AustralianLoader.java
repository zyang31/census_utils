package edu.gatech.c4g.r4g.util;

import java.util.Set;
import java.util.Map.Entry;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;

public class AustralianLoader extends Loader {

	private static final String NATURAL_BORDER_WATER = "Water";
	
	@Override
	public BlockGraph load(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {

		BlockGraph bg = super.load(source, galFile);
		return removeNaturalBorders(bg);
	}
	
	private BlockGraph removeNaturalBorders(BlockGraph bg){
		Set<Entry<Integer, Block>> blocks = bg.blockTable.entrySet();
		
		
		
		return null; 
	}

}
