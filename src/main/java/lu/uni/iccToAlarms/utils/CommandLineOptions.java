package lu.uni.iccToAlarms.utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class sets the different option for the application
 * @author Jordan Samhi
 *
 */
public class CommandLineOptions {

	private static final Triplet<String, String, String> APK = new Triplet<String, String, String>("apk", "a", "Apk file");
	private static final Triplet<String, String, String> HELP = new Triplet<String, String, String>("help", "h", "Print this message");
	private static final Triplet<String, String, String> PLATFORMS = new Triplet<String, String, String>("platforms", "p", "Android platforms folder");
	private static final Triplet<String, String, String> OUTPUT = new Triplet<String, String, String>("output", "o", "Output folder");
	private static final String doesApkContainParticularLocalType = "doesApkContainParticularLocalType";
	
	private Options options, firstOptions;
	private CommandLineParser parser;
	private CommandLine cmdLine, cmdFirstLine;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public CommandLineOptions(String[] args) {
		this.options = new Options();
		this.firstOptions = new Options();
		this.initOptions();
		this.parser = new DefaultParser();
		this.parse(args);
	}

	/**
	 * This method does the parsing of the arguments.
	 * It distinguished, real options and help option.
	 * @param args the arguments of the application
	 */
	private void parse(String[] args) {
		HelpFormatter formatter = null;
		try {
			this.cmdFirstLine = this.parser.parse(this.firstOptions, args, true);
			if (this.cmdFirstLine.hasOption(HELP.getValue0())) {
				formatter = new HelpFormatter();
				formatter.printHelp(doesApkContainParticularLocalType, this.options, true);
				System.exit(0);
			}
			this.cmdLine = this.parser.parse(this.options, args);
		} catch (ParseException e) {
			this.logger.error(e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Initialization of all recognized options
	 */
	private void initOptions() {
		final Option file = Option.builder(APK.getValue1())
				.longOpt(APK.getValue0())
				.desc(APK.getValue2())
				.hasArg(true)
				.argName(APK.getValue0())
				.required(true)
				.build();

		final Option platforms = Option.builder(PLATFORMS.getValue1())
				.longOpt(PLATFORMS.getValue0())
				.desc(PLATFORMS.getValue2())
				.hasArg(true)
				.argName(PLATFORMS.getValue0())
				.required(true)
				.build();
		
		final Option output = Option.builder(OUTPUT.getValue1())
				.longOpt(OUTPUT.getValue0())
				.desc(OUTPUT.getValue2())
				.hasArg(true)
				.argName(OUTPUT.getValue0())
				.required(true)
				.build();

		final Option help = Option.builder(HELP.getValue1())
				.longOpt(HELP.getValue0())
				.desc(HELP.getValue2())
				.argName(HELP.getValue0())
				.build();

		this.firstOptions.addOption(help);

		this.options.addOption(file);
		this.options.addOption(platforms);
		this.options.addOption(output);
		for(Option o : this.firstOptions.getOptions()) {
			this.options.addOption(o);
		}
	}

	public String getApk() {
		return this.cmdLine.getOptionValue(APK.getValue0());
	}

	public String getPlatforms() {
		return this.cmdLine.getOptionValue(PLATFORMS.getValue0());
	}
	
	public String getOutput() {
		return this.cmdLine.getOptionValue(OUTPUT.getValue0());
	}
}
