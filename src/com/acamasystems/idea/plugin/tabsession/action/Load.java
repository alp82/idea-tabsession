package com.acamasystems.idea.plugin.tabsession.action;

import com.acamasystems.idea.plugin.tabsession.SessionComponent;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

public class Load extends AnAction {

    public Load() {
        super();
    }

    public void actionPerformed(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);
        SessionComponent sessionComponent = project.getComponent(SessionComponent.class);
        sessionComponent.showLoadDialog();
    }

}