package Databases.Users_MediaItems_HQL.Entities;

import javax.persistence.*;
import java.util.Objects;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
@Entity
@Table(name = "MEDIAITEMS", schema = "ATTIASAS", catalog = "")
public class Mediaitems {
    private long mid;
    private String title;
    private Long prodYear;
    private Short titleLength;

    @Id
    @Column(name = "MID")
    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    @Basic
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "PROD_YEAR")
    public Long getProdYear() {
        return prodYear;
    }

    public void setProdYear(Long prodYear) {
        this.prodYear = prodYear;
    }

    @Basic
    @Column(name = "TITLE_LENGTH")
    public Short getTitleLength() {
        return titleLength;
    }

    public void setTitleLength(Short titleLength) {
        this.titleLength = titleLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mediaitems that = (Mediaitems) o;
        return mid == that.mid &&
                Objects.equals(title, that.title) &&
                Objects.equals(prodYear, that.prodYear) &&
                Objects.equals(titleLength, that.titleLength);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid, title, prodYear, titleLength);
    }

}
