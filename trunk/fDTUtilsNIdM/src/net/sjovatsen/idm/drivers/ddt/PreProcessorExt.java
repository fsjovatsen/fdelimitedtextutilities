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

/**
 *
 * @author FSjovatsen
 */
public class PreProcessorExt implements PreProcessor {

    private Tracer tracer = null;

    public void init(String parameterString, Tracer tracer) {

        this.tracer = tracer;

        tracer.traceMessage("Greetings from Frode Sjovatsen!");
    }

    public void nextInputFile(File inputFile) throws SkipFileException {

        tracer.traceMessage("--- Executing PreProcessorExt.nextInputFile() ---");

        File ndFile = new File(inputFile.getPath() + ".NDF");
        File odFile = new File(inputFile.getPath() + ".ODF");
        File niFile = new File(inputFile.getPath() + ".NIF");
        DTDuplicateKeyFinder dupsFinder = new DTDuplicateKeyFinder();
        DTDeltaBuilder deltaBuilder = new DTDeltaBuilder();
        TracerDSTrace dstrace = new TracerDSTrace(tracer);

        tracer.traceMessage(" Determin if there is a corresponding ODF file?");

        try {
            if (!odFile.exists()) {

                tracer.traceMessage(" ODF file  do not exits (" + odFile.getPath() + ")");
                tracer.traceMessage(" Assuming this is the first run for the driver. Creating a empty ODF. This will make all records marked with a add event");
                odFile.createNewFile();
            } else {
                tracer.traceMessage(" ODF exits (" + odFile.getPath() + ")");
            }

            tracer.traceMessage(" Copy inputFile to NDF.");
            copyFile(inputFile, ndFile);
            tracer.traceMessage(" Rename inputFile to NIF.");
            //inputFile.renameTo(niFile);

            tracer.traceMessage(" All files are ready to be processed.");
            dupsFinder.setFile(ndFile);
            dupsFinder.setDelimiter(";");
            dupsFinder.setKey(3);
            dupsFinder.setTracer(dstrace);
            dupsFinder.setTrace(TRACE.VERBOSE);
            if (dupsFinder.hasDuplicateKeys()) {
                dupsFinder.getDuplicateKeys();
                throw new SkipFileException();
            }
            deltaBuilder.setNDF(ndFile);
            deltaBuilder.setODF(odFile);
            deltaBuilder.setNIF(inputFile);
            deltaBuilder.setDelimiter(";");
            deltaBuilder.setKey(3);
            deltaBuilder.setTrace(TRACE.VERBOSE);
            deltaBuilder.setTracer(new TracerDSTrace(tracer));
            deltaBuilder.buildDeltaFile();





            tracer.traceMessage("--- End executing PreProcessorExt.nextInputFile() ---");

        } catch (IOException e) {
        } finally {

        }

    }

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
}
