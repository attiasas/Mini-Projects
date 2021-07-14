package Databases.Users_MediaItems_HQL.Entities;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
public class HistoryEntityPK implements Serializable {
    private long userid;
    private long mid;
    private Timestamp viewtime;

    @Column(name = "USERID")
    @Id
    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    @Column(name = "MID")
    @Id
    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    @Column(name = "VIEWTIME")
    @Id
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
        HistoryEntityPK that = (HistoryEntityPK) o;
        return userid == that.userid &&
                mid == that.mid &&
                Objects.equals(viewtime, that.viewtime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, mid, viewtime);
    }
}
