package me.sergiobarriodelavega.xend.entities;

import java.io.Serializable;

/**
 * Stores data from a XMPPUser
 */
public class XMPPUser implements Serializable {
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

    //TODO For optimization it could be possible to save only the domain String and concatenate 'username + @ + domain' on getJid
    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }


}
