package org.myorg.demo;

@Table("user")
public class User {

    @Column(name = "id", value = "EQ")
    private int id;

    @Column(name = "user_name", value = "LIKE")
    private String userName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
