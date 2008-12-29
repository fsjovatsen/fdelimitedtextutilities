package net.sjovatsen.delimitedtext;

/**
 *  This class implements the fTracer interface. It traces to standard output.
 * 
 * @author fsjovatsen
 * @version 1.0.0
 * @see fTracer
 */
public class fTracerStandardOutput implements fTracer {
    
    public fTracerStandardOutput() {
        
    }

    /**
     * Prints the message string to standard output.
     * 
     * @param message   The string to trace.
     */
    public void traceMessage(String message) {
        System.out.println(message);
    }
}
