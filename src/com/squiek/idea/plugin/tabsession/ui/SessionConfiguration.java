package com.squiek.idea.plugin.tabsession.ui;


import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import com.intellij.util.containers.ContainerUtil;
import com.squiek.idea.plugin.tabsession.SessionState;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * created by alp (30.03.2013)
 */
public class SessionConfiguration {
    private JPanel rootComponent;
    private JSplitPane splitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JComboBox focusedFile;
    private JPanel sessionListPanel;
    private JBList sessionList;
    private CollectionListModel sessionListModel;
    private JPanel fileListPanel;
    private JBList fileList;
    private CollectionListModel fileListModel;
    private SessionState sessionState;
    private SessionState sessionStateCopy;
    private Project project;

    public SessionConfiguration(Project project) {
        this.project = project;
        sessionState = new SessionState();

        focusedFile.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String sessionName = (String) sessionList.getSelectedValue();
                SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
                session.focusedFile = (String) focusedFile.getSelectedItem();
                sessionList.setSelectedValue(sessionName, true);
            }
        });
    }

    public JComponent getRootComponent() {
        return rootComponent;
    }

    private void createUIComponents() {
        sessionListModel = new CollectionListModel(ContainerUtil.newArrayList());
        sessionList = new JBList(sessionListModel);
        sessionList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sessionListPanel = ToolbarDecorator.createDecorator(sessionList)
            .setAddAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doNewSession();
                }
            }).setEditAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doEditSession();
                }
            }).setRemoveAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doRemoveSession();
                }
//            }).addExtraAction(new AnActionButton("Export", AllIcons.Actions.Export) {
//                public void actionPerformed(AnActionEvent e) {
//                }
            })
            .setToolbarPosition(ActionToolbarPosition.TOP)
            .createPanel();

        new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                doEditSession();
                return true;
            }
        }.installOn(sessionList);

        sessionListModel.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
            }
            public void intervalRemoved(ListDataEvent e) {
            }
            public void contentsChanged(ListDataEvent e) {
                doReorderSessions();
            }
        });

        fileListModel = new CollectionListModel(ContainerUtil.newArrayList());
        fileList = new JBList(fileListModel);
        fileList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileListPanel = ToolbarDecorator.createDecorator(fileList)
            .setAddAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doNewFile();
                }
            }).setEditAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doEditFile();
                }
            }).setRemoveAction(new AnActionButtonRunnable() {
                public void run(AnActionButton button) {
                    doRemoveFile();
                }
            })
            .setToolbarPosition(ActionToolbarPosition.TOP)
            .createPanel();

        new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                doEditFile();
                return true;
            }
        }.installOn(fileList);

        fileListModel.addListDataListener(new ListDataListener() {
            public void intervalAdded(ListDataEvent e) {
            }
            public void intervalRemoved(ListDataEvent e) {
            }
            public void contentsChanged(ListDataEvent e) {
                doReorderFiles();
            }
        });

        ToolbarDecorator.findAddButton(fileListPanel).addCustomUpdater(new AnActionButtonUpdater() {
            public boolean isEnabled(AnActionEvent e) {
                return !sessionList.isSelectionEmpty();
            }
        });

        sessionList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String sessionName = (String) sessionList.getSelectedValue();
                for(SessionState.Session session : sessionStateCopy.sessions) {
                    if(session.name.equals(sessionName)) {
                        DefaultComboBoxModel focusedFileModel = new DefaultComboBoxModel();
                        fileListModel.removeAll();
                        for (String path : session.files) {
                            fileListModel.add(path);
                            focusedFileModel.addElement(path);
                        }
                        focusedFileModel.setSelectedItem(session.focusedFile);
                        focusedFile.setModel(focusedFileModel);
                    }
                }
            }
        });

    }

    protected void doNewSession() {
        showEditSessionDialog(null, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditSessionDialog editSessionDialog = (EditSessionDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                final String sessionName = editSessionDialog.getSessionName();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        sessionStateCopy.createSession(sessionName);
                        mapStateToUI();
                    }
                });
            }
        });
    }

    protected void doEditSession() {
        final String selectedName = (String) sessionList.getSelectedValue();
        showEditSessionDialog(selectedName, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditSessionDialog editSessionDialog = (EditSessionDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                final String sessionName = editSessionDialog.getSessionName();
                if (!sessionName.equals(selectedName)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            SessionState.Session session = sessionStateCopy.getSessionByName(selectedName);
                            session.name = sessionName;
                            mapStateToUI();
                        }
                    });
                }
            }
        });
    }

    private EditSessionDialog showEditSessionDialog(final String selectedName, ActionListener actionOnOKListener) {
        final EditSessionDialog editSessionDialog = selectedName != null ? new EditSessionDialog(selectedName) : new EditSessionDialog();
        editSessionDialog.setSavedSessions(sessionStateCopy.getSessionNames());
        editSessionDialog.addOnOKListener(actionOnOKListener);
        editSessionDialog.display(sessionList);
        return editSessionDialog;
    }

    protected void doRemoveSession() {
        final String selectedName = (String) sessionList.getSelectedValue();
        sessionStateCopy.removeSessionByName(selectedName);
        mapStateToUI();
    }

    protected void doReorderSessions() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ArrayList<String> sessionNames = new ArrayList<String>();
                for (Object name : sessionListModel.getItems()) {
                    sessionNames.add((String) name);
                }
                sessionStateCopy.setSessionOrder(sessionNames);
                mapStateToUI();
            }
        });
    }

    protected void doNewFile() {
        final String sessionName = (String) sessionList.getSelectedValue();
        showEditFileDialog(null, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final EditFileDialog editFileDialog = (EditFileDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                final SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                    String file = editFileDialog.getFilePath();
                    session.files.add(file);
                    if(editFileDialog.isFocusedFile()) session.focusedFile = file;
                    mapStateToUI();
                    }
                });
            }
        });
    }

    protected void doEditFile() {
        final String sessionName = (String) sessionList.getSelectedValue();
        final String selectedFile = (String) fileList.getSelectedValue();
        showEditFileDialog(selectedFile, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final EditFileDialog editFileDialog = (EditFileDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                final SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        String enteredFile = editFileDialog.getFilePath();
                        if (!enteredFile.equals(selectedFile)) {
                            for (int i = 0; i < session.files.size(); i++) {
                                String sessionFile = session.files.get(i);
                                if(sessionFile.equals(selectedFile)) {
                                    sessionFile = enteredFile;
                                }
                            }
//                            sessionListModel.setElementAt(sessionName, sessionList.getSelectedIndex());
                        }
                        if(editFileDialog.isFocusedFile()) {
                            session.focusedFile = enteredFile;
                        }
                        mapStateToUI();
                    }
                });
            }
        });
    }

    private EditFileDialog showEditFileDialog(final String selectedName, ActionListener actionOnOKListener) {
        final EditFileDialog editFileDialog = selectedName != null ? new EditFileDialog(project, selectedName) : new EditFileDialog(project);
        String sessionName = (String) sessionList.getSelectedValue();
        SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
        editFileDialog.setSavedFilePaths(new ArrayList<String>(session.files));
        editFileDialog.setIsFocusedFile(session.fileIsFocused(selectedName));
        editFileDialog.addOnOKListener(actionOnOKListener);
        editFileDialog.display(fileList);
        return editFileDialog;
    }


    protected void doRemoveFile() {
        String sessionName = (String) sessionList.getSelectedValue();
        String selectedFile = (String) fileList.getSelectedValue();
        SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
        session.files.remove(selectedFile);
        if(session.focusedFile.equals(selectedFile)) {
            session.focusedFile = session.files.size() > 0 ? session.files.get(0) : "";
        }
        mapStateToUI();
    }

    protected void doReorderFiles() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ArrayList<String> sessionFiles = new ArrayList<String>();
                for (Object name : fileListModel.getItems()) {
                    sessionFiles.add((String) name);
                }
                String sessionName = (String) sessionList.getSelectedValue();
                SessionState.Session session = sessionStateCopy.getSessionByName(sessionName);
                session.setFileOrder(sessionFiles);
                mapStateToUI();
            }
        });
    }

    public void setInitialState(SessionState sessionState) {
        this.sessionState = sessionState;
        sessionStateCopy = sessionState.clone();
        mapStateToUI();
    }

    public void mapStateToUI() {
        final String sessionName = (String) sessionList.getSelectedValue();
        final String selectedFile = (String) fileList.getSelectedValue();

        DefaultComboBoxModel focusedFileModel = new DefaultComboBoxModel();
        sessionListModel.removeAll();
        fileListModel.removeAll();

        for (SessionState.Session session : sessionStateCopy.sessions) {
            sessionListModel.add(session.name);
            fileListModel.removeAll();
            focusedFileModel.removeAllElements();
        }
        focusedFile.setModel(focusedFileModel);

        if(sessionName != null) {
            sessionList.setSelectedValue(sessionName, true);
            if(selectedFile != null) {
                fileList.setSelectedValue(selectedFile, true);
            }
        }
    }

    public SessionState getData() {
        return sessionStateCopy;
    }

    public boolean isModified() {
        return !sessionState.equals(sessionStateCopy);
    }
}
