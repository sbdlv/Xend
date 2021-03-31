package me.sergiobarriodelavega.xend.entities;

/**
 * Stores data from a XMPPUser
 */
public class XMPPUser {
    private String userName, jid;

    public XMPPUser(String userName, String jid) {
        this.userName = userName;
        this.jid = jid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }


}
