package net.sjovatsen.delimitedtext;

/**
 *  This class implements the Tracer interface. It traces to standard output.
 * 
 * @author fsjovatsen
 * @version 1.0.0
 * @see Tracer
 */
public class TracerStandardOutput implements Tracer {
    
    public TracerStandardOutput() {
        
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
