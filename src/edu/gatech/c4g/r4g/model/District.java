package edu.gatech.c4g.r4g.model;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Copyright (C) 2009
 * 
 * @author Joshua Justice This program is free software: you can redistribute it
 *         and/or modify it under the terms of the GNU General Public License as
 *         published by the Free Software Foundation, either version 2 of the
 *         License, or (at your option) any later version. This program is
 *         distributed in the hope that it will be useful, but WITHOUT ANY
 *         WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *         FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 *         for more details. You should have received a copy of the GNU General
 *         Public License along with this program. If not, see
 *         <http://www.gnu.org/licenses/>.
 * 
 *         The JavaDBF library is licensed under the GNU Lesser General Public
 *         License. A copy of the GNU LGPL should be included in the merge-xbase
 *         folder. If it is not, it can be found on
 *         http://www.gnu.org/licenses/lgpl-3.0.txt . For details regarding
 *         JavaDBF, see http://javadbf.sarovar.org/
 * 
 * 
 */
public class District {
	private int districtNo;
	private int pop;
	private Hashtable<Integer, Block> blockTable;

	public District(int districtNo) {
		this.districtNo = districtNo;
	}

	public int getPopulation() {
//		int sum = 0;
//		for (Block b : blockTable) {
//			sum += b.population;
//		}
//		return sum;
		return pop;
	}

	public int getDistrictNo() {
		return districtNo;
	}

	public boolean hasBlock(Block b) {
		return blockTable.contains(b);
	}
	
	public void addBlock(Block b){
		blockTable.put(b.recordNo, b);
		pop += b.population;
	}
}
