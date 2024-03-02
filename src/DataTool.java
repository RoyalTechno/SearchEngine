import java.io.Serializable;
import java.util.*;

public class DataTool{
    private static Map<String,Integer> wordWebsiteFrequency = new HashMap<String,Integer>();
    private static int numWebsitesVisited = 0;
    public static Map<String,Integer> getWordWebsiteFrequency(){ return new HashMap<>(wordWebsiteFrequency); }
    public static int getNumWebsitesVisited() { return numWebsitesVisited; }
    public static void addNumWebsitesVisited() { numWebsitesVisited++;}
    public static void addWordWebsiteFrequency(String word,int num){
        wordWebsiteFrequency.put(word,num);
    }
}
