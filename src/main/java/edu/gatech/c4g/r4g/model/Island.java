package edu.gatech.c4g.r4g.model;

import java.util.Collection;
import java.util.Hashtable;

import com.vividsolutions.jts.geom.Coordinate;

public class Island extends Graph {

	public Island(Collection<Block> blocks){
		this.blocks = new Hashtable<Integer, Block>();
		addAllBlocks(blocks);
	}
	
	public Coordinate getCenter() {
		double x = 0;
		double y = 0;

		for (Block b : blocks.values()) {
			x += b.getPolygon().getCentroid().getX();
			y += b.getPolygon().getCentroid().getY();
		}
		
		x = x/blocks.size();
		y = y/blocks.size();
		
		return new Coordinate(x,y);
	}
}
