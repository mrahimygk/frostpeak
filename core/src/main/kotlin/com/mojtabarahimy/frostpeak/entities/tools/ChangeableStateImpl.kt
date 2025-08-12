package com.mojtabarahimy.frostpeak.entities.tools

class ChangeableStateImpl : ChangeableState {
    override var stateChanged: Boolean = false

    override fun changeState(newState: Boolean) {
        stateChanged = newState
    }
}
