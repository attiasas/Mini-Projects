package NLP.Search_Engine.src.Model.SearchEngine.Index.Stem;


import org.tartarus.snowball.ext.PorterStemmer;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description:     Stemming using Porter's Stemmer
 *
 * Using SnowBall Stemmer from jar (https://snowballstem.org/)
 */
public class Stemmer implements TermStemmer
{
    PorterStemmer stemmer;

    /**
     * Constructor
     */
    public Stemmer()
    {
        stemmer = new PorterStemmer();
    }

    @Override
    public String stem(String term)
    {
        stemmer.setCurrent(term);
        stemmer.stem();
        term = stemmer.getCurrent();

        return term;
    }
}
