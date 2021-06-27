/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagewebbrowser;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

class fileUtility {
    
    class RegionData implements Comparable {
        String name;
        public ArrayList settings = new ArrayList();
        
        public RegionData(String n){
            name = n;
        }
        
        public boolean equals(RegionData r){
            if (name.equals(r)){
                return true;
            }
            return false;
        }

        @Override
        public int compareTo(Object o) {
            return name.compareTo(((RegionData)o).name);
        }
    }
    
    TreeSet<RegionData> Region = new TreeSet<RegionData>();
    public void LoadSetting(String path){
        BufferedReader br = null;
         try {
            RegionData CurrentRegion = new RegionData("UntitledRegion");
            String CurrentLine;
            br = new BufferedReader(new FileReader(path));
            while ((CurrentLine = br.readLine()) != null) {
                //content += CurrentLine + "\n";
                if (CurrentLine.startsWith("[") && 
                     !CurrentLine.startsWith("[/") && 
                      !Region.contains(new RegionData(CurrentLine))){
                    CurrentRegion = new RegionData(CurrentLine);
                    Region.add(CurrentRegion);
                }else if (CurrentLine.startsWith("[/")){
                    CurrentRegion = new RegionData("UntitledRegion");
                }
                //empty line
                CurrentRegion.settings.add(CurrentLine);
            }
            if (br != null){
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String getRegionData(String r){
        Iterator it = Region.iterator();
        String ret = "";
        while(it.hasNext())
        {
            RegionData region = (RegionData)it.next();
            if (region.name.equals(r)){
                for (int i = 0 ; i< region.settings.size(); i++){
                    ret += region.settings.get(i) + "\r\n";
                }
                return ret;
            }
        }
        return null;
    }
    
    public static void mkdirIfNotExist(File f){
        if (!f.exists()){
            f.mkdir();
        }
    }
    
    public static void openFolder(File f){
        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(f);
        } catch (IOException e2) {
            //do nothing
        }
    }
    
    public static String dismissInvalidFoldername(String s){
        return Pattern.compile("[\\\\/:*?\"'<>|.]").matcher(s).replaceAll("_"); 
    }
        
    public static String dismissInvalidFilename(String s){
        return Pattern.compile("[\\\\/:*?\"'<>|]").matcher(s).replaceAll("_"); 
    }
    
    public static String ReadAllText(String path){
        String content = "";
        BufferedReader br = null;
         try {
            String CurrentLine;
            br = new BufferedReader(new FileReader(path));
            while ((CurrentLine = br.readLine()) != null) {
                content += CurrentLine + "\n";
            }
            if (br != null){
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content;
    }
}
