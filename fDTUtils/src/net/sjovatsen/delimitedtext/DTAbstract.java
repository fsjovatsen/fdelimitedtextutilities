package net.sjovatsen.delimitedtext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This is the abstract class for all the fDT* classes. 
 * It provides some data for:
 * <ul>
 *  <li>time messering
 *  <li>parsing a delimited record
 *  <li>datastructures performance
 *  <li>tracing the build
 * </ul>
 * 
 * @author fsjovatsen
 * @version 1.0.0
 * 
 * TODO: Add methods for formating elapsed time.
 */
public abstract class DTAbstract {

    protected long _startTime;
    protected long _currentTime;
    protected int _key;
    protected String _delimiter;
    protected TRACE _trace;
    protected int _minRowCount;
    protected Tracer _tracer;

   /**
     * Class constructor.
     */
    public DTAbstract() {
        this._startTime = 0;
        this._currentTime = 0;
        this._key = 0;
        this._delimiter = ",";
        this._trace = TRACE.DEFAULT;
        this._tracer = null;
        this._minRowCount = 0;
    }
    
     /**
     * Sends a verbose message to the tracer.
     * 
     * @param s Message to trace.
     */
    protected void trace(String s) {
        if ((_trace == TRACE.VERBOSE) && (_tracer != null)) {
            _tracer.traceMessage(s);
        }
    }

    /**
     * Sends a informational message to the tracer.
     * 
     * @param s Message to trace.
     */
    protected void message(String s) {
        if ((_trace != TRACE.QUIET) && (_tracer != null)) {
            _tracer.traceMessage(s);
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
     * @return  Time between _currentTime and _startTime.
     * 
     * TODO: Do we need _currentTime?
     */
    protected long getElapsedTime() {
    
        long currentTime = System.currentTimeMillis();
        
        return (currentTime - this._startTime);
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
        this._delimiter = delimiter;
    }

    /**
     * Gets the delimiter.
     * 
     * @return  _delimiter
     * @see #setDelimiter(java.lang.String) 
     */
    public String getDelimiter() {
        return _delimiter;
    }

    /**
     * Gets the index that is the matching key in a record. 
     * 
     * @return  _key.
     * @see #setKey(int _key)
     */
    public int getKey() {
        return _key;
    }
    
//    /**
//     * Gets the start timestamp.
//     * @return _startTime
//     */
//    public long getStartTime() {
//        return _startTime;
//    }

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
        this._key = key;
    }

    /**
     * Sets the level of tracing.
     * 
     * @param _trace    Trace level.
     * @see TRACE
     */
    public void setTrace(TRACE _trace) {
        this._trace = _trace;
    }
/**
 * Sets how big the ArrayList structures should initialy be. If the value is
 * greater than 0 it sets the ensureCapacity() on the ArrayList.
 *  
 * @param _minRowCount Value to pass to ensureCapacity()
 * @see java.util.ArrayList#ensureCapacity(int) 
 */
    public void setMinRowCount(int _minRowCount) {
        this._minRowCount = _minRowCount;
    }

    /**
     * Sets a tracer object.
     * 
     * @param _tracer   Tracer object.
     * @see Tracer
     */
    public void setTracer(Tracer _tracer) {
        this._tracer = _tracer;
    }
}
