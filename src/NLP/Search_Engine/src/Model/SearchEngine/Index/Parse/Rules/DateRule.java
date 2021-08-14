package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 22/11/2019
 * Description:     Parsing token that represents Dates.
 *                  Month will be recognise in short or long writing, regardless of upper/lower form.
 *
 * Patterns:        * DD Month      --> MM-DD
 *                  * Month DD      --> MM-DD
 *                  * Month YYYY    --> YYYY-MM
 *                  * DD Month YYYY --> [ YYYY-MM , MM-DD ]
 */
public class DateRule implements ParseRule
{
    private final String MONTHS = "([Jj][Aa][Nn](?:[Uu][Aa][Rr][Yy])?|[Ff][Ee][Bb](?:[Rr][Uu][Aa][Rr][Yy])?|[Mm][Aa][Rr](?:[Cc][Hh])?|[Aa][Pp][Rr](?:[Ii][Ll])?|[Mm][Aa][Yy]|[Jj][Uu][Nn](?:[Ee])?|[Jj][Uu][Ll](?:[Yy])?|[Aa][Uu][Gg](?:[Uu][Ss][Tt])?|[Ss][Ee][Pp](?:[Tt][Ee][Mm][Bb][Ee][Rr])?|[Oo][Cc][Tt](?:[Oo][Bb][Ee][Rr])?|[Nn][Oo][Vv](?:[Ee][Mm][Bb][Ee][Rr])?|[Dd][Ee][Cc](?:[Ee][Mm][Bb][Ee][Rr])?)";
    private final String DAYS = "(0[1-9]|[12]\\d|3[01])";
    private final String YEARS = "(\\d{4})";

    private Pattern mPattern = Pattern.compile("^" + MONTHS + "$");
    private Pattern dPattern = Pattern.compile("^" + DAYS + "$");
    private Pattern yPattern = Pattern.compile("^" + YEARS + "$");

    /**
     * Turn month alphabet writing to numeric writing
     * @param s - month to convert
     * @return - numeric writing of the month
     */
    private String valueOf(String s)
    {
        switch (s)
        {
            case "JAN":
            case "JANUARY": return "01";
            case "FEB":
            case "FEBRUARY": return "02";
            case "MAR":
            case "MARCH": return "03";
            case "APR":
            case "APRIL": return "04";
            case "MAY": return "05";
            case "JUN":
            case "JUNE": return "06";
            case "JUL":
            case "JULY": return "07";
            case "AUG":
            case "AUGUST": return "08";
            case "SEP":
            case "SEPTEMBER": return "09";
            case "OCT":
            case "OCTOBER": return "10";
            case "NOV":
            case "NOVEMBER": return "11";
            case "DEC":
            case "DECEMBER": return "12";
        }

        return null;
    }

    @Override
    public RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens)
    {
        if(tokens == null || tokens.length - termIndex < 2) return null;

        Matcher firstMonthMatcher = mPattern.matcher(tokens[termIndex]);

        if(firstMonthMatcher.find())
        {
            Matcher secondDayMatcher = dPattern.matcher(tokens[termIndex +1]);
            if(secondDayMatcher.find())
            {
                return new SingleResult("" + valueOf(tokens[termIndex].toUpperCase()) + "-" + tokens[termIndex +1],2);
            }

            Matcher secondYearMatcher = yPattern.matcher(tokens[termIndex +1]);
            if (secondYearMatcher.find())
            {
                return new SingleResult("" + tokens[termIndex +1] + "-" + valueOf(tokens[termIndex].toUpperCase()),2);
            }
        }

        Matcher firstDayMatcher = dPattern.matcher(tokens[termIndex]);
        Matcher secondMonthMatcher = mPattern.matcher(tokens[termIndex +1]);

        if(firstDayMatcher.find() && secondMonthMatcher.find())
        {
            if(tokens.length - termIndex > 3)
            {
                Matcher thirdYearMatcher = yPattern.matcher(tokens[termIndex + 2]);
                if(thirdYearMatcher.find())
                {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("" + valueOf(tokens[termIndex +1].toUpperCase()) + "-" + tokens[termIndex]);
                    result.add("" + tokens[termIndex + 2] + "-" + valueOf(tokens[termIndex + 1].toUpperCase()));
                    return new MultipleResult(result,3);
                }
            }

            return new SingleResult("" + valueOf(tokens[termIndex +1].toUpperCase()) + "-" + tokens[termIndex],2);
        }

        return null;
    }
}
