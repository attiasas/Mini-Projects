package NLP.Search_Engine.src.Model;

import Model.SearchEngine.Index.*;
import Model.SearchEngine.Index.Parse.Parse;
import Model.SearchEngine.Index.Read.CorpusReader;
import Model.SearchEngine.Index.Read.ReadFile;
import Model.SearchEngine.Index.Stem.Stemmer;
import Model.SearchEngine.Queries.Searcher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: Represents a logic model in MVVM architecture that controls all the Search Engine logic
 */
public class MyModel extends Observable
{
    private Indexer indexer = null;
    private Parse parser = null;
    private Index index = null;
    private Searcher searcher = null;

    /**
     * Builds a Index base on a given path
     * @param dataPath
     * @param postPath
     */
    public void buildIndex(String dataPath, String postPath, boolean stemming)
    {
        try
        {
            CorpusReader reader = new ReadFile(dataPath + "\\corpus");
            parser = new Parse(reader,dataPath + "\\stop_words.txt",stemming ? new Stemmer() : null);
            indexer = new Indexer(postPath,parser);
            index = null;
            searcher = null;

            long startTimeStamp = System.currentTimeMillis();
            index = indexer.buildIndex();
            double elapsed = (double)(System.currentTimeMillis() - startTimeStamp);

            searcher = new Searcher(index,parser);

            setChanged();
            notifyObservers(new double[]{parser.getNumberOfDocsParsed()/*numOfDoc*/,indexer.getUniqueTermCount()/*numOfTerm*/,(elapsed / 1000)});

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Collection<Indexer.DictionaryEntry> getDictionary()
    {
        if(index == null) return null;

        return index.getDictionary().values();
    }

    public void loadIndex(String path,boolean stemming)
    {
        parser = new Parse(null,null,stemming ? new Stemmer() : null,stemming);
        indexer = new Indexer(path,parser);
        index = null;
        searcher = null;

        index = indexer.loadIndex(path,stemming);
        searcher = new Searcher(index,parser);

        setChanged();
        notifyObservers("LOAD");
    }

    public void deleteIndex(String path, boolean stemming)
    {
        parser = new Parse(null,null,stemming ? new Stemmer() : null,stemming);
        indexer = new Indexer(path,parser);

        indexer.deleteIndex(path,stemming);

        setChanged();
        notifyObservers();
    }

    public void singleQuery(String query, File resultDir, boolean semantic, boolean clickStream)
    {
        if(index == null || searcher == null) return;
        searcher.setOptions(semantic,clickStream);

        long startTimeStamp = System.currentTimeMillis();
        List<Indexer.DocumentEntry> result = searcher.singleQuery(query,resultDir == null ? null : new File(resultDir.getPath() + (parser.isStemming() ? "\\S_Query_result.txt" : "\\NS_Query_result.txt")));
        double elapsed = (double)(System.currentTimeMillis() - startTimeStamp);

        int numOfDocsMatch = searcher.numOfDocsForLastQuery();

        setChanged();
        notifyObservers(new Object[]{(elapsed / 1000),numOfDocsMatch,result});
    }

    public void multipleQueries(File queriesFile, File resultDir, boolean semantic, boolean clickStream)
    {
        if(index == null || searcher == null) return;
        searcher.setOptions(semantic,clickStream);

        long startTimeStamp = System.currentTimeMillis();

        HashMap<String, List<Indexer.DocumentEntry>> result = searcher.multipleQueries(queriesFile,resultDir == null ? null : new File(resultDir.getPath() + (parser.isStemming() ? "\\Stemmed_Queries_result.txt" : "\\Not_Stemmed_Queries_result.txt")));

        double elapsed = (double)(System.currentTimeMillis() - startTimeStamp);

        setChanged();
        notifyObservers(new Object[]{(elapsed / 1000),result});
    }
}
