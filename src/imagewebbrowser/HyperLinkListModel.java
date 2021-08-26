/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author USER
 */
public class HyperLinkListModel implements ListModel {

    private HyperLinkList list;
    
    public HyperLinkListModel(HyperLinkList l){
        list = l;
    }
    
    @Override
    public int getSize() {
        return list.getSize();
    }

    @Override
    public Object getElementAt(int index) {
        return list.getHyperLinks()[index];
    }

    @Override
    public void addListDataListener(ListDataListener l) {
        
    }

    @Override
    public void removeListDataListener(ListDataListener l) {
        
    }

    void clear() {
        list = new HyperLinkList();
    }
    
}
