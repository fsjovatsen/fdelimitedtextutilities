/*
    This file is part of fDTUtils.

    fDTUtils is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
*/

package net.sjovatsen.delimitedtext;

/**
 * This class finds if a delimited dupsFile has duplicate rows based on a key.
 * 
 * @author fsjovatsen
 * @version 1.0.0
 * 
 * TODO: Need to hardend the exception handling. Spesially regarding files. Should
 *       we also consider adding a method for removing duplicates?
 */
import java.io.File;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
//import java.util.Collections;
public class DTDuplicateKeyFinder extends DTAbstract {

    private File dupsFile = null;

    /**
     * The default class constructor.
     */
    public DTDuplicateKeyFinder() {
    }

    /**
     * The class constructor.
     * 
     * @param dupsFile The file to search for duplicates in.
     */
    public DTDuplicateKeyFinder(File file) {
        if (file != null) {
            this.dupsFile = file;
        }
    }

    /**
     * Finds all the duplicate keys and returns them.
     * 
     * @return  A list of all the duplicate keys.
     * 
     * TODO: Needs some cleanup and time messuring.
     */
    public ArrayList<String> getDuplicateKeys() {

        try {
            ArrayList<String> dups = new ArrayList<String>();
            BufferedReader file = new BufferedReader(new FileReader(this.dupsFile));
            ArrayList<String> al1 = new ArrayList<String>();
            String[] lineArray1 = null;
            String line = null;
            int searchedRecords = 0;
            int firstIndex = 0;
            int lastIndex = 0;

            message("--------------------------------------------------");
            message(" Start getting duplicates ...");

            this.startTime = System.currentTimeMillis();

            /*
             * Reading the dupsFile into a array.
             */
            while ((line = file.readLine()) != null) {
                lineArray1 = line.split(this.delimiter);
                al1.add(lineArray1[this.key]);
            }
            file.close();

//            recordCount = al1.size();

            for (String s : al1) {
                firstIndex = al1.indexOf(s);
                lastIndex = al1.lastIndexOf(s);
                searchedRecords++;
                if (lastIndex > firstIndex) {
                    dups.add(s);
                }
            }
            message(" The search took " + formatElapsedTime() + " to finish.");
            message(" Records searched " + searchedRecords);
            message(" Duplicate count " + dups.size());
            trace(" The duplcates are:");
            for (String l : dups) {
                trace("  " + l);
            }
            message("--------------------------------------------------");

            return dups;

        } catch (FileNotFoundException e) {
            trace("File not found (" + e.toString() + ").");
            return null;
        } catch (IOException e) {
            trace(e.toString());
            return null;
        }

    }
//    public ArrayList<String> getDuplicateKeys() {
//
//        try {
//            ArrayList<String> al = new ArrayList<String>();
//            //ArrayList<int> dupsFoundIndex = new ArrayList<int>();
//            BufferedReader dupsFile = new BufferedReader(new FileReader(dupsFile));
//            ArrayList<String> al1 = new ArrayList<String>();
//            ArrayList<String> al2 = new ArrayList<String>();
//            String[] lineArray1 = null;
//            String[] lineArray2 = null;
//            //boolean bMatchedKey = false;
//            int matchedCount = 0;
//            String line = null;
//
//            /*
//             * Reading the dupsFile into a array.
//             */
//            while ((line = dupsFile.readLine()) != null) {
//                al1.add(line);
//            }
////            while ((line = dupsFile.readLine()) != null) {
////                al2.add(line);
////            }
//            dupsFile.close();
//            al2 = al1;
//
//            for (String line1 : al1) {
//                lineArray1 = line1.split(this.delimiter);
//                for (String line2 : al2) {
//                    lineArray2 = line2.split(this.delimiter);
//                    if (lineArray1[key].equals(lineArray2[key])) {
//                        matchedCount++;
//                    }
//                }
//
//                if (matchedCount > 1) {
//                    al.add(line1);
//                    matchedCount = 0;
//                } else {
//                    matchedCount = 0;
//                }
//            }
//
//            return al;
//
//        } catch (FileNotFoundException e) {
//            trace("File not found (" + e.toString() + ").");
//            return null;
//        } catch (IOException e) {
//            trace(e.toString());
//            return null;
//        }
//
//    }
    /**
     * Search the dupsFile for duplicate keys and returns on the first one found.
     * 
     * @return  true if found, false if not found.
     * 
     * TODO: Needs some cleanup and time messuring.
     */
    public boolean hasDuplicateKeys() {

        try {

            BufferedReader file = new BufferedReader(new FileReader(this.dupsFile));
            ArrayList<String> al1 = new ArrayList<String>();
            String[] lineArray1 = null;
            boolean bMatchedKey = false;
            String line = null;
            int firstIndex = 0;
            int lastIndex = 0;
            int searchedRecords = 0;
            int recordCount = 0;

            message("--------------------------------------------------");
            message(" Start searching for duplicates ...");

            this.startTime = System.currentTimeMillis();

            /*
             * Reading the dupsFile into a array.
             */
            while ((line = file.readLine()) != null) {
                lineArray1 = line.split(this.delimiter);
                al1.add(lineArray1[this.key]);
            }
            file.close();

            recordCount = al1.size();

            /*
             * Lets find out if there are any dups.
             */
            for (String s : al1) {
                firstIndex = al1.indexOf(s);
                lastIndex = al1.lastIndexOf(s);
                searchedRecords++;
                if (lastIndex > firstIndex) {
                    bMatchedKey = true;
                    break;
                }
            }

            message(" Determing if the file has duplicates took " + formatElapsedTime() + " to finish.");
            message(" File count " + recordCount);
            if (bMatchedKey) {
                message(" Records searched before duplicate found " + searchedRecords);
            } else {
                message(" No duplicates found.");
            }
            message("--------------------------------------------------");

            return bMatchedKey;

        } catch (FileNotFoundException e) {
            trace("File not found (" + e.toString() + ").");
            return false;
        } catch (IOException e) {
            trace(e.toString());
            return false;
        }
    }

    /**
     * Sets the dupsFile to check.
     * 
     * @param dupsFile  File to check.
     * @see #fDuplicateKeyFinder(File)
     */
    public void setFile(File file) {
        if (file != null) {
            this.dupsFile = file;
        }
    }
}
