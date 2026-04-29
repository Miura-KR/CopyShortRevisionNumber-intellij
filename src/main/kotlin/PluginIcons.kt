package com.k.pmpstudy

import com.intellij.openapi.util.IconLoader
import com.intellij.util.IconUtil
import javax.swing.Icon

object PluginIcons {
    private const val MENU_ICON_SIZE = 16f

    private val rawGitHub: Icon =
        IconLoader.getIcon("/icons/github.svg", PluginIcons::class.java)

    @JvmField
    val GitHub: Icon = IconUtil.scale(rawGitHub, null, MENU_ICON_SIZE / rawGitHub.iconWidth.toFloat())
}
