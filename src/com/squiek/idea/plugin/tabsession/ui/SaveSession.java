package com.squiek.idea.plugin.tabsession.ui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.ArrayList;

public class SaveSession extends JDialog {
    final private String ERROR_MESSAGE_NAME_EMPTY = "Session name can't be empty";
    final private String ERROR_MESSAGE_NAME_EXISTS = "Session with that name already exists and will be overwritten";

    private JPanel contentPane;
    private JPanel messagePanel;
    private JLabel errorMessage;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField newNameTextField;
    private JComboBox savedNameComboBox;
    private JRadioButton radioNew;
    private JRadioButton radioSaved;

    private boolean newNameFirstKeyTyped = false;

    public SaveSession() {
        setTitle("Save Tab Session");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        messagePanel.setVisible(false);

        radioNew.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                boolean newSelected = radioNew.isSelected();
                newNameTextField.setEnabled(newSelected);
                savedNameComboBox.setEnabled(!newSelected);

                if(newSelected) {
                    validateNewNameTextField();
                } else {
                    validateSavedNameTextField();
                }
            }
        });

//        newNameTextField.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (radioNew.isSelected()) return;
//                radioNew.setSelected(true);
//            }
//        });
//
//        savedNameComboBox.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (radioSaved.isSelected()) return;
//                radioSaved.setSelected(true);
//            }
//        });

        newNameTextField.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {}
            public void keyPressed(KeyEvent e) {}
            public void keyReleased(KeyEvent e) {
                newNameFirstKeyTyped = true;
                validateNewNameTextField();
            }
        });

        savedNameComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
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

    private void validateNewNameTextField() {
        if(!newNameFirstKeyTyped || !newNameTextField.isEnabled()) return;

        String newName = newNameTextField.getText();
        boolean isValid = newName.length() > 0;
        boolean isDuplicate = false;

        if (isValid) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) savedNameComboBox.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                String savedName = (String) model.getElementAt(i);
                if (savedName.equals(newName)) {
                    isDuplicate = true;
                    errorMessage.setText(ERROR_MESSAGE_NAME_EXISTS);
                }
            }
        } else {
            errorMessage.setText(ERROR_MESSAGE_NAME_EMPTY);
        }

        buttonOK.setEnabled(isValid);
        messagePanel.setVisible(!isValid || isDuplicate);
        pack();
    }

    private void validateSavedNameTextField() {
        if(!savedNameComboBox.isEnabled()) return;

        DefaultComboBoxModel model = (DefaultComboBoxModel) savedNameComboBox.getModel();
        boolean isValid = model.getSelectedItem() != null;
        buttonOK.setEnabled(isValid);
        messagePanel.setVisible(false);
        pack();
    }

    private void onOK() {
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public void setSavedSessions(ArrayList<String> sessionNames) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (String name : sessionNames) {
            model.addElement(name);
        }
        savedNameComboBox.setModel(model);
    }

    public void addOnOKListener(ActionListener listener) {
        buttonOK.addActionListener(listener);
    }

    public String getSessionName() {
        String name;
        if(radioNew.isSelected()) {
            name = newNameTextField.getText();
        } else {
            name = (String) savedNameComboBox.getModel().getSelectedItem();
        }
        return name;
    }

    public void display() {
        pack();
        setVisible(true);
    }
}
