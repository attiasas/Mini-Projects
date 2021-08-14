package NLP.Search_Engine.src.Model.SearchEngine.Index;

import Model.SearchEngine.Index.Parse.TermParser;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description:     Capable of Building Inverted Index of terms.
 *                  This class will handle the building in concurrent and will have three stages:
 *                  1. Parse - receive all the information, divide it to partitions and save it to the disks (temp folders).
 *                  2. Merge - merge all the files in each partition in a sorted way (lexicography) - merge will be done as tournament.
 *                  3. Posting - generate posting files base on the final file in each partition, file size base on parameter.
 *
 *                  * This class needs to be provided with a TermParser and a path to store the posting information.
 *                  * This class will save terms in lowercase if it encounter with the term that starts in lower form, otherwise upper form will be saved.
 *                  * This class will save the posting with an without stemming in different folders (base on the TermParser).
 *                  * This class can loadIndex/deleteIndex indexes that it built previously - base on the posting path provided.
 *
 * Build Result:    1. Folder that stores all the following information (base on stemming) in the path provided.
 *                  2. Dictionary file - stores all the information about the terms and the close list that will handle lower/upper case.
 *                  3. Documents file - stores all the information about the documents.
 *                  4. Posting Folder - stores all the posting file (each row belongs to a term and holds the documents and tf in each of them).
 *                  5. Index object created.
 *
 * Parameters:      * TERMS_INIT_CAPACITY - Number of Unique Terms, To prevent rehashing of the dictionary.
 *                  * DOCS_INIT_CAPACITY - Number of Documents in Corpus, To prevent rehashing of the doc index.
 *                  * MAX_DOCUMENT_BEFORE_FLUSH - base on the RAM of the running machine, how much docs to parse before writing to disk.
 *                  * SEPARATOR - char that will separate information in the file, should be char that will never appear in terms.
 *                  * MAX_FILE_SIZE - final posting file size, should be as low as possible but bigger that block size for faster querying.
 *                  * PARSE_POOL_SIZE - number of threads that handles writing to disk in Parse Stage.
 *                  * MERGE_POOL_SIZE - number of merging threads in each partition in Merge Stage.
 */
public class Indexer
{
    private TermParser parser;

    private String postingPath;
    private AtomicInteger postingIdCounter;

    private HashMap<Integer,DocumentEntry> documentIndex;
    private int documentIdCounter = 0;

    private HashMap<String,DictionaryEntry> dictionary;
    private HashSet<String> closeList;

    private int documentsLenCount = 0;
    private int uniqueTermCount = 0;

    private final int TERMS_INIT_CAPACITY = 1500000;
    private final int DOCS_INIT_CAPACITY = 472525;
    private final int MAX_DOCUMENT_BEFORE_FLUSH = 8000;
    private final String SEPARATOR = "\t";
    private final int MAX_FILE_SIZE = 1000000; // file size will be up to 1Mb
    private final int PARSE_POOL_SIZE = 3;
    private final int MERGE_POOL_SIZE = 2;
    private final int MAX_TOP_ENTITIES = 5;

    /**
     * Constructor
     * @param postingPath - directory path to store the final information of the build
     * @param parser - parser that can provide bag of terms to index.
     */
    public Indexer(String postingPath, TermParser parser)
    {
        this.parser = parser;
        this.postingPath = postingPath;

        dictionary = new HashMap<>(TERMS_INIT_CAPACITY);
        closeList = new HashSet<>(TERMS_INIT_CAPACITY);

        documentIndex = new HashMap<>(DOCS_INIT_CAPACITY);

        postingIdCounter = new AtomicInteger(0);
    }

    //<editor-fold desc="Index Inner Structures">
    /**
     * Holds an entry in the dictionary that stores all the information of a term
     * term         - the term the entry represents.
     * count        - number of total appearance in the corpus.
     * df           - document frequency, number of documents the term appear.
     * postingFile  - the path of the posting file of the term
     * rowInPosting - the row of the term in the posting file.
     */
    public class DictionaryEntry
    {
        public String term;
        public int count;
        public int df;

        public String postingFile;
        public int rowInPosting;

        public DictionaryEntry(String term, int count) {
            this.term = term;
            this.count = count;
        }

        public DictionaryEntry(String term) { this(term,1); }

        public DictionaryEntry(String[] split)
        {
            term = split[0];
            count = Integer.parseInt(split[1]);
            df = Integer.parseInt(split[2]);
            postingFile = split[3];
            rowInPosting = Integer.parseInt(split[4]);
        }

        @Override
        public String toString() {
            return term + SEPARATOR + count + SEPARATOR + df + SEPARATOR + postingFile + SEPARATOR + rowInPosting;
        }
    }

    /**
     * Holds an entry in the document index that stores all the information of a document
     * id               - the number that was given to the document in the index process.
     * docName          - the document unique name.
     * title            - the title of the document.
     * date             - the date that the document was created.
     * max_tf           - the number of appearance of the most frequent term in the document.
     * uniqueTermsCount - the number of unique terms in the document.
     */
    public class DocumentEntry
    {
        public final int id;

        public String docName;
        public String title;
        public String date;
        public String source;

        public int docLength;

        public int max_tf;
        public int uniqueTermsCount;

        public String[] entities;
        public double[] score;

        public DocumentEntry(int id, String docName, String title, String date, String source, int startLength)
        {
            this.id = id;

            this.docName = docName;
            this.title = title;
            this.date = date;
            this.source = source;

            docLength = startLength;

            this.uniqueTermsCount = 0;
            max_tf = 1;

            entities = new String[MAX_TOP_ENTITIES];
            score = new double[entities.length];
        }

        public DocumentEntry(String[] split)
        {
            id = Integer.parseInt(split[0]);
            docName = split[1];
            title = split[2];
            date = split[3];
            max_tf = Integer.parseInt(split[4]);
            uniqueTermsCount = Integer.parseInt(split[5]);
            docLength = Integer.parseInt(split[6]);
            source = split[7];

            entities = new String[MAX_TOP_ENTITIES];
            score = new double[entities.length];

            if(split.length > 8)
            {
                //System.out.println("has entities: " + split[8]);
                String[] entitiesInfo = split[8].split(";");

                int i = 0;

                for(int j = 0; j < entitiesInfo.length - 1 && i < MAX_TOP_ENTITIES; j += 2)
                {
                    entities[i] = entitiesInfo[j];
                    score[i] = Double.parseDouble(entitiesInfo[j + 1]);
                    i++;
                }
            }

        }

        @Override
        public String toString() {
            String res = id + SEPARATOR + docName + SEPARATOR + title + SEPARATOR + date + SEPARATOR + max_tf + SEPARATOR + uniqueTermsCount + SEPARATOR + docLength + SEPARATOR + source;

            String allEntitiesInfo = "";
            for(int i = 0; i < entities.length && entities[i] != null; i++)
            {
                allEntitiesInfo += entities[i] + ";" + score[i];
                if(i < entities.length - 1) allEntitiesInfo += ";";
            }
            if(!allEntitiesInfo.isEmpty()) res += SEPARATOR + allEntitiesInfo;

            return res;
        }
    }

    /**
     * Temporary entry that will be stored in the disk, in the parse stage of indexing.
     */
    private class PostingEntry
    {
        public int docId;
        public int tf;

        public PostingEntry(int docId, int tf) {
            this.docId = docId;
            this.tf = tf;
        }

        @Override
        public String toString()
        {
            return "" + docId;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Getter For Index State">
    /**
     * Get the number of total unique terms that was parsed in the indexing process
     * @return - number of unique terms
     */
    public int getUniqueTermCount() {
        return uniqueTermCount;
    }

    /**
     * Get the dictionary of the inverted index
     * @return - terms dictionary
     */
    public HashMap<String, DictionaryEntry> getDictionary() {
        return dictionary;
    }
    //</editor-fold>

    //<editor-fold desc="Partitions">
    /**
     * Represents the names of the temp partition folders in the indexing process.
     */
    private enum Partition
    {
        AB,C,D,E,FG,HJ,KM,N,O,P,QR,S,T,UZ,OTHER
    }

    /**
     * Get the index of the enum partition for a given string, base on the starting character
     * @param s - non empty or null string
     * @return - the partition that the string belongs to
     */
    private int toPartition(String s)
    {
        char ch = s.charAt(0);
        if(ch >= 'a' && ch <= 'b') return 0;
        else if(ch == 'c') return 1;
        else if(ch == 'd') return 2;
        else if(ch == 'e') return 3;
        else if(ch >= 'f' && ch <= 'g') return 4;
        else if(ch >= 'h' && ch <= 'j') return 5;
        else if(ch >= 'k' && ch <= 'm') return 6;
        else if(ch == 'n') return 7;
        else if(ch == 'o') return 8;
        else if(ch == 'p') return 9;
        else if(ch >= 'q' && ch <= 'r') return 10;
        else if(ch == 's') return 11;
        else if(ch == 't') return 12;
        else if(ch >= 'u' && ch <= 'z') return 13;
        else return 14;

    }
    //</editor-fold>

    //<editor-fold desc="Indexing Stages">

    //<editor-fold desc="Parse Stage">
    /**
     * Parsing all the documents to terms, handles counting, and write each batch to the disk while diving it to partitions
     * and making sure that the heap will not overflow, also handling the lower/upper case of each term.
     * after a batch is ready a writen thread will be called to write it concurrently.
     * @param tempDir - temp dir with the partition folders
     * @throws IOException - if parser will throw exception
     */
    private HashMap<String,Integer> parseStage(File tempDir) throws IOException
    {
        // init stage
        HashMap<String,Integer> documentMapper = new HashMap<>(DOCS_INIT_CAPACITY);
        HashMap<String,HashMap<Integer,PostingEntry>> currentPosting = new HashMap<>(TERMS_INIT_CAPACITY / 2);
        int docParsedSinceFlush = 0;
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(PARSE_POOL_SIZE);

        // Parsing Phase
        while (parser.hasNext())
        {
            TermParser.BagOfTerms bagOfTerms = parser.nextTermsBag();

            docParsedSinceFlush++;

            // handle document
            int currentDocId;

            if(!documentMapper.containsKey(bagOfTerms.document.id))
            {
                currentDocId = documentIdCounter++;
                DocumentEntry entry = new DocumentEntry(currentDocId,bagOfTerms.document.id, bagOfTerms.document.title.toLowerCase(), bagOfTerms.document.date,bagOfTerms.document.source,bagOfTerms.termsBag.size());
                documentIndex.put(currentDocId,entry);
                documentMapper.put(bagOfTerms.document.id,currentDocId);

                documentsLenCount += bagOfTerms.termsBag.size();
            }
            else currentDocId = documentMapper.get(bagOfTerms.document.id);

            // handle Terms
            for(String termInBag : bagOfTerms.termsBag)
            {
                String term = termInBag.trim().toLowerCase();
                // handle lower/upper case
                char firstChar = termInBag.charAt(0);
                if((firstChar < 'A' || firstChar > 'Z') && !closeList.contains(term))
                {
                    closeList.add(term);
                }

                if(!dictionary.containsKey(term))
                {
                    // first time seen
                    dictionary.put(term,new DictionaryEntry(term));
                    uniqueTermCount++;
                }
                else dictionary.get(term).count++;

                // update posting
                updatePosting(currentPosting,term,currentDocId);
            }

            // Handle New Entities
            List<TermParser.TermInDocument> entities = parser.getEntities();
            if(entities != null)
            {
                for(TermParser.TermInDocument td : entities)
                {
                    String term = td.term.trim().toLowerCase();
                    if(!dictionary.containsKey(term))
                    {
                        dictionary.put(term,new DictionaryEntry(term));
                        uniqueTermCount++;
                    }
                    else dictionary.get(term).count++;

                    int docId = documentMapper.get(td.docId);
                    documentIndex.get(docId).docLength++;
                    documentsLenCount++;

                    // update posting
                    updatePosting(currentPosting,term,docId);
                }
            }

            // flush current posting
            if(docParsedSinceFlush >= MAX_DOCUMENT_BEFORE_FLUSH)
            {
                final HashMap<String,HashMap<Integer,PostingEntry>> postingToFlush = currentPosting;
                threadPoolExecutor.execute(()->flushTempPosting(postingToFlush,tempDir.getPath()));

                currentPosting = new HashMap<>(TERMS_INIT_CAPACITY / 2);
                docParsedSinceFlush = 0;
            }

        }

        if(docParsedSinceFlush != 0)
        {
            final HashMap<String,HashMap<Integer,PostingEntry>> postingToFlush = currentPosting;
            threadPoolExecutor.execute(()->flushTempPosting(postingToFlush,tempDir.getPath()));
        }

        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return documentMapper;
    }

    /**
     * Update the given batch posting data structure with the new term , handles the counting of the term
     * and document meta data (tf, max_tf, uniqueTermCount).
     * @param currentPosting - the batch posting data structure.
     * @param term - the term to add to the structure.
     * @param docId - the document id number that the term belongs to.
     */
    private void updatePosting(HashMap<String,HashMap<Integer,PostingEntry>> currentPosting,String term,int docId)
    {
        if(!currentPosting.containsKey(term)) currentPosting.put(term,new HashMap<>(MAX_DOCUMENT_BEFORE_FLUSH / 10));
        HashMap<Integer,PostingEntry> termPostingEntries = currentPosting.get(term);

        DocumentEntry documentEntry = documentIndex.get(docId);

        if(termPostingEntries.containsKey(docId))
        {
            PostingEntry postingEntry = termPostingEntries.get(docId);
            postingEntry.tf++;
            if(documentEntry.max_tf < postingEntry.tf) documentEntry.max_tf = postingEntry.tf;
        }
        else
        {
            termPostingEntries.put(docId,new PostingEntry(docId,1));
            documentEntry.uniqueTermsCount++;
        }
    }

    /**
     * Write the batch posting data structure to the disk and divied the terms to the correct partition.
     * This method can be called concurrently.
     * @param currentPosting - the batch posting data structure.
     * @param tempDirPath - the path to the temp directory with the partition folders.
     */
    private void flushTempPosting(HashMap<String,HashMap<Integer,PostingEntry>> currentPosting, String tempDirPath)
    {
        // sort terms
        String[] terms = new String[currentPosting.keySet().size()];
        currentPosting.keySet().toArray(terms);
        Arrays.sort(terms);

        Partition[] partitions = Partition.values();
        int count = postingIdCounter.incrementAndGet();

        try
        {
            BufferedWriter[] writers = new BufferedWriter[partitions.length];
            for(int i = 0; i < writers.length; i++)
            {
                writers[i] = new BufferedWriter(new FileWriter(tempDirPath + "\\" + partitions[i] + "\\" + count + ".txt"));
            }

            for(String term : terms)
            {
                // sort documentList
                PostingEntry[] termPosting = new PostingEntry[currentPosting.get(term).values().size()];
                currentPosting.get(term).values().toArray(termPosting);
                Arrays.sort(termPosting,Comparator.comparing(postingEntry -> postingEntry.docId));

                // write to the correct partition
                int partition = toPartition(term);
                for(int p = 0; p < termPosting.length; p++)
                {
                    writers[partition].write(term + SEPARATOR + termPosting[p].docId + SEPARATOR + termPosting[p].tf + "\n");
                }
            }

            for(int i = 0; i < writers.length; i++)
            {
                writers[i].flush();
                writers[i].close();
            }

        }
        catch (Exception e) { e.printStackTrace(); }
    }
    //</editor-fold>

    //<editor-fold desc="Merge Stage">
    /**
     *  Merge all the batches in each partition into one file ordered by terms (and then by docId).
     *  The merge will be in tournament style, each epoch a snapshot of all the files will be taken,
     *  each file will be paired with another and it will merge each pair concurrently inside each partition
     *  and also all the partitions will make this tournament concurrently.
     * @param tempDir - temp dir with the partitions temp folders
     */
    private void mergeStage(File tempDir)
    {
        postingIdCounter.set(5000);

        File[] partitions = tempDir.listFiles();
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(partitions.length);

        // merge partitions separately
        for(int p = 0; p < partitions.length; p++)
        {
            File partition = partitions[p];
            threadPoolExecutor.execute(() ->
            {
                File[] tempFiles;
                while ((tempFiles = partition.listFiles()) != null && tempFiles.length >= 2)
                {
                    // tournament merges
                    ExecutorService threadPoolExecutor2 = Executors.newFixedThreadPool(MERGE_POOL_SIZE);
                    for(int i = 0; i < tempFiles.length - 1; i += 2)
                    {
                        File first = tempFiles[i];
                        File second = tempFiles[i + 1];
                        threadPoolExecutor2.execute(()->mergeFiles(first,second, partition.getPath() + "\\" + postingIdCounter.incrementAndGet() + ".txt"));
                    }
                    threadPoolExecutor2.shutdown();
                    try {
                        threadPoolExecutor2.awaitTermination(1, TimeUnit.HOURS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Merge two files into one file, so that the order will remain (by term and than by docId).
     * when the merge is done, the pair source files will be deleted
     * @param first - first file in the pair to merge
     * @param second - second file in the pair to merge
     * @param resPath - path and name of the file that will contain the merged files.
     */
    private void mergeFiles(File first,File second,String resPath)
    {
        try(BufferedReader firstReader = new BufferedReader(new FileReader(first));
            BufferedReader secondReader = new BufferedReader(new FileReader(second));
            BufferedWriter writer = new BufferedWriter(new FileWriter (new File(resPath))))
        {
            String firstTermInfo = firstReader.readLine();
            String secondTermInfo = secondReader.readLine();

            while ((firstTermInfo) != null && (secondTermInfo) != null)
            {
                String firstTerm = firstTermInfo.substring(0,firstTermInfo.indexOf(SEPARATOR));
                String secondTerm = secondTermInfo.substring(0,secondTermInfo.indexOf(SEPARATOR));

                if(firstTerm.equals(secondTerm))
                {
                    // same term - write base on document number
                    String[] firstTokens = firstTermInfo.split(SEPARATOR);
                    String[] secondTokens = secondTermInfo.split(SEPARATOR);

                    if(firstTokens[1].equals(secondTokens[1]))
                    {
                        // same document - merge
                        writer.write(firstTokens[0] + SEPARATOR + firstTokens[1] + SEPARATOR + (Integer.parseInt(firstTokens[2]) + Integer.parseInt(secondTokens[2]))  + "\n");
                        firstTermInfo = firstReader.readLine();
                        secondTermInfo = secondReader.readLine();
                    }
                    else if(firstTokens[1].compareTo(secondTokens[1]) < 0)
                    {
                        // first doc lower
                        writer.write(firstTermInfo + "\n");
                        firstTermInfo = firstReader.readLine();
                    }
                    else
                    {
                        // second doc lower
                        writer.write(secondTermInfo + "\n");
                        secondTermInfo = secondReader.readLine();
                    }
                }
                else if(firstTerm.compareTo(secondTerm) < 0)
                {
                    // first is smaller
                    writer.write(firstTermInfo + "\n");
                    // update smaller reader for new line
                    firstTermInfo = firstReader.readLine();
                }
                else
                {
                    // second is smaller
                    writer.write(secondTermInfo + "\n");
                    // update smaller reader for new line
                    secondTermInfo = secondReader.readLine();
                }
            }

            while (firstTermInfo != null)
            {
                writer.write(firstTermInfo + "\n");
                firstTermInfo = firstReader.readLine();
            }

            while (secondTermInfo != null)
            {
                writer.write(secondTermInfo + "\n");
                secondTermInfo = secondReader.readLine();
            }
        }
        catch (Exception e) {e.printStackTrace();}

        first.delete();
        second.delete();
    }
    //</editor-fold>

    //<editor-fold desc="Posting Stage">
    /**
     * Divides the final merged file in each partition into a final posting file concurrently, this will be managed as
     * tornament merging, each epoch a snapshot will be taken and files will be paired and merge until one file remains.
     * after posting is done the final dictionary, document index and closeList will be stored in the posting dir.
     * @param tempDir - temp posting dir with the partitions
     * @param posting - final posting directory
     */
    private Index postingStage(File tempDir, File posting, HashMap<String,Integer> docMapper)
    {
        File[] temps = tempDir.listFiles();
        File postingDir = new File(posting.getPath()  + "\\posting");
        postingDir.mkdir();
        postingIdCounter.set(0);

        // dived merged files in each partition to final posting with a constant file size
        ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(temps.length);
        for(File temp : temps)
        {
            threadPoolExecutor.execute(()-> divideToPosting(temp,postingDir));
        }

        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Done Posting ! -----------------------

        // Write Index to memory
        Index finalIndex = new Index(documentIndex,dictionary,closeList,documentsLenCount / documentIndex.size(),docMapper);

        finalIndex.saveToMemory(posting);

        return finalIndex;
    }

    /**
     * divide a file holding terms posting entries into final posting while making sure the file will be around
     * the same size in the parameter and updating the dictionary with the final information
     * they need (df, posting path and row), this method can be called concurrently into files with different terms in it.
     * @param from - file to divide
     * @param postingFolder - folder that will store the final posting file
     */
    private void divideToPosting(File from, File postingFolder)
    {
        File[] lastFile = from.listFiles();
        if(lastFile.length != 1) throw new IllegalArgumentException();

        try(BufferedReader reader = new BufferedReader(new FileReader(lastFile[0])))
        {
            String currentPostingPath = postingFolder.getPath() + "\\posting" + postingIdCounter.getAndIncrement() + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(currentPostingPath));

            String line = reader.readLine();
            String[] termInfo;

            // term info to gather
            DictionaryEntry currentEntry = null;
            int rowCounter = 0;

            int sizeCounter = 0;

            Pattern entityPattern = Pattern.compile("^(([a-z\\-]+)([ ])([a-z\\-]+))+$");
            boolean entity = false;

            while (line != null)
            {
                termInfo = line.split(SEPARATOR);

                if(!termInfo[0].equals(currentEntry != null ? currentEntry.term : null))
                {
                    if(currentEntry != null)
                    {
                        writer.write("\n");
                        rowCounter++;
                    }
                    // check if reached max size
                    if(sizeCounter >= MAX_FILE_SIZE)
                    {
                        writer.flush();
                        writer.close();

                        // reset
                        rowCounter = 0;
                        sizeCounter = 0;
                        currentPostingPath = postingFolder.getPath() + "\\posting" + postingIdCounter.getAndIncrement() + ".txt";
                        writer = new BufferedWriter(new FileWriter(currentPostingPath));

                    }

                    // prepare new term
                    currentEntry = dictionary.get(termInfo[0]);
                    currentEntry.postingFile = currentPostingPath;
                    currentEntry.rowInPosting = rowCounter;
                    currentEntry.df = 1;


                    Matcher matcher = entityPattern.matcher(currentEntry.term);
                    entity = currentEntry.term.length() > 60 ? true :matcher.matches();
                }
                else
                {
                    // accumulate
                    currentEntry.df++;
                    sizeCounter += termInfo[1].length() + termInfo[2].length() + 2;
                    writer.write(SEPARATOR);
                }


                if(entity)
                {
                    DocumentEntry doc = documentIndex.get(Integer.parseInt(termInfo[1]));

                    // rank
                    double entityScore = Double.parseDouble(termInfo[2]) / (double)doc.max_tf;

                    synchronized (doc)
                    {
                        // scan for place
                        int i = 0;
                        while (i < doc.entities.length && doc.entities[i] != null)
                        {
                            if(doc.score[i] < entityScore) break;
                            i++;
                        }

                        if(i < doc.entities.length)
                        {
                            // in TOP ENTITIES, store and move if needed
                            if(doc.entities[i] == null)
                            {
                                doc.entities[i] = currentEntry.term;
                                doc.score[i] = entityScore;
                            }
                            else
                            {
                                String swapEntity = currentEntry.term;
                                double swapScore = entityScore;
                                while (i < doc.entities.length && swapEntity != null)
                                {
                                    String tempEntity = doc.entities[i];
                                    double tempScore = doc.score[i];
                                    doc.entities[i] = swapEntity;
                                    doc.score[i] = swapScore;
                                    swapEntity = tempEntity;
                                    swapScore = tempScore;
                                    i++;
                                }
                            }
                        }


                    }
                }

                writer.write(termInfo[1] + SEPARATOR + termInfo[2]);
                line = reader.readLine();
            }

            writer.write("\n");
            writer.flush();
            writer.close();
        }
        catch (Exception e) {e.printStackTrace();}

        lastFile[0].delete();
        from.delete();
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="Index Actions">
    /**
     * Build the inverted index base on the parser and path that was provided.
     * @throws IOException - if parser throws exception
     */
    public Index buildIndex() throws IOException
    {
        // Init Phase
        File postingDir = new File(postingPath + (parser.isStemming() ? "\\Postings_Stemmed" : "\\Postings_Not_Stemmed"));
        postingDir.mkdir();

        File tempDir = new File(postingDir + "\\temp");
        tempDir.mkdir();
        Partition[] partitions = Partition.values();
        for(Partition partition : partitions)
        {
            new File(tempDir + "\\" + partition).mkdir();
        }

        // parse all documents in corpus
        HashMap<String,Integer> docMapper = parseStage(tempDir);

        // merge temp posting into one ordered file
        mergeStage(tempDir);

        // Create Final Posing Stage
        Index index = postingStage(tempDir, postingDir,docMapper);

        tempDir.delete();

        return index;
    }

    /**
     * Load an inverted index that was built previously into memory
     * @param path - path that the posting were stored
     * @param stemming - is the index that was stored is stemmed or not.
     */
    public Index loadIndex(String path, boolean stemming)
    {
        File dictionaryFile = new File(path + (stemming ? "\\Postings_Stemmed" : "\\Postings_Not_Stemmed") + "\\termsDictionary.txt");
        File documentFile = new File(path + (stemming ? "\\Postings_Stemmed" : "\\Postings_Not_Stemmed") + "\\documentsDictionary.txt");

        if(!dictionaryFile.exists() || !documentFile.exists()) return null;

        HashMap<Integer, DocumentEntry> documentIndex = new HashMap<>(DOCS_INIT_CAPACITY);
        HashMap<String,Integer> docMapper = new HashMap<>(DOCS_INIT_CAPACITY);
        HashMap<String, DictionaryEntry> dictionary = new HashMap<>(TERMS_INIT_CAPACITY);
        HashSet<String> lowerCaseTerms = new HashSet<>(TERMS_INIT_CAPACITY / 4);
        double avgDocLen = 0;

        try(BufferedReader reader = new BufferedReader(new FileReader(dictionaryFile)))
        {
            // read dictionary index
            String entry;
            while (!(entry = reader.readLine()).equals(SEPARATOR))
            {
                String[] split = entry.split(SEPARATOR);

                dictionary.put(split[0],new DictionaryEntry(split));
            }

            // read closeList
            while ((entry = reader.readLine()) != null)
            {
                lowerCaseTerms.add(entry);
            }

        }
        catch (Exception e) { e.printStackTrace(); }

        try(BufferedReader reader = new BufferedReader(new FileReader(documentFile)))
        {
            // read document index
            String entry;
            while (!(entry = reader.readLine()).equals(SEPARATOR))
            {
                String[] split = entry.split(SEPARATOR);
                documentIndex.put(Integer.parseInt(split[0]),new DocumentEntry(split));
            }

            // read mapper
            while (!(entry = reader.readLine()).equals(SEPARATOR))
            {
                String[] split = entry.split(SEPARATOR);
                docMapper.put(split[0],Integer.parseInt(split[1]));
            }

            avgDocLen = ((entry = reader.readLine()) != null) ? Double.parseDouble(entry) : 0;
        }
        catch (Exception e) { e.printStackTrace(); }

        return new Index(documentIndex,dictionary,lowerCaseTerms,avgDocLen,docMapper);
    }

    /**
     * Delete an index that was built previously
     * @param path - path that the posting were stored
     * @param stemming - is the index that was stored is stemmed or not.
     */
    public void deleteIndex(String path, boolean stemming)
    {
        File deleteDir = new File(path + (stemming ? "\\Postings_Stemmed" : "\\Postings_Not_Stemmed"));
        if(!deleteDir.exists()) return;

        // deleteIndex
        File dictionaryFile = new File(deleteDir.getPath() + "\\termsDictionary.txt");
        dictionaryFile.delete();

        File documentFile = new File(deleteDir.getPath() + "\\documentsDictionary.txt");
        documentFile.delete();

        File postingDir = new File(deleteDir.getPath() + "\\posting");
        File[] postings = postingDir.listFiles();
        for(File posting : postings)
        {
            posting.delete();
        }
        postingDir.delete();

        deleteDir.delete();

        // reset
        dictionary = new HashMap<>(TERMS_INIT_CAPACITY);
        documentIndex = new HashMap<>(DOCS_INIT_CAPACITY);
        closeList = new HashSet<>();
        postingIdCounter = new AtomicInteger(0);
    }
    //</editor-fold>

}