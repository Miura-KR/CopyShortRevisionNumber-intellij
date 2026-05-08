package com.k.pmpstudy

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.GitReference
import git4idea.actions.ref.GitSingleRefAction
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import git4idea.repo.GitRepository
import java.awt.datatransfer.StringSelection
import java.util.function.Supplier

class CopyShortRefAction : GitSingleRefAction<GitReference>(Supplier { "" }) {

    override fun actionPerformed(
        e: AnActionEvent,
        project: Project,
        repositories: List<GitRepository>,
        reference: GitReference
    ) {
        val settings = CopyShortRevisionNumberSettings.getInstance().state
        val mode = settings.mode
        val fixedLength = settings.fixedLength

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Copying short revision number", true) {
                override fun run(indicator: ProgressIndicator) {
                    val results = repositories.mapNotNull { repo ->
                        val hash = resolveRefHash(project, repo.root, reference) ?: return@mapNotNull null
                        abbreviateHash(project, repo.root, hash, mode, fixedLength)
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

private fun resolveRefHash(project: Project, root: VirtualFile, ref: GitReference): String? {
    val handler = GitLineHandler(project, root, GitCommand.REV_PARSE)
    handler.addParameters("--verify", "${ref.fullName}^{commit}")
    handler.setSilent(true)
    val result = Git.getInstance().runCommand(handler)
    if (!result.success()) return null
    return result.output.joinToString("").trim().takeIf { it.isNotEmpty() }
}
