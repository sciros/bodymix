package net.sciros.bodymix.userstate;

import java.util.HashMap;
import java.util.Map;

public class RunningSession {
    private Map<String,Object> attributes;
    public Map<String,Object> getAttributes () { return this.attributes; }
    public void setAttributes (Map<String,Object> value) { this.attributes = value; }
    
    private static RunningSession instance = new RunningSession();
    
    private RunningSession () {
        attributes = new HashMap<String,Object>();
    }
    
    public static RunningSession getInstance () {
        return instance;
    }
}
