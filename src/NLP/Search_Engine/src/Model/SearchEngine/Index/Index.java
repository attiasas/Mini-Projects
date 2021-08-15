package NLP.Search_Engine.src.Model.SearchEngine.Index;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Created By: Assaf Attias
 * On 31/12/2019
 * Description:     Representing Inverted Index from terms to documents and provides all the necessary method to search
 *                  base on the information that was built.
 */
public class Index
{
    private HashMap<Integer, Indexer.DocumentEntry> documentIndex;
    private HashMap<String, Integer> documentMapper;

    private HashMap<String, Indexer.DictionaryEntry> dictionary;
    private HashSet<String> lowerCaseTerms;

    private double avgDocLen;

    /**
     *  constructor
     * @param documentIndex
     * @param dictionary
     * @param lowerCaseTerms
     * @param avgDocLen
     * @param documentMapper
     */
    public Index(HashMap<Integer, Indexer.DocumentEntry> documentIndex, HashMap<String, Indexer.DictionaryEntry> dictionary, HashSet<String> lowerCaseTerms, double avgDocLen,HashMap<String,Integer> documentMapper)
    {
        if(documentIndex == null || dictionary == null || lowerCaseTerms == null) throw new IllegalArgumentException("NULL NOT LEGAL!");
        this.documentIndex = documentIndex;
        this.dictionary = dictionary;
        this.lowerCaseTerms = lowerCaseTerms;
        this.avgDocLen = avgDocLen;
        this.documentMapper = documentMapper;
    }

    //<editor-fold desc="Getters">
    /**
     * Get the average length of the documents in the index
     * @return
     */
    public double getAvgDocLen() {
        return avgDocLen;
    }

    /**
     * Get a document entry base on the document serial number that is stored inside it
     * @param docNo
     * @return
     */
    public Indexer.DocumentEntry getDocumentEntryByDocNo(String docNo)
    {
        if(!documentMapper.containsKey(docNo)) return null;

        return documentIndex.get(documentMapper.get(docNo));
    }

    /**
     * Get all the entries of a terms in the index
     * @return
     */
    public HashMap<String, Indexer.DictionaryEntry> getDictionary() { return dictionary; }

    /**
     * Get the number of documents that the inverted index is storing
     * @return
     */
    public int getNumDocuments() { return documentIndex.size(); }

    /**
     * Get a document entry base on the unique id that was given to him and stored in the posting files
     * @param id
     * @return
     */
    public Indexer.DocumentEntry getDocumentEntry(int id)
    {
        return documentIndex.get(id);
    }

    /**
     * get term entry base on the term, if the term is not known to the index null will be returned,
     * this method is not case sensitive !
     * @param term
     * @return
     */
    public Indexer.DictionaryEntry getTermEntry(String term)
    {
        // handle lower/upper
        String handled = handleLowerUpper(term.toLowerCase());

        // check if dictionary contains it
        return dictionary.get(handled);
    }
    //</editor-fold>

    /**
     * transform the term into the case form that is stored
     * @param term
     * @return
     */
    private String handleLowerUpper(String term)
    {
        String result = term;
        if(term.indexOf(' ') != -1 || term.indexOf('-') != -1)
        {
            // handle lower/upper of entity and phrase
            boolean entity = term.indexOf(' ') != -1 ? true : false;
            String[] split = term.split("[ -]");
            String finalTerm = "";
            for (int i = 0; i < split.length; i++) {
                finalTerm += (lowerCaseTerms.contains(split[i]) ? split[i] : split[i].toUpperCase());
                if (i < split.length - 1) finalTerm += (entity ? " " : "-");
            }
            result = finalTerm;
        }
        else if(!lowerCaseTerms.contains(term))
        {
            result = term.toUpperCase();
        }
        return result;
    }

    /**
     * save the index information to the given folder
     * @param posting
     */
    public void saveToMemory(File posting)
    {
        // Write Dictionary Index to memory
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(posting.getPath() + "\\termsDictionary.txt")))
        {
            Iterator<Indexer.DictionaryEntry> dictionaryEntries = dictionary.values().iterator();
            while (dictionaryEntries.hasNext())
            {
                Indexer.DictionaryEntry entry = dictionaryEntries.next();

                entry.term = handleLowerUpper(entry.term);

                writer.write(entry.toString() + "\n");
            }

            writer.write(CorpusIndexer.DELIMITER + "\n");

            // Write Close List
            Iterator<String> iterator = lowerCaseTerms.iterator();
            while (iterator.hasNext())
            {
                writer.write(iterator.next() + "\n");
            }
        }
        catch (Exception e) {e.printStackTrace();}

        // Write Document Index to memory
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(posting.getPath() + "\\documentsDictionary.txt")))
        {
            Iterator<Indexer.DocumentEntry> documentEntries = documentIndex.values().iterator();
            while (documentEntries.hasNext())
            {
                Indexer.DocumentEntry entry = documentEntries.next();
                writer.write(entry.toString() + "\n");
            }

            writer.write(CorpusIndexer.DELIMITER + "\n");
            // Write Mapper
            for(Map.Entry<String,Integer> entry : documentMapper.entrySet())
            {
                writer.write(entry.getKey() + CorpusIndexer.DELIMITER + entry.getValue() + "\n");
            }

            writer.write(CorpusIndexer.DELIMITER + "\n");
            // Write avgDocLen
            writer.write("" + avgDocLen);
        }
        catch (Exception e) {e.printStackTrace();}
    }
}
