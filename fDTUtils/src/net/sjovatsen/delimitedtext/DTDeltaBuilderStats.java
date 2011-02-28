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
 * This class holds statistics about a delta build and preforms varius
 * calculations on these stats.
 *
 * @author Frode Sjovatsen <frode AT sjovatsen.net>
 *
 * TODO: 
 */
public class DTDeltaBuilderStats {

    private int addEvents = 0;
    private int deleteEvents = 0;
    private int modifyEvents = 0;
    private int ndfCount = 0;
    private int odfCount = 0;
    private int nifCount = 0;
    private int addThreshold = 0;
    private int deleteThreshold = 0;
    private int modifyThreshold = 0;
    private int nifThreshold = 0;

    /**
     * Class constructor.
     */
    public DTDeltaBuilderStats() {
    }

    public DTDeltaBuilderStats(int addEvents, int deleteEvents, int modifyEvents, int ndfCount, int odfCount, int nifCount) {

        this.addEvents = addEvents;
        this.deleteEvents = deleteEvents;
        this.modifyEvents = modifyEvents;
        this.ndfCount = ndfCount;
        this.odfCount = odfCount;
        this.nifCount = nifCount;
    }

    public DTDeltaBuilderStats(int addThreshold, int deleteThreshold, int modifyThreshold, int nifThreshold) {

        this.addThreshold = addThreshold;
        this.deleteThreshold = deleteThreshold;
        this.modifyThreshold = modifyThreshold;
        this.nifThreshold = nifThreshold;
    }

    /**
     * Checks if add events count is lagrer than the threshold.
     * If the Threshold is 0 it's disabled
     *
     * @return true if exceeds, false if not found.
     */
    public boolean exceedsAddThreshold() {

        if (addThreshold == 0) return false;
        return (addEvents > addThreshold) ? true : false;
    }

    /**
     * Checks if delete events count is lagrer than the threshold.
     * If the Threshold is 0 it's disabled
     *
     * @return true if exceeds, false if not found.
     */
    public boolean exceedsDeleteThreshold() {

        if (deleteThreshold == 0) return false;
        return (deleteEvents > deleteThreshold) ? true : false;
    }

    /**
     * Checks if modify events count is lagrer than the threshold.
     * If the Threshold is 0 it's disabled
     *
     * @return true if exceeds, false if not found.
     */
    public boolean exceedsModifyThreshold() {

        if (modifyThreshold == 0) return false;
        return (modifyEvents > modifyThreshold) ? true : false;
    }

    
    public void setAddEvents(int addEvents) {
        this.addEvents = addEvents;
    }

    public void setAddThreshold(int addThreshold) {
        this.addThreshold = addThreshold;
    }

    public void setDeleteEvents(int deleteEvents) {
        this.deleteEvents = deleteEvents;
    }

    public void setDeleteThreshold(int deleteThreshold) {
        this.deleteThreshold = deleteThreshold;
    }

    public void setModifyEvents(int modifyEvents) {
        this.modifyEvents = modifyEvents;
    }

    public void setModifyThreshold(int modifyThreshold) {
        this.modifyThreshold = modifyThreshold;
    }

    public void setNdfCount(int ndfCount) {
        this.ndfCount = ndfCount;
    }

    public void setNifCount(int nifCount) {
        this.nifCount = nifCount;
    }

    public void setNifThreshold(int nifThreshold) {
        this.nifThreshold = nifThreshold;
    }

    public void setOdfCount(int odfCount) {
        this.odfCount = odfCount;
    }
}


