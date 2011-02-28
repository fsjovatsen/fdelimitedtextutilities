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
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sjovatsen.delimitedtext.DTDeltaBuilder;
import net.sjovatsen.delimitedtext.DTDeltaBuilderStats;
import net.sjovatsen.delimitedtext.DTDuplicateKeyFinder;
import net.sjovatsen.delimitedtext.TRACE;
import net.sjovatsen.idm.drivers.Config;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Frode Sjovatsen <frode @ sjovatsen.net>
 */
public class PreProcessorExt implements PreProcessor {

    private TracerDSTrace dstracer = null;
    private TRACE trace = null;
    private Config config = null;
    private boolean allowDuplicates = false;
    private int addThreshold = 0; //(config.get("addThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("addThreshold"));
    private int deleteThreshold = 0; //(config.get("deleteThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("deleteThreshold"));
    private int modifyThreshold = 0; //(config.get("modifyThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("modifyThreshold"));
    private String backupDir = null;
    private int key = 0;
    private String delimiter = null;
    private static final String VERSION = "1.2.0";

    public void init(String parameterString, Tracer tracer) throws StatusException {

        try {
            this.dstracer = new TracerDSTrace(tracer);
            this.config = new Config(parameterString);
            this.trace = traceTypeFromConfig(config.get("trace"));

            message("--- Loading PreProcessorExt ---");
            message("Greetings from Frode Sjovatsen!");
            message("fDTUtilsNIdm version " + this.version());
            message("DTDeltaBuilder version " + DTDeltaBuilder.version());
            message("DTDuplicateKeyFinder version " + DTDuplicateKeyFinder.version());
            message("Extention config:");
            message(" " + config.configString());

            message("Validating config...");
            this.addThreshold = (config.get("addThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("addThreshold"));
            this.deleteThreshold = (config.get("deleteThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("deleteThreshold"));
            this.modifyThreshold = (config.get("modifyThreshold").equals("na")) ? 0 : Integer.parseInt(config.get("modifyThreshold"));
            this.key = (config.get("key").equals("na")) ? 0 : Integer.parseInt(config.get("key"));
            //this.trace = traceTypeFromConfig(config.get("trace"));
            this.allowDuplicates = (config.get("dups").toString().equals("true")) ? true : false; //new Boolean(config.get("dups")).booleanValue();
            this.delimiter = config.get("delimiter");
            this.backupDir = config.get("backupDir");
            if (!verifyBackupDir(backupDir)) {
                //message("backupDir does not exist, is not a directory or contains a trailing path seperator.");
                throw new StatusException(StatusException.STATUS_FATAL, "backupDir (" + this.backupDir + ") does not exist, is not a directory or contains a trailing path seperator.");
            }
            message("Config seems ok!");

            message("--- End loading PreProcessorExt ---");

        } catch (NumberFormatException nfe) {
            message("A config value was expected to be an integer, but was not.");
            throw new StatusException(StatusException.STATUS_FATAL, nfe.getMessage());
        }
    }

    public void nextInputFile(File inputFile) throws SkipFileException, StatusException {

        message("--- Executing PreProcessorExt.nextInputFile() ---");

        File ndFile = new File(inputFile.getPath() + ".NDF");
        File odFile = new File(inputFile.getPath() + ".ODF");
        File newODFile = new File(inputFile.getPath() + ".ODF");
        DTDuplicateKeyFinder dupsFinder = new DTDuplicateKeyFinder();
        DTDeltaBuilder deltaBuilder = new DTDeltaBuilder();
        DTDeltaBuilderStats stats = null;

        message(" Determin if there is a corresponding ODF file?");

        try {
            // Make ODF if not exists
            if (!odFile.exists()) {
                message(" ODF file  do not exits (" + odFile.getPath() + ")");
                message(" Assuming this is the first run for the driver. Creating a empty ODF. This will make all records marked with a add event");
                odFile.createNewFile();
            } else {
                message(" ODF exits (" + odFile.getPath() + ")");
            }

            // Make backup of new inputfile and current ODF.
            backupInputAndODF(inputFile, odFile, backupDir);

            // Make NDF
            message(" Copy inputFile to NDF (" + inputFile.getPath() + " ==> " + ndFile.getPath() + ").");
            copyFile(inputFile, ndFile);

            message(" All files are ready to be processed.");

            // Check for duplicates
            dupsFinder.setFile(ndFile);
            dupsFinder.setDelimiter(this.delimiter);
            dupsFinder.setKey(this.key);
            dupsFinder.setTracer(dstracer);
            dupsFinder.setTrace(trace);
            if (dupsFinder.hasDuplicateKeys()) {
                dupsFinder.getDuplicateKeys();
                if (!allowDuplicates) {
                    message(" Found duplicates. Keeping the old ODF and deleting the NDF (" + ndFile.getPath() + ")");
                    deleteFile(ndFile);
                    deleteFile(inputFile);
                    throw new StatusException(StatusException.STATUS_FATAL, "The NDF file contains duplicates.");
                }
            }

            // Building delta file.
            deltaBuilder.setNDF(ndFile);
            deltaBuilder.setODF(odFile);
            deltaBuilder.setNIF(inputFile);
            deltaBuilder.setDelimiter(config.get("delimiter"));
            deltaBuilder.setKey(Integer.parseInt(config.get("key")));
            deltaBuilder.setTrace(trace);
            deltaBuilder.setTracer(dstracer);
            stats = deltaBuilder.buildDeltaFile();

            message(" Checking statistics...");
            if (addThreshold > 0) {
                stats.setAddThreshold(addThreshold);
                if (stats.exceedsAddThreshold()) {
                    message(" Add threshold exceeded. Keeping the old ODF and deleting the NDF (" + ndFile.getPath() + ")");
                    deleteFile(ndFile);
                    deleteFile(inputFile);
                    throw new StatusException(StatusException.STATUS_FATAL, "The NDF file exceeds add treshold.");
                }
            } else {
                message(" addThreshold is disabled.");
            }

            if (deleteThreshold > 0) {
                stats.setDeleteThreshold(deleteThreshold);
                if (stats.exceedsDeleteThreshold()) {
                    message(" Delete threshold exceeded. Keeping the old ODF and deleting the NDF (" + ndFile.getPath() + ")");
                    deleteFile(ndFile);
                    deleteFile(inputFile);
                    throw new StatusException(StatusException.STATUS_FATAL, "The NDF file exceeds delete treshold.");
                }
            } else {
                message(" deleteThreshold is disabled.");
            }

            if (modifyThreshold > 0) {
                stats.setModifyThreshold(modifyThreshold);
                if (stats.exceedsModifyThreshold()) {
                    message(" Modify threshold exceeded. Keeping the old ODF and deleting the NDF (" + ndFile.getPath() + ")");
                    deleteFile(ndFile);
                    deleteFile(inputFile);
                    throw new StatusException(StatusException.STATUS_FATAL, "The NDF file exceeds modify treshold.");
                }
            } else {
                message(" modifyThreshold is disabled.");
            }

            // Making new ODF file
            message(" Deleting ODF (" + odFile.getPath() + ")");
            deleteFile(odFile);

            message(" Rename NDF to ODF (" + ndFile.getPath() + " ==> " + newODFile.getPath() + ")");
            if (ndFile.renameTo(newODFile)) {
                message(" Rename NDF to ODF success!");
            } else {
                message(" Renamed NDF to ODF failed!");
            }
            message("--- End executing PreProcessorExt.nextInputFile() ---");

        } catch (IOException e) {
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

    private void backupInputAndODF(File inputFile, File odFile, String backupDir) {

        Date now = new Date();
        int len;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String[] filesToZip = new String[2];
        String[] fileNamesToZip = new String[2];

        filesToZip[0] = inputFile.getPath();
        filesToZip[1] = odFile.getPath();
        fileNamesToZip[0] = inputFile.getName();
        fileNamesToZip[1] = odFile.getName();

        String zipFileName = backupDir + File.separator + fileNamesToZip[0] + "-" + format.format(now) + ".zip";

        message(" Make backup of current ODF and new inputfile. Backuparchive is " + zipFileName);

        byte[] buffer = new byte[18024];

        try {

            ZipOutputStream out =
                    new ZipOutputStream(new FileOutputStream(zipFileName));

            // Set the compression ratio
            out.setLevel(Deflater.BEST_COMPRESSION);

            // Adding each file to the zip file
            for (int i = 0; i < filesToZip.length; i++) {
                message(" Adding " + filesToZip[i] + " (" + fileNamesToZip[i] + ") to the backup archive.");
                FileInputStream in = new FileInputStream(filesToZip[i]);
                out.putNextEntry(new ZipEntry(fileNamesToZip[i]));

                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }

                out.closeEntry();
                in.close();
            }

            out.close();

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void deleteFile(File file) {
        if (file.delete()) {
            message(" Delete " + file.getPath() + " successede.");
        } else {
            message(" Delete " + file.getPath() + " failed.");
        }
    }

    private boolean verifyBackupDir(String backupDir) {

        File file = new File(backupDir);

        if (backupDir.equals("na")) {
            message("Backup is disabled.");
            return true;
        }

        if (backupDir.endsWith(File.separator)) {
            message("backupDir ends with a path seperator.");
            return false;
        }

        if (!file.isDirectory()) {
            return false;
        }

        return true;
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

    /**
     * Returns class version
     * @return <code>VERSION</code>
     */
    public String version() {
        return VERSION;
    }
}
