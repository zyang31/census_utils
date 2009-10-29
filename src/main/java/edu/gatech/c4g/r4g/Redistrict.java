package edu.gatech.c4g.r4g;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.geotools.factory.GeoTools;

import edu.gatech.c4g.r4g.redistricting.IRedistrictingAlgorithm;

public class Redistrict {

	private static final String USAGE = "[-h] -a <algorithm> -n <number_of_districts> -i <input_file>";
	private static final String HEADER = "Redistricting4Good - A bunch of algorithms to (fairly?) redistrict Australia (with GeoTools "+GeoTools.getVersion()+")";
	private static final String FOOTER = "\nCopyright 2009 - Aaron Ciaghi, Stephen Long, Joshua Justice";
	private static Options options;

	public static void main(String[] args) throws Exception {

		setup();

		// Create a Parser
		CommandLineParser parser = new BasicParser();

		// Parse the program arguments
		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				printUsage(options);
				System.exit(0);
			}

			if (commandLine.hasOption('a') && commandLine.hasOption('n')
					&& commandLine.hasOption('i')) {
				int ndis = Integer.parseInt(commandLine.getOptionValue('n'));
				String file = commandLine.getOptionValue('i');
				String alg = commandLine.getOptionValue('a');
				
				//select algorithm
				IRedistrictingAlgorithm ra;//TODO
				
			} else {
				printUsage(options);
				System.exit(0);
			}

		} catch (Exception e) {
			System.out.println("You provided bad program arguments!");
			printUsage(options);

		}

	}

	@SuppressWarnings("static-access")
	private static void setup() {
		Option help = new Option("h", "Print this message");
		Option algorithm = OptionBuilder.withArgName("algorithm").hasArgs(1)
				.withDescription("Specify the desired redistricting algorithm")
				.create("a");
		Option ndists = OptionBuilder.withArgName("n_dist").hasArgs(1)
				.withDescription("Specify the number of districts to create")
				.create("n");
		Option file = OptionBuilder.withArgName("input_file").hasArgs(1)
				.withDescription("Specify the input file (without extension)")
				.create("i");

		options = new Options();
		options.addOption(help);
		options.addOption(algorithm);
		options.addOption(ndists);
		options.addOption(file);
	}

	private static void printUsage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(80);
		helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
	}

}