package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;
import Model.SearchEngine.Index.Parse.TermParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 06/12/2019
 * Description:     Parsing tokens that represents words, phrases or entities.
 *                  This class needs the context of all the documents in the corpus to determine entities.
 *                  confirmed entities will be send through a buffer of TermInDocument that can be offered from a method.
 *                  When parsing entities all of its sub words will be parsed as terms as well.
 *                  The class also parsing ranges of numbers, it needs to be injected with number parsing rule.
 *
 * Patterns:        * WORD - alphabet characters only
 *                  * (Phrase) WORD-WORD
 *                  * (Phrase) WORD-WORD-WORD
 *                  * (Phrase) WORD-NUMBER
 *                  * (Phrase) NUMBER-WORD
 *                  * (Phrase) NUMBER-NUMBER
 *                  * Entity - stream of words (two or more) that starts with upper case and appearing in two or more documents.
 */
public class WordPhraseEntitiesRule implements ParseRule
{
    private final String WORD = "([a-zA-Z]+)";
    private final String NUMBER = "(" + "((?:\\d+|\\d{1,3}(?:,\\d{3})+)(?:(\\.|,)\\d+)?)([%m]|[Bb][Nn])?" + ")";
    private final String TWO_WORDS = "(" + WORD + "[-]" + WORD + ")|(" + NUMBER + "[-]" + WORD + ")|(" + WORD + "[-]" + NUMBER + ")|(" + NUMBER + "[-]" + NUMBER + ")";
    private final String THREE_WORDS = "(" + WORD + "[-]" + WORD + "[-]" + WORD + ")";

    private Pattern totalPattern = Pattern.compile("^(" + WORD + "|" + TWO_WORDS + "|" + THREE_WORDS + ")$");
    private Pattern entityPattern = Pattern.compile("^([A-Z]([a-z])*|[A-Z]([a-z])*[-][A-Z]([a-z])*|[A-Z]([a-z])*[-][A-Z]([a-z])*[-][A-Z]([a-z])*)$");

    private ParseRule numberRule;

    public HashMap<String,Object[]> openEntites;
    public HashSet<String> close;
    private LinkedList<TermParser.TermInDocument> buffer;

    private final int INIT_CAPACITY = 1000000;

    /**
     * Constructor
     * @param numberRule - parse rule that can handle numbers
     */
    public WordPhraseEntitiesRule(ParseRule numberRule)
    {
        this.numberRule = numberRule;

        close = new HashSet<>(INIT_CAPACITY);
        openEntites = new HashMap<>(INIT_CAPACITY);
    }

    //<editor-fold desc="Entity Methods">

    /**
     * Check if the rule confirmed more entities from older documents
     * @return - true if there are entities in the buffer
     */
    public boolean hasEntities() { return buffer != null; }

    /**
     * return all the confirmed entities from older documents after executing the rule
     * @return - list of entities terms and the source document of them
     */
    public List<TermParser.TermInDocument> getEntities()
    {
        LinkedList<TermParser.TermInDocument> res = buffer;
        buffer = null;
        return res;
    }
    //</editor-fold>

    //<editor-fold desc="Phrase Methods">

    /**
     * Handle and get all the sub tokens of the phrase, parsing number if needed
     * @param document - current document
     * @param phraseSplit - the phrase tokens
     * @return - list of sub terms of the phrase
     */
    private ArrayList<String> handlePhrase(CorpusDocument document, String[] phraseSplit)
    {
        ArrayList<String> res = new ArrayList<>();
        int parsed = 0;

        while (parsed < phraseSplit.length)
        {
            SingleResult numberProcessed = (SingleResult)numberRule.executeRule(document,parsed,phraseSplit);
            if(numberProcessed == null)
            {
                res.add(phraseSplit[parsed]);
                parsed++;
            }
            else
            {
                res.add(numberProcessed.term);
                parsed += numberProcessed.numOfTokensParsed;
            }
        }

        return res;
    }

    /**
     * merge the sub terms to the final form of the phrase after handling parsing them.
     * @param phrase - sub tokens of the phrase, after parsing them to terms
     * @return - the phrase that will become the term
     */
    private String mergeToPhrase(ArrayList<String> phrase)
    {
        String parsedPhrase = "";
        for(int i = 0; i < phrase.size(); i++)
        {
            parsedPhrase += phrase.get(i);
            if(i < phrase.size() - 1) parsedPhrase += "-";
        }

        return parsedPhrase;
    }
    //</editor-fold>

    @Override
    public RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens)
    {
        Matcher ruleMatch = totalPattern.matcher(tokens[termIndex]);
        if(ruleMatch.find())
        {
            boolean gatheringTokens = true;
            int numOfTokensParsed = 0;
            ArrayList<String> gatheredTokens = new ArrayList<>();
            ArrayList<String> entityTokens = new ArrayList<>();

            while (tokens.length - termIndex - numOfTokensParsed >= 2 && gatheringTokens)
            {
                // interpret tokens for entity
                String token = tokens[termIndex + numOfTokensParsed];

                Matcher entityMatcher = entityPattern.matcher(token);
                if(entityMatcher.find())
                {
                    if(token.indexOf('-') != -1)
                    {
                        // Phrase
                        String[] phraseSplit = token.split("-");
                        ArrayList<String> phrase = handlePhrase(document,phraseSplit);

                        gatheredTokens.addAll(phrase);
                        gatheredTokens.add(mergeToPhrase(phrase));

                        entityTokens.addAll(phrase);
                    }
                    else
                    {
                        // Normal
                        gatheredTokens.add(token);
                        entityTokens.add(token);
                    }

                    numOfTokensParsed++;
                }
                else
                {
                    if(gatheredTokens.isEmpty())
                    {
                        // not an entity and nothing is parsed
                        if(token.indexOf('-') != -1)
                        {
                            // Phrase
                            String[] phraseSplit = token.split("-");
                            ArrayList<String> phrase = handlePhrase(document,phraseSplit);
                            phrase.add(mergeToPhrase(phrase));

                            return new MultipleResult(phrase);
                        }
                        else
                        {
                            // Normal
                            return new SingleResult(token);
                        }
                    }

                    gatheringTokens = false;
                }
            }

            if(entityTokens.size() > 1)
            {
                // an entity
                String entityId = "";
                for(int i = 0; i < entityTokens.size(); i++)
                {
                    entityId += entityTokens.get(i);
                    if(i < entityTokens.size() - 1) entityId += " ";
                }
                entityId.toLowerCase();

                if(document == null)
                {
                    // query
                    gatheredTokens.add(entityId);
                }
                else
                {
                    if(close.contains(entityId))
                    {
                        // entity already in close list
                        gatheredTokens.add(entityId);
                    }
                    else if(openEntites.containsKey(entityId))
                    {
                        Object[] openEntity = openEntites.get(entityId);
                        if(openEntity[0].equals(document.id))
                        {
                            // more than one time in the same document
                            openEntity[1] = (Integer)openEntity[1] + 1;
                        }
                        else
                        {
                            // second document found - confirmed entity
                            Object[] map = (openEntites.remove(entityId));

                            if(buffer == null) buffer = new LinkedList<>();

                            for(int i = 0; i < (Integer)map[1]; i++)
                            {
                                buffer.add(new TermParser.TermInDocument((String)map[0],entityId)); // cached doc
                            }

                            gatheredTokens.add(entityId); // new doc
                            close.add(entityId);
                        }
                    }
                    else
                    {
                        // first time seen add to open
                        openEntites.put(entityId,new Object[]{document.id,1});
                    }
                }

                return new MultipleResult(gatheredTokens,numOfTokensParsed);
            }
            else if (numOfTokensParsed == 0)
            {
                // not an entity and nothing is parsed
                if(tokens[termIndex].indexOf('-') != -1)
                {
                    // Phrase
                    String[] phraseSplit = tokens[termIndex].split("-");
                    ArrayList<String> phrase = handlePhrase(document,phraseSplit);
                    phrase.add(mergeToPhrase(phrase));

                    return new MultipleResult(phrase);
                }
                else
                {
                    // Normal
                    return new SingleResult(tokens[termIndex]);
                }
            }
            else
            {
                // entityTokens = 1 and already processed
                return new SingleResult(entityTokens.get(0));
            }
        }

        return null;
    }

}
