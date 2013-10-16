package net.sciros.bodymix.domain;

import net.sciros.bodymix.R;

public enum AlbumType {
    BODYATTACK ("BODYATTACK", "ATTACK", R.drawable.bodyattack_icon_1, R.drawable.gradient_yellow_1),
    //BODYBALANCE ("BODYBALANCE", "BALANCE", R.drawable.bodyvive_icon_1), //same as bodyflow
    BODYCOMBAT ("BODYCOMBAT", "COMBAT", R.drawable.bodycombat_icon_1, R.drawable.gradient_green_1),
    BODYFLOW ("BODYFLOW", "FLOW", R.drawable.bodyflow_icon_1, R.drawable.gradient_gray_1),
    BODYJAM ("BODYJAM", "JAM", R.drawable.bodyjam_icon_1, R.drawable.gradient_yellow_1),
    BODYPUMP ("BODYPUMP", "PUMP", R.drawable.bodypump_icon_1, R.drawable.gradient_red_1),
    BODYVIVE ("BODYVIVE", "VIVE", R.drawable.bodyvive_icon_1, R.drawable.gradient_gray_1),
    CXWORX ("CXWORX", "CX", R.drawable.cxworx_icon_1, R.drawable.gradient_gray_1),
    GRIT_CARDIO ("GRIT CARDIO", "CARDIO", R.drawable.rate_star_med_off, R.drawable.gradient_gray_1),
    GRIT_PLYO ("GRIT PLYO", "PLYO", R.drawable.rate_star_med_off, R.drawable.gradient_gray_1),
    GRIT_STRENGTH ("GRIT STRENGTH", "STRENGTH", R.drawable.rate_star_med_off, R.drawable.gradient_gray_1),
    RPM ("RPM", "RPM", R.drawable.rpm_icon_1, R.drawable.gradient_gray_1),
    SHBAM ("SH'BAM", "BAM", R.drawable.shbam_icon_1, R.drawable.gradient_gray_1);
    
    private String typeAsString;
    public String getType () { return this.typeAsString; }
    public void setType (String value) { this.typeAsString = value; }
    
    private String shortHand;
    public String getShortHand () { return this.shortHand; }
    public void setShortHand (String value) { this.shortHand = value; }
    
    private int iconResourceId;
    public int getIconResourceId () { return this.iconResourceId; }
    public void setIconResourceId (int value) { this.iconResourceId = value; }
    
    private int backgroundResourceId;
    public int getBackgroundResourceId () { return this.backgroundResourceId; }
    public void setBackgroundResourceId (int value) { this.backgroundResourceId = value; }
    
    private AlbumType (String typeAsString, String shortHand, int iconResourceId, int bgResourceId) {
        this.typeAsString = typeAsString;
        this.shortHand = shortHand;
        this.iconResourceId = iconResourceId;
        this.backgroundResourceId = bgResourceId;
    }
}
