/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

public class HyperLinkRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(
                            JList list, Object value, int index, 
                            boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)super.getListCellRendererComponent(
                                                       list, 
                                                       value, 
                                                       index, 
                                                       isSelected, 
                                                       cellHasFocus);
            HyperLink link = null;
            if (value instanceof HyperLink){
                link = (HyperLink)value;
                //label.setData(link);
                label.setFont(new Font("微軟正黑體", Font.PLAIN, 16));
                label.setText(link.getName());
                //Logger.getLogger(ImageWebBrowserApp.class.getName()).log(Level.INFO, "label" + link.getName(), "");
                label.setToolTipText(link.getAddress());
            }
            //empty line
            if (link.getViewed()){
                label.setForeground(Color.RED);
            }else{
                label.setForeground(Color.BLUE);
            }
            //empty line
            if (link != null && isSelected){
                link.setViewed(true);
            }
            return label;
        }
}

