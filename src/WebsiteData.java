import java.io.*;
import java.util.*;

public class WebsiteData implements Serializable, SearchResult, Comparable<WebsiteData>{
    private Map<String,Double> wordTfMap;
    private double pageRankValue;
    private double score;
    private Map<String,Double> wordTfIdfMap;
    private String title;
    private List<String> outgoingLinks;
    private List<String> incomingLinks;
    //copy constructor
    public WebsiteData(WebsiteData web){
        score = web.score;
        title = web.title;
        pageRankValue = web.pageRankValue;
        outgoingLinks = web.outgoingLinks;
        incomingLinks = web.incomingLinks;
        wordTfIdfMap = web.wordTfIdfMap;
        wordTfMap = web.wordTfMap;
    }
    public WebsiteData(){
        pageRankValue = 0.0;
        score = 0.0;
        wordTfMap = new HashMap<>();
        wordTfIdfMap = new HashMap<>();
        title = "";
        outgoingLinks = new ArrayList<>();
        incomingLinks = new ArrayList<>();
    }
    public String getTitle() { return title; }
    public void storePageRankValue(double value){ pageRankValue = value; }
    public List<String> getOutgoingLinks() { return new ArrayList<>(outgoingLinks); }
    public List<String> getIncomingLinks() { return new ArrayList<>(incomingLinks); }
    public void addIncomingLink(String url) { incomingLinks.add(url); }
    public double getPageRank() { return pageRankValue; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score=score; }
    public double getWordTf(String word) {
        //check that word is contained within map
        if (wordTfMap.get(word)!=null)
            return wordTfMap.get(word);
        //0.0 value for non-existing words
        return 0.0;
    }
    public double getWordTfIdfMap(String word) {
        //check that word is contained within map
        if (wordTfIdfMap.get(word)!=null)
            return wordTfIdfMap.get(word);
        //0.0 value for non-existing words
        return 0.0;
    }
    private String storePageLinks(String data,String currentLink,Set<String> duplicateLinks){
        int linkStart,linkEnd,parentLinkEnd;
        String link,parentLink;

        linkStart = data.indexOf("href=") + 6;
        linkEnd = data.indexOf("\">");
        link = data.substring(linkStart,linkEnd);
        // in case of relative link found, splice parent link and combine with relative link
        if (link.startsWith(".")) {
            parentLinkEnd = currentLink.lastIndexOf("/");
            parentLink = currentLink.substring(0,parentLinkEnd);
            link = parentLink+link.substring(1);
        }
        // check for duplicate links
        if (!duplicateLinks.contains(link)){
            outgoingLinks.add(link);
            return link;
        }return null;
    }

    private void storeTitle(String data){
        int titleStart,titleEnd;
        titleStart = data.indexOf("<title>")+7;
        titleEnd = data.indexOf("</title>");
        title = data.substring(titleStart,titleEnd);
    }

    private void extractData(String currentLink) throws IOException{
        String[] website = WebRequester.readURL(currentLink).split("\\s+");
        Map<String,Integer> pageWords = new HashMap<>();
        Set<String> duplicateLinks = new HashSet<>();
        int pageWordCount=0;
        String link;
        boolean inParagraph = false;

        //Loop through all lines of the website's data
        for (String data:website) {
            // Disable storing word data if end paragraph tag is found
            if (data.contains("</p>")) {
                inParagraph = false;
                continue;
            }
            // Store current line if in between paragraph tags
            if (inParagraph) {
                if (!pageWords.containsKey(data))
                    pageWords.put(data, 0);
                pageWords.put(data, pageWords.get(data) + 1);
                pageWordCount++;
            }
                // Store links if links start tag is found
            else if (data.contains("href=")) {
                link = storePageLinks(data, currentLink, duplicateLinks);
                if(link!=null)
                    duplicateLinks.add(link);
            }
                //store title when title tag is found
            else if (data.contains("<title>"))
                storeTitle(data);
            // Enable word data storing
            if (data.contains("<p>"))
                inParagraph = true;
        }
        // store tf calculations
        calculateTf(pageWords,pageWordCount);
    }

    public void storePageData(String curLink, String dir) throws IOException{
        extractData(curLink);
        storeWebsite(dir,""+DataTool.getNumWebsitesVisited());
    }

    private void calculateTf(Map<String,Integer> wordData,int websiteWordCount) {
        //store tf of each word
        for (String word : wordData.keySet()) {
            wordTfMap.put(word, (double) wordData.get(word) / websiteWordCount);
            //store word appearance frequency among all websites for idf calc
            Map<String, Integer> tempMap = DataTool.getWordWebsiteFrequency();
            if (!tempMap.containsKey(word)) {
                //update the private map and the
                DataTool.addWordWebsiteFrequency(word, 1);
            }else
                DataTool.addWordWebsiteFrequency(word, tempMap.get(word) + 1);
        }
    }
    public void storeTfIdf(IdfData idf,String c,String filename){
        for (String word:DataTool.getWordWebsiteFrequency().keySet()){
            if(wordTfMap.get(word)==null)
                continue;
            double tfIdf = Math.log(1+wordTfMap.get(word))/Math.log(2)*(idf.getWordIdfValue(word));
            wordTfIdfMap.put(word,tfIdf);
        }
        this.storeWebsite(c,filename);
    }

    public void storeWebsite(String dir,String filename){
        ObjectOutputStream oos = null;
        try{
            oos = new ObjectOutputStream(
                    new FileOutputStream(dir+File.separator+filename));
            oos.writeObject(this);
        }catch (IOException ex){
            System.out.println("Error: Failed to open website data file.");
        } finally {
            if (oos!=null){
                try{
                    oos.close();
                } catch (IOException ex){
                    System.out.println("Error: Failed to close website data file.");
                }
            }
        }
    }

    public int compareTo(WebsiteData w) {
        return title.compareTo(w.getTitle());
    }
}
