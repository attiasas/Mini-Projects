package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse;

import Model.SearchEngine.Index.CorpusDocument;

import java.io.IOException;
import java.util.List;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: A decorator for CorpusReader Capable of parsing text from a document into terms and supplying a list of entities recognize from it.
 */
public interface TermParser
{
    /**
     * Return the next document that was read and a bag of terms parsed from it
     * @return - BagOfTerms, an class that combines a document and its terms
     * @throws IOException
     */
    BagOfTerms nextTermsBag() throws IOException;

    /**
     * Check if there are more document to read and parse
     * @return
     */
    boolean hasNext();

    /**
     * Parse the given query into terms and entities terms
     * @param q - query to process
     * @return list of terms from the given query
     */
    List<String> parseQuery(String q);

    /**
     * Return a list of entities that was recognise (from the last document or any previous ones)
     * @return
     */
    List<TermInDocument> getEntities();

    /**
     * Return a flag if this parser use stemming on terms or not
     * @return
     */
    boolean isStemming();

    /**
     * Get the number of documents that was parsed from the first call to nextTermsBag
     * @return - number of none empty documents that was parsed
     */
    int getNumberOfDocsParsed();

    /**
     * close the parser and release all its resources
     * @throws IOException
     */
    void close() throws IOException;

    /**
     *  a single term and its source document
     */
    class TermInDocument
    {
        public String docId;
        public String term;

        public TermInDocument(String docId, String term) {
            this.docId = docId;
            this.term = term;
        }
    }

    /**
     * a class that represents a document and its terms after parsing its text
     */
    class BagOfTerms
    {
        public CorpusDocument document;
        public List<String> termsBag;

        public BagOfTerms(CorpusDocument document, List<String> termsBag) {
            this.document = document;
            this.termsBag = termsBag;
        }
    }
}
