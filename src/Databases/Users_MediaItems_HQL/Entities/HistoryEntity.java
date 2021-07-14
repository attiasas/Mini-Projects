package Databases.Users_MediaItems_HQL.Entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
@Entity
@Table(name = "HISTORY", schema = "ATTIASAS", catalog = "")
@IdClass(HistoryEntityPK.class)
public class HistoryEntity {
    private long userid;
    private long mid;
    private Timestamp viewtime;

    @Id
    @Column(name = "USERID")
    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    @Id
    @Column(name = "MID")
    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    @Id
    @Column(name = "VIEWTIME")
    public Timestamp getViewtime() {
        return viewtime;
    }

    public void setViewtime(Timestamp viewtime) {
        this.viewtime = viewtime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryEntity that = (HistoryEntity) o;
        return userid == that.userid &&
                mid == that.mid &&
                Objects.equals(viewtime, that.viewtime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, mid, viewtime);
    }
}
