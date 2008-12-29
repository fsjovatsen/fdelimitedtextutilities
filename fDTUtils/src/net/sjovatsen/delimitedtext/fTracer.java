package net.sjovatsen.delimitedtext;

/**
 * This interface defines a tracer. Implement this interface to your needs of
 * tracing. E.g. one can implement a class that traces to the DSTrace screen on
 * eDirectory or to standard output.
 * 
 * @author fsjovatsen
 * @version 1.0.0
 */
public interface fTracer {

    /**
     * This method should output a the parameter string to what ever you want
     * to do a trace against. It could be a file, console etc...
     * 
     * @param message   The string to trace.
     */
    public void traceMessage(String message);
    
}
