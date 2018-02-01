package io.github.intellij.dub.toolwindow

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.externalSystem.model.ProjectSystemId
import com.intellij.openapi.externalSystem.util.ExternalSystemApiUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Condition
import io.github.intellij.dub.Dub

/**
 * @author Samael Bate (singingbush)
 * Created on 24/03/2017.
 *
 * If dub gets treated as an external system then this class should extend AbstractExternalSystemToolWindowCondition
 */
class DubToolWindowCondition : Condition<Project> {

    private val LOG: Logger = Logger.getInstance(DubToolWindowCondition::class.java)


//    companion object {
//        val TOOLCHAIN_TOPIC: Topic<ToolchainListener> = Topic(
//                "toolchain changes",
//                ToolchainListener::class.java
//        )
//    }
//
//    interface ToolchainListener {
//        fun toolchainChanged()
//    }

    override fun value(t: Project?): Boolean {
        LOG.debug("DUB Tool Window Condition: value()")
        val manager = ExternalSystemApiUtil.getManager(Dub.SYSTEM_ID)
        return true
    }
}