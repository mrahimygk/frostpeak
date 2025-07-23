package com.mojtabarahimy.frostpeak.entities.tools

class Shovel : Tool {
    override val name = "Shovel"

    override fun use(target: ToolTarget?) {
        if (target != null) {
            target.onToolUsed(this)
        } else {
            println("Swinging Shovel at nothing.")
        }
    }
}
