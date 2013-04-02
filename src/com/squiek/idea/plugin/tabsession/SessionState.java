package com.squiek.idea.plugin.tabsession;

import java.util.ArrayList;
import java.util.List;

/**
 * created by alp (30.03.2013)
 */
public class SessionState {

    public class Session {
        public String name;
        public ArrayList<String> files = new ArrayList<String>();
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

        public void setFileOrder(ArrayList<String> sessionFiles) {
            ArrayList<String> newFiles = new ArrayList<String>();
            for(String file : sessionFiles) {
                newFiles.add(file);
            }
            files = newFiles;
        }

        public boolean fileIsFocused(String fileToCheck) {
            for(String file : files) {
                if(file.equals(fileToCheck) && file.equals(focusedFile)) return true;
            }
            return false;
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

    public void setSessionOrder(ArrayList<String> sessionNames) {
        List<Session> newSessions = new ArrayList<Session>();
        for(String name : sessionNames) {
            newSessions.add(getSessionByName(name));
        }
        sessions = newSessions;
    }

    public Session getOrCreateSessionByName(String name) {
        Session session = getSessionByName(name);
        return session == null ? createSession(name) : session;
    }

    public Session getSessionByName(String name) {
        Session namedSession = null;
        for (Session session : sessions) {
            if(session.name.equals(name)) {
                namedSession = session;
            }
        }
        return namedSession;
    }

    public Session createSession(String name) {
        Session session = new Session(name);
        sessions.add(session);
        return session;
    }

    public void removeSessionByName(String name) {
        Session session = getSessionByName(name);
        sessions.remove(session);
    }

    public String toString() {
        String result = "All Sessions:\n";
        for (Session session : sessions) {
            result += session.toString() + "\n";
        }
        result += "Session count: " + sessions.size();
        return result;
    }

    public SessionState clone() {
        SessionState stateClone = new SessionState();
        for (Session session : sessions) {
            Session sessionClone = new Session(session.name);
            for (String file : session.files) {
                sessionClone.files.add(file);
            }
            sessionClone.focusedFile = session.focusedFile;
            stateClone.sessions.add(sessionClone);
        }
        return stateClone;
    }

    public boolean equals(SessionState otherState) {
        boolean isEqual = true;
        if(sessions.size() != otherState.sessions.size()) isEqual = false;

        if(isEqual) {
            for (int i = 0; i < sessions.size(); i++) {
                Session sessionA = sessions.get(i);
                Session sessionB = otherState.sessions.get(i);
                if(!sessionA.name.equals(sessionB.name) || !sessionA.focusedFile.equals(sessionB.focusedFile) || sessionA.files.size() != sessionB.files.size()) {
                    isEqual = false;
                    break;
                }
                for (int j = 0; j < sessionA.files.size(); j++) {
                    String fileA = sessionA.files.get(j);
                    String fileB = sessionB.files.get(j);
                    if(!fileA.equals(fileB)) isEqual = false;
                }
            }
        }

        return isEqual;
    }

}
