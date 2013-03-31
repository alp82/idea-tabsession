package com.acamasystems.idea.plugin.tabsession;

import org.jdom.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * created by alp (30.03.2013)
 */
public class SessionState {

    public class Session {
        public String name;
        public List<String> files = new ArrayList<String>();
        public String focusedFile;

        public Session(String name) {
            this.name = name;
        }

        public String toString() {
            String result = "Session: " + name + "\n";
            for (String file : files) {
                result += file + ", ";
            }
            result += "selected: " + focusedFile;
            return result;
        }

        public void addFileIfNew(String path) {
            if(!files.contains(path)) {
                files.add(path);
            }
        }
    }

    public List<Session> sessions = new ArrayList<Session>();

    public SessionState() {
    }

    public ArrayList<String> getSessionNames() {
        ArrayList<String> names = new ArrayList<String>();
        for (Session session : sessions) {
            names.add(session.name);
        }
        return names;
    }

    public Session getOrCreateSessionByName(String name) {
        for (Session session : sessions) {
            if(session.name.equals(name)) {
                return session;
            }
        }

        Session session = new Session(name);
        sessions.add(session);
        return session;
    }

    public String toString() {
        String result = "All Sessions:\n";
        for (Session session : sessions) {
            result += session.toString() + "\n";
        }
        result += "Session count: " + sessions.size();
        return result;
    }

}
