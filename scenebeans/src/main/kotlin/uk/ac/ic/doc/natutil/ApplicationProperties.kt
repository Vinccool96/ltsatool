package uk.ac.ic.doc.natutil

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

class ApplicationProperties @Throws(IOException::class) constructor(application: String, mainClass: Class<*>,
        resourceName: String) : Properties() {

    @Throws(IOException::class)
    constructor(application: String, mainClass: Class<*>) : this(application, mainClass, "$application.properties")

    init {
        try {
            val `in` = openApplicationProperties(application, mainClass, resourceName)
            try {
                this.load(`in`)
            } finally {
                `in`.close()
            }
        } catch (ex: IOException) { // Leave an empty set of properties and let the program fall
            // back on compiled-in default values
        }
    }

    companion object {

        @Throws(IOException::class)
        fun openApplicationProperties(application: String, main_class: Class<*>, resource_name: String): InputStream {
            val file = getPropertiesFile(application, resource_name)
            return if (file != null) {
                FileInputStream(file)
            } else {
                val `is` = main_class.getResourceAsStream(resource_name)
                `is` ?: throw IOException("resource \"$resource_name\" not found")
            }
        }

        @Throws(IOException::class)
        private fun getPropertiesFile(application: String, resource_name: String): File? {
            val jar_file = "$application.jar"
            val classpath = StringTokenizer(System.getProperty("java.class.path"), System.getProperty("path.separator"))
            var result: File? = null
            while (classpath.hasMoreTokens()) {
                val entry = File(classpath.nextToken())
                if (entry.isDirectory) {
                    result = findDirProperties(entry, application, resource_name)
                } else if (entry.name == jar_file) {
                    result = findJarProperties(entry, application, resource_name)
                }
                if (result != null && result.exists()) {
                    return result
                }
            }
            return null
        }

        private fun findDirProperties(entry: File, application: String, resource_name: String): File? {
            val parent = entry.parentFile
            return if (parent != null && parent.exists() && parent.name == application) {
                File(File(parent, "lib"), resource_name)
            } else {
                null
            }
        }

        private fun findJarProperties(entry: File, application: String, resource_name: String): File? {
            val parent = entry.parentFile
            return if (parent != null) {
                File(parent, resource_name)
            } else {
                null
            }
        }

    }

}