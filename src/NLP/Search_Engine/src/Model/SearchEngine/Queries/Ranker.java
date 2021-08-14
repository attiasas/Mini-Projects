package NLP.Search_Engine.src.Model.SearchEngine.Queries;

import Model.SearchEngine.Index.Index;
import Model.SearchEngine.Index.Indexer;
import org.joda.time.Interval;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created By: Assaf Attias, On 31/12/2019
 * Description: Ranks the documents based on how they are relevant to a query
 */
public class Ranker
{



    /**
     * Represents a couple of a document and it's rating
     */
    public class DocRating
    {
        public int docId;
        public double rate;

        public DocRating(int docId, double rate) {
            this.docId = docId;
            this.rate = rate;
        }

    }

    private Index index; // the inverted index of the corpus
    HashMap<String, HashMap<Indexer.DocumentEntry, Integer>> queryLog; // the clickstream log- holds all the clicks on each document for each query in the clickstream log file

    private final double BM25_K = 1.2; //k coefficient for BM25 rating
    private final double BM25_B = 0.75; //b coefficient for BM25 rating
    private final double DATE_COEFFICIENT = 1; //the weight of a document being published lately
    private final double PERCENTAGE_OF_RANK_TO_ADD_FOR_A_CLICK = 0.1; //the weight of the added value of each click on a document in query
    private final double WEIGHT_OF_TITLE_CONTAINS_TERM = 0.1; //the weight of the added value of when term occure in the title of a document


    public Ranker (Index index)
    {
        this.index = index;
        queryLog = readClickStreamFile("/clickstream.txt");
    }


    /**
     * Creates a hashmap which maps query-> documentEntry -> numClicks based on a clickstream file
     * @param clickStreamFilePath the path of the clickstream file
     * @return a hashmap that describes number of clicks per document per query
     */
    public HashMap<String, HashMap<Indexer.DocumentEntry, Integer>> readClickStreamFile (String clickStreamFilePath)
    {

        HashMap<String, HashMap<Indexer.DocumentEntry, Integer>> res = new HashMap<>();
        if(index == null) return res;

        InputStream inputStream = getClass().getResourceAsStream(clickStreamFilePath);
        File clickStreamFile = new File(System.getProperty("user.dir") + "\\click.txt");

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            OutputStream outputStream = new FileOutputStream(clickStreamFile))
        {
            String s;
            if(clickStreamFile.exists()) clickStreamFile.delete();

            while ((s = reader.readLine()) != null)
            {
                outputStream.write((s+"\n").getBytes());
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        try(BufferedReader reader = new BufferedReader(new FileReader(clickStreamFile)))
        {
            reader.readLine(); //skip first line
            int i=0;

            String line;

            while((line = reader.readLine()) != null)
            {
                String[] split = line.split(",");
                int userId = Integer.parseInt(split[1]);
                String docNo = split[2];
                String query = split[3];

                Indexer.DocumentEntry docEntry = index.getDocumentEntryByDocNo(docNo);

                if(!res.containsKey(query))
                { //insert docEntry
                    HashMap<Indexer.DocumentEntry, Integer> docToNumClicks = new HashMap<>();
                    docToNumClicks.put(docEntry, 1);

                    res.put(query, docToNumClicks);
                }
                else
                { //queryLog contains query already
                    HashMap<Indexer.DocumentEntry, Integer> docToNumClicks = res.get(query);

                    if(!docToNumClicks.containsKey(docEntry))
                    {
                        docToNumClicks.put(docEntry, 1);
                    }
                    else
                    {
                        docToNumClicks.put(docEntry, docToNumClicks.get(docEntry) + 1); //increase doc clicks
                    }

                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return res;
    }






    /**
     * Go over all entries (terms). on each term accumulate the BM25 of each document that the term occure at. in the end sort the documents according to their rating.
     * @param entriesToRank
     * @param query
     * @param useClickStream
     * @return sorted doc rating list by rate of relevant documents to the given query
     */
    List<DocRating> rank(Set<Searcher.SearchEntry> entriesToRank, String query, boolean useClickStream)
    {
        HashMap<Integer, Double> docRatingMap = getDocRatingMap(entriesToRank);

        //add rating by date of the document
        addDateRatingToDoc(docRatingMap);

        //clickStream
        if(useClickStream)
        {
            addClickStreamRatingToDoc(docRatingMap, query);
        }


        //make sorted list
        List<DocRating> docRatingsList = new ArrayList<>();
        Set<Integer> docIds = docRatingMap.keySet();

        for (int currDocId : docIds)
        {
            DocRating currDocRating = new DocRating(currDocId, docRatingMap.get(currDocId));
            docRatingsList.add(currDocRating);
        }

        docRatingsList.sort(Comparator.comparing(docRating -> -docRating.rate));

        return docRatingsList;
    }


    /**
     * Create a HashMap of document ID to it's rating based on BM25 and the occurence of every term in the entriesToRank in the document's title
     * @param entriesToRank
     * @return a HashMap of every document and it's rank
     */
    HashMap<Integer, Double> getDocRatingMap (Set<Searcher.SearchEntry> entriesToRank)
    {
        HashMap<Integer, Double> docRatingMap = new HashMap<>();

        for(Searcher.SearchEntry sEntry : entriesToRank)
        {
            int[] sDocsId = sEntry.documentsId;

            for(int i=0; i<sDocsId.length; i++)
            {
                int currDocId = sDocsId[i];

                if(!docRatingMap.containsKey(currDocId))
                {
                    docRatingMap.put(currDocId, 0.0);
                }

                //here doc surely exists in docRating

                double docTermRating = rateTermInDoc(i, sEntry); //rate current searchEntry (term) in this doc
                double newDocTermRating = docRatingMap.get(currDocId) + docTermRating;

                docRatingMap.put(currDocId, newDocTermRating);
            }
        }

        return docRatingMap;
    }


    /**
     * Adds to every document in docRatingMap rating by the query and number of times the document was clicked
     * @param docRatingMap
     * @param query
     */
    private void addClickStreamRatingToDoc(HashMap<Integer, Double> docRatingMap, String query)
    {
        //queryLog maps query->docEntry->numClicks

        HashMap<Indexer.DocumentEntry, Integer> queryDocToNumClicks = queryLog.get(query);

        if(queryDocToNumClicks != null)
        {//query has documents clicked
            for( Indexer.DocumentEntry docEntry : queryDocToNumClicks.keySet())
            {//for each document update rank
                int docId = docEntry.id;
                double currDocRank = docRatingMap.get(docId);
                int docNumOfClicks = queryDocToNumClicks.get(docEntry);

                double withClickStreamRank = currDocRank + PERCENTAGE_OF_RANK_TO_ADD_FOR_A_CLICK*docNumOfClicks*currDocRank;
                //update to new rank
                docRatingMap.put(docId, withClickStreamRank);
            }
        }
    }


    /**
     * Adds rating based on how a new is the document
     * @param docRatingMap
     */
    private void addDateRatingToDoc(HashMap<Integer, Double> docRatingMap)
    {
        for(int docId : docRatingMap.keySet())
        {
            Indexer.DocumentEntry currDocEntry = index.getDocumentEntry(docId);
            double currRate = docRatingMap.get(docId);

            //add date rating
            try
            {
                Date todayDate = new Date(System.currentTimeMillis());

                String currDocDate = currDocEntry.date;

                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                Date docDate = dateFormatter.parse(currDocDate);

                //will get here only if currDoc has a legal date
                long differenceMilliSeconds = todayDate.getTime() - docDate.getTime();
                long differenceByDays = differenceMilliSeconds / 1000 / 60 / 60 / 24;

                double rateWithDateRating = currRate + DATE_COEFFICIENT*((double)1 / differenceByDays);

                docRatingMap.put(docId, rateWithDateRating);
            }
            catch(Exception e)
            {
                //Document does not have date in the necessary format (dd MMM yyyy)
            }

        }
    }


    /**
     * Rates how relevant is the word to the doc according to BM25 rating and if the term is in the document's title
     * @param docIdInd
     * @param termSEntry
     * @return A rating of how related is a term to a document
     */
    private double rateTermInDoc (int docIdInd, Searcher.SearchEntry termSEntry)
    {
        int docId = termSEntry.documentsId[docIdInd];

        Indexer.DocumentEntry currDocEntry = index.getDocumentEntry(docId);
        Indexer.DictionaryEntry currDictionaryEntry = index.getTermEntry(termSEntry.term);

        int tfTermQuery = termSEntry.queryTf; //the tf of the term in the query
        int tfTermDoc = termSEntry.tf[docIdInd]; //get tf of the term in document

        int numDocsInCorpus = index.getNumDocuments();
        int docFreqOfTerm = currDictionaryEntry.df; //in how many documents in the corpus this term was occured

        int docLength =  currDocEntry.docLength;
        double avgDocLength = index.getAvgDocLen();

        double rateBM25 = (double)tfTermQuery*( (BM25_K + 1)*tfTermDoc*Math.log( ((double)numDocsInCorpus+1) / docFreqOfTerm ) )  / (tfTermDoc + BM25_K*( 1 - BM25_B + BM25_B*(docLength / avgDocLength)));

        double rate = rateBM25;

        //rate by title

        if(currDocEntry.title.contains(termSEntry.term))
        {// if the title of the document contain the term- add rate
            rate += rate * WEIGHT_OF_TITLE_CONTAINS_TERM;
        }

        return rate;
    }



}