package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class EventStateEnumerator(var es: EventState?) : Enumeration<EventState> {

    var list: EventState? = es?.list

    override fun hasMoreElements(): Boolean {
        return es != null
    }

    override fun nextElement(): EventState {
        val eventState = this.es
        return if (eventState != null) {
            if (eventState.nondet != null) {
                es = eventState.nondet
            } else {
                val l = this.list
                es = l
                if (l != null) {
                    list = l.list
                }
            }
            eventState
        } else {
            throw NoSuchElementException("EventStateEnumerator")
        }
    }

}