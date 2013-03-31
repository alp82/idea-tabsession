package com.squiek.idea.plugin.tabsession.ui;


import com.squiek.idea.plugin.tabsession.SessionState;
import com.intellij.ui.components.JBList;

import javax.swing.*;

/**
 * created by alp (30.03.2013)
 */
public class SessionConfiguration {
    private JPanel rootComponent;
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JBList sessionList;
    private JBList fileList;
    private JComboBox focusedFile;

    public SessionConfiguration() {
        splitPane.setVisible(false);
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    public void setData(SessionState sessionState) {
        DefaultListModel sessionListModel = new DefaultListModel();
        DefaultListModel fileListModel = new DefaultListModel();
        DefaultComboBoxModel focusedFileModel = new DefaultComboBoxModel();

        for(SessionState.Session session : sessionState.sessions) {
            sessionListModel.addElement(session.name);
            for(String path : session.files) {
                fileListModel.addElement(path);
                focusedFileModel.addElement(path);
                if(path.equals(focusedFile)) {
                    focusedFileModel.setSelectedItem(path);
                }
            }
        }

        sessionList.setModel(sessionListModel);
        fileList.setModel(fileListModel);
        focusedFile.setModel(focusedFileModel);
    }

    public void getData(SessionState sessionState) {
        // TODO
    }

    public boolean isModified(SessionState sessionState) {
        // TODO
        return false;
    }

}
