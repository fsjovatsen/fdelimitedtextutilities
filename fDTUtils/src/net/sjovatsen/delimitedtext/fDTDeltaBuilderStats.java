/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sjovatsen.delimitedtext;

/**
 *
 * @author fsjovatsen
 */
public class fDTDeltaBuilderStats {

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
    public fDTDeltaBuilderStats() {
    }

    public fDTDeltaBuilderStats(int adds, int deletes, int modifies, int ndf, int odf, int nif) {

        this.addEvents = adds;
        this.deleteEvents = deletes;
        this.modifyEvents = modifies;
        this.ndfCount = ndf;
        this.odfCount = odf;
        this.nifCount = nif;
    }

    public fDTDeltaBuilderStats(int addThreshold, int deleteThreshold, int modifyThreshold, int nifThreshold) {
    }
}


