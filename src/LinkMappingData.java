import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class LinkMappingData implements Serializable {
    private static String filename = "$linkmap";
    private Map<String, String> linkMap;
    public LinkMappingData(){
        linkMap = new HashMap<>();
    }
    public Map<String, String> getLinkMap() { return new HashMap<>(linkMap); }
    public static String getFilename() { return filename;}
    public void updateLinkMap(String url){ linkMap.put(url,""+ DataTool.getNumWebsitesVisited()); }
    public void storeLinkMap(String dirName){
        ObjectOutputStream oos = null;
        try{
            //store link map into object stream
            oos = new ObjectOutputStream(new FileOutputStream
                    (dirName+ File.separator+filename));
            oos.writeObject(this);
        }catch (Exception ex){
            System.out.println("Error: Failed to open link mapping data file.");
        } finally {
            if (oos!=null){
                try {
                    oos.close();
                } catch (IOException ex){
                    System.out.println("Error: Failed to close link mapping data file.");
                }
            }
        }
    }
}
