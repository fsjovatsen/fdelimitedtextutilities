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
package net.sjovatsen.delimitedtext;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Class to make a delta file.
 *
 * This class takes a new data file (NDF) and a old data file (ODF) and finds
 * the changes. It markes the changes with ADD, MODIFY or DELETE.
 *
 * The use is initially for the Delimited Text Driver for Novell Identity Manager,
 * but it should work fine for more generic use also.
 *
 * @author      Frode Sjovatsen <frode@sjovatsen.net>
 *
 * TODO: Need to hardend the error checking/exceptions.
 */
public class DTDeltaBuilder extends DTAbstract {

    private File ndFile;
    private File odFile;
    private File niFile;
//    private static final String DEFAULT_DELIMITER = ",";
//    private static final int DEFAULT_KEY = 0;
//    private static final int DEFAULT_ROWCOUNT = 0;
    private static final String VERSION = "1.0.0";

    /**
     * Class constructor.
     */
    public DTDeltaBuilder() {
        this.ndFile = null;
        this.odFile = null;
        this.niFile = null;
        this.key = DEFAULT_KEY;
        this.trace = TRACE.DEFAULT;
        this.delimiter = DEFAULT_DELIMITER;
        this.minRowCount = DEFAULT_ROWCOUNT;
        this.tracer = null;
    }

    /**
     * Class constructor specifying files for delta building.
     *
     * @param ndf   New data file.
     * @param odf   Old data file.
     * @param nif   New input file.
     */
    public DTDeltaBuilder(File ndf, File odf, File nif) {
        this.ndFile = ndf;
        this.odFile = odf;
        this.niFile = nif;
        this.key = DEFAULT_KEY;
        this.trace = TRACE.DEFAULT;
        this.minRowCount = DEFAULT_ROWCOUNT;
        this.delimiter = DEFAULT_DELIMITER;
        this.tracer = null;
    }

    /**
     * Class constructor specifying files for delta building.
     *
     * @param ndf   New data file.
     * @param odf   Old data file.
     * @param nif   New input file.
     * @param key   Index of the key in a record.
     * @param delimiter Char that separates the fields in a record.
     */
    public DTDeltaBuilder(File ndf, File odf, File nif, int key, String delimiter) {
        this.ndFile = ndf;
        this.odFile = odf;
        this.niFile = nif;
        this.key = key;
        this.delimiter = delimiter;
        this.minRowCount = DEFAULT_ROWCOUNT;
        this.trace = TRACE.DEFAULT;
        this.tracer = null;
    }

    /**
     * Builds a delta file (NIF) by running a compartion of the NDF and ODF.
     * If a key is found in both NDF and ODF, and the whole record is equal, the
     * record is omitted in NIF. If the key is in both NDF and ODF, but the record
     * is not equal, it is marked as MODIFY and pushed to NIF. A record that is
     * only in NDF and not in ODF, is marked as ADD and pushed to NIF.
     * If a record is in ODF, but not in NDF, the record is marked as DELETE
     * and pushed to NIF.
     */
    public void buildDeltaFile() {

        try {
            BufferedReader ndfFile = new BufferedReader(new FileReader(ndFile));
            BufferedReader odfFile = new BufferedReader(new FileReader(odFile));
            PrintWriter nifFile = new PrintWriter(new FileWriter(niFile));
            ArrayList<String> odfArrayList = new ArrayList<String>();
            ArrayList<String> ndfArrayList = new ArrayList<String>();
            String ndfLine = null;
            String[] ndfLineArray = null;
            String odfLine = null;
            String[] odfLineArray = null;
            int iNDFCount = 0;
            int iODFCount = 0;
            int iNIFCount = 0;
            int iADDCount = 0;
            int iMODIFYCount = 0;
            int iDELETECount = 0;
            int i = 0;
            Boolean matchedKey = false;

            message("--------------------------------------------------");
            message(" Start building delta file...");
            /*
             * I wonder how much time this takes?
             */
            this.startTime = System.currentTimeMillis();

            /*
             * Reading the ODF into a array.
             */
            if (this.minRowCount > 0) {
                odfArrayList.ensureCapacity(this.minRowCount);
            }
            while ((odfLine = odfFile.readLine()) != null) {
                odfArrayList.add(odfLine);
            }
            iODFCount = odfArrayList.size();
            odfFile.close();

            /*
             * Reading the NDF into a array.
             */
            if (this.minRowCount > 0) {
                ndfArrayList.ensureCapacity(this.minRowCount);
            }
            while ((ndfLine = ndfFile.readLine()) != null) {
                ndfArrayList.add(ndfLine);
            }
            iNDFCount = ndfArrayList.size();
            ndfFile.close();

            /*
             * Checking if there is records in the NDF that is not
             * in the ODF and mark them as ADD. Also checking if any
             * of the matching records have changed and marking them as
             * MODIFY.
             */
            for (String lineNDFList : ndfArrayList) {
                ndfLine = lineNDFList;
                ndfLineArray = ndfLine.split(this.delimiter);
                i = 0;
                for (String lineODFList : odfArrayList) {
                    odfLine = lineODFList;
                    odfLineArray = odfLine.split(this.delimiter);
                    if (ndfLineArray[this.key].equals(odfLineArray[this.key])) {
                        matchedKey = true;
                        break;
                    }
                    i++;
                }

                /* Searching for ADD or MODIFY */
                if (matchedKey) { /* The record exits in both NDF and ODF */
                    odfArrayList.remove(i);
                    trace(" The key (" + ndfLineArray[this.key] + ") was found i both NDF and ODF. Now we need to check if the whole record matches");

                    if (ndfLine.equals(odfLine)) {
                        trace(" The record was equal. Skipping this record in NIF.");
                    } else {
                        trace(" The record was not equal. This is a MODIFY. Pushing record to NIF.");
                        iMODIFYCount++;
                        iNIFCount++;
                        nifFile.println("MODIFY" + this.delimiter + ndfLine);
                    }
                    matchedKey = false;
                } else { /* The record was found in NDF but not in ODF */
                    trace(" The key (" + ndfLineArray[this.key] + ") was not found i both NDF and ODF. This is a ADD");
                    iADDCount++;
                    iNIFCount++;
                    nifFile.println("ADD" + this.delimiter + ndfLine);
                }
            } /* End ADD or MODIFY */


            /*
             * Comparing ODF with NDF to find persons that is no longer present.
             * We will find the DELETE events.
             */
            matchedKey = false;
            for (String lineODFList : odfArrayList) {
                odfLine = lineODFList;
                odfLineArray = odfLine.split(this.delimiter);
                for (String lineNDFList : ndfArrayList) {
                    ndfLine = lineNDFList;
                    ndfLineArray = ndfLine.split(this.delimiter);
                    if (odfLineArray[this.key].equals(ndfLineArray[this.key])) {
                        matchedKey = true;
                        break;
                    }
                }

                if (matchedKey) {
                    trace(" The key (" + ndfLineArray[this.key] + ") was found i both ODF and NDF. We should keep this user.");
                    matchedKey = false;
                } else {
                    trace(" The key (" + ndfLineArray[this.key] + ") was not found in NDF but is in ODF. This is a DELETE.");
                    iDELETECount++;
                    iNIFCount++;
                    nifFile.println("DELETE" + this.delimiter + odfLine);
                }
            } /* End of DELETE */

            /*
             * Lets share some stats with the user
             */
            message(" The fDTDeltaBuilder took " + formatElapsedTime() + " to finish.");
            message(" NDF line count = " + iNDFCount);
            message(" ODF line count = " + iODFCount);
            message(" NIF line count = " + iNIFCount);
            message(" MODIFY count = " + iMODIFYCount);
            message(" ADD count = " + iADDCount);
            message(" DELETE count = " + iDELETECount);
            message("--------------------------------------------------");

            /*
             * Clean up
             */
            nifFile.close();


        } catch (FileNotFoundException e) {
            trace("File not found (" + e.toString() + ").");
        } catch (IOException e) {
            trace(e.toString());
        } catch (ArrayIndexOutOfBoundsException e) {
            trace("Array index out of bounds (" + e.toString() + ").");
        }
    }

    /**
     * Sets the new data file.
     *
     * @param ndf
     */
    public void setNDF(File ndf) {
        this.ndFile = ndf;
    }

    /**
     * Sets the new input file.
     *
     * @param nif
     */
    public void setNIF(File nif) {
        this.niFile = nif;
    }

    /**
     * Sets the old data file.
     *
     * @param odf
     */
    public void setODF(File odf) {

        this.odFile = odf;
    }

    /**
     * Returns class version
     * @return <code>VERSION</code>
     */
    public static String version() {
        return VERSION;
    }
}