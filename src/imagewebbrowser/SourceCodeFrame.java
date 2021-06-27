/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagewebbrowser;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author USER
 */
public class SourceCodeFrame extends JFrame {
    public SourceCodeFrame(String content){
        getContentPane().add(new JScrollPane(
                new JTextArea(content)
                ));
        setSize(800, 600);
        this.setLocationRelativeTo(null);
        setVisible(true);
    }
}
