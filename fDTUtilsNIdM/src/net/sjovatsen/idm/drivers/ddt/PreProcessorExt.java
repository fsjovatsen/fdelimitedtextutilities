/*
 * This file is part of fDTUtils.
 *
 * fDTUtils is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * fDTUtils is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with fDTUtils.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Note that some of the embedded libraries may be using other licences.
 *
 */
package net.sjovatsen.idm.drivers.ddt;

import com.novell.nds.dirxml.driver.delimitedtext.*;
import java.io.*;
import java.nio.channels.FileChannel;
import net.sjovatsen.delimitedtext.DTDeltaBuilder;
import net.sjovatsen.delimitedtext.DTDuplicateKeyFinder;
//import net.sjovatsen.delimitedtext.DTDeltaBuilder;
import net.sjovatsen.delimitedtext.TRACE;
import net.sjovatsen.idm.drivers.Config;

/**
 *
 * @author Frode Sjovatsen <frode @ sjovatsen.net>
 */
public class PreProcessorExt implements PreProcessor {

    private TracerDSTrace dstracer = null;
    private TRACE trace = null;
    private Config config = null;
    private boolean allowDuplicates = false;

    public void init(String parameterString, Tracer tracer) {

        this.dstracer = new TracerDSTrace(tracer);
        this.config = new Config(parameterString);
        this.trace = traceTypeFromConfig(config.get("trace"));
        this.allowDuplicates = new Boolean(config.get("dups")).booleanValue();

        message("--- Loading PreProcessorExt ---");
        message("Greetings from Frode Sjovatsen!");
        message("DTDeltaBuilder version " + DTDeltaBuilder.version());
        message("DTDuplicateKeyFinder version " + DTDuplicateKeyFinder.version());
        config.dumpConfig(dstracer);
        message("--- End loading PreProcessorExt ---");
    }

    public void nextInputFile(File inputFile) throws SkipFileException, StatusException {

        message("--- Executing PreProcessorExt.nextInputFile() ---");

        File ndFile = new File(inputFile.getPath() + ".NDF");
        File odFile = new File(inputFile.getPath() + ".ODF");
        File niFile = new File(inputFile.getPath() + ".NIF");
        DTDuplicateKeyFinder dupsFinder = new DTDuplicateKeyFinder();
        DTDeltaBuilder deltaBuilder = new DTDeltaBuilder();
        //TracerDSTrace dstrace = new TracerDSTrace(tracer);

        message(" Determin if there is a corresponding ODF file?");

        try {
            if (!odFile.exists()) {

                message(" ODF file  do not exits (" + odFile.getPath() + ")");
                message(" Assuming this is the first run for the driver. Creating a empty ODF. This will make all records marked with a add event");
                odFile.createNewFile();
            } else {
                message(" ODF exits (" + odFile.getPath() + ")");
            }

            message(" Copy inputFile to NDF (" + inputFile.getPath() + " ==> " + ndFile.getPath() + ").");
            copyFile(inputFile, ndFile);
            //tracer.traceMessage(" Rename inputFile to NIF.");
            //inputFile.renameTo(niFile);

            message(" All files are ready to be processed.");

            dupsFinder.setFile(ndFile);
            dupsFinder.setDelimiter(config.get("delimiter"));
            dupsFinder.setKey(Integer.parseInt(config.get("key")));
            dupsFinder.setTracer(dstracer);
            dupsFinder.setTrace(trace);
            if (dupsFinder.hasDuplicateKeys()) {
                dupsFinder.getDuplicateKeys();
                if (!allowDuplicates) {
                    throw new StatusException(StatusException.STATUS_WARNING, "The NDF contains duplicates.");
                }
            }
            deltaBuilder.setNDF(ndFile);
            deltaBuilder.setODF(odFile);
            deltaBuilder.setNIF(inputFile);
            deltaBuilder.setDelimiter(config.get("delimiter"));
            deltaBuilder.setKey(Integer.parseInt(config.get("key")));
            deltaBuilder.setTrace(trace);
            deltaBuilder.setTracer(dstracer);
            deltaBuilder.buildDeltaFile();

            odFile.delete();
            ndFile.renameTo(odFile);
            message("--- End executing PreProcessorExt.nextInputFile() ---");

        } catch (IOException e) {
        } catch (StatusException e) {
            throw new SkipFileException();
        } finally {
        }

    }

    private void message(String s) {
        if ((trace == TRACE.QUIET) || (dstracer == null)) {
            return;
        }
        dstracer.traceMessage(s);
    }

    private void trace(String s) {
        if ((trace == TRACE.VERBOSE) || (dstracer != null)) {
            dstracer.traceMessage(s);
        }
    }

    /**
     * 
     * @param in
     * @param out
     * @throws java.io.IOException
     */
    private void copyFile(File in, File out) throws IOException {

        FileChannel inChannel = new FileInputStream(in).getChannel();
        FileChannel outChannel = new FileOutputStream(out).getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }

            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    private TRACE traceTypeFromConfig(String s) {

        if (s.equals("verbose")) {
            return TRACE.VERBOSE;
        } else if (s.equals("quiet")) {
            return TRACE.QUIET;
        } else {
            return TRACE.DEFAULT;
        }

    }
}
