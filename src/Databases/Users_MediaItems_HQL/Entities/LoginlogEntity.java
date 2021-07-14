package Databases.Users_MediaItems_HQL.Entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Created By: Assaf, On 06/12/2020
 * Description:
 */
@Entity
@Table(name = "LOGINLOG", schema = "ATTIASAS", catalog = "")
@IdClass(LoginlogEntityPK.class)
public class LoginlogEntity {
    private long userid;
    private Timestamp logintime;

    @Id
    @Column(name = "USERID")
    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    @Id
    @Column(name = "LOGINTIME")
    public Timestamp getLogintime() {
        return logintime;
    }

    public void setLogintime(Timestamp logintime) {
        this.logintime = logintime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginlogEntity that = (LoginlogEntity) o;
        return userid == that.userid &&
                Objects.equals(logintime, that.logintime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userid, logintime);
    }
}
