package edu.gatech.c4g.r4g.model;

import java.util.Hashtable;

/**
 * Copyright (C) 2009
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version. This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 * @author Joshua Justice
 * 
 * 
 */
public class District extends Graph {
	private int districtNo;

	public District(int districtNo) {
		blocks = new Hashtable<Integer, Block>();
		this.districtNo = districtNo;
	}

	public int getDistrictNo() {
		return districtNo;
	}

	public void addBlock(Block b) {
		if (!blocks.containsKey(b.getId())) {
			super.addBlock(b);
			b.setDistNo(districtNo);
		}
	}

	public void removeBlock(Block b) {
		super.removeBlock(b);
		b.setDistNo(Block.UNASSIGNED);
	}

	public boolean isInRange(double min, double max) {
		return (population > min) && (population <= max);
	}

}
