package edu.gatech.c4g.r4g;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class Redistrict {

	private static final String USAGE = "[-h] -a <algorithm> -i <input_file>";
	private static final String HEADER = "Redistricting - A bunch of algorithms to (fairly?) redistrict Australia";
	private static final String FOOTER = "Copyright 2009 - Aaron Ciaghi, Stephen Long, Joshua Justice";

	public static void main(String[] args) throws Exception {
		// Create a Parser
		CommandLineParser parser = new BasicParser();
		Options options = new Options();
		options.addOption("h", "help", false, "Print this usage information");
		OptionGroup optionGroup = new OptionGroup();
		optionGroup.addOption(OptionBuilder.hasArg(true).withArgName("file")
				.withLongOpt("file").create('f'));
		optionGroup.addOption(OptionBuilder.hasArg(true).withArgName("email")
				.withLongOpt("email").create('m'));
		options.addOptionGroup(optionGroup);
		// Parse the program arguments
		try {
			CommandLine commandLine = parser.parse(options, args);
			if (commandLine.hasOption('h')) {
				printUsage(options);
				System.exit(0);
			}

			// ... do important stuff ...
		} catch (Exception e) {
			System.out.println("You provided bad program arguments!");
			printUsage(options);

		}

	}

	private static void printUsage(Options options) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.setWidth(80);
		helpFormatter.printHelp(USAGE, HEADER, options, FOOTER);
	}

}