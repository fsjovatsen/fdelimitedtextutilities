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
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class finds if a delimited dupsFile has duplicate rows based on a key.
 * 
 * @author Frode Sjovatsen <frode@sjovatsen.net>
 * 
 * TODO: Need to hardend the exception handling. Spesially regarding files. Should
 *       we also consider adding a method for removing duplicates?
 */
public class DTDuplicateKeyFinder extends DTAbstract {

    private File dupsFile;
    private static final int DUMMY_MAP_VALUE = 0;
    private static final String VERSION = "1.0.0";
    
    /**
     * The default class constructor.
     */
    public DTDuplicateKeyFinder() {
        dupsFile = null;
    }

    /**
     * The class constructor.
     * 
     * @param file The file to search for duplicates in.
     */
    public DTDuplicateKeyFinder(File file) {
        //if (file != null) {
        this.dupsFile = file;
    //}
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
            //ArrayList<String> al1 = new ArrayList<String>();
            String line = null;
            int searchedRecords = 0;
            Map<String, Integer> dupMap = new HashMap<String, Integer>();
            String[] record = null;

            message("--------------------------------------------------");
            message(" Start getting duplicates ...");

            this.startTime = System.currentTimeMillis();

            /*
             * Searching for dups and adding them to the array.
             */
            while ((line = file.readLine()) != null) {
                record = line.split(this.delimiter);
                searchedRecords++;
                if (dupMap.containsKey(record[this.key])) {
                    //bMatchedKey = true;
                    dups.add(record[this.key]);
                //break;
                } else {
                    dupMap.put(record[this.key], DUMMY_MAP_VALUE);
                }

            }


            /*
             * Lets share some stats with the user
             */
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

    /**
     * Search the dupsFile for duplicate keys and returns on the first one found.
     * 
     * @return  true if found, false if not found.
     */
    public boolean hasDuplicateKeys() {

        try {

            BufferedReader file = new BufferedReader(new FileReader(this.dupsFile));
            Map<String, Integer> dupMap = new HashMap<String, Integer>();
            String[] record = null;
            boolean matchedKey = false;
            String line = null;
            int searchedRecords = 0;

            message("--------------------------------------------------");
            message(" Start searching for duplicates ...");

            this.startTime = System.currentTimeMillis();

            /*
             * Let's check for duplicates.
             */
            while ((line = file.readLine()) != null) {
                record = line.split(this.delimiter);
                searchedRecords++;
                if (dupMap.containsKey(record[this.key])) {
                    matchedKey = true;
                    break;
                } else {
                    dupMap.put(record[this.key], DUMMY_MAP_VALUE);
                }

            }

            file.close();

            message(" Determing if the file has duplicates took " + formatElapsedTime() + " to finish.");
            if (matchedKey) {
                message(" Records searched before duplicate found " + searchedRecords);
            } else {
                message(" No duplicates found.");
                message(" Records searched " + searchedRecords);
            }
            message("--------------------------------------------------");

            return matchedKey;

        } catch (FileNotFoundException e) {
            trace("File not found (" + e.toString() + ").");
            return false;
        } catch (IOException e) {
            trace(e.toString());
            return false;
        } catch (Exception e) {
            trace(e.toString());
            return false;
        }
    }

    /**
     * Sets the duplcate file to check.
     * 
     * @param file  File to check.
     * @see DTDuplicateKeyFinder
     */
    public void setFile(File file) {
        this.dupsFile = file;
    }

    /**
     * Returns class version
     * @return <code>VERSION</code>
     */
    public static String version() {
        return VERSION;
    }
}
