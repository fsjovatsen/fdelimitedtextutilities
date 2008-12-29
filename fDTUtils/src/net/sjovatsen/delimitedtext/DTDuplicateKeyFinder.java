package net.sjovatsen.delimitedtext;

/**
 * This class finds if a delimited file has duplicate rows based on a key.
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
import java.util.Collections;

public class DTDuplicateKeyFinder extends DTAbstract {

    private File _file;
    //private String[] _duplicates;
    //private int _key;
    /**
     * The default class constructor.
     */
    public DTDuplicateKeyFinder() {
    }

    /**
     * The class constructor.
     * 
     * @param file The file to search.
     */
    public DTDuplicateKeyFinder(File file) {
        if (file != null) {
            _file = file;
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
            BufferedReader file = new BufferedReader(new FileReader(_file));
            ArrayList<String> al1 = new ArrayList<String>();
            String[] lineArray1 = null;
            String line = null;
            int searchedRecords = 0;
            int firstIndex = 0;
            int lastIndex = 0;

            message("--------------------------------------------------");
            message(" Start getting duplicates ...");

            _startTime = System.currentTimeMillis();

            /*
             * Reading the file into a array.
             */
            while ((line = file.readLine()) != null) {
                lineArray1 = line.split(_delimiter);
                al1.add(lineArray1[_key]);
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
//            BufferedReader file = new BufferedReader(new FileReader(_file));
//            ArrayList<String> al1 = new ArrayList<String>();
//            ArrayList<String> al2 = new ArrayList<String>();
//            String[] lineArray1 = null;
//            String[] lineArray2 = null;
//            //boolean bMatchedKey = false;
//            int matchedCount = 0;
//            String line = null;
//
//            /*
//             * Reading the file into a array.
//             */
//            while ((line = file.readLine()) != null) {
//                al1.add(line);
//            }
////            while ((line = file.readLine()) != null) {
////                al2.add(line);
////            }
//            file.close();
//            al2 = al1;
//
//            for (String line1 : al1) {
//                lineArray1 = line1.split(this._delimiter);
//                for (String line2 : al2) {
//                    lineArray2 = line2.split(this._delimiter);
//                    if (lineArray1[_key].equals(lineArray2[_key])) {
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
     * Search the file for duplicate keys and returns on the first one found.
     * 
     * @return  true if found, false if not found.
     * 
     * TODO: Needs some cleanup and time messuring.
     */
    public boolean hasDuplicateKeys() {

        try {

            BufferedReader file = new BufferedReader(new FileReader(_file));
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

            _startTime = System.currentTimeMillis();

            /*
             * Reading the file into a array.
             */
            while ((line = file.readLine()) != null) {
                lineArray1 = line.split(_delimiter);
                al1.add(lineArray1[_key]);
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
     * Sets the file to check.
     * 
     * @param file  File to check.
     * @see #fDuplicateKeyFinder(File)
     */
    public void setFile(File file) {
        if (file != null) {
            _file = file;
        }
    }
}
