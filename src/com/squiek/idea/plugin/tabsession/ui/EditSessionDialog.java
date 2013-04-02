package com.squiek.idea.plugin.tabsession.ui;

import com.squiek.idea.plugin.tabsession.SessionComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class EditSessionDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField sessionNameTextField;
    private JPanel messagePanel;
    private JLabel errorMessage;
    private ArrayList<String> sessionNames;
    private boolean newNameFirstKeyTyped = false;
    private String editedName = null;
    private Container relativeContainer;

    public EditSessionDialog() {
        setTitle("Session Settings");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        messagePanel.setVisible(false);

        sessionNameTextField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                newNameFirstKeyTyped = true;
                validateSavedNameTextField();
            }
        });

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

    public EditSessionDialog(String name) {
        this();
        sessionNameTextField.setText(name);
        editedName = name;
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

    private void validateSavedNameTextField() {
        if(!newNameFirstKeyTyped) return;

        String enteredName = sessionNameTextField.getText();
        boolean isValid = !enteredName.isEmpty();

        if(isValid) {
            for(String sessionName : sessionNames) {
                if(sessionName.equals(enteredName) && (editedName == null || !editedName.equals(enteredName))) {
                    isValid = false;
                    errorMessage.setText(SessionComponent.ERROR_MESSAGE_NAME_EXISTS);
                    break;
                }
            }
        } else {
            errorMessage.setText(SessionComponent.ERROR_MESSAGE_NAME_EMPTY);
        }

        buttonOK.setEnabled(isValid);
        messagePanel.setVisible(!isValid);
        refresh();
    }

    public void setSavedSessions(ArrayList<String> sessionNames) {
        this.sessionNames = sessionNames;
    }

    public String getSessionName() {
        return sessionNameTextField.getText();
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
