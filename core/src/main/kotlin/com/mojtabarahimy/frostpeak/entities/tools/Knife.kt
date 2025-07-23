package com.mojtabarahimy.frostpeak.entities.tools

class Knife : Tool {
    override val name = "Knife"

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging Knife at nothing.")
        }
    }
}
