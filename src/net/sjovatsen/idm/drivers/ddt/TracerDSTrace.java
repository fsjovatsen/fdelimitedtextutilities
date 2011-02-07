/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sjovatsen.idm.drivers.ddt;

import net.sjovatsen.delimitedtext.Tracer;

/**
 *
 * @author FSjovatsen
 */
public class TracerDSTrace implements Tracer {

    com.novell.nds.dirxml.driver.delimitedtext.Tracer tracer;

    public TracerDSTrace(com.novell.nds.dirxml.driver.delimitedtext.Tracer tracer) {
        this.tracer = tracer;
    }

    /**
     * Prints the message string to standard output.
     *
     * @param message   The string to trace.
     */
    public void traceMessage(String message) {
        tracer.traceMessage(message);
    }
}
