package com.k.pmpstudy

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.vcs.log.VcsLogCommitSelection
import com.intellij.vcs.log.VcsLogDataKeys
import java.awt.datatransfer.StringSelection

class CopyShortRevisionNumberAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val selection = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)
        e.presentation.isEnabledAndVisible =
            e.project != null && selection != null && selection.commits.size == 1
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val selection: VcsLogCommitSelection =
            e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION) ?: return
        val commits = selection.commits.toList()
        if (commits.isEmpty()) return

        val settings = CopyShortRevisionNumberSettings.getInstance().state
        val mode = settings.mode
        val fixedLength = settings.fixedLength

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Copying short revision number", true) {
                override fun run(indicator: ProgressIndicator) {
                    val result = commits.map { commit ->
                        abbreviateHash(project, commit.root, commit.hash.asString(), mode, fixedLength)
                    }
                    CopyPasteManager.getInstance()
                        .setContents(StringSelection(result.joinToString("\n")))
                }
            }
        )
    }
}
