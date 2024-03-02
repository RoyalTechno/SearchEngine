import java.util.*;

public class ProjectTesterImp implements ProjectTester{
    private static String crawlerStorageDirName = "crawler-storage";
    private Crawler mainCrawl;
    public static String getCrawlerStorageDirName() {
        return crawlerStorageDirName;
    }

    @Override
    public void initialize() {
        mainCrawl = new Crawler(crawlerStorageDirName);
        //clear and make storage directory
        mainCrawl.clearDirectory(crawlerStorageDirName);
    }

    @Override
    public void crawl(String seedURL) {
        //run crawl
        mainCrawl.crawl(seedURL);
    }

    @Override
    public List<String> getOutgoingLinks(String url) {
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(crawlerStorageDirName);
        if (linkFiles!=null&&linkFiles.containsKey(url)) {
            //open website and return outgoing links
            WebsiteData web = LoadingTools.loadWebsiteData(linkFiles.get(url),crawlerStorageDirName);
            if (web != null)
                return web.getOutgoingLinks();
        }
        return null;
    }

    @Override
    public List<String> getIncomingLinks(String url) {
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(crawlerStorageDirName);
        if (linkFiles!=null&&linkFiles.containsKey(url)) {
            //open website and return incoming links
            WebsiteData web = LoadingTools.loadWebsiteData(linkFiles.get(url),crawlerStorageDirName);
            if (web != null)
                return web.getIncomingLinks();
        }
        return null;
    }

    @Override
    public double getPageRank(String url) {
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(crawlerStorageDirName);
        if (linkFiles!=null&&linkFiles.containsKey(url)) {
            //open website and return page rank
            WebsiteData web = LoadingTools.loadWebsiteData(linkFiles.get(url), crawlerStorageDirName);
            if (web != null)
                return web.getPageRank();
        }
        return -1;
    }

    @Override
    public double getIDF(String word) {
        //open idf stream
        IdfData idf = LoadingTools.loadIdfData(crawlerStorageDirName);
        //in case exception is thrown
        if(idf==null)
            return 0.0;
        double num = idf.getWordIdfValue(word);
        if (num<0)
            return 0.0;
        return num;
    }

    @Override
    public double getTF(String url, String word) {
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(crawlerStorageDirName);
        if (linkFiles!=null&&linkFiles.containsKey(url)) {
            //open website and get tf number
            WebsiteData web = LoadingTools.loadWebsiteData(linkFiles.get(url), crawlerStorageDirName);
            if (web != null)
                return web.getWordTf(word);
        }
        return 0.0;
    }

    @Override
    public double getTFIDF(String url, String word) {
        //get link to file mapping
        Map<String,String> linkFiles = LoadingTools.loadLinkMap(crawlerStorageDirName);
        if (linkFiles!=null&&linkFiles.containsKey(url)) {
            //open website and get tfidf number
            WebsiteData web = LoadingTools.loadWebsiteData(linkFiles.get(url), crawlerStorageDirName);
            if (web != null)
                return web.getWordTfIdfMap(word);
        }
        return 0.0;
    }

    @Override
    public List<SearchResult> search(String query, boolean boost, int X) {
        //create search class instance and return search result
        return new SearchQuery().searchFor(query,boost,X);
    }
}
