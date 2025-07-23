package com.mojtabarahimy.frostpeak.entities.tools

class Axe : Tool {
    override val name = "Axe"

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging axe at nothing.")
        }
    }
}
