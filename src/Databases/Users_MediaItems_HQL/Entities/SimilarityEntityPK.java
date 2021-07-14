package Databases.Users_MediaItems_HQL.Entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
public class SimilarityEntityPK implements Serializable {
    private long mid1;
    private long mid2;

    @Column(name = "MID1")
    @Id
    public long getMid1() {
        return mid1;
    }

    public void setMid1(long mid1) {
        this.mid1 = mid1;
    }

    @Column(name = "MID2")
    @Id
    public long getMid2() {
        return mid2;
    }

    public void setMid2(long mid2) {
        this.mid2 = mid2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimilarityEntityPK that = (SimilarityEntityPK) o;
        return mid1 == that.mid1 &&
                mid2 == that.mid2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mid1, mid2);
    }
}
