package com.squiek.idea.plugin.tabsession.ui;

import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.fileChooser.FileTextField;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton;
import com.squiek.idea.plugin.tabsession.SessionComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EditFileDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private TextFieldWithBrowseButton filePathTextField;
    private JCheckBox focusedTabCheckBox;
    private JPanel messagePanel;
    private JLabel errorMessage;
    private ArrayList<String> filePaths;
    private String editedPath;
    private boolean pathFirstAction = false;
    private Container relativeContainer;

    public EditFileDialog(final Project project) {
        setTitle("Tab File Settings");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        messagePanel.setVisible(false);

        filePathTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            if(pathFirstAction || !filePathTextField.getChildComponent().getText().isEmpty()) {
                pathFirstAction = true;
                validateSavedFilePathTextField();
            }
            }
        });

//        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> browserFolderActionListener =
            new ComponentWithBrowseButton.BrowseFolderActionListener<JTextField>(
                "Select File", "This file will be associated with the selected Session", filePathTextField, project,
                BrowseFilesListener.SINGLE_FILE_DESCRIPTOR, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT) {
                    @Override
                    protected void onFileChoosen(VirtualFile chosenFile) {
                        String contentEntryPath = null;
                        String path = chosenFile.getPath();
                        if (contentEntryPath != null) {
                            int i = StringUtil.commonPrefixLength(contentEntryPath, path);
                            filePathTextField.setText(path.substring(i));
                        } else {
                            filePathTextField.setText(path);
                        }
                    }

                };
        filePathTextField.addBrowseFolderListener(project, browserFolderActionListener);

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

    public EditFileDialog(Project project, String filePath) {
        this(project);
        filePathTextField.getChildComponent().setText(filePath);
        editedPath = filePath;
        buttonOK.setEnabled(true);
    }

    public void addOnOKListener(ActionListener listener) {
        buttonOK.addActionListener(listener);
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    private void validateSavedFilePathTextField() {
        if(!pathFirstAction) return;

        String enteredPath = filePathTextField.getChildComponent().getText();
        boolean isValid = !enteredPath.isEmpty();

        if(isValid) {
            for(String path : filePaths) {
                if(path.equals(enteredPath) && (editedPath == null || !editedPath.equals(enteredPath))) {
                    isValid = false;
                    errorMessage.setText(SessionComponent.ERROR_MESSAGE_FILE_EXISTS);
                    break;
                }
            }
        } else {
            errorMessage.setText(SessionComponent.ERROR_MESSAGE_FILE_EMPTY);
        }

        if(isValid) {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(enteredPath);
            if(vf == null || !LocalFileSystem.getInstance().exists(vf)) {
                isValid = false;
                errorMessage.setText(SessionComponent.ERROR_MESSAGE_FILE_WRONG_PATH);
            }
        }

        buttonOK.setEnabled(isValid);
        messagePanel.setVisible(!isValid);
        refresh();
    }

    public void setSavedFilePaths(ArrayList<String> filePaths) {
        this.filePaths = filePaths;
    }

    public void setIsFocusedFile(boolean isSelected) {
        focusedTabCheckBox.setSelected(isSelected);
    }

    public String getFilePath() {
        return filePathTextField.getChildComponent().getText();
    }

    public boolean isFocusedFile() {
        return focusedTabCheckBox.isSelected();
    }

    public void refresh() {
        pack();
        setLocationRelativeTo(relativeContainer);
    }

    public void display(Container relativeContainer) {
        this.relativeContainer = relativeContainer;
        refresh();
        setVisible(true);
    }
}
