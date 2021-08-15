package NLP.Search_Engine.src.Model.SearchEngine.Index.Read;

import Model.SearchEngine.Index.CorpusDocument;

import java.io.IOException;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: This interface defines a Corpus Reader that capable of reading a given corpus and generating a stream of Documents from it.
 */
public interface CorpusReader
{
    /**
     * Get the next document from the corpus
     * @return the next CorpusDocument in the stream, if no other document remains in the stream null is returned
     * @throws IOException - can happened while reading the corpus if reading from files
     */
    CorpusDocument nextDocument() throws IOException;

    /**
     * Checks whether another document remains in the stream.
     * @return true if there is a document remaining, otherwise false is returned
     */
    boolean hasNext();

    /**
     * closes all inner proccess and clean up after reading, use this function when you finish reading the corpus
     * @throws IOException - can happen while closing.
     */
    void close() throws IOException;
}
