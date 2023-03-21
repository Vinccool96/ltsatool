package io.github.vinccool96.ltsa.ltsatool.lts

import java.util.*
import java.util.concurrent.locks.ReentrantLock

class EventManager : Runnable {

    var clients = Hashtable<EventClient, EventClient>()

    var queue = Vector<LTSEvent>()

    var athread: Thread = Thread(this)

    var stopped = false

    private val lock = ReentrantLock()

    private val condition = lock.newCondition()

    init {
        athread.start()
    }

    @Synchronized
    fun addClient(var1: EventClient) {
        clients[var1] = var1
    }

    @Synchronized
    fun removeClient(var1: EventClient) {
        clients.remove(var1)
    }

    @Synchronized
    fun post(var1: LTSEvent) {
        queue.addElement(var1)
        condition.signalAll()
    }

    fun stop() {
        stopped = true
    }

    @Synchronized
    private fun doPost() {
        while (queue.size == 0) {
            try {
                condition.await()
            } catch (_: InterruptedException) {
            }
        }
        val var1 = queue.firstElement()
        val var2 = clients.keys()
        while (var2.hasMoreElements()) {
            val var3 = var2.nextElement()
            var3.ltsAction(var1)
        }
        queue.removeElement(var1)
    }

    override fun run() {
        while (!stopped) {
            doPost()
        }
    }

}