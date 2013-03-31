package com.squiek.idea.plugin.tabsession.action;

import com.squiek.idea.plugin.tabsession.SessionComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

public class Save extends AnAction {

    public Save() {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        SessionComponent sessionComponent = project.getComponent(SessionComponent.class);
        sessionComponent.showSaveDialog();
    }

}