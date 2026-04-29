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
import git4idea.repo.GitRepositoryManager
import java.awt.datatransfer.StringSelection

class CopyGitHubCommitLinkAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val selection = e.getData(VcsLogDataKeys.VCS_LOG_COMMIT_SELECTION)
        e.presentation.isEnabledAndVisible = project != null &&
            selection != null &&
            selection.commits.size == 1 &&
            githubBaseUrl(project, selection.commits[0].root) != null
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
            object : Task.Backgroundable(project, "Copying GitHub commit link", true) {
                override fun run(indicator: ProgressIndicator) {
                    val links = commits.mapNotNull { commit ->
                        val base = githubBaseUrl(project, commit.root) ?: return@mapNotNull null
                        val shortHash = abbreviateHash(
                            project, commit.root, commit.hash.asString(), mode, fixedLength
                        )
                        "$base/commit/$shortHash"
                    }
                    if (links.isNotEmpty()) {
                        CopyPasteManager.getInstance()
                            .setContents(StringSelection(links.joinToString("\n")))
                    }
                }
            }
        )
    }
}

private fun githubBaseUrl(project: Project, root: VirtualFile): String? {
    val repo = GitRepositoryManager.getInstance(project).getRepositoryForRoot(root) ?: return null
    val urls = repo.remotes.asSequence().flatMap { it.urls.asSequence() }.toList()
    if (urls.isEmpty()) return null

    val githubUrls = urls.mapNotNull { toGitHubHttpsUrl(it) }
    if (githubUrls.isEmpty()) return null

    val preferred = repo.remotes.firstOrNull { it.name == "origin" }
        ?.urls?.firstNotNullOfOrNull { toGitHubHttpsUrl(it) }
    return preferred ?: githubUrls.first()
}

private val githubUrlPatterns = listOf(
    Regex("""^https?://(?:[^@/]+@)?github\.com/([^/]+/[^/]+?)$"""),
    Regex("""^git@github\.com:([^/]+/[^/]+?)$"""),
    Regex("""^ssh://git@github\.com[:/]([^/]+/[^/]+?)$""")
)

internal fun toGitHubHttpsUrl(remoteUrl: String): String? {
    val trimmed = remoteUrl.trim().removeSuffix("/").removeSuffix(".git")
    for (re in githubUrlPatterns) {
        re.matchEntire(trimmed)?.let {
            return "https://github.com/${it.groupValues[1]}"
        }
    }
    return null
}
