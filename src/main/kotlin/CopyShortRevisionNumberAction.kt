package com.k.pmpstudy

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.vcs.log.VcsLogDataKeys
import java.awt.datatransfer.StringSelection

class CopyShortRevisionNumberAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val commitSelection = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)
        e.presentation.isEnabledAndVisible =
            project != null && commitSelection != null && commitSelection.commits.size == 1
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val commitSelection = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION) ?: return
        val commits = commitSelection.commits
        if (commits.isEmpty()) return

        val settings = CopyShortRevisionNumberSettings.getInstance().state
        val mode = settings.mode
        val fixedLength = settings.fixedLength

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Copying short revision number", true) {
                override fun run(indicator: ProgressIndicator) {
                    val results = commits.map { commit ->
                        abbreviateHash(project, commit.root, commit.hash.asString(), mode, fixedLength)
                    }
                    if (results.isNotEmpty()) {
                        CopyPasteManager.getInstance()
                            .setContents(StringSelection(results.joinToString("\n")))
                    }
                }
            }
        )
    }
}
