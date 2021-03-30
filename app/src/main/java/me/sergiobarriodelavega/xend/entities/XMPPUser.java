package me.sergiobarriodelavega.xend.entities;

/**
 * Stores data from a XMPPUser
 */
public class XMPPUser {
    private String userName, email;

    public XMPPUser(String userName, String email) {
        this.userName = userName;
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
