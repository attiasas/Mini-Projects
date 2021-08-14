package NLP.Search_Engine.src.Model.SearchEngine.Index;

import java.util.Objects;

/**
 * Created By: Assaf Attias
 * On 12/11/2019
 * Description: a representation of an immutable CorpusDocument and all the metadata that associated with it.
 *              a document considers equals if id are equals. (ID needs to be unique)
 */
public class CorpusDocument
{
    public final String id;
    public final String date; // [Day Month Year] format
    public final String title;
    public final String text;
    public final String source;

    public CorpusDocument(String id, String date, String title, String text, String source)
    {
        this.id = id;
        this.date = date;
        this.title = title;
        this.text = text;
        this.source = source;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CorpusDocument document = (CorpusDocument) o;
        return Objects.equals(id, document.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
