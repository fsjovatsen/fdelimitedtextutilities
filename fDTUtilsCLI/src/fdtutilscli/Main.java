/*
 * This file is part of fDTUtilsCLI.
 *
 * fDTUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * fDTUtilsCLI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fDTUtilsCLI.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Note that some of the embedded libraries may be using other licences.
 * 
 */
package fdtutilscli;

import java.io.File;
import org.apache.commons.cli.*;
import net.sjovatsen.delimitedtext.*;

/**
 * Main method for the application.
 * 
 * @author Frode Sjovatsen <frode @ sjovatsen.net>
 * 
 * TODO: Need nice descriptions for all the options in the usage message.
 */
public class Main {

    private static final String HELP_HEADER = "where options include:";
    private static final String HELP_FOOTER = "Error: ";
    private static final String HELP_SYNTAX = "java -jar fDTUtilsCLI.jar";
    private static final String E_MSG_UNREC_OPT = "unknown option.";
    private static final String DEFAULT_SEPERATOR = ",";
    private static final int DEFAULT_KEY = 0;

    /**
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        CommandLineParser parser = new PosixParser();
        CommandLine line = null;
        Options options = new Options();
        OptionGroup optCommand = new OptionGroup();
        OptionGroup optOutput = new OptionGroup();
        HelpFormatter formatter = new HelpFormatter();
        TRACE trace = TRACE.DEFAULT;

        optCommand.addOption(OptionBuilder.withArgName("ndf odf nif key seperator").hasArgs(5).withValueSeparator(' ').withDescription("Description").create("delta"));
        optCommand.addOption(OptionBuilder.withArgName("dupsfile key seperator").hasArgs(3).withDescription("Description").create("duplicates"));
        optCommand.addOption(OptionBuilder.withLongOpt("help").withDescription("print this message.").create("h"));
        optOutput.addOption(new Option("verbose", "be extra verbose"));
        optOutput.addOption(new Option("quiet", "be extra quiet"));
        optOutput.addOption(new Option("silent", "same as --quiet"));
        options.addOptionGroup(optCommand);
        options.addOptionGroup(optOutput);

        try {
            line = parser.parse(options, args);

            if (line.hasOption("verbose")) {
                trace = TRACE.VERBOSE;
            } else if (line.hasOption("quiet") || line.hasOption("silent")) {
                trace = TRACE.QUIET;
            }

            if (line.hasOption("h")) {
                formatter.printHelp(HELP_SYNTAX, HELP_HEADER, options, null, true);
                return;
            } else if (line.hasOption("delta")) {
                String ndf = line.getOptionValues("delta")[0];
                String odf = line.getOptionValues("delta")[1];
                String nif = line.getOptionValues("delta")[2];
                Integer key =
                        (line.getOptionValues("delta").length <= 3)
                        ? DEFAULT_KEY
                        : new Integer(line.getOptionValues("delta")[3]);
                String seperator =
                        (line.getOptionValues("delta").length <= 4)
                        ? DEFAULT_SEPERATOR
                        : line.getOptionValues("delta")[4];

                doDelta(ndf, odf, nif, key.intValue(), seperator, trace);

                return;
            } else if (line.hasOption("duplicates")) {
                String dupsFile = line.getOptionValues("duplicates")[0];
                Integer key =
                        (line.getOptionValues("duplicates").length <= 1)
                        ? DEFAULT_KEY
                        : new Integer(line.getOptionValues("duplicates")[1]);
                String seperator =
                        (line.getOptionValues("duplicates").length <= 2)
                        ? DEFAULT_SEPERATOR
                        : line.getOptionValues("duplicates")[2];
                doDuplicates(dupsFile, key.intValue(), seperator, trace);
                return;
            } else if (args.length == 0) {
                formatter.printHelp(HELP_SYNTAX, HELP_HEADER, options, null, true);
            } else {
                throw new UnrecognizedOptionException(E_MSG_UNREC_OPT);
            }

        } catch (UnrecognizedOptionException e) {
            formatter.printHelp(HELP_SYNTAX, HELP_HEADER, options, HELP_FOOTER + e.getMessage(), true);
        } catch (ParseException e) {
            formatter.printHelp(HELP_SYNTAX, HELP_HEADER, options, HELP_FOOTER + e.getMessage(), true);
        }
    }

    private static void doDelta(String ndf, String odf, String nif, int key, String seperator, TRACE trace) {

        File ndFile = new File(ndf);
        File odFile = new File(odf);
        File niFile = new File(nif);
        DTDeltaBuilder dtdb = new DTDeltaBuilder(ndFile, odFile, niFile);
        TracerStandardOutput tracer = new TracerStandardOutput();

        dtdb.setKey(key);
        dtdb.setTrace(trace);
        dtdb.setDelimiter(seperator);
        dtdb.setMinRowCount(0);
        dtdb.setTracer(tracer);
        dtdb.buildDeltaFile();

    }

    private static void doDuplicates(String dups, int key, String seperator, TRACE trace) {
        File dupsFile = new File(dups);
        DTDuplicateKeyFinder dtdf = new DTDuplicateKeyFinder(dupsFile);
        TracerStandardOutput tracer = new TracerStandardOutput();

        dtdf.setKey(key);
        dtdf.setTrace(trace);
        dtdf.setTracer(tracer);
        dtdf.setDelimiter(seperator);
        dtdf.setMinRowCount(0);
        if (dtdf.hasDuplicateKeys()) {
            dtdf.getDuplicateKeys();
        }
    }
}
