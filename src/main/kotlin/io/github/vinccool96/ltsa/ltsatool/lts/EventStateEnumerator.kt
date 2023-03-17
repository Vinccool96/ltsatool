package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*

class EventStateEnumerator(val es: EventState?) : Enumeration<EventState> {

    val list: EventState? = es?.list

}