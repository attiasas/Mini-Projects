package NLP.Search_Engine.src.Model.SearchEngine.Queries;

import Model.SearchEngine.Index.Index;
import Model.SearchEngine.Index.Indexer;
import Model.SearchEngine.Index.Parse.TermParser;
import com.medallia.word2vec.Word2VecModel;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created By: Assaf Attias
 * On 31/12/2019
 * Description:     Capable of Searching queries from a given index
 *                  * Single Query
 *                  * Multiple Queries
 *
 * Options:         * Semantic
 *                  * ClickStream
 *                  * Saving results to file
 */
public class Searcher
{
    private Index index;
    private TermParser parser;
    private Ranker ranker;

    private boolean semantic;
    private boolean clickStream;

    private int numOfDocs;

    private Word2VecModel model;
    public static final String MODEL_PATH = "/word2vec.txt";
    private final int MAX_SEMANTIC_RETURN = 2;
    private com.medallia.word2vec.Searcher semanticSearcher;

    private final int MAX_DOCS_TO_RETURN = 50;
    private final String SEPARATOR = "\t";

    private static int queryIDCounter = 100;

    /**
     *
     * @param index
     * @param parser
     * @param ranker
     */
    public Searcher(Index index, TermParser parser, Ranker ranker)
    {
        this.index = index;
        this.parser = parser;
        this.ranker = ranker;

        try
        {
            InputStream inputStream = getClass().getResourceAsStream(MODEL_PATH);

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String s;

            File target = new File(System.getProperty("user.dir") + "\\word2vec.txt");
            OutputStream outputStream = new FileOutputStream(target);

            if(target.exists()) target.delete();

            while ((s = reader.readLine()) != null)
            {
                outputStream.write((s+"\n").getBytes());
            }
            inputStream.close();
            outputStream.close();

            model = Word2VecModel.fromTextFile(target);
            semanticSearcher = model.forSearch();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param index
     * @param parser
     */
    public Searcher(Index index, TermParser parser)
    {
        this(index,parser,new Ranker(index));
    }

    /**
     *
     * @param semantic
     * @param clickStream
     */
    public void setOptions(boolean semantic, boolean clickStream)
    {
        this.semantic = semantic;
        this.clickStream = clickStream;
    }

    /**
     *
     */
    public class SearchEntry
    {
        public String term;
        public int queryTf;
        public int[] documentsId;
        public int[] tf;

        public SearchEntry(String term, String[] posting)
        {
            this.term = term;
            queryTf = 1;

            documentsId = new int[posting.length / 2];
            tf = new int[documentsId.length];

            int i = 0;
            for(int j = 0; j < posting.length - 1; j += 2)
            {
                documentsId[i] = Integer.parseInt(posting[j]);
                tf[i] = Integer.parseInt(posting[j+1]);
                i++;
            }
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchEntry that = (SearchEntry) o;
            return Objects.equals(term, that.term);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(term);
        }
    }

    /**
     *
     * @param queriesFile
     * @param resultFile
     * @return
     */
    public HashMap<String, List<Indexer.DocumentEntry>> multipleQueries(File queriesFile, File resultFile)
    {
        HashMap<Integer, List<Indexer.DocumentEntry>> results = new HashMap<>();
        HashMap<String, List<Indexer.DocumentEntry>> finalResults = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(queriesFile)))
        {
            String line;

            while ((line = reader.readLine()) != null)
            {
                if(line.startsWith("<num>"))
                {
                    String[] split = line.split(" ");
                    int queryNum = Integer.parseInt(split[split.length - 1]);
                    String query = reader.readLine().substring("<title> ".length());

                    List<Indexer.DocumentEntry> result = search(query);

                    results.put(queryNum,result);
                    finalResults.put(query,result);
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        if(resultFile != null)
        {
            save(results,resultFile);
        }

        return finalResults;
    }

    /**
     *
     * @param results
     * @param resultFile
     */
    private void save(HashMap<Integer,List<Indexer.DocumentEntry>> results , File resultFile)
    {
        if(results == null || resultFile == null) return;

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile)))
        {
            ArrayList<Map.Entry<Integer,List<Indexer.DocumentEntry>>> listResults = new ArrayList<>(results.entrySet());
            listResults.sort(Comparator.comparing(integerListEntry -> integerListEntry.getKey()));

            for(Map.Entry<Integer,List<Indexer.DocumentEntry>> result : listResults)
            {
                List<Indexer.DocumentEntry> docsInQuery = result.getValue();

                System.out.println("write: " + result.getKey());

                for(int i = 0; i < docsInQuery.size(); i++)
                {
                    writer.write(result.getKey() + " 0 " + docsInQuery.get(i).docName + " " + (i) + " 42.38 mt\n");
                }
            }
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     *
     * @return
     */
    public int numOfDocsForLastQuery()
    {
        return numOfDocs;
    }

    /**
     *
     * @param query
     * @param resultFile
     * @return
     */
    public List<Indexer.DocumentEntry> singleQuery(String query, File resultFile)
    {
        List<Indexer.DocumentEntry> results = search(query);

        if(resultFile != null)
        {
            HashMap<Integer,List<Indexer.DocumentEntry>> mapper = new HashMap<>();
            mapper.put(queryIDCounter++,results);

            save(mapper,resultFile);
        }

        return results;
    }

    /**
     *
     * @param query
     * @return
     */
    private List<Indexer.DocumentEntry> search(String query)
    {
        ArrayList<Indexer.DocumentEntry> result = new ArrayList<>(MAX_DOCS_TO_RETURN);

        List<String> terms = parser.parseQuery(query);

        if(terms == null) return result;

        // Semantic
        if(semantic)
        {
            for(int i = terms.size() - 1; i >= 0; i--)
            {
                try
                {
                    List<com.medallia.word2vec.Searcher.Match> matches = semanticSearcher.getMatches(terms.get(i),MAX_SEMANTIC_RETURN);
                    for(com.medallia.word2vec.Searcher.Match match : matches)
                    {
                        System.out.println(match.match());
                        terms.add(match.match());
                    }

                } catch (com.medallia.word2vec.Searcher.UnknownWordException e) {
                    // WORD NOT KNOWN TO MODEL
                }
            }
        }

        // get posting
        HashMap<String,SearchEntry> searchEntries = new HashMap<>();
        HashMap<String,List<String>> postings = new HashMap<>();

        for(String term : terms)
        {
            Indexer.DictionaryEntry entry = index.getTermEntry(term);
            if(entry == null) continue; // not known

            // get posting and add new documents to the set
            try
            {
                if(!postings.containsKey(entry.postingFile))
                {
                    postings.put(entry.postingFile,Files.readAllLines(Paths.get(entry.postingFile)));
                }

                String postingLine = postings.get(entry.postingFile).get(entry.rowInPosting);
                String[] split = postingLine.split(SEPARATOR);

                //System.out.println("get Post For: " + entry.term + "(" + term + ") - " + Arrays.toString(split));

                if(searchEntries.containsKey(term))
                {
                    searchEntries.get(term).queryTf++;
                }
                else searchEntries.put(term,new SearchEntry(term,split));

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        // rank results
        List<Ranker.DocRating> ranked = ranker.rank(new HashSet<>(searchEntries.values()),query.toUpperCase() ,clickStream);
        numOfDocs = ranked.size();

        // remove to max size and return

        for(int i = 0; i < MAX_DOCS_TO_RETURN && i < ranked.size(); i++)
        {
            result.add(index.getDocumentEntry(ranked.get(i).docId));
        }

        return result;
    }
}
