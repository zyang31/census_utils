package redistricting;

import java.io.File;

public class LoaderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BlockGraph bg = new BlockGraph();
		bg.load(new File(args[0]), BlockGraph.TYPE_AUS);
 
	}

}
