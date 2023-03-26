/**
 * SceneBeans, a Java API for animated 2D graphics.
 *
 *
 * Copyright (C) 2000 Nat Pryce and Imperial College
 *
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
 * USA.
 */
package uk.ac.ic.doc.scenebeans.activity

/**
 * An ActivityThread can be executed by a Thread and simulates the
 * behaviour of a set of [Activity]
 * objects in real-time.
 */
class ActivityThread : ActivityRunner, Runnable {
    var _perform_lock: Any
    var _activities: ActivityList? = ActivityList.Companion.EMPTY
    var _list_lock = Any()
    var _start: Long = 0
    var _thread: Thread? = null
    var _stop = false
    var _sleep: Long = 100

    constructor() {
        _perform_lock = this
    }

    constructor(perform_lock: Any) {
        _perform_lock = perform_lock
    }

    override fun addActivity(a: Activity) {
        synchronized(_list_lock) {
            a.activityRunner = this
            _activities = _activities!!.add(a)
        }
    }

    override fun removeActivity(a: Activity) {
        synchronized(_list_lock) {
            a.activityRunner = null
            _activities = _activities!!.remove(a)
        }
    }

    var sleepDelay: Long
        get() {
            synchronized(_perform_lock) { return _sleep }
        }
        set(millis) {
            synchronized(_perform_lock) { _sleep = millis }
        }

    fun start() {
        synchronized(_perform_lock) {
            _start = System.currentTimeMillis()
            _stop = false
            _thread = Thread(this)
            _thread!!.start()
        }
    }

    @Throws(InterruptedException::class)
    fun stop() {
        synchronized(_perform_lock) {
            _stop = true
            _thread!!.interrupt()
            _thread!!.join()
            _thread = null
        }
    }

    override fun run() {
        var sleep: Long
        try {
            while (!_stop) {
                synchronized(_perform_lock) {
                    val now = System.currentTimeMillis()
                    val delta = now - _start
                    val t = delta.toDouble() / 1000.0
                    _activities!!.performActivities(t)
                    _start = now
                    sleep = _sleep
                }
                if (sleep != 0L) {
                    Thread.sleep(sleep)
                }
            }
        } catch (ex: InterruptedException) {
        }
    }
}