package Databases.Users_MediaItems_HQL.test;

import java.util.Objects;

/**
 * Created By: Assaf, On 13/12/2020
 * Description:
 */
public class AdministratorsEntity {
    private long adminid;
    private String username;
    private String password;

    public long getAdminid() {
        return adminid;
    }

    public void setAdminid(long adminid) {
        this.adminid = adminid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdministratorsEntity that = (AdministratorsEntity) o;
        return adminid == that.adminid &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminid, username, password);
    }
}
