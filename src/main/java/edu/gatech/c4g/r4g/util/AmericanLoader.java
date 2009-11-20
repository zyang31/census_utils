package edu.gatech.c4g.r4g.util;

import java.util.ArrayList;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.model.Block;
import edu.gatech.c4g.r4g.model.BlockGraph;

public class AmericanLoader extends Loader {
	@Override
	public BlockGraph load(
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {

		BlockGraph bg = super.load(source, galFile);

		return bg;
	}


}
