package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse;

import Model.SearchEngine.Index.CorpusDocument;
import Model.SearchEngine.Index.Read.CorpusReader;
import Model.SearchEngine.Index.Parse.Rules.*;
import Model.SearchEngine.Index.Stem.TermStemmer;

import java.io.*;
import java.util.*;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description:             * An implementation of term parser capable of parsing terms from a given document.
 *                          * In order to stem, This parser needs to be supplied with a stemmer.
 *                          * This parser comes with 5 inner rules, all rules will be apply one after another
 *                            until a rule produce a term, changing the order of the rules will result different terms
 *                            adding new rules or supplying a list of rules instead is allowed.
 *                          * This class can be provided with a 'stop words' list that will be filtered after parsing.
 *                          * Documents with empty text will not be passed in the stream.
 *
 * Separator Format:        * whitespace
 *                          * {+ : ; = | ! * " [ ] ( ) ?}
 *                          * {. -} : only when 2 or more are one after another.
 *                          * {. , -} : only if not followed by alphaBet char.
 *                          * { ' } : only if in a start of a word.
 *                          * { / } : only if followed by alphaBet char.
 *                          * 's : at an end of a word
 *
 * Inner Rules (by order):  1. Date Rule
 *                          2. Number Rule
 *                          3. Between Rule
 *                          4. Word & Phrase & Entities Rule
 *                          5. Remove Rule
 */
public class Parse implements TermParser
{
    private CorpusReader reader;
    public HashSet<String> stopWords;
    private int numOfDocumentParsed;

    private TermStemmer stemmer;
    private boolean stemming;

    private ArrayList<ParseRule> parseRules;
    public WordPhraseEntitiesRule wordPhraseEntitiesRule;

    public static final String DELIMITERS = "(([\\s+:;\"`()\\[\\]?|!*=]+|[.-]{2,}|[.',-](?!\\w)|\\B(['])(?=\\w)|[/](?=[a-zA-Z])|('s)(?!\\w))+)";

    //<editor-fold desc="Constructors">
    /**
     * Constructor
     * @param reader - Corpus reader to decorate
     * @param stopWords - A list of stop words that will be filter after parsing
     * @param stemmer - stemmer for stemming, if null stemming will be set to false
     * @param stemming - should the parser use its stemmer or not
     */
    public Parse(CorpusReader reader,HashSet<String> stopWords, TermStemmer stemmer,boolean stemming)
    {
        this.reader = reader;

        this.stemmer = stemmer;
        setStemming(stemming);

        this.stopWords = stopWords == null ? new HashSet<>() : stopWords;

        parseRules = new ArrayList<>();
        addAllRules();

    }

    public Parse(CorpusReader reader,HashSet<String> stopWords, TermStemmer stemmer)
    {
        this(reader,stopWords,stemmer,stemmer == null ? false : true);
    }

    public Parse(CorpusReader reader, String stopWordsPath, TermStemmer stemmer) throws FileNotFoundException
    {
        this(reader,Parse.getStopWords(stopWordsPath),stemmer);
    }

    public Parse(CorpusReader reader, String stopWordsPath) throws FileNotFoundException { this(reader,stopWordsPath,null); }
    //</editor-fold>

    //<editor-fold desc="Help Methods">

    /**
     * This flag decides if the parser will stem terms or not.
     * @param stemming - true if the parser will stem, false otherwise.
     */
    public void setStemming(boolean stemming) {
        if(stemmer == null) return;
        this.stemming = stemming;
    }

    /**
     * Inner Method to set the default parsing rules
     */
    private void addAllRules()
    {
        parseRules.add(new DateRule()); // 1st rule
        ParseRule numberRule = new NumbersRules();
        parseRules.add(numberRule); // 2nd rule
        parseRules.add(new BetweenRule(numberRule)); // 3rd rule
        wordPhraseEntitiesRule = new WordPhraseEntitiesRule(numberRule);
        parseRules.add(wordPhraseEntitiesRule); // 4th rule
        parseRules.add(new RemoverRule()); // 5th rule - (extra 2)
    }

    /**
     * Get the list of stop words from a given file, each word needs to be in lower case and
     * only one word per row.
     * @param path - a path to the stop words file
     * @return - a list of stop words that was parsed from the file
     * @throws FileNotFoundException
     */
    public static HashSet<String> getStopWords(String path) throws FileNotFoundException
    {
        HashSet<String> stopWords = new HashSet<>();
        File f = new File(path);
        if(!f.exists() || !f.isFile()) return stopWords;

        Scanner scanner = new Scanner(f);

        while (scanner.hasNext())
        {
            stopWords.add(scanner.nextLine());
        }

        scanner.close();

        return stopWords;
    }

    /**
     * Set the rules of the parser, the order of the rules is important, rules will be
     * apply from the first (index 0) to the last.
     * @param parseRules - not null, rules for parsing
     */
    public void setParseRules(ArrayList<ParseRule> parseRules)
    {
        if(parseRules == null) return;
        this.parseRules = parseRules;
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    @Override
    public boolean hasNext()
    {
        return reader.hasNext() || wordPhraseEntitiesRule.hasEntities();
    }

    @Override
    public List<TermInDocument> getEntities() {
        return wordPhraseEntitiesRule.getEntities();
    }

    @Override
    public boolean isStemming() {
        return stemming;
    }

    @Override
    public int getNumberOfDocsParsed() {
        return numOfDocumentParsed;
    }
    //</editor-fold>

    @Override
    public List<String> parseQuery(String q)
    {
        if(q == null) return null;

        String[] toParse = q.split(DELIMITERS);
        int currentParseIndex = 0;

        ArrayList<String> bagOfWords = new ArrayList<>(toParse.length);

        while (currentParseIndex < toParse.length)
        {
            int parsedTerms = 1;

            // Apply Rules
            for(int i = 0;i < parseRules.size(); i++)
            {
                ParseRule.RuleResult ruleResult = parseRules.get(i).executeRule(null, currentParseIndex, toParse);

                if (ruleResult != null)
                {
                    // cached result
                    if(ruleResult instanceof ParseRule.MultipleResult)
                    {
                        ParseRule.MultipleResult multipleResult = ((ParseRule.MultipleResult) ruleResult);
                        for(int t = 0; t < multipleResult.terms.size(); t++)
                        {
                            if(!stopWords.contains(multipleResult.terms.get(t).toLowerCase())) bagOfWords.add(stemming ? stemmer.stem(multipleResult.terms.get(t)) : multipleResult.terms.get(t));
                        }
                    }
                    else if(!stopWords.contains(((ParseRule.SingleResult)ruleResult).term.toLowerCase()))
                    {
                        bagOfWords.add(stemming ? stemmer.stem(((ParseRule.SingleResult)ruleResult).term) : ((ParseRule.SingleResult)ruleResult).term);
                    }

                    parsedTerms = ruleResult.numOfTokensParsed;
                    break;
                }
            }

            currentParseIndex += parsedTerms;
        }

        return bagOfWords;
    }

    @Override
    public BagOfTerms nextTermsBag() throws IOException
    {
        CorpusDocument currentDocument = null;

        // get next document
        boolean foundDocumentToParse = false; // for document without text
        while (!foundDocumentToParse && reader.hasNext())
        {
            currentDocument = reader.nextDocument();
            if(currentDocument.text != null && currentDocument.text.length() > 0) foundDocumentToParse = true;
        }
        if(!foundDocumentToParse) return null;

        numOfDocumentParsed++;

        // Parse
        String[] toParse = currentDocument.text.split(DELIMITERS);
        int currentParseIndex = 0;

        ArrayList<String> bagOfWords = new ArrayList<>(toParse.length);

        while (currentParseIndex < toParse.length)
        {
            int parsedTerms = 1;

            // Apply Rules
            for(int i = 0;i < parseRules.size(); i++)
            {
                ParseRule.RuleResult ruleResult = parseRules.get(i).executeRule(currentDocument, currentParseIndex, toParse);

                if (ruleResult != null)
                {
                    // cached result
                    if(ruleResult instanceof ParseRule.MultipleResult)
                    {
                        ParseRule.MultipleResult multipleResult = ((ParseRule.MultipleResult) ruleResult);
                        for(int t = 0; t < multipleResult.terms.size(); t++)
                        {
                            if(!stopWords.contains(multipleResult.terms.get(t).toLowerCase())) bagOfWords.add(stemming ? stemmer.stem(multipleResult.terms.get(t)) : multipleResult.terms.get(t));
                        }
                    }
                    else if(!stopWords.contains(((ParseRule.SingleResult)ruleResult).term.toLowerCase()))
                    {
                        bagOfWords.add(stemming ? stemmer.stem(((ParseRule.SingleResult)ruleResult).term) : ((ParseRule.SingleResult)ruleResult).term);
                    }

                    parsedTerms = ruleResult.numOfTokensParsed;
                    break;
                }
            }

            currentParseIndex += parsedTerms;
        }

        return new BagOfTerms(currentDocument,bagOfWords);
    }

    @Override
    public void close() throws IOException
    {
        reader.close();
    }
}
