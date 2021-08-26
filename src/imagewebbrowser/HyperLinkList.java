/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

public class HyperLinkList {
    private ArrayList<HyperLink> list;
    
    public HyperLinkList(){
        list = new ArrayList<HyperLink>();
    }
    
    public HyperLinkList(ArrayList<HyperLink> l){
        list = l;
    }
    
    public int getSize(){
        return list.size();
    }
    
    public HyperLink[] getHyperLinks(){
        HyperLink[] aryLinks = new HyperLink[list.size()];
        return list.toArray(aryLinks);
    }
    
}
