package io.github.intellij.dub.toolwindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.service.task.ui.AbstractExternalSystemToolWindowFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory


/**
 * Provides a tool window for dub (displayed on right-hand side).
 * @author Samael Bate (singingbush)
 * Created on 29/07/2016.
 *
 * for an example ToolWindow check out IDEtalkToolWindow in IDEtalk, see: https://github.com/JetBrains/intellij-plugins/
 * or the tool_window code sample in https://github.com/JetBrains/intellij-sdk-docs/
 *
 * but best example is the gradle plugin in https://github.com/JetBrains/intellij-community
 *
 * see
 */
class DubToolWindow : ToolWindowFactory {

    private val LOG: Logger = Logger.getInstance(DubToolWindow::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        LOG.debug("Creating DUB Tool Window: ${toolWindow.title}")

        //val modules = io.github.intellij.dlanguage.module.DlangModuleType.findModules(project)

        LOG.info("we have modules")

        val dtw = DubToolWindowPanel(project, toolWindow)
        val content = toolWindow.contentManager.factory.createContent(dtw, null, false)
        toolWindow.contentManager.addContent(content)
        Disposer.register(project, dtw)
    }

}