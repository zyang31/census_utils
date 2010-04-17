/*
Redistricting application
Copyright (C) <2009> <Aaron Ciaghi, Stephen Long, Joshua Justice>
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
 
package edu.gatech.c4g.r4g.model;
 
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
 
import com.vividsolutions.jts.geom.Geometry;
 
//import edu.gatech.c4g.r4g.redistricting.RedistrictingAlgorithm.BlockDensityComparator;
 
/**
* Graph of blocks that represents a district.
*
* @author aaron
*
*/
public class District extends Graph {
/**
* District identifier
*/
	public Geometry hull;
	private int districtNo;
	public Hashtable<Integer, Block> flags = new Hashtable<Integer, Block>();
	public District(int districtNo) {
		blocks = new Hashtable<Integer, Block>();
		this.districtNo = districtNo;
		this.hull = null;
	}
	 
	/**
	* Returns this district's identifier
	*
	* @return
	*/
	public int getDistrictNo() {
		return districtNo;
	}
	
	public void addBlock(Block b) {
		if (!blocks.containsKey(b.getId())) {
			super.addBlock(b);
			b.setDistNo(districtNo);
		}
	}
	
	/**
	* Finds all the blocks on the border of this district, namely all the
	* blocks that have one or more neighbors in another district.
	*
	* @return
	*/
	public Hashtable<Integer, Block> getBorderingBlocks() {
		Hashtable<Integer, Block> neighbors = new Hashtable<Integer, Block>();
		for (Block b : blocks.values()) {
			Iterator<Block> i = b.neighbors.iterator();
			while (i.hasNext()) {
				Block current = i.next();
				if (current.getDistNo() != b.getDistNo()) {
					neighbors.put(current.getDistNo(), current);
					}
				}
			}
		return neighbors;
	}
	
	/**
	* Returns all the blocks bordering with the district with the input
	* district identifier.
	*
	* @param DistNo
	* @return
	*/
	
	
	public Hashtable<Integer, Block> getBorderingBlocks(District Dist) {
		Hashtable<Integer, Block> neighbors = new Hashtable<Integer, Block>();
		label:
			for (Block b : blocks.values()) {
				Iterator<Block> i = b.neighbors.iterator();
				while (i.hasNext()) {
					Block a = i.next();
					double thisHull = this.getCompactness();
					this.hull = this.hull.union(a.hull);
					Dist.hull = Dist.hull.difference(a.hull);
					if ((a.getDistNo() == Dist.getDistrictNo()) && (isContiguous(a,Dist))) {
						if(this.getCompactness()>thisHull){
							System.out.println("transfer");
							System.out.println("before compactness:"+thisHull);
							System.out.println("after compactness:"+this.getCompactness());
							neighbors.put(a.getDistNo(), a);
							break label;
						}
						else{
							System.out.println("no transfer");
							System.out.println("before compactness:"+thisHull);
							System.out.println("after compactness:"+this.getCompactness());
							this.hull=this.hull.difference(a.hull);
							Dist.hull = Dist.hull.union(a.hull);
						}
					}
				}
			}
		return neighbors;
	}
	
	public Geometry getShape(){
		Geometry distPoly = null;
		for (Block b : blocks.values()) {
			if (distPoly == null) {
				distPoly = b.getPolygon();
			}
			else {
				distPoly = distPoly.union(b.getPolygon());
			}
		}
	
	// System.out.println("Compactness of " + districtNo + ": " +
	// (1/(convexHull.getArea() - distPoly.getArea())));
	
		return distPoly;
	
	}
	
	
	/**
	* Returns an {@link ArrayList} containing the district identifiers of the
	* neighboring districts.
	*
	* @return
	*/
	public ArrayList<Integer> getNeighboringDistricts() {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		Hashtable<Integer, Block> neighborBlks = new Hashtable<Integer, Block>();
		for (Block b : blocks.values()) {
			Iterator<Block> i = b.neighbors.iterator();
			while (i.hasNext()) {
				Block a = i.next();
				if (a.getDistNo() != b.getDistNo()) {
					neighborBlks.put(a.getDistNo(), a);
				}
			}
		}
		Enumeration<Integer> DistNo = neighborBlks.keys();
		while (DistNo.hasMoreElements()) {
			neighbors.add(DistNo.nextElement());
		}
		return neighbors;
	}
	
	/**
	* Removes the input block from this district.
	*/
	public void removeBlock(Block b) {
		super.removeBlock(b);
		b.setDistNo(Block.UNASSIGNED);
	}
	
	/**
	* Checks if this district's population is within the required range.
	*
	* @param min
	* minimum allowed population
	* @param max
	* maximum allowed population
	* @return
	*/
	public boolean isInRange(double min, double max) {
		return (population > min) && (population <= max);
	}
	public boolean isContiguous(Block a, District Dist){
		//int z =2;
		int count = 0;
		int count1 = 1;
		Hashtable<Integer, Block> temp = new Hashtable<Integer, Block>();
		Hashtable<Integer, Block> neighbors = new Hashtable<Integer, Block>();
		Hashtable<Integer, Block> neighborsCompare = new Hashtable<Integer, Block>();
		Iterator<Block> h = a.neighbors.iterator();
		while(h.hasNext()){
			Block b = h.next();
			if(b.getDistNo()==a.getDistNo() && !(neighbors.containsValue(b)) && !(b==a)){
				neighbors.put(count, b);
				count = count + 1;
			}
			 Iterator<Block> j = b.neighbors.iterator();
             while(j.hasNext()){
                     Block c = j.next();
                     if(c.getDistNo()==a.getDistNo() && !(neighbors.containsValue(c)) && !(c==a)){
                             neighbors.put(count, c);
                             count = count + 1;
                     }
             }

		}
		Block d = neighbors.get(0);
        neighborsCompare.put(0, d);
        Iterator<Block> k = d.neighbors.iterator();
        while(k.hasNext()){
                Block e = k.next();
                if (e.getDistNo()==a.getDistNo() && !(e==a) && !(neighborsCompare.containsValue(e)) && neighbors.containsValue(e)){
                        neighborsCompare.put(count1, e);
                        temp.put(count1, e);
                        count1 = count1 + 1;
                }
        }
        while(!temp.isEmpty()){
                Enumeration<Integer> enume = temp.keys();
                if(enume.hasMoreElements()){
                        Block f = temp.remove(enume.nextElement());
                        Iterator<Block> l = f.neighbors.iterator();
                        while(l.hasNext()){
                                Block g = l.next();
                                if((g.getDistNo()==a.getDistNo()) && !(g==a) && !(neighborsCompare.containsValue(g)) && neighbors.containsValue(g)){
                                        neighborsCompare.put(count1, g);
                                        temp.put(count1, g);
                                        count1 = count1+1;
                                }
                        }
                }
        }
        if(neighbors.size()==neighborsCompare.size()){
            return true;
        }
		return false;
			
	}
	/**
	* Calculates a compactness score for this district. The score is the
	* percentage of the convex hull of this district covered by the actual
	* district. The higher the better. WARNING! This function is very
	* expensive!
	*
	* @return
	*/
	public double getCompactness() {
		return this.hull.getArea() / this.hull.convexHull().getArea();
	}
	 
}