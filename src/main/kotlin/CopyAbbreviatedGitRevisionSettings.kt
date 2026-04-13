package com.k.pmpstudy

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@Service(Service.Level.APP)
@State(
    name = "CopyAbbreviatedGitRevisionSettings",
    storages = [Storage("copyAbbreviatedGitRevision.xml")]
)
class CopyAbbreviatedGitRevisionSettings :
    PersistentStateComponent<CopyAbbreviatedGitRevisionSettings.State> {

    enum class Mode { UNIQUE_SHORTEST, FIXED_LENGTH }

    class State {
        var mode: Mode = Mode.UNIQUE_SHORTEST
        var fixedLength: Int = 8
    }

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(): CopyAbbreviatedGitRevisionSettings =
            ApplicationManager.getApplication()
                .getService(CopyAbbreviatedGitRevisionSettings::class.java)
    }
}
