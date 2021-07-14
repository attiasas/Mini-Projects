package Databases.Users_MediaItems_HQL.test;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created By: Assaf, On 13/12/2020
 * Description:
 */
public class HistoryEntity {
    private long userid;
    private long mid;
    private Timestamp viewtime;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

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
