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
package uk.ac.ic.doc.scenebeans.animation

import uk.ac.ic.doc.scenebeans.*
import uk.ac.ic.doc.scenebeans.activity.*
import java.awt.Graphics2D
import java.io.Serializable
import java.util.*

/**
 * An Animation is the basic unit of animation design.  It encapsulates a
 * scene graph and the activities animating that graph, and can itself be
 * embedded in a scene graph and run as an activity.
 */
class Animation : ActivityBase(), CompositeNode, Serializable, ActivityRunner {
    private var _activities: ActivityList? = ActivityList.EMPTY
    private val _layers = Layered()
    private val _commands: MutableMap<String, Command> = HashMap()
    private val _event_names: MutableSet<String> = HashSet()
    /**
     * Returns the width of the animation.  This is *not* calculated from
     * the scene graph, because the graph may be animated in arbitrary ways,
     * but must be precalculated by the animation designer.
     *
     * @return The width of the animation.
     */
    /**
     * Sets the width of the animation.
     *
     * @param width The width of the animation.
     */
    var width = 0.0
    /**
     * Returns the height of the animation.  This is *not* calculated from
     * the scene graph, because the graph may be animated in arbitrary ways,
     * but must be precalculated by the animation designer.
     *
     * @return The height of the animation.
     */
    /**
     * Sets the height of the animation.
     *
     * @param height The height of the animation.
     */
    var height = 0.0
    private val _is_animated = false
    override var isDirty = false

    /**
     * Calls back to the [SceneGraphProcessor]
     * <var>p</var> to be processed as a
     * [CompositeNode].
     *
     * @param p A SceneGraphProcessor that is traversing the scene graph.
     */
    override fun accept(p: SceneGraphProcessor) {
        p.process(this)
    }

    override val subgraphCount: Int
        get() = _layers.subgraphCount

    override fun getSubgraph(n: Int): SceneGraph? {
        return _layers.getSubgraph(n)
    }

    override val visibleSubgraphCount: Int
        get() = _layers.visibleSubgraphCount

    override fun getVisibleSubgraph(n: Int): SceneGraph? {
        return _layers.getVisibleSubgraph(n)
    }

    override val lastDrawnSubgraphCount: Int
        get() = _layers.lastDrawnSubgraphCount

    override fun getLastDrawnSubgraph(n: Int): SceneGraph? {
        return _layers.getLastDrawnSubgraph(n)
    }

    override fun addSubgraph(sg: SceneGraph) {
        _layers.addSubgraph(sg)
    }

    override fun removeSubgraph(sg: SceneGraph) {
        _layers.removeSubgraph(sg)
    }

    override fun removeSubgraph(n: Int) {
        _layers.removeSubgraph(n)
    }

    override fun draw(g: Graphics2D) {
        _layers.draw(g)
    }

    @Synchronized
    override fun addActivity(a: Activity) {
        if (a.activityRunner !== this) {
            a.activityRunner = this
            _activities = _activities!!.add(a)
        }
    }

    @Synchronized
    override fun removeActivity(a: Activity) {
        if (a.activityRunner === this) {
            a.activityRunner = null
            _activities = _activities!!.remove(a)
        }
    }

    override val isFinite: Boolean
        get() = false

    override fun reset() {
        val i = _activities!!.iterator()
        while (i!!.hasNext()) {
            (i.next() as Activity).reset()
        }
    }

    override fun performActivity(t: Double) {
        _activities!!.performActivities(t)
    }

    /**
     * Adds a command to the Animation.  The command can then be invoked by
     * the [.invokeCommand] method.
     *
     * @param name    The name of the command.
     * @param command The object that implements the command.
     */
    @Synchronized
    fun addCommand(name: String, command: Command) {
        _commands[name] = command
    }

    /**
     * Removes a command from the Animation.
     *
     * @param name The name of the command to be invoked.
     */
    @Synchronized
    fun removeCommand(name: String) {
        _commands.remove(name)
    }

    @get:Synchronized
    val commandNames: Set<*>
        /**
         * Returns the set of set command names that can be invoked on this
         * Animation.
         *
         * @return An immutable set of Strings, each of which is the name of a command
         * that can be invoked upon the Animation.
         */
        get() = Collections.unmodifiableSet(_commands.keys)

    /**
     * Finds the object that implements a command that can be invoked upon
     * this Animation.
     *
     * @param name The name of the command.
     * @return The object implementing the command, or `null` if the
     * Animation does not have a command of that name.
     */
    @Synchronized
    fun getCommand(name: String?): Command? {
        return _commands[name] as Command?
    }

    /**
     * Invokes a command on the Animation by name.
     *
     * @param name The name of the command.
     * @throws CommandException An error occurred in processing the command, or the Animation
     * does not have a command of that name.
     */
    @Synchronized
    @Throws(CommandException::class)
    fun invokeCommand(name: String) {
        val cmd = _commands[name] as Command?
        if (cmd != null) {
            cmd.invoke()
        } else {
            throw CommandException("unknown command \"$name\"")
        }
    }

    val eventNames: Set<*>
        /**
         * Returns all the names of
         * [uk.ac.ic.doc.scenebeans.event.AnimationEvent]s that will be
         * announced by this Animation.
         *
         * @return An immutable set of Strings, each of which is the name of an
         * AnimationEvent that will be announced by this Animation.
         */
        get() = Collections.unmodifiableSet(_event_names)

    /**
     * Adds an name to the set of names of AnimationEvents that will
     * be posted by this Animation.
     *
     * @param name The name of to be added.
     */
    fun addEventName(name: String) {
        _event_names.add(name)
    }

    /**
     * Removes an name from the set of names of AnimationEvents that will
     * be posted by this Animation.
     *
     * @param name The name to be removed.
     */
    fun removeEventName(name: String) {
        _event_names.remove(name)
    }

    fun announceAnimationEvent(event: String) {
        postActivityComplete(event!!)
    }
}