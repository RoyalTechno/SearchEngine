import java.io.*;
import java.util.*;

public class IdfData implements Serializable{
    public static String filename = "$idf.dat";
    private Map<String,Double> wordIdfMap;
    public IdfData(){
        wordIdfMap = new HashMap<>();
    }
    public static String getFilename() { return filename; }
    public double getWordIdfValue(String word) {
        //check that word is contained within map
        if (wordIdfMap.get(word)!=null)
            return wordIdfMap.get(word);
        //0.0 value for non-existing words
        return -1;
    }
    public void calculateIdf(){
        Map<String,Integer> freq = DataTool.getWordWebsiteFrequency();
        for (String word:freq.keySet()){
            double idf = Math.log(DataTool.getNumWebsitesVisited()/(1+(double)freq.get(word)))/Math.log(2);
            wordIdfMap.put(word,idf);
        }
    }

    public void storeIdf(String dirName){
        ObjectOutputStream oos = null;
        try{
            //store object stream of current class
            oos = new ObjectOutputStream(new FileOutputStream
                    (dirName+File.separator+filename));
            oos.writeObject(this);
        }catch (IOException ex){
            System.out.println("Error: Failed to open IDF data file.");
        } finally {
            if (oos!=null) {
                try {
                    oos.close();
                } catch (IOException ex) {
                    System.out.println("Error: Failed to close IDF data file.");
                }
            }
        }
    }
}
