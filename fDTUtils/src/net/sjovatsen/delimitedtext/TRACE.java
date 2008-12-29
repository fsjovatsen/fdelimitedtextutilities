/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sjovatsen.delimitedtext;

/**
 *
 * @author fsjovatsen
 */
public enum TRACE {

    /**
     * The fTracer object don't trace anything at all.
     */
    QUIET,
    /**
     * The fTracer object traces some informasjon like when it starts, stats 
     * and when it's fininsh.
     */
    DEFAULT, 
    /**
     * The fTracer object traces everything. 
     */
    VERBOSE
}
