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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This is the abstract class for all the fDT* classes. 
 * It provides some data for:
 * <ul>
 *  <li>time measuring 
 *  <li>parsing a delimited record
 *  <li>datastructures performance
 *  <li>tracing
 * </ul>
 * 
 * @author Frode Sjovatsen <frode@sjovatsen.net>
 */
public abstract class DTAbstract {

    protected long startTime;
    protected long currentTime;
    protected int key;
    protected String delimiter;
    protected TRACE trace;
    protected int minRowCount;
    protected Tracer tracer;

    protected static final String DEFAULT_DELIMITER = ",";
    protected static final int DEFAULT_KEY = 0;
    protected static final int DEFAULT_ROWCOUNT = 0;

    /**
     * Class constructor.
     */
    public DTAbstract() {
        this.startTime = 0;
        this.key = 0;
        this.delimiter = DEFAULT_DELIMITER;
        this.trace = TRACE.DEFAULT;
        this.tracer = null;
        this.minRowCount = 0;
    }

    /**
     * Sends a verbose message to the tracer.
     * 
     * @param s Message to trace.
     */
    protected void trace(String s) {
        if ((this.trace == TRACE.VERBOSE) && (this.tracer != null)) {
            this.tracer.traceMessage(s);
        }
    }

    /**
     * Sends a informational message to the tracer.
     * 
     * @param s Message to trace.
     */
    protected void message(String s) {
        if ((this.trace != TRACE.QUIET) && (this.tracer != null)) {
            this.tracer.traceMessage(s);
        }
    }

    /**
     * 
     */
    protected String formatElapsedTime() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat.format(new Date(getElapsedTime()));
    }

    /**
     * Calculates the time between the start timestamp and the current timestamp.
     * 
     * @return  Time between <code>currentTime</code> and <code>startTime</code>.
     * 
     */
    public long getElapsedTime() {

        long currentTime = System.currentTimeMillis();

        return (currentTime - this.startTime);
    }

    /**
     *  Sets the delimiter character for a record.
     * 
     * @param delimiter The character that splits a record into fields. 
     *                  E.g. "," will split the record 1,Frode,Sjovatsen into
     *                  1
     *                  Frode
     *                  Sjovatsen
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Gets the delimiter.
     * 
     * @return  <code>delimiter</code>
     * @see #setDelimiter(java.lang.String) 
     */
    public String getDelimiter() {
        return this.delimiter;
    }

    /**
     * Gets the index that is the matching key in a record. 
     * 
     * @return  <code>key</code>
     * @see #setKey(int key)
     */
    public int getKey() {
        return this.key;
    }

    /**
     * Gets the start timestamp. The <code>startTime</code> is set/should be set
     * by the methods that processes the files.
     * 
     * @return <code>startTime</code>
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Sets the index of the key in a delimitied string. <p>
     * E.g. <p>
     * 1,Frode,Sjovatsen<br>
     * 4,Ole,Olsen<p>
     * Index 0 will give the key 1 for the record "1,Frode,Sjovatsen".
     * 
     * @param key
     */
    public void setKey(int key) {
        this.key = key;
    }

    /**
     * Sets the level of tracing.
     * 
     * @param trace    Trace level.
     * @see TRACE
     */
    public void setTrace(TRACE trace) {
        this.trace = trace;
    }

    /**
     * Sets how big the ArrayList structures should initialy be. If the value is
     * greater than 0 it sets the <code>ensureCapacity()</code> on the ArrayList.
     *  
     * @param minRowCount Value to pass to ensureCapacity()
     * @see java.util.ArrayList#ensureCapacity(int) 
     */
    public void setMinRowCount(int minRowCount) {
        this.minRowCount = minRowCount;
    }

    /**
     * Sets a tracer object.
     * 
     * @param tracer   Tracer object.
     * @see Tracer
     */
    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }
}
