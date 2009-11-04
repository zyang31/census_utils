package edu.gatech.c4g.r4g.model;

import java.util.ArrayList;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.MultiPolygon;

public class Block {
	public static final String POPULATION_FIELD = "TURPOP2006"; // to be
																// externalized

	private int id;

	private SimpleFeature feature;
	private MultiPolygon polygon;
	public ArrayList<Block> neighbors;

	public Block(SimpleFeature sf) {
		feature = sf;
		polygon = (MultiPolygon) sf.getDefaultGeometry();

		// use only the integer part of the feature ID for compatibility with
		// GAL files
		String fId = sf.getID();
		id = Integer.parseInt(fId.substring(fId.lastIndexOf('.')));
	}

	public int getId() {
		return id;
	}

	public double getPopulation() {
		Property population = feature.getProperty(POPULATION_FIELD);
		return Double.parseDouble(population.getValue().toString());
	}

	public double getArea() {
		return polygon.getArea();
	}

	public double getDensity() {
		if (getArea() > 0) {
			return getPopulation() / getArea();
		}
		return 0;
	}

}
