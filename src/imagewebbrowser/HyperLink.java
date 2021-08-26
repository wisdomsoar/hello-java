/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


public class HyperLink {
    private String address;
    private String name;
    private String content;
    private boolean viewed = false;
    
    public HyperLink(String a, String n){
        address = a;
        name = n;
    }
    
    public String getAddress(){
        return address;
    }
    
    public String getName(){
        return name;
    }
    
    public void setViewed(boolean ved){
        viewed = ved;
    }
    
    public boolean getViewed(){
        return viewed;
    }
}
