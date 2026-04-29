package com.k.pmpstudy

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler

internal fun abbreviateHash(
    project: Project,
    root: VirtualFile,
    fullHash: String,
    mode: CopyShortRevisionNumberSettings.Mode,
    fixedLength: Int
): String = when (mode) {
    CopyShortRevisionNumberSettings.Mode.UNIQUE_SHORTEST -> {
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
    CopyShortRevisionNumberSettings.Mode.FIXED_LENGTH -> {
        fullHash.take(fixedLength.coerceIn(1, fullHash.length))
    }
}
