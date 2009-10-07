package redistricting;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;

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
public class BlockGraph {
	// used to know where to look for population data
	public static final int TYPE_US = 0;
	public static final int TYPE_AUS = 1;

	public ArrayList<Block> blockList;
	public ArrayList<District> distList;

	public BlockGraph() {
		this.blockList = null;
	}

	public void addDistrict(District d) {
		distList.add(d);
	}

	public int getDistrictCount() {
		return distList.size();
	}

	public void addBlock(Block b) {
		blockList.add(b);
	}

	public void removeBlock(Block b) {
		blockList.remove(b);
	}

	public void load(File dbfFile, int type) {
		try {
			InputStream inputStream = new FileInputStream(dbfFile);
			DBFReader reader = new DBFReader(inputStream);

			int numberOfFields = reader.getFieldCount();
			int popfield = -1;
			int areafield = -1;

			// search for population and area fields
			for (int i = 0; i < numberOfFields; i++) {
				DBFField field = reader.getField(i);

				switch (type) {
				case TYPE_US: {
					if (field.getName().equals("POP100")) {
						popfield = i;
					}
					if (field.getName().equals("AREALAND")) {
						areafield = i;
					}
				}
					break;
				case TYPE_AUS: {
					// 2006 is the year of the census
					if (field.getName().equals("TURPOP2006")) {
						popfield = i;
					}
					if (field.getName().equals("AREA")) {
						areafield = i;
					}
				}
					break;
				default: {
					System.err.println("Cannot recognize type!");
					System.exit(1);
				}
				}
			}

			if (popfield == -1) {
				System.err.println("Could not find population data!");
				System.exit(1);
			}
			if (areafield == -1) {
				System.err.println("Could not find area data!");
				System.exit(1);
			}

			Object[] rowObjects;

			while ((rowObjects = reader.nextRecord()) != null) {
				int recordNum = -1;
				int pop = -1;
				int area = -1;

				if (type == TYPE_US) {
					recordNum = Integer.parseInt((String) rowObjects[0]);
					pop = Integer.parseInt((String) rowObjects[popfield]);
					area = Integer.parseInt((String) rowObjects[areafield]);
				} else if (type == TYPE_AUS) {
					recordNum = (Integer) rowObjects[0];
					pop = (Integer) rowObjects[popfield];
					area = (Integer) rowObjects[areafield];
				}

				Block b = new Block(recordNum, pop, area);
				this.addBlock(b);
			}
			inputStream.close();
		} catch (DBFException e) {

			System.out.println(e.getMessage());
		} catch (IOException e) {

			System.out.println(e.getMessage());
		}

		// second, loop through GAL file and add neighbors
	}

	public void save() {
		// should support writing to a file somehow
	}

}
