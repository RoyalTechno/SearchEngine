import java.io.*;
import java.util.*;

public class Crawler{
    private String dirName;
    public Crawler(String dirName){
        this.dirName = dirName;
    }

    public void clearDirectory(String dirPath){
        File path = new File(dirPath);
        File[] files = path.listFiles();

        if (files != null){
            // Loop through each file in the directory
            for (File f:files) {
                if (f.isFile()) {
                    //delete all files in the directory
                    File curFile = new File(f.getAbsolutePath());
                    curFile.delete();
                } else if (f.isDirectory())
                    // recursively call the function for directory within a directory
                    clearDirectory(f.getAbsolutePath());
            }
        }
        //delete empty directory
        path.delete();
        //create new storage directory
        File storageDir = new File(dirName);
        storageDir.mkdir();
    }

    private int visitLinks(Queue<String> linkList,LinkMappingData linkData){
        int count = 0;
        while (!linkList.isEmpty()) {
            //set current link to crawl
            String curLink = linkList.peek();
            //check that current link hasn't already been viewed
            if (linkData.getLinkMap().containsKey(curLink)) {
                linkList.remove();
                continue;
            }

            //create new instance and store useful data
            WebsiteData web = new WebsiteData();
            try {
                web.storePageData(curLink, dirName);
            }catch (IOException ex){
                System.out.println("Error: Web requester failed for "+curLink+" putting it back into queue");
                linkList.add(curLink);
                continue;
            }
            count++;
            //add link to links mapping
            linkData.updateLinkMap(curLink);
            //increment website file naming scheme for old links mapping
            DataTool.addNumWebsitesVisited();
            linkList.remove();

            //add new links to link queue
            linkList.addAll(web.getOutgoingLinks());
        }
        return count;
    }
    private Set<String> storeIncomingLinks(String url, Map<String, String> map,WebsiteData curWebsite){
        Set<String> linkOutgoing = new HashSet<>();

        for (String w:curWebsite.getOutgoingLinks()){
            WebsiteData websiteIn = LoadingTools.loadWebsiteData(map.get(w), dirName);
            if (websiteIn == null)
                continue;
            websiteIn.addIncomingLink(url);
            websiteIn.storeWebsite(dirName, map.get(w));
            //hashset to create pagerank adj matrix
            linkOutgoing.add(w);
        }
        return linkOutgoing;
    }
    private List<Double> updateProbMatrix(Map<String, String> linkMap, Set<String> linkOut){
        int count1 = 0;
        double num;
        List<Double> adjMatrix = new ArrayList<>();

        //create NxN matrix
        for (String url2:linkMap.keySet()){
            //check if url2 links to url
            if (linkOut.contains(url2)){
                adjMatrix.add(1.0);
                count1++;
            } else {
                adjMatrix.add(0.0);
            }
        }

        //modify adjacency matrix
        for (int i=0; i<adjMatrix.size(); i++){
            if (count1 == 0) //row is only 0s -> replace by 1/# columns
                num = 1.0/linkMap.size();
            else if (adjMatrix.get(i)==1) //replace 1s with 1/# of 1s
                num = 1.0/count1;
            else
                num = 0;
            //calculate final probability value
            adjMatrix.set(i,num*(1-0.1)+0.1/linkMap.size());
        }
        return adjMatrix;
    }
    private void initPageRank(Map<String, String> map,List<List<Double>> probMatrix, List<List<Double>> vectorX1){
        List<List<Double>> vectorX2;
        WebsiteData curWebsite;
        int index=0;

        while (true){
            //multiply the vector by the probability matrix
            vectorX2 = MatrixTools.multMatrix(vectorX1,probMatrix);

            //iterate until matrix euclidean distance is <= 0.0001
            if (MatrixTools.euclideanDistance(vectorX1,vectorX2)<=0.0001)
                break;
            //update to the previous vector
            vectorX1 = vectorX2;
        }
        //initialize page rank value for each instance
        for (String urlKey:map.keySet()){
            curWebsite = LoadingTools.loadWebsiteData(map.get(urlKey),dirName);
            if (curWebsite==null)
                continue;
            curWebsite.storePageRankValue(vectorX2.get(0).get(index));
            curWebsite.storeWebsite(dirName,map.get(urlKey));
            index++;
        }
    }

    public int crawl(String seed){
        //initialize link queue
        Queue<String> linkList = new ArrayDeque<>();
        linkList.add(seed);
        //create link mapping and idf instance for current crawl
        LinkMappingData linkData = new LinkMappingData();
        IdfData idf = new IdfData();

        //clear and make storage directory
        clearDirectory(dirName);

        //go through all links from seed
        int numOfLinks = visitLinks(linkList,linkData);

        //store idf object and link mapping object
        idf.calculateIdf();
        idf.storeIdf(dirName);
        linkData.storeLinkMap(dirName);
        Map<String,String> linkMap = linkData.getLinkMap();

        //initialize for calculations
        int count2 = 0;
        List<Double> vectorNum = new ArrayList<>();
        List<List<Double>> probMatrix = new ArrayList<>();
        List<List<Double>> vectorX1 = new ArrayList<>();
        WebsiteData curWebsite;
        //add list within vectorX1 list
        vectorNum.add(1.0);
        vectorX1.add(vectorNum);

        for (String url:linkMap.keySet()){
            curWebsite = LoadingTools.loadWebsiteData(linkMap.get(url),dirName);
            if (curWebsite==null)
                continue;
            //initialize incoming links
            Set<String> curLinkOutgoing = storeIncomingLinks(url,linkMap,curWebsite);
            //initialize tfidf calculations
            curWebsite.storeTfIdf(idf,dirName,linkMap.get(url));

            //update probability matrix as we iterate
            probMatrix.add(updateProbMatrix(linkMap,curLinkOutgoing));

            //create vector for pagerank matrix multiplication
            if (count2< linkMap.size()-1)
                vectorX1.get(0).add(0.0);
            count2++;
        }
        initPageRank(linkMap,probMatrix,vectorX1);
        return numOfLinks;
    }
}
