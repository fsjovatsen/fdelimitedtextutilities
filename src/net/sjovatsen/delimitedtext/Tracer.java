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

/**
 * This interface defines a tracer. Implement this interface to your needs of
 * tracing. E.g. one can implement a class that traces to the DSTrace screen on
 * eDirectory or to standard output.
 *
 * @author Frode Sjovatsen <frode@sjovatsen.net>
 */
public interface Tracer {

    /**
     * This method should output a the parameter string to what ever you want
     * to do a trace against. It could be a file, console etc...
     *
     * @param message   The string to trace.
     */
    public void traceMessage(String message);
}