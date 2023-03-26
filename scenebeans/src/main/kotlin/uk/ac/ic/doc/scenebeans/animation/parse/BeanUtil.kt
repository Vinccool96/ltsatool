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
package uk.ac.ic.doc.scenebeans.animation.parse

import java.beans.*

/**
 * Convenience functions for accessing features of Java Beans.
 */
internal object BeanUtil {
    @Throws(AnimationParseException::class)
    fun getBeanInfo(c: Class<*>): BeanInfo {
        return try {
            Introspector.getBeanInfo(c)
        } catch (ex: IntrospectionException) {
            throw AnimationParseException("could not find information about " + c.name + " bean: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun getBeanInfo(o: Any?): BeanInfo {
        return getBeanInfo(o!!.javaClass)
    }

    @Throws(AnimationParseException::class)
    fun getProperty(bean: Any?, name: String): Any {
        val info = getBeanInfo(bean)
        return getProperty(bean, info, name)
    }

    @Throws(AnimationParseException::class)
    fun getProperty(bean: Any?, info: BeanInfo?, name: String): Any {
        val pd = getPropertyDescriptor(info, name)
        return try {
            val get = pd.readMethod
            get.invoke(bean, *arrayOfNulls(0))
        } catch (ex: RuntimeException) {
            throw ex
        } catch (ex: Exception) {
            throw AnimationParseException("cannot get " + name + " property of bean: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun setProperty(bean: Any?, info: BeanInfo?, name: String, value_str: String?, parser: ValueParser) {
        val pd = getPropertyDescriptor(info, name)
        val value = parser.newObject(pd.propertyType, value_str!!)
        try {
            val set = pd.writeMethod
            if (set != null) {
                set.invoke(bean, *arrayOf(value))
            } else {
                throw AnimationParseException("attempted to set read-only property $name")
            }
        } catch (ex: RuntimeException) {
            throw ex
        } catch (ex: Exception) {
            throw AnimationParseException("cannot set " + name + " property of bean: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun setIndexedProperty(bean: Any?, info: BeanInfo?, name: String, index: Int, value_str: String?,
            parser: ValueParser) {
        val pd = getPropertyDescriptor(info, name) as? IndexedPropertyDescriptor ?: throw AnimationParseException(
                "the " + name + " property is not indexed")
        val ipd = pd
        val value = parser.newObject(ipd.indexedPropertyType, value_str!!)
        try {
            val set = ipd.indexedWriteMethod
            if (set != null) {
                set.invoke(bean, *arrayOf(index, value))
            } else {
                throw AnimationParseException("attempted to set read-only property $name")
            }
        } catch (ex: RuntimeException) {
            throw ex
        } catch (ex: Exception) {
            throw AnimationParseException("cannot set " + name + " property of bean: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun getPropertyDescriptor(info: BeanInfo?, name: String): PropertyDescriptor {
        val props = info!!.propertyDescriptors
        for (i in props.indices) {
            if (props[i].name == name) {
                return props[i]
            }
        }
        throw AnimationParseException(
                "beans of type " + info.beanDescriptor.name + " do not have a property named " + name)
    }

    @Throws(AnimationParseException::class)
    fun bindEventListener(listener: Any?, event_source: Any?) {
        val ev = findCompatibleEvent(listener, event_source)
        val add = ev.addListenerMethod
        try {
            add.invoke(event_source, *arrayOf(listener))
        } catch (ex: Exception) {
            throw AnimationParseException("failed to register event listener: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun findCompatibleEvent(listener: Any?, event_source: Any?): EventSetDescriptor {
        val source_info: BeanInfo
        source_info = try {
            Introspector.getBeanInfo(event_source!!.javaClass)
        } catch (ex: IntrospectionException) {
            throw AnimationParseException("cannot find info about event source: " + ex.message)
        }
        val events = source_info.eventSetDescriptors
        for (i in events.indices) {
            val ev_listener_type = events[i].listenerType
            if (ev_listener_type.isInstance(listener)) {
                return events[i]
            }
        }
        throw AnimationParseException("listener not compatible with event source")
    }
}