package imagewebbrowser;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class HtmlUtility {
    static public Document getDocument(String strUrl){
        Document doc = null;
        //try {
            //doc = Jsoup.connect(strUrl).timeout(7*1000).get();
            doc = Jsoup.parse(fetchHtml(strUrl));
        //} catch (IOException ex) {
        //    Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, null, ex);
        //}
        return doc;
    }
    
    static public String fetchHtml(String strUrl){
        URL url;
        URLConnection con = null;
        InputStream is = null;
        try {
            url = new URL(strUrl);
            con = url.openConnection();
            con.setConnectTimeout(10*1000);
            con.setReadTimeout(10*1000);
            is = con.getInputStream();
        } catch (MalformedURLException ex) {
            return "";
        } catch (IOException ex) {
            return "";
        }
        int ptr = 0;
        StringBuilder buffer = new StringBuilder();
        try {
            while ((ptr = is.read()) != -1) {
                buffer.append((char)ptr);
            }
        } catch (IOException ex) {
            return "";
        }
        try {
            return new String(buffer.toString().getBytes("ISO-8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    static public ArrayList<String> getImages(String uri, Document doc){
        doc.setBaseUri(uri);
        ArrayList<String> ret = new ArrayList<String>();
        
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            if ( (link.attr("href")).contains(".jpg")  )
            {
                //to do: hard code
                String abs_href = link.attr("abs:href");
                    Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, 
                            "\n jpg in link " + abs_href );
                ret.add(abs_href);
                
            }
        }
        
        
        
        Elements images = doc.select("img");
        for (Element image : images) {
            //String url = image.attr("abs:src");
            ret.add(image.attr("abs:src"));
            if (!image.attr("file").equals("")){
                ret.add(image.attr("file"));
            }
        }
        /* */
        
        
        return ret;
    }
    
    static public ArrayList<HyperLink> getHyperLinks(String uri, Document doc){
        doc.setBaseUri(uri);
        ArrayList<HyperLink> ret = new ArrayList<HyperLink>();
        Elements links = doc.select("a[href]");
        
        for (Element link : links) {
            Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, 
                             "\nlink: (" + link.attr("href") + ")", "");
            String abs_href = link.attr("abs:href");
            if (HtmlUtility.trim(link.text(), 35).equals("")){
                continue;
            }
            if (abs_href.length() <= 7){
                continue;
            }
            if (abs_href.equals("#")){
                continue;
            }
            if (abs_href.contains("*http://")){
                abs_href= abs_href.substring(abs_href.indexOf("*http://" ) + 1);
            }
            HyperLink hyperlink = new HyperLink(abs_href, 
                                                HtmlUtility.trim(link.text(), 35));
            ret.add(hyperlink);         
        }
        return ret ;
    }
    
    static public ArrayList<String> getImages(String strUrl){
        ArrayList<String> ret = new ArrayList<String>();
        Document doc = null;
        try {
            doc = Jsoup.connect(strUrl).get();
        } catch (IOException ex) {
            Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        Elements images = doc.select("img");
        for (Element image : images) {
            ret.add(image.attr("abs:src"));
        }
        return ret;
    }
    
    static public ArrayList<HyperLink> getHyperLinks(String strUrl){
        ArrayList<HyperLink> ret = new ArrayList<HyperLink>();
        Document doc = null;
        try {
            doc = Jsoup.connect(strUrl).get();
        } catch (IOException ex) {
            Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        Elements links = doc.select("a[href]");
        Logger.getLogger(HtmlUtility.class.getName()).log(Level.SEVERE, 
        "\nLinks: (" + links.size() + ")", "");
        for (Element link : links) {
            if (HtmlUtility.trim(link.text(), 35).equals("")){
                continue;
            }
            if (link.attr("abs:href").length() <= 7){
                continue;
            }
            if (link.attr("href").equals("#")){
                continue;
            }
            HyperLink hyperlink = new HyperLink(link.attr("abs:href"), 
                                                HtmlUtility.trim(link.text(), 35));
            ret.add(hyperlink);                                 
        }
        return ret ;
    }
    
    static public String getPageTitle(Document doc){
        return doc.title();
    }
    
    private static String trim(String s, int width) {
        if (s.length() > width){
            return s.substring(0, width-1) + ".";
        }else{
            return s;
        }
    }
    
    static public void openBrowser(String url){
        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        try {
            java.net.URI uri = new java.net.URI(url);
            desktop.browse(uri);
        }
        catch ( Exception e ) {
            System.err.println( e.getMessage() );
        }
    }
}
