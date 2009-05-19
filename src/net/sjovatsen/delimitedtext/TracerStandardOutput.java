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
 *  This class implements the Tracer interface. It traces to standard output.
 *
 * @author Frode Sjovatsen <frode@sjovatsen.net>
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