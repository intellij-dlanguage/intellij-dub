package io.github.intellij.dub.toolwindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import com.intellij.openapi.vfs.VirtualFile
import java.util.*

/**
 * @author Samael Bate (singingbush)
 * Created on 24/03/2017.
 *
 * If dub gets treated as an external system then this class should extend AbstractExternalSystemToolWindowCondition
 */
class DubToolWindowCondition : Condition<Project> {

    private val LOG: Logger = Logger.getInstance(DubToolWindowCondition::class.java)

    override fun value(project: Project?): Boolean {
        LOG.debug("DUB Tool Window Condition: value(${project?.name})")
        val baseDir = project?.baseDir ?: return false

        return Arrays.stream<VirtualFile>(baseDir.children)
            .filter { f -> !f.isDirectory }
            .anyMatch { f -> "dub.json".equals(f.name, ignoreCase = true) || "dub.sdl".equals(f.name, ignoreCase = true) }
    }
}
