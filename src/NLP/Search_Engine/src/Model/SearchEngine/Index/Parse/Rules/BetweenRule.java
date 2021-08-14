package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 30/11/2019
 * Description:     Parsing tokens that represent a number range, regardless of upper/lower form.
 *                  This class needs to be injected with number parse rule to be able to parse the numbers in the range.
 *
 * Patterns:        * between NUMBER and NUMBER --> NUMBER-NUMBER
 */
public class BetweenRule implements ParseRule
{
    private ParseRule numberRule;

    private Pattern betweenPattern = Pattern.compile("^[Bb][Ee][Tt][Ww][Ee][Ee][Nn]$");
    private Pattern andPattern = Pattern.compile("^[Aa][Nn][Dd]$");

    /**
     * Constructor
     * @param numberRule - parse rule that can parse numbers
     */
    public BetweenRule(ParseRule numberRule) {
        this.numberRule = numberRule;
    }

    @Override
    public RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens)
    {
        if(tokens.length - termIndex >= 4)
        {
            Matcher betweenMatcher = betweenPattern.matcher(tokens[termIndex]);
            if(betweenMatcher.find())
            {
                SingleResult firstNumber = (SingleResult)numberRule.executeRule(document,termIndex+1,tokens);
                if(firstNumber != null && tokens.length - termIndex - firstNumber.numOfTokensParsed > 1)
                {
                    Matcher andMatcher = andPattern.matcher(tokens[termIndex + firstNumber.numOfTokensParsed]);
                    if(andMatcher.find())
                    {
                        SingleResult secondNumber = (SingleResult)numberRule.executeRule(document,termIndex + firstNumber.numOfTokensParsed + 2,tokens);
                        if(secondNumber != null)
                        {
                            // Pattern = Between NUMBER and NUMBER
                            return new SingleResult(firstNumber.term + "-" + secondNumber.term,firstNumber.numOfTokensParsed + secondNumber.numOfTokensParsed + 2);
                        }
                    }
                }
            }
        }
        return null;
    }
}
