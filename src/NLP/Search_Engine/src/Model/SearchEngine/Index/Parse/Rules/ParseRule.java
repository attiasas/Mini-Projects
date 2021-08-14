package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;

import java.util.ArrayList;

/**
 * Created By: Assaf Attias
 * On 21/11/2019
 * Description: representing a parsing rule, capable of receiving a token and its context (array of split text - tokens)
 *              and producing terms (one - single result, more - multiple result) from it, allowing to skip the
 *              tokens that were parsed already
 */
public interface ParseRule
{
    /**
     * Execute the parse rule and produce terms
     * @param document - the document that is currently parsed to store if needed
     * @param termIndex - the current token index that needs to be parsed
     * @param tokens - the context of the token, the list of all the potential tokens
     * @return - a rule result base on the execution
     */
    RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens);

    //<editor-fold desc="Rule Result Classes">

    /**
     * Abstract class that defines a result
     */
    abstract class RuleResult
    {
        public int numOfTokensParsed;

        public RuleResult(int numOfTokensParsed)
        {
            if(numOfTokensParsed <= 0) throw new IllegalArgumentException("Number Of Token Parsed must be positive, if none were parsed return null instead");
            this.numOfTokensParsed = numOfTokensParsed;
        }
    }

    /**
     * A result of a single term that was parsed
     */
    class SingleResult extends RuleResult
    {
        public String term;
        public SingleResult(String term, int numOfTokensParsed)
        {
            super(numOfTokensParsed);
            this.term = term;
        }

        public SingleResult(String term) { this(term,1);}
    }

    /**
     * A result of multiple terms that were parsed
     */
    class MultipleResult extends RuleResult
    {
        public ArrayList<String> terms;

        public MultipleResult(ArrayList<String> terms, int numOfTokensParsed)
        {
            super(numOfTokensParsed);
            this.terms = terms;
        }

        public MultipleResult(ArrayList<String> terms) { this(terms,1); }
    }
    //</editor-fold>
}
