package com.k.pmpstudy

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.vcs.log.VcsLogCommitSelection
import com.intellij.vcs.log.VcsLogDataKeys
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import java.awt.datatransfer.StringSelection

class CopyAbbreviatedGitRevisionAction : AnAction() {

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

        val settings = CopyAbbreviatedGitRevisionSettings.getInstance().state
        val mode = settings.mode
        val fixedLength = settings.fixedLength

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Copying abbreviated Git revision", true) {
                override fun run(indicator: ProgressIndicator) {
                    val result = commits.map { commit ->
                        abbreviate(project, commit.root, commit.hash.asString(), mode, fixedLength)
                    }
                    CopyPasteManager.getInstance()
                        .setContents(StringSelection(result.joinToString("\n")))
                }
            }
        )
    }

    private fun abbreviate(
        project: Project,
        root: VirtualFile,
        fullHash: String,
        mode: CopyAbbreviatedGitRevisionSettings.Mode,
        fixedLength: Int
    ): String = when (mode) {
        CopyAbbreviatedGitRevisionSettings.Mode.UNIQUE_SHORTEST -> {
            val handler = GitLineHandler(project, root, GitCommand.REV_PARSE)
            handler.addParameters("--short", fullHash)
            handler.setSilent(true)
            val cmdResult = Git.getInstance().runCommand(handler)
            if (cmdResult.success()) {
                cmdResult.output.joinToString("").trim().ifEmpty { fullHash.take(7) }
            } else {
                fullHash.take(7)
            }
        }
        CopyAbbreviatedGitRevisionSettings.Mode.FIXED_LENGTH -> {
            fullHash.take(fixedLength.coerceIn(1, fullHash.length))
        }
    }
}
