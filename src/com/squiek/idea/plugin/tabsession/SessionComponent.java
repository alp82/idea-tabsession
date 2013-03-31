package com.squiek.idea.plugin.tabsession;

import com.squiek.idea.plugin.tabsession.ui.LoadSession;
import com.squiek.idea.plugin.tabsession.ui.SaveSession;
import com.squiek.idea.plugin.tabsession.ui.SessionConfiguration;
import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import org.jdom.Element;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * created by alp (29.03.2013)
 */

@State(
    name = "SessionConfiguration",
    storages = {
        @Storage(id = "default", file = StoragePathMacros.PROJECT_FILE),
        @Storage(id = "dir", file = StoragePathMacros.PROJECT_CONFIG_DIR + "/tabsession.xml", scheme = StorageScheme.DIRECTORY_BASED)
    }
)
public class SessionComponent implements ProjectComponent, Configurable, PersistentStateComponent<Element> {

    /*
     * Initialization and Component Configuration
     */

    public static final int STATE_VERSION = 1;

    private static Logger LOG = Logger.getInstance(SessionComponent.class);

    private Project project;
    private SessionState sessionState;
    private SessionConfiguration form;

    public SessionComponent(Project project) {
        this.project = project;
        sessionState = new SessionState();
    }

    @NotNull
    public String getComponentName() {
        return "SessionComponent";
    }

    public void initComponent() {
        // TODO: insert component initialization logic here
    }

    public void disposeComponent() {
        // TODO: insert component disposal logic here
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    /*
     * Component Persistence
     */

    @Override
    public void loadState(Element element) {
        LOG.debug("Loading state");

        sessionState = new SessionState();

        // meta configuration information
        Element stateElement = element.getChild("state");
        // version not needed yet
//        Integer stateVersion = Integer.valueOf(stateElement.getAttributeValue("version"));

        // session data
        Element sessionsElement = element.getChild("sessions");
        for (Object se : sessionsElement.getChildren("session")) {
            Element sessionElement = (Element) se;
            if(sessionElement != null) {
                Element filesElement = sessionElement.getChild("files");
                if(filesElement != null) {
                    SessionState.Session session = sessionState.getOrCreateSessionByName(sessionElement.getAttributeValue("name"));
                    for (Object fe : filesElement.getChildren("file")) {
                        Element fileElement = (Element) fe;
                        session.files.add(fileElement.getText());
                    }
                    session.focusedFile = filesElement.getAttributeValue("focusedFile");
                }
            }
        }

    }

    @Nullable
    @Override
    public Element getState() {
        LOG.debug("Saving state");

        final Element element = new Element("tabsession");

        // meta configuration information
        final Element stateElement = new Element("state");
        stateElement.setAttribute("version", Integer.toString(STATE_VERSION));
        element.addContent(stateElement);

        // session data
        final Element sessionsElement = new Element("sessions");
        for(SessionState.Session session : sessionState.sessions) {
            final Element sessionElement = new Element("session");
            sessionElement.setAttribute("name", session.name);
            final Element filesElement = new Element("files");
            for(String path : session.files) {
                final Element fileElement = new Element("file");
                fileElement.setText(path);
                filesElement.addContent(fileElement);
            }
            filesElement.setAttribute("focusedFile", session.focusedFile);
            sessionElement.addContent(filesElement);
            sessionsElement.addContent(sessionElement);
        }
        element.addContent(sessionsElement);

        return element;
    }

    /*
     * Save/Load logic
     */

    public int saveCurrentTabs(String sessionName) {
        Editor[] editors = getOpenEditors();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
        FileDocumentManager fileDocManager = FileDocumentManager.getInstance();

        SessionState.Session session = sessionState.getOrCreateSessionByName(sessionName);
        session.files.clear();

        for(Editor editor : editors) {
            VirtualFile vf = fileDocManager.getFile(editor.getDocument());
            String path = vf.getCanonicalPath();
            if(path.equals(selectedFiles[0].getCanonicalPath())) {
                session.focusedFile = path;
            }
            session.addFileIfNew(path);
        }

        return editors.length;
    }

    public int loadSession(String sessionName) {
        closeCurrentTabs();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

        VirtualFile focusedFile = null;
        SessionState.Session session = sessionState.getOrCreateSessionByName(sessionName);
        for(String path : session.files) {
            VirtualFile vf = LocalFileSystem.getInstance().findFileByPath(path);
            fileEditorManager.openFile(vf, false, true);
            if(path.equals(session.focusedFile)) {
                focusedFile = vf;
            }
        }

        if(focusedFile != null) {
            fileEditorManager.openFile(focusedFile, true, true);
        }

        return session.files.size();
    }

    public void closeCurrentTabs() {
        Editor[] editors = getOpenEditors();
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileDocumentManager fileDocManager = FileDocumentManager.getInstance();
        for(Editor editor : editors) {
            VirtualFile vf = fileDocManager.getFile(editor.getDocument());
            if(vf != null) {
                fileEditorManager.closeFile(vf);
            }
        }
    }

    private Editor[] getOpenEditors() {
        return EditorFactory.getInstance().getAllEditors();
    }

    /*
     * UI elements
     */

    public void showMessage(String htmlText) {
        JMenuBar menuBar = WindowManager.getInstance().getFrame(project).getJMenuBar();
        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder(htmlText, MessageType.INFO, null)
            .setFadeoutTime(7500)
            .setCloseButtonEnabled(true)
            .createBalloon()
            .show(RelativePoint.getCenterOf(menuBar), Balloon.Position.atRight);
    }

    public void showSaveDialog() {
        JMenuBar menuBar = WindowManager.getInstance().getFrame(project).getJMenuBar();
        final SaveSession saveDialog = new SaveSession();
        saveDialog.setLocationRelativeTo(menuBar);
        saveDialog.setSavedSessions(sessionState.getSessionNames());
        saveDialog.addOnOKListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sessionName = saveDialog.getSessionName();
                int tabCount = saveCurrentTabs(sessionName);
                String htmlText = "Saved " + String.valueOf(tabCount) + " tabs in session \"" + sessionName + "\"";
                showMessage(htmlText);
            }
        });
        saveDialog.display();
    }

    public void showLoadDialog() {
        JMenuBar menuBar = WindowManager.getInstance().getFrame(project).getJMenuBar();
        final LoadSession loadDialog = new LoadSession();
        loadDialog.setLocationRelativeTo(menuBar);
        loadDialog.setSessionState(sessionState);
        loadDialog.addOnOKListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sessionName = loadDialog.getSessionName();
                int tabCount = loadSession(sessionName);
                String htmlText = "Loaded " + String.valueOf(tabCount) + " tabs from session \"" + sessionName + "\"";
                showMessage(htmlText);
            }
        });
        loadDialog.display();
    }

    /*
     * IDEA Settings
     */

    @Nls
    @Override
    public String getDisplayName() {
        return "Tab Sessions";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if(form == null) {
            form = new SessionConfiguration();
        }
        return form.getRootComponent();
    }

    @Override
    public boolean isModified() {
        return form != null && form.isModified(sessionState);
    }

    @Override
    public void apply() throws ConfigurationException {
        if(form != null) {
            form.getData(sessionState);
        }
    }

    @Override
    public void reset() {
        if(form != null) {
            form.setData(sessionState);
        }
    }

    @Override
    public void disposeUIResources() {
        form = null;
    }
}
