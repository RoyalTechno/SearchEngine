import java.util.*;
public class SearchQuery {
    private static final List<WebsiteData> websites = LoadingTools.loadAllWebsites(ProjectTesterImp.getCrawlerStorageDirName());
    private static final IdfData idf = LoadingTools.loadIdfData(ProjectTesterImp.getCrawlerStorageDirName());
    private Map<String,Integer> initWordDict(String[] sentence){
        Map<String,Integer> wordDict = new HashMap<>();
        //map word duplicates and their appearance frequency
        for (String word:sentence){
            word = word.toLowerCase();
            //initialize a dictionary counter
            if (!wordDict.containsKey(word))
                wordDict.put(word,0);
            wordDict.put(word,wordDict.get(word)+1);
        }
        return wordDict;
    }
    private Object[] initNumQuery(Map<String,Integer> wordDict,int totalWords){
        if(idf==null)
            return null;
        double num,vectorNum,leftDenom =0.0;
        List<Double> numQuery = new ArrayList<>();
        List<String> wordList = new ArrayList<>();

        //loop through word dictionary to avoid duplicates
        for (String word:wordDict.keySet()) {
            //get precomputed idf of word
            num = idf.getWordIdfValue(word);
            //if no idf present disregard word from query
            if (num<0)
                continue;
            //keep only relevant words with an idf
            wordList.add(word);
            vectorNum = (Math.log(1+(double)wordDict.get(word)/totalWords)/Math.log(2))*num;
            //calculate query tf using wordDict word frequency
            numQuery.add(vectorNum);
            //calculate left denom for cosine similarity
            leftDenom += Math.pow(vectorNum,2);
        }
        return new Object[]{leftDenom,numQuery,wordList};
    }
    private void initCosineSimilarity(boolean boost,double leftDenom,List<Double> numQuery,List<String> wordList){

        //calculate cosine similarity with all directories
        for (WebsiteData w : websites){
            double numerator=0.0,rightDenom=0.0,cosSim;

            for (int n=0; n<wordList.size();n++){
                numerator += numQuery.get(n)*w.getWordTfIdfMap(wordList.get(n));
                rightDenom += Math.pow(w.getWordTfIdfMap(wordList.get(n)),2);
            }
            if(numerator<=0)
                cosSim=0.0;
            else {
                cosSim = numerator / (Math.pow(leftDenom, 0.5) * Math.pow(rightDenom, 0.5));
                if (boost)
                    cosSim = cosSim*w.getPageRank();
            }
            w.setScore(cosSim);
        }
    }
    public List<SearchResult> searchFor(String phrase,boolean boost,int x){
        List<SearchResult> rankedSearches = new ArrayList<>();
        String[] phraseList = phrase.split("\\s+");
        Object[] results = initNumQuery(initWordDict(phraseList),phraseList.length);
        if(results==null)
            return null;
        initCosineSimilarity(boost,(double)results[0],(List<Double>)results[1],(List<String>)results[2]);

        //sort list
        for (int j=0; j<x;j++) {
            int max = j;
            for (int k = j + 1; k < websites.size(); k++) {
                double num1 = Double.parseDouble(String.format("%.3f",websites.get(k).getScore()));
                double num2 = Double.parseDouble(String.format("%.3f",websites.get(max).getScore()));
                if (num1>num2 || (num1==num2 && (websites.get(k).compareTo(websites.get(max))<0)))
                    max = k;
            }
            //make copy of website and add to sorted list
            WebsiteData websiteCopy = new WebsiteData(websites.get(max));
            rankedSearches.add(websiteCopy);
            //rearrange websites list to keep track of top x scores seen
            if (max!=j){
                WebsiteData temp2 = websites.get(j);
                websites.set(j,websites.get(max));
                websites.set(max,temp2);
            }
        }
        return rankedSearches;
    }
}