

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
 import java.awt.event.ActionEvent;
 import java.awt.event.ActionListener;
 import java.awt.event.MouseAdapter;
 import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
 import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
 import javax.swing.JComboBox;
 import javax.swing.JFrame;
 import javax.swing.JLabel;
 import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
 import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
 import javax.swing.JSplitPane;
 import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jsoup.nodes.Document;


public class ImageWebBrowserApp extends JFrame{

    private String appName;
    private String currentTitle ;
    private String currentContent ;
    
    HyperLinkList collection;
    HyperLinkListModel model;

    private ArrayList<Thread> loadImageThread;
    private JLabel lblStatus;
    private String fileSeparator;
    private String lineSeparator;
    private JSplitPane splitPane;
    private JList controlList;
    final JFrame myThis = this;
    
    JPanel imagePanel;
    JComboBox urlField;
    
    class SelectOrUnselectAllItemListener implements ActionListener{
        boolean setSelected;
        
        public SelectOrUnselectAllItemListener(boolean s){
            setSelected = s;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            Component [] C = imagePanel.getComponents();
            for (int i = 0; i< C.length; i++){
                JToggleButton button = (JToggleButton)C[i];
                button.setSelected(setSelected);
                if (setSelected){
                    button.setBorder(new LineBorder(Color.blue, 5));
                }else{
                    button.setBorder(new LineBorder(Color.black, 5));
                }
            }
        }
    }

    class SaveImageItemListener implements ActionListener{
        
        private boolean SaveAllItems = true;
        
        public SaveImageItemListener()
        {
            super();
        }
        
        public SaveImageItemListener(boolean SaveAllItems)
        {
            super();
            this.SaveAllItems = SaveAllItems;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            String folderName = fileUtility.dismissInvalidFoldername(currentTitle);
            File folder = new File(folderName);
            fileUtility.mkdirIfNotExist(folder);
            Component [] C = imagePanel.getComponents();
            boolean hasImage = false;
            for (int i = 0; i< C.length; i++){
                JToggleButton button = (JToggleButton)C[i];
                ImageIcon icon = (ImageIcon)button.getIcon();
                if (icon == null){
                    continue;
                }
                hasImage = true;
                BufferedImage bi = (BufferedImage) icon.getImage();
                String address = button.getToolTipText();
                String filename = address.substring(
                                        address.lastIndexOf("/") + 2, 
                                        address.length());
                String extension = filename.substring(filename.lastIndexOf(".") + 1);
                filename = fileUtility.dismissInvalidFilename(filename);
                File outputfile = new File(folder.getAbsolutePath() + 
                                           fileSeparator + 
                                           filename);
                try {
                    ImageIO.write(bi, extension, outputfile);
                    if (!SaveAllItems)
                    {
                        if ( outputfile.length() < 50*1024 )
                        {
                           outputfile.delete();
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(ImageWebBrowserApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (hasImage){
                fileUtility.openFolder(folder);
            }
        }
    }

    //Pop-up menu: save all image, view source code
    class ImagePanelMouseAdapter extends MouseAdapter{
        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)){
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem SelectAllItem = new JMenuItem("Select all images");
                JMenuItem UnselectAllItem = new JMenuItem("Unselect all images");
                JMenuItem SaveAllItem = new JMenuItem("Save all images");
                JMenuItem SaveLargeItem = new JMenuItem("Save large images");
                JMenuItem ViewSource = new JMenuItem("View source code");
                SelectAllItem.addActionListener(new SelectOrUnselectAllItemListener(true));
                UnselectAllItem.addActionListener(new SelectOrUnselectAllItemListener(false));
                SaveAllItem.addActionListener(new SaveImageItemListener());
                SaveLargeItem.addActionListener(new SaveImageItemListener(false));
                ViewSource.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        new SourceCodeFrame(currentContent);
                    }
                });
                popupMenu.add(SelectAllItem);
                popupMenu.add(UnselectAllItem);
                popupMenu.add(SaveAllItem);
                popupMenu.add(SaveLargeItem);
                popupMenu.add(ViewSource);
                popupMenu.show(imagePanel, e.getX(), e.getY());
            }
        }
    }
    
    class loadImage implements Runnable {
        String URL = "";
        JToggleButton toggleButton;

        public loadImage(String url, JToggleButton t){
            URL = url;
            toggleButton = t;
        }
        
        @Override
        public void run() {
            ImageIcon img = null;
            toggleButton.setBorder(new LineBorder(Color.black, 5));
            try {
                URLConnection URLConn = null;
                URLConn = (HttpURLConnection) (new URL(URL)).openConnection();
                URLConn.setRequestProperty("User-agent",
                        "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.3) Gecko/20100401");
                InputStream is = null ;
                is = URLConn.getInputStream();
                Image image = ImageIO.read(is);
                img = new ImageIcon(image);
                
                toggleButton.setIcon(img);
            } catch (IOException ex) {
                Logger.getLogger(ImageWebBrowserApp.class.getName()).log(Level.SEVERE, null, ex);
            }
            imagePanel.repaint();
        }
    }
    
    public void addImageHorse(String url, boolean refleshHyperlink){
        lblStatus.setText("parse: " + url);
        Document doc = HtmlUtility.getDocument(url);
        currentContent = doc.html();
        
        if (currentContent.contains("<title>301 Moved Permanently</title>"))
        {
            doc = HtmlUtility.getDocument(url + "/");
            currentContent = doc.html();
            //urlField.setSelectedItem(url + "/");
        }
        
        if (refleshHyperlink){
            ArrayList<HyperLink> list = HtmlUtility.getHyperLinks(url, doc);
            lblStatus.setText("hyper link counts: " + list.size());
            model = new HyperLinkListModel(new HyperLinkList(list));
            controlList = new JList(model);
            controlList.setCellRenderer(new HyperLinkRenderer());
            controlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            controlList.setMinimumSize(new Dimension(100, 300));
            splitPane.setTopComponent(new JScrollPane(controlList));
        }
        for (int i = 0; i< loadImageThread.size(); i++){
            loadImageThread.get(i).stop();
        }
        imagePanel.removeAll();
        loadImageThread = new ArrayList<Thread>();
        String title = HtmlUtility.getPageTitle(doc);
        currentTitle = title;
        myThis.setTitle(appName + currentTitle);
        ArrayList<String> JPG_Addresses = HtmlUtility.getImages(url, doc);
        lblStatus.setText("# of pics: " + JPG_Addresses.size());
        for (int i = 0 ; i< JPG_Addresses.size(); i++){
            final JToggleButton toggleButton = new JToggleButton();
            toggleButton.setToolTipText(JPG_Addresses.get(i));
            toggleButton.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (toggleButton.isSelected()){
                        toggleButton.setBorder(new LineBorder(Color.blue, 5));
                    }else{
                        toggleButton.setBorder(new LineBorder(Color.black, 5));
                    }
                }
            });
            imagePanel.add(toggleButton);
            Thread t = new Thread(new loadImage(JPG_Addresses.get(i), toggleButton));
            t.start();
            loadImageThread.add(t);
        }
    }
    
    class UrlFieldListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //Read URL from 'field'
            //Analysis the URL and check the hyperlink in it.
            JComboBox field = (JComboBox)e.getSource();
            String url = field.getSelectedItem().toString();
            addImageHorse(url, true);
            controlList.addMouseListener(new HyperLinkCtrlMouseAdapter());
        }
    }
    
    class HyperLinkCtrlMouseAdapter extends MouseAdapter{
        //User choose a hyper link
        //It will parse the link and find images in it.
        @Override
        public void mouseReleased(MouseEvent e) {
            JList target = (JList)e.getSource();
            int previousIndex = target.getSelectedIndex();
            if (SwingUtilities.isLeftMouseButton(e) ||
                SwingUtilities.isRightMouseButton(e)){
                target.setSelectedIndex(target.locationToIndex(e.getPoint()));
            }
            if (SwingUtilities.isLeftMouseButton(e) ||
                 SwingUtilities.isRightMouseButton(e) && 
                 previousIndex != target.getSelectedIndex() ){
                //User left click the address in Hyper Link List
                //=>Cancel the prior loading-image-thread
                HyperLink link = (HyperLink)(target.getSelectedValue());
                String address = link.getAddress();
                addImageHorse(address, false);
            }
            //empty line
            if (SwingUtilities.isRightMouseButton(e)) {
                final HyperLink selected = (HyperLink)(((JList)e.getSource()).getSelectedValue());
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem openImageBrowserItem = new JMenuItem("Open");
                JMenuItem openBrowserItem = new JMenuItem("Open in default browser");
                openImageBrowserItem.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        urlField.addItem(selected.getAddress());
                        urlField.setSelectedItem(selected.getAddress());
                        urlField.actionPerformed(e);
                    }
                });
                openBrowserItem.addActionListener(new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        HtmlUtility.openBrowser(selected.getAddress());
                    }
                });
                popupMenu.add(openImageBrowserItem);
                popupMenu.add(openBrowserItem);
                popupMenu.show(controlList, e.getX(), e.getY()); //and show the menu
            }
        }
    }
    
    public ImageWebBrowserApp(){
        appName = "image web browser2: ";
        currentTitle = "";
        currentContent = "";  
        loadImageThread = new ArrayList<Thread>();
        fileSeparator = System.getProperty("file.separator");
        lineSeparator = "\n";
        
        
        ArrayList<HyperLink> list = new ArrayList<HyperLink>();
        JPanel framePanel = new JPanel();
        framePanel.setLayout(new BorderLayout());
        String defaultAddress = getDefaultURL();
        String [] aryDefaultAddress = defaultAddress.split(lineSeparator);
        urlField = new JComboBox(aryDefaultAddress);
        urlField.setEditable(true);
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        lblStatus = new JLabel("status");
        
        //The following is in splitPane
        imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        //The following is default JList object,
        //it will be reconstructed while there is new URL request
        list.add(new HyperLink("", "Enter URL to browse image..."));
        collection = new HyperLinkList(list);
        model = new HyperLinkListModel(collection);
        controlList = new JList(model);
        controlList.setCellRenderer(new HyperLinkRenderer());
        controlList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        controlList.setMinimumSize(new Dimension(100, 300));
        //End of default JList
        JScrollPane imageScroller = new JScrollPane(imagePanel);
        imageScroller.getVerticalScrollBar().setUnitIncrement(16);
       
        imagePanel.addMouseListener(new ImagePanelMouseAdapter());
        splitPane.setTopComponent(new JScrollPane(controlList));
        splitPane.setBottomComponent(imageScroller);
        splitPane.setDividerLocation(150);          
        
        framePanel.add(urlField, BorderLayout.NORTH);
        framePanel.add(splitPane, BorderLayout.CENTER);
        framePanel.add(lblStatus, BorderLayout.SOUTH);
        
        urlField.setFont(new Font("Courier New", Font.PLAIN, 14));
        
        //events
        urlField.addActionListener(new UrlFieldListener());
        
        add(framePanel);
        setCommonUI_Setting();
    }
    
    private String getDefaultURL(){
        File f = new File(System.getProperty("user.dir") + 
                          fileSeparator + "address.txt");
        if (!f.exists()){
            try {
                f.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ImageWebBrowserApp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        String defaultAddress = fileUtility.ReadAllText(
                                    System.getProperty("user.dir") + 
                                    fileSeparator + "address.txt" );
        return defaultAddress;
    }
    
    private void setCommonUI_Setting(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private static void setWindowsLookFeel(){
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info:
                 javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            //do nothing
        } catch (InstantiationException ex) {
            //do nothing
        } catch (IllegalAccessException ex) {
            //do nothing
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            //do nothing
        }
    }
    
    public static void main(String[] args) {
        setWindowsLookFeel();
        fileUtility setting = new fileUtility();
        setting.LoadSetting("Setting.txt");
        Logger.getLogger(ImageWebBrowserApp.class.getName()).log(Level.OFF, 
                
                
        setting.getRegionData("[AbsoluteAddressFilter]")
                );
        new ImageWebBrowserApp();
    }
}
