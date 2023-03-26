package uk.ac.ic.doc.natutil

/**
 * The exception thrown by the [CommandLineParser] class when
 * it fails to parse a command line.
 */
class CommandLineException(msg: String?) : Exception(msg) {

    constructor() : this(null)

}