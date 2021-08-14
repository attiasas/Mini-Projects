package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 11/12/2019
 * Description:     Parsing AlphaBet tokens that are Acronyms and has '.'.
 *                  Parsing AlphaBet tokens that has apostrophe (') in them.
 *
 * Pattern:         * X.X.X     --> XXX (removing the dot)
 *                  * XX'X'X    --> XXXX (removing the apostrophe)
 */
public class RemoverRule implements ParseRule
{
    private Pattern rulePattern = Pattern.compile("^[a-zA-Z]+[.'][a-zA-Z]+([.][a-zA-Z])*$");

    @Override
    public RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens)
    {
        Matcher matcher = rulePattern.matcher(tokens[termIndex]);
        if(matcher.find())
        {
            if(tokens[termIndex].indexOf('.') != -1)
            {
                return new SingleResult(tokens[termIndex].replace(".",""));
            }
            else
            {
                return new SingleResult(tokens[termIndex].replace("'",""));
            }
        }
        return null;
    }
}
