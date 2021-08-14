package NLP.Search_Engine.src.Model.SearchEngine.Index.Read;

import Model.SearchEngine.Index.CorpusDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

/**
 * Created By: Assaf Attias
 * On 22/11/2019
 * Description:     an implementation of a corpus reader, can read a corpus from files meeting the format.
 *
 * Format:      * All files must be stored inside a folder with the same name as the file name (one file per folder).
 *              * Documents are stored inside files (multiple documents in a single file is allowed).
 *              * Documents are represented using Tags (<Name>) - ALL TAGS MUST HAVE A CLOSING TAG WITH IT! (</Name>).
 *              * A start of a CorpusDocument is represented by Tag: <DOC></DOC> (all the information must be inside the tag).
 *
 * Using Jsoup from maven (https://jsoup.org/)
 */
public class ReadFile implements CorpusReader
{
    private String mainPath;

    public String[] fileListPaths;
    public int currentFileIndex = 0;
    private Elements docsInFile;
    public int currentDocIndex = 0;

    /**
     * Constructor
     * @param corpusDirectoryPath - path of the folder holding the corpus files
     * @throws IOException - while reading
     */
    public ReadFile(String corpusDirectoryPath) throws IOException
    {
        mainPath = corpusDirectoryPath;
        // get files information
        File corpusDirectory = new File(corpusDirectoryPath);
        fileListPaths = corpusDirectory.list();
        changeFile();
    }

    /**
     * Change to a new file from the main path
     * @throws IOException
     */
    private void changeFile() throws IOException
    {
        if(currentFileIndex < fileListPaths.length)
        {
            Document file = Jsoup.parse(new File(mainPath + "\\" + fileListPaths[currentFileIndex]+ "\\" + fileListPaths[currentFileIndex]),"UTF-8");
            docsInFile = file.select("doc");
            currentDocIndex = 0;
        }
    }

    @Override
    public CorpusDocument nextDocument() throws IOException
    {
        if(!hasNext()) return null;

        Element docInFile = docsInFile.get(currentDocIndex);
        currentDocIndex++;

        String id = docInFile.getElementsByTag("docno").text();
        String date = docInFile.getElementsByTag("date1").text();
        if(date.length() == 0) date = docInFile.getElementsByTag("date").text();
        String title = docInFile.getElementsByTag("ti").text();
        String text = docInFile.getElementsByTag("text").text();
        String source = fileListPaths[currentFileIndex];

        if(currentDocIndex >= docsInFile.size())
        {
            // change file
            currentFileIndex++;
            changeFile();
        }

        return new CorpusDocument(id,date,title,text,source);
    }

    @Override
    public boolean hasNext() {
        return currentFileIndex < fileListPaths.length || currentDocIndex < docsInFile.size();
    }

    @Override
    public void close() throws IOException { }
}
