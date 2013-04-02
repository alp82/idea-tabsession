package com.squiek.idea.plugin.tabsession.ui;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.squiek.idea.plugin.tabsession.SessionComponent;
import com.squiek.idea.plugin.tabsession.SessionState;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;

public class LoadSessionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton buttonManage;
    private JBList sessionList;
    private JBList fileList;

    private SessionState sessionState;
    private Project project;
    private Configurable configurable;

    public LoadSessionDialog() {
        setTitle("Load Tab Session");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        buttonManage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onManage();
            }
        });

        sessionList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String sessionName = (String) sessionList.getSelectedValue();
                for(SessionState.Session session : sessionState.sessions) {
                    if(session.name.equals(sessionName)) {
                        DefaultListModel model = new DefaultListModel();
                        for (String path : session.files) {
                            String fe = path + (path.equals(session.focusedFile) ? " (focused tab)" : "");
                            model.addElement(fe);
                        }
                        fileList.setModel(model);
                    }
                }
                buttonOK.setEnabled(true);
                pack();
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void onManage() {
        if(project != null && configurable != null) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, configurable);
            setSessionState(((SessionComponent) configurable).getSessionState());
        }
    }

    public String getSessionName() {
        return (String) sessionList.getSelectedValue();
    }

    public void setSessionState(SessionState sessionState) {
        this.sessionState = sessionState;

        DefaultListModel model = new DefaultListModel();
        for (String name : sessionState.getSessionNames()) {
            model.addElement(name);
        }
        sessionList.setModel(model);
        fileList.setModel(new DefaultListModel());
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setConfigurable(Configurable configurable) {
        this.configurable = configurable;
    }

    public void addOnOKListener(ActionListener listener) {
        buttonOK.addActionListener(listener);
    }

    public void display(Container relativeContainer) {
        pack();
        setLocationRelativeTo(relativeContainer);
        setVisible(true);
    }

}
