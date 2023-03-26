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

internal class BeanFactory {
    private val _categories: MutableMap<String, Category> = HashMap()

    /**
     * Add a new bean category.
     */
    fun addCategory(name: String, prefix: String, postfix: String, capitalise: Boolean) {
        require(_categories[name] == null) {
            "category name \"" + name + "\" already defined"
        }
        _categories[name] = Category(name, prefix, postfix, capitalise)
    }

    /**
     * Add a package to a bean category.
     */
    fun addPackage(category: String, pkg_name: String) {
        getCategory(category).addPackage(pkg_name)
    }

    /**
     * Add a package to a bean category.
     */
    fun addPackage(category: String, loader: ClassLoader, pkg_name: String) {
        getCategory(category).addPackage(loader, pkg_name)
    }

    /**
     * Allocate a new Bean of the given type in the given category.
     */
    @Throws(ClassNotFoundException::class, IllegalAccessException::class, InstantiationException::class)
    fun newBean(category: String, type: String): Any {
        return getCategory(category).newBean(type)
    }

    private fun getCategory(name: String): Category {
        val c = _categories[name] as Category?
        return c ?: throw IllegalArgumentException("no category named \"" + name + "\"")
    }

    private class Package(private val _loader: ClassLoader, private val _package: String) {
        constructor(pkg: String) : this(ClassLoader.getSystemClassLoader(), pkg)

        fun loadClass(class_name: String): Class<*>? {
            val full_name = "$_package.$class_name"
            return try {
                _loader.loadClass(full_name)
            } catch (ex: ClassNotFoundException) {
                null
            }
        }
    }

    private class Category internal constructor(private val _name: String, private val _prefix: String,
            private val _postfix: String, private val _capitalise: Boolean) {
        private val _packages: MutableList<Package> = ArrayList()
        fun addPackage(loader: ClassLoader, pkg_name: String) {
            _packages.add(Package(loader, pkg_name))
        }

        fun addPackage(pkg_name: String) {
            _packages.add(Package(pkg_name))
        }

        @Throws(ClassNotFoundException::class, InstantiationException::class, IllegalAccessException::class)
        fun newBean(type: String): Any {
            val class_name = _prefix + type[0].uppercaseChar() + type.substring(1) + _postfix
            val i: Iterator<*> = _packages.iterator()
            while (i.hasNext()) {
                val c = (i.next() as Package).loadClass(class_name)
                if (c != null) {
                    return c.newInstance()
                }
            }
            throw ClassNotFoundException("no class found for " + _name + " bean of type \"" + type + "\"")
        }
    }
}