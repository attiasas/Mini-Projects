package NLP.Search_Engine.src.Model.SearchEngine.Index.Parse.Rules;

import Model.SearchEngine.Index.CorpusDocument;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created By: Assaf Attias
 * On 23/11/2019
 * Description:     Parsing tokens that represents numbers (Percentage, prices and normal numbers), regardless of form.
 *
 * Patterns:        * NUMBER%                                    --> NUMBER%
 *                  * NUMBER percent(age)                        --> NUMBER%
 *                  * PRICE Dollars                              --> PRICE Dollars (price under 1M, can have fraction as well)
 *                  * $PRICE                                     --> PRICE (M) Dollars
 *                  * $PRICE (m/b)illion                         --> PRICE M Dollars
 *                  * PRICE (m/bn) Dollars                       --> PRICE M Dollars
 *                  * PRICE (m/b/tr)illion U.S Dollars           --> PRICE M Dollars
 *                  * NUMBER (fraction/thousand/million/billion) --> NUMBER(K/M/B)
 */
public class NumbersRules implements ParseRule {

    private Pattern numberPattern = Pattern.compile("^[$]?(-?(?:\\d+|\\d{1,3}(?:,\\d{3})+)(?:(\\.|,)\\d+)?)([%m]|[Bb][Nn])?$");
    private Pattern percentPattern = Pattern.compile("^([Pp][Ee][Rr][Cc][Ee][Nn][Tt](?:[Aa][Gg][Ee])?)$");
    private Pattern dollarPattern = Pattern.compile("^([Dd][Oo][Ll][Ll][Rr](?:[Ss])?)$");
    private Pattern illionPattern = Pattern.compile("^(([MmBb]|[Tt][Rr])[Ii][Ll][Ll][Ii][Oo][Nn])$");
    private Pattern thousandPattern = Pattern.compile("^([Tt][Hh][Oo][Uu][Ss][Aa][Nn][Dd])$");
    private Pattern fractionPattern = Pattern.compile("^([\\d]+/[\\d]+)$");
    private Pattern normalPattern = Pattern.compile("^(-?(?:\\d+|\\d{1,3}(?:,\\d{3})+)(?:(\\.|,)\\d+)?([m]|[Bb][Nn])?)$");

    //<editor-fold desc="Help Methods">
    /**
     * Convert a string to the number that it represents, removing ',' if needed
     * @param toParse - number in string to parse
     * @return - number value of string
     */
    private double toDouble(String toParse)
    {
        String parseRes = "";
        for(int i = 0; i < toParse.length(); i++)
        {
            if(toParse.charAt(i) != ',') parseRes += toParse.charAt(i);
        }
        double number = Double.parseDouble(parseRes);
        return number;
    }

    /**
     * takes input from handle methods and create a rule result while handling the decimal format
     * @param number - number that needs to be handled
     * @param end - the strings that will come after the number in the term
     * @param numOfTermsParsed - how much tokens parsed in the rule
     * @return rule result after the decimal format was handled
     */
    private RuleResult handleDecimal(double number, String end, int numOfTermsParsed)
    {
        if(number % 1 != 0)
        {
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            return new SingleResult(decimalFormat.format(number) + end,numOfTermsParsed);
        }
        return new SingleResult((int)(number) + end,numOfTermsParsed);
    }
    //</editor-fold>

    //<editor-fold desc="Handle Methods">
    /**
     * Handle price formats
     * @param termIndex - index of the current token to parse
     * @param tokens - context of the token
     * @return - result of the parse, null if not matching any format
     */
    private RuleResult handlePrice(int termIndex, String... tokens)
    {

        if(tokens[termIndex].startsWith("$"))
        {
            String toParse = tokens[termIndex].substring(1);
            // Pattern = $Price(m/bn)
            if(tokens[termIndex].endsWith("m")||tokens[termIndex].endsWith("bn")|| tokens[termIndex].endsWith("Bn")/*||tokens[termIndex].endsWith("BN")*/)
            {
                double number = toDouble(tokens[termIndex].endsWith("m") ? toParse.substring(0,toParse.length()-1) : toParse.substring(0,toParse.length()-2));
                if(tokens[termIndex].endsWith("bn")||tokens[termIndex].endsWith("Bn")) number *= 1000;
                return handleDecimal(number," M Dollars",1);
            }

            if(tokens.length - termIndex >=2)
            {
                // Pattern = $Price (m/b)illion
                Matcher illionMatcher = illionPattern.matcher(tokens[termIndex+1]);
                if(illionMatcher.find())
                {
                    double number = toDouble(toParse);
                    if(tokens[termIndex+1].startsWith("b") || tokens[termIndex+1].startsWith("B")) number *= 1000;
                    return handleDecimal(number," M Dollars",2);
                }
            }

            // Pattern = $Price
            double number = toDouble(toParse.endsWith("%")? toParse.substring(0,toParse.length()-1) : toParse);
            if(number < 1000000)
            {
                return handleDecimal(number," Dollars",1);
            }
            number /= 1000000;
            return handleDecimal(number," M Dollars",1);
        }

        if(tokens.length - termIndex >= 2)
        {
            Matcher dMatcher = dollarPattern.matcher(tokens[termIndex + 1]);
            if(dMatcher.find())
            {
                // Pattern = Price(m/bn) Dollars
                if(tokens[termIndex].endsWith("m")||tokens[termIndex].endsWith("bn")|| tokens[termIndex].endsWith("Bn")/*||tokens[termIndex].endsWith("BN")*/)
                {
                    double number = toDouble(tokens[termIndex].endsWith("m") ? tokens[termIndex].substring(0,tokens[termIndex].length()-1) : tokens[termIndex].substring(0,tokens[termIndex].length()-2));
                    if(tokens[termIndex].endsWith("bn")||tokens[termIndex].endsWith("Bn")) number *= 1000;
                    return handleDecimal(number," M Dollars",2);
                }

                // Pattern = Price Dollars
                double number = toDouble(tokens[termIndex]);
                if(number < 1000000)
                {
                    return handleDecimal(number," Dollars",2);
                }
                number /= 1000000;
                return handleDecimal(number," M Dollars",2);
            }

            if(tokens.length - termIndex >= 3)
            {
                Matcher dMathcer2 = dollarPattern.matcher(tokens[termIndex + 2]);
                Matcher fMatcher = fractionPattern.matcher(tokens[termIndex + 1]);
                if(dMathcer2.find())
                {
                    // Pattern = Price m/bn Dollars
                    if(tokens[termIndex + 1].equals("m")||tokens[termIndex + 1].equals("bn")|| tokens[termIndex + 1].equals("Bn")/*||tokens[termIndex].endsWith("BN")*/)
                    {
                        double number = toDouble(tokens[termIndex]);
                        if(tokens[termIndex + 1].equals("bn") || tokens[termIndex + 1].equals("Bn")) number *= 1000;
                        return handleDecimal(number," M Dollars",3);
                    }
                    // Pattern = Price fraction Dollars
                    if(fMatcher.find())
                    {
                        return new SingleResult(tokens[termIndex] + " " + tokens[termIndex + 1] + " Dollars",3);
                    }
                }

                if(tokens.length - termIndex >= 4)
                {
                    // Pattern = Price (m/b/tr)illion U.S. Dollars
                    Matcher dMatcher3 = dollarPattern.matcher(tokens[termIndex + 3]);
                    Matcher illionMatcher = illionPattern.matcher(tokens[termIndex + 1]);
                    if(dMatcher3.find() && illionMatcher.find() && (tokens[termIndex + 2].equals("U.S") || tokens[termIndex + 2].equals("u.s") || tokens[termIndex + 2].equals("U.S.") || tokens[termIndex + 2].equals("u.s.")))
                    {
                        double number = toDouble(tokens[termIndex]);
                        if(tokens[termIndex + 1].startsWith("b") || tokens[termIndex + 1].startsWith("B")) number *= 1000;
                        if(tokens[termIndex + 1].startsWith("t") || tokens[termIndex + 1].startsWith("T")) number *= 1000000;
                        return handleDecimal(number," M Dollars",4);
                    }
                }
            }
        }

        return null;
    }

    /**
     * Handle percent formats
     * @param termIndex - index of the current token to parse
     * @param tokens - context of the token
     * @return - result of the parse, null if not matching any format
     */
    private RuleResult handlePercent(int termIndex, String... tokens)
    {
        // Pattern = number%
        if(tokens[termIndex].endsWith("%"))
        {
            return new SingleResult(tokens[termIndex]);
        }
        if(tokens.length - termIndex >= 2 && !tokens[termIndex].startsWith("$"))
        {
            // Pattern = number percent(age)
            Matcher pMatcher = percentPattern.matcher(tokens[termIndex + 1]);
            if(pMatcher.find())
            {
                return new SingleResult(tokens[termIndex] + "%",2);
            }
        }

        return null;
    }

    /**
     * Handle normal numbers formats
     * @param termIndex - index of the current token to parse
     * @param tokens - context of the token
     * @return - result of the parse, null if not matching any format
     */
    private RuleResult handleNumbers(int termIndex, String... tokens)
    {
        Matcher nMatcher = normalPattern.matcher(tokens[termIndex]);
        if(nMatcher.find())
        {
            // Pattern = number(m/bn)
            if(tokens[termIndex].endsWith("m") || tokens[termIndex].endsWith("bn") || tokens[termIndex].endsWith("Bn")|| tokens[termIndex].endsWith("BN"))
            {
                double number = toDouble(tokens[termIndex].endsWith("m") ? tokens[termIndex].substring(0,tokens[termIndex].length()-1) : tokens[termIndex].substring(0,tokens[termIndex].length()-2));
                if(tokens[termIndex].endsWith("bn")||tokens[termIndex].endsWith("Bn")||tokens[termIndex].endsWith("BN")) return handleDecimal(number,"B",1);
                return handleDecimal(number,"M",1);
            }

            if(tokens.length - termIndex >= 2)
            {
                // Pattern = number (m/b)illion
                Matcher illionMatcher = illionPattern.matcher(tokens[termIndex + 1]);
                if(illionMatcher.find())
                {
                    double number = toDouble(tokens[termIndex]);
                    if(tokens[termIndex + 1].startsWith("M") || tokens[termIndex + 1].startsWith("m")) return handleDecimal(number,"M",2);
                    if(tokens[termIndex + 1].startsWith("B") || tokens[termIndex + 1].startsWith("b")) return handleDecimal(number,"B",2);
                }
                // Pattern = number Thousand
                Matcher thousandMatcher = thousandPattern.matcher(tokens[termIndex + 1]);
                if(thousandMatcher.find())
                {
                    double number = toDouble(tokens[termIndex]);
                    return handleDecimal(number,"K",2);
                }
                // Pattern = number Fraction
                Matcher fractionMatcher = fractionPattern.matcher(tokens[termIndex + 1]);
                if(fractionMatcher.find())
                {
                    return new SingleResult(tokens[termIndex] + " " + tokens[termIndex + 1],2);
                }
            }

            // Pattern = number
            double number = toDouble(tokens[termIndex]);
            if(number < 1000)
            {
                return handleDecimal(number,"",1);
            }
            else if(number < 1000000)
            {
                // K
                number /= 1000;
                return handleDecimal(number,"K",1);
            }
            else if(number < 1000000000)
            {
                // M
                number /= 1000000;
                return handleDecimal(number,"M",1);
            }
            else
            {
                // B
                number /= 1000000000;
                return handleDecimal(number,"B",1);
            }
        }

        return null;
    }
    //</editor-fold>

    @Override
    public RuleResult executeRule(CorpusDocument document, int termIndex, String... tokens)
    {
        Matcher numberMatcher = numberPattern.matcher(tokens[termIndex]);
        if(numberMatcher.find())
        {
            RuleResult res;
            res = handlePercent(termIndex,tokens);
            if(res != null) return res;
            res = handlePrice(termIndex,tokens);
            if(res != null) return res;
            res = handleNumbers(termIndex,tokens);
            return res;
        }

        return null;
    }
}
