package com.k.pmpstudy

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import java.awt.datatransfer.StringSelection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class CopyGitHubFileLinkAction : AnAction() {

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun update(e: AnActionEvent) {
        val project = e.project
        val files = collectFiles(e)
        e.presentation.isEnabledAndVisible = project != null &&
            files.isNotEmpty() &&
            files.all {
                val ctx = resolveContext(project, it) ?: return@all false
                ctx.repo.currentRevision != null
            }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val files = collectFiles(e)
        if (files.isEmpty()) return
        val editor = e.getData(CommonDataKeys.EDITOR)
        val sharedSuffix = if (files.size == 1 && editor != null && !files[0].isDirectory) {
            editorLineSuffix(editor)
        } else {
            ""
        }

        val settings = CopyShortRevisionNumberSettings.getInstance().state
        val mode = settings.mode
        val fixedLength = settings.fixedLength

        ProgressManager.getInstance().run(
            object : Task.Backgroundable(project, "Copying GitHub file link", true) {
                override fun run(indicator: ProgressIndicator) {
                    val links = files.mapNotNull { file ->
                        buildFileLink(project, file, sharedSuffix, mode, fixedLength)
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

private fun collectFiles(e: AnActionEvent): List<VirtualFile> {
    e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY)?.takeIf { it.isNotEmpty() }?.let { return it.toList() }
    e.getData(CommonDataKeys.VIRTUAL_FILE)?.let { return listOf(it) }
    return emptyList()
}

private data class FileContext(
    val repo: GitRepository,
    val baseUrl: String,
    val relativePath: String
)

private fun resolveContext(project: Project, file: VirtualFile): FileContext? {
    val repo = GitRepositoryManager.getInstance(project).getRepositoryForFile(file) ?: return null
    val baseUrl = githubBaseUrlFromRepo(repo) ?: return null
    val relativePath = VfsUtilCore.getRelativePath(file, repo.root, '/') ?: return null
    return FileContext(repo, baseUrl, relativePath)
}

private fun githubBaseUrlFromRepo(repo: GitRepository): String? {
    val originUrl = repo.remotes.firstOrNull { it.name == "origin" }
        ?.urls
        ?.asSequence()
        ?.mapNotNull { toGitHubHttpsUrl(it) }
        ?.firstOrNull()
    if (originUrl != null) return originUrl
    return repo.remotes.asSequence()
        .flatMap { it.urls.asSequence() }
        .mapNotNull { toGitHubHttpsUrl(it) }
        .firstOrNull()
}

private fun buildFileLink(
    project: Project,
    file: VirtualFile,
    lineSuffix: String,
    mode: CopyShortRevisionNumberSettings.Mode,
    fixedLength: Int
): String? {
    val ctx = resolveContext(project, file) ?: return null
    val fullHash = ctx.repo.currentRevision ?: return null
    val shortHash = abbreviateHash(project, ctx.repo.root, fullHash, mode, fixedLength)
    val kind = if (file.isDirectory) "tree" else "blob"
    val encodedPath = ctx.relativePath.split('/').joinToString("/", transform = ::encodePathSegment)
    return "${ctx.baseUrl}/$kind/$shortHash/$encodedPath$lineSuffix"
}

private fun encodePathSegment(segment: String): String =
    URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20")

private fun editorLineSuffix(editor: Editor): String {
    val doc = editor.document
    val selection = editor.selectionModel
    return if (selection.hasSelection()) {
        val startLine = doc.getLineNumber(selection.selectionStart) + 1
        val endOffset = selection.selectionEnd
        val adjustedEnd = if (
            endOffset > selection.selectionStart &&
            doc.getLineStartOffset(doc.getLineNumber(endOffset)) == endOffset
        ) endOffset - 1 else endOffset
        val endLine = doc.getLineNumber(adjustedEnd) + 1
        if (startLine == endLine) "#L$startLine" else "#L$startLine-L$endLine"
    } else {
        val line = doc.getLineNumber(editor.caretModel.offset) + 1
        "#L$line"
    }
}
