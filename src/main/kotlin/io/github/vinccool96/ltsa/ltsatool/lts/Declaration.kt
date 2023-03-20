package io.github.vinccool96.ltsa.ltsatool.lts

abstract class Declaration {

    fun explicitStates(stateMachine: StateMachine) {}

    fun crunch(stateMachine: StateMachine) {}

    fun transition(stateMachine: StateMachine) {}

    companion object {

        const val TAU = 0

        const val ERROR = -1

        const val STOP = 0

        const val SUCCESS = 1

    }

}