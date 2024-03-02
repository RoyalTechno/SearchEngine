import java.io.*;
import java.util.*;

public class LoadingTools {
    public static Map<String, String> loadLinkMap(String dirName){
        ObjectInputStream ois = null;
        try{
            //load link mapping for accessing urls specific files
            ois = new ObjectInputStream(new FileInputStream
                    (dirName+ File.separator+LinkMappingData.getFilename()));
            return ((LinkMappingData)ois.readObject()).getLinkMap();
        } catch (IOException ex){
            System.out.println("Error: Link mapping file not found.");
        } catch (ClassNotFoundException ex){
            System.out.println("Error: LinkMappingData class type casting failed.");
        } finally {
            if (ois!=null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println("Error: Failed to close link mapping file");
                }
            }
        }
        return null;
    }

    public static IdfData loadIdfData(String dirName){
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new FileInputStream
                    (dirName+File.separator+IdfData.getFilename()));
            return (IdfData)ois.readObject();
        } catch (IOException ex){
            System.out.println("Error: IDF data file not found.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Error: IDFData class type casting failed.");
        } finally {
            if (ois!=null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println("Error: Failed to close IDF data file");
                }
            }
        }
        return null;
    }
    public static WebsiteData loadWebsiteData(String fileName, String dirName){
        ObjectInputStream ois = null;
        try{
            ois = new ObjectInputStream(new FileInputStream
                    (dirName + File.separator + fileName));
            return (WebsiteData) ois.readObject();
        } catch (IOException ex){
            System.out.println("Error: This File was not found - "+fileName);
        } catch (ClassNotFoundException ex){
            System.out.println("Error: WebsiteData class type casting failed.");
        } finally {
            if (ois!=null) {
                try {
                    ois.close();
                } catch (IOException ex) {
                    System.out.println("Error: Failed to close website object file");
                }
            }
        }
        return null;
    }
    public static List<WebsiteData> loadAllWebsites(String dirName){
        List<WebsiteData> data = new ArrayList<>();
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(dirName);
        if (linkFiles!=null) {
            for (String filename:linkFiles.values()) {
                WebsiteData w = LoadingTools.loadWebsiteData(filename, dirName);
                if (w!=null)
                    data.add(w);
            }
        }
        return data;
    }
}
