package io.github.vinccool96.ltsa.ltsatool.lts

abstract class Declaration {

    open fun explicitStates(stateMachine: StateMachine) {}

    open fun crunch(stateMachine: StateMachine) {}

    open fun transition(stateMachine: StateMachine) {}

    companion object {

        const val TAU = 0

        const val ERROR = -1

        const val STOP = 0

        const val SUCCESS = 1

    }

}