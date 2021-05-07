package me.sergiobarriodelavega.xend.entities;

import java.io.Serializable;

/**
 * Stores data from a XMPPUser
 * @deprecated Should not be used anymore since the users info now is picked from their respective
 * VCard
 * @see me.sergiobarriodelavega.xend.App
 */
public class XMPPUser implements Serializable {
    private String userName, jid;

    public XMPPUser(String userName, String jid) {
        this.userName = userName;
        this.jid = jid;
    }

    public XMPPUser(String jid) {
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
