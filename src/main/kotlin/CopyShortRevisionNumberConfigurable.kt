package com.k.pmpstudy

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bind
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent

class CopyShortRevisionNumberConfigurable : Configurable {

    private val settings get() = CopyShortRevisionNumberSettings.getInstance().state

    private var uiMode: CopyShortRevisionNumberSettings.Mode = settings.mode
    private var uiFixedLength: Int = settings.fixedLength

    private var myPanel: DialogPanel? = null

    override fun getDisplayName(): String = "Copy Short Revision Number"

    override fun createComponent(): JComponent {
        uiMode = settings.mode
        uiFixedLength = settings.fixedLength

        val p = panel {
            buttonsGroup("Hash mode") {
                row {
                    radioButton(
                        "Unique shortest (git rev-parse --short)",
                        CopyShortRevisionNumberSettings.Mode.UNIQUE_SHORTEST
                    )
                }
                row {
                    radioButton(
                        "Fixed length",
                        CopyShortRevisionNumberSettings.Mode.FIXED_LENGTH
                    )
                }
            }.bind(::uiMode)

            row("Fixed length:") {
                intTextField(1..40)
                    .bindIntText(::uiFixedLength)
            }
        }
        myPanel = p
        return p
    }

    override fun isModified(): Boolean {
        myPanel?.apply()
        return uiMode != settings.mode || uiFixedLength != settings.fixedLength
    }

    override fun apply() {
        myPanel?.apply()
        settings.mode = uiMode
        settings.fixedLength = uiFixedLength
    }

    override fun reset() {
        uiMode = settings.mode
        uiFixedLength = settings.fixedLength
        myPanel?.reset()
    }

    override fun disposeUIResources() {
        myPanel = null
    }
}
