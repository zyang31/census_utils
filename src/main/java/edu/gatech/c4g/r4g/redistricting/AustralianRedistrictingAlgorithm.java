package edu.gatech.c4g.r4g.redistricting;

import org.geotools.data.FeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import edu.gatech.c4g.r4g.util.AustralianLoader;

public class AustralianRedistrictingAlgorithm extends RedistrictingAlgorithm {

	public AustralianRedistrictingAlgorithm(AustralianLoader loader,
			FeatureSource<SimpleFeatureType, SimpleFeature> source,
			String galFile) {
		super(loader, source, galFile);
	}

}
