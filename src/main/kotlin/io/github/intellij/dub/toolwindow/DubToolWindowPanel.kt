package io.github.intellij.dub.toolwindow

import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.configurations.ConfigurationType
import com.intellij.execution.configurations.ModuleBasedConfiguration
import com.intellij.execution.dashboard.actions.RunAction
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.impl.RunManagerImpl
import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.TreeStructureProvider
import com.intellij.ide.projectView.ViewSettings
import com.intellij.ide.projectView.impl.nodes.PackageViewProjectNode
import com.intellij.ide.projectView.impl.nodes.ProjectViewModuleGroupNode
import com.intellij.ide.projectView.impl.nodes.ProjectViewModuleNode
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.ide.util.treeView.AbstractTreeStructureBase
import com.intellij.ide.util.treeView.NodeDescriptor
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.extensions.Extensions
import com.intellij.openapi.externalSystem.action.ExternalSystemAction
import com.intellij.openapi.externalSystem.model.execution.ExternalSystemTaskExecutionSettings
import com.intellij.openapi.externalSystem.model.execution.ExternalTaskExecutionInfo
import com.intellij.openapi.externalSystem.service.task.ui.ExternalSystemTasksTree
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil
import com.intellij.openapi.externalSystem.view.*
import com.intellij.openapi.module.Module
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderEntry
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService
import com.intellij.openapi.roots.ui.configuration.artifacts.nodes.ArtifactRootNode
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ex.ToolWindowEx
import com.intellij.packageDependencies.ui.LibraryNode
import com.intellij.packageDependencies.ui.PackageDependenciesNode
import com.intellij.packageDependencies.ui.PackageNode
import com.intellij.packageDependencies.ui.RootNode
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.SimpleNode
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.IconUtil
import io.github.intellij.dlanguage.DlangSdkType
import io.github.intellij.dub.actions.ConfigureDToolsAction
import io.github.intellij.dub.actions.DubBuildAction
import io.github.intellij.dlanguage.actions.ProcessDLibs
import io.github.intellij.dlanguage.icons.DlangIcons
import io.github.intellij.dlanguage.messagebus.DubChangeNotifier
import io.github.intellij.dlanguage.messagebus.ToolChangeListener
import io.github.intellij.dlanguage.messagebus.Topics
import io.github.intellij.dlanguage.module.DlangModuleType
import io.github.intellij.dlanguage.project.DubConfigurationParser
import io.github.intellij.dlanguage.project.DubPackage
import io.github.intellij.dlanguage.settings.*
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel



/**
 * @author Samael Bate (singingbush)
 * Created on 31/03/2017.
 *
 * should this extend ExternalProjectsViewImpl or SimpleToolWindowPanel ???
 */
//class DubToolWindowPanel(project: Project, toolWindow: ToolWindowEx) : ExternalProjectsViewImpl(project, toolWindow, Dub.SYSTEM_ID) {}

class DubToolWindowPanel(val project: Project, val toolWindow: ToolWindow) :
    SimpleToolWindowPanel(true, true),
    ToolChangeListener,
    DubChangeNotifier,
    Disposable {

    private val LOG: Logger = Logger.getInstance(DubToolWindowPanel::class.java)

    init {
        LOG.info("the DubToolWindowPanel has been created")

        setToolbar(createToolbarPanel())

        createToolbarContent()

        with(project.messageBus.connect()) {
            subscribe(Topics.DUB_FILE_CHANGE, this@DubToolWindowPanel)
        }
    }

    // fired from intellij-dlanguage plugin when tool settings changed
    override fun onToolSettingsChanged(settings: ToolSettings) {
        createToolbarContent()
    }

    override fun onDubFileChange(project: Project, module: Module, dubFile: VirtualFile) {
        createToolbarContent()
    }

    override fun onDubSelectionsFileChange(project: Project, module: Module, dubFile: VirtualFile) {
        createToolbarContent()
    }

    fun createToolbarContent() {
        val root = DefaultMutableTreeNode(project.name) // ProjectNode()

        // then for every sub module would need to add:
        DlangModuleType.findModules(project).forEach {
            val dubModule = DefaultMutableTreeNode(it.name) // ModuleNode()
            dubModule.userObject = ProjectViewModuleNode(project, it, ViewSettings.DEFAULT)

            //dubModule.add(DefaultMutableTreeNode("source set"))
//            val dubTasks = DefaultMutableTreeNode("tasks") // should this be: ExternalSystemTasksTree
//            dubTasks.userObject = TaskNode()
//            dubModule.add(dubTasks)

            root.add(dubModule)
        }

        ToolKey.DUB_KEY.path?.let {
            val dubConfig = DubConfigurationParser(project, it, false)

//            val nd = SimpleNode()
            val dependenciesRootNode = DefaultMutableTreeNode("Dependencies")
            dependenciesRootNode.userObject = PackageDependenciesNode(project)

            dubConfig.dubPackageDependencies?.forEach {
                // LibraryNode ??
//                val dependencyNode = DefaultMutableTreeNode("${it.name} ${it.version}")
                val dependencyNode = DefaultMutableTreeNode(it)

                //dependencyNode.userObject = PackageViewProjectNode(project, ViewSettings.DEFAULT)
                dependenciesRootNode.add(dependencyNode)
                //dependencyNode.add(PackageNode("${it.name} ${it.version}"))
            }
            root.add(dependenciesRootNode)
        }

        val model = DefaultTreeModel(root)
        val tree: Tree = Tree(model)

        tree.cellRenderer = DubTreeRenderer()

        //add(JBScrollPane(tree), BorderLayout.CENTER)
        // or
        setContent(ScrollPaneFactory.createScrollPane(tree))
    }

//    class DubTreeStructure : AbstractTreeStructureBase() {
//        override fun commit() {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getProviders(): MutableList<TreeStructureProvider> {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getRootElement(): Any {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun hasSomethingToCommit(): Boolean {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//    }

    class DubTreeRenderer : ColoredTreeCellRenderer() {
        override fun customizeCellRenderer(tree: JTree, value: Any?, selected: Boolean, expanded: Boolean, leaf: Boolean, row: Int, hasFocus: Boolean) {
            val node = value as DefaultMutableTreeNode
            val userObject = node.userObject

            if (userObject is ProjectViewModuleNode) {
                icon = DlangIcons.LIBRARY
                append(userObject.value.name) //, SimpleTextAttributes.STYLE_BOLD)
            } else if (userObject is PackageDependenciesNode) {
                icon = AllIcons.Modules.ModulesNode
                append("Dependencies") //, SimpleTextAttributes.STYLE_BOLD)
            } else if (userObject is DubPackage) {
                val dependency = userObject
                icon = AllIcons.Modules.Library
                append("${dependency.name} ${dependency.version}",
                        SimpleTextAttributes(
                                SimpleTextAttributes.STYLE_ITALIC,
                                Color.GRAY
                        )
                )
            } else {
                append(userObject.toString()) // the default
            }
        }
    }

    fun createToolbarPanel(): JPanel {
        val group = DefaultActionGroup()

        group.add(RefreshAction( {createToolbarContent()} ))
        //group.add(AddAction())
        group.addSeparator()
        group.add(DubBuildAction()) // <-- defined in XML and code
        //group.add(ToggleOffline())
        group.addSeparator()
        group.add(ConfigureDToolsAction())

        val actionToolBar = ActionManager.getInstance().createActionToolbar("DubToolWindow", group, true)
        return com.intellij.util.ui.JBUI.Panels.simplePanel(actionToolBar.component)
    }

    // the Disposable interface is so we can unregister any event listeners that get registered
    override fun dispose() {
        LOG.info("dispose of DubToolWindowPanel")
    }


    // -----

    class RefreshAction(private val callback: (() -> Unit)? = null) :
        DumbAwareAction("Refresh", "", AllIcons.Actions.Refresh) {
        override fun actionPerformed(e: AnActionEvent) {
            val project = e.project ?: return

            val sdk = ProjectSettingsService.getInstance(project).chooseAndSetSdk() ?: return

            ProcessDLibs().actionPerformed(e) // should this be ProcessDLibs().update(e)

            this.callback?.invoke()
        }
    }

    class AddAction : DumbAwareAction("Add", "", IconUtil.getAddIcon()) {
        override fun actionPerformed(e: AnActionEvent) {
            TODO("add a dub.json file")
        }
    }

//    class DubRunAction : ExternalSystemAction() {
//        override fun actionPerformed(e: AnActionEvent) {
//            e?.project?.let {
//                val runManager = RunManagerImpl.getInstanceImpl(it)
//                //val configName = "${e.project!!.name} [dub run]"
//
//                val dubRunExecutionSettings = ExternalSystemTaskExecutionSettings()
//                dubRunExecutionSettings.executionName = "${e.project!!.name} [dub run]"
//                dubRunExecutionSettings.externalSystemIdString = "dub run"
//
//                val dubRunExecutionInfo = ExternalTaskExecutionInfo(dubRunExecutionSettings, DefaultRunExecutor.EXECUTOR_ID)
//                ExternalSystemUtil
//                        .createExternalSystemRunnerAndConfigurationSettings(
//                                dubRunExecutionInfo.settings,
//                                e.project!!,
//                                Dub.SYSTEM_ID
//                        ).let {
//                    val currentRunDubSettings = runManager.findConfigurationByName(it!!.name)
//
//                    if (currentRunDubSettings == null) {
//                        runManager.setTemporaryConfiguration(it)
//                    } else {
//                        runManager.selectedConfiguration = currentRunDubSettings
//                    }
//                }
//            }
//        }
//    }

    // AllIcons.FileTypes.Archive
    // AllIcons.Toolwindows.ToolWindowRun
    // this class has moved. see DubBuildAction
//    class RunAction : DumbAwareAction("_Run Dub", "", AllIcons.Actions.Execute) {
//        override fun actionPerformed(e: AnActionEvent) {
//            e?.project?.let {
//                val runManager = RunManagerImpl.getInstanceImpl(it)
//                val configName = "${e.project!!.name} [dub run]"
//
//                var runDubSettings = runManager.findConfigurationByName(configName)
//
//                if (runDubSettings == null) {
//                    val configurationType = Extensions.findExtension(ConfigurationType.CONFIGURATION_TYPE_EP, DlangRunDubConfigurationType::class.java)
//                    val factory = configurationType.configurationFactories[0]
//                    runDubSettings = runManager.createRunConfiguration(configName, factory)
//
//                    //(runDubSettings.configuration as DlangRunDubConfiguration)
//                    //(runDubSettings.configuration as ModuleBasedConfiguration<*>).setModule(rootModel.getModule())
//
//                    runManager.addConfiguration(runDubSettings, false)
//                }
//
//                runManager.selectedConfiguration = runDubSettings
//            }
//        }
//    }

    // set the '--nodeps'or maybe '--skip-registry=all' flag on dub args
    class ToggleOffline : ToggleAction("Toggle Offline Mode", "Toggle offline mode for Dub builds", AllIcons.Nodes.ExceptionClass) {
        override fun isSelected(e: AnActionEvent): Boolean {
            return false // todo:
        }

        override fun setSelected(e: AnActionEvent, state: Boolean) {
            // todo
        }
    }

}
