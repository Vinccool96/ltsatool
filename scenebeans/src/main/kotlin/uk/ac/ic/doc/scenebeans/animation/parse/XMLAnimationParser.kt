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

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.ProcessingInstruction
import org.xml.sax.SAXException
import org.xml.sax.SAXParseException
import uk.ac.ic.doc.natutil.MacroException
import uk.ac.ic.doc.natutil.MacroExpander
import uk.ac.ic.doc.scenebeans.*
import uk.ac.ic.doc.scenebeans.activity.*
import uk.ac.ic.doc.scenebeans.animation.*
import java.awt.*
import java.beans.BeanInfo
import java.io.*
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.net.URL
import java.net.URLClassLoader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

/**
 * An XMLAnimationParser is responsible for translating an XML document
 * into an [Animation].  It can
 * load the XML document from a file or URL.
 *
 * @see Animation
 */
class XMLAnimationParser(
        /**
         * Returns the URL of the XML document being parsed.
         */
        val documentURL: URL,
        /**
         * Returns the Component on which the parsed Animation is to be displayed.
         */
        val viewComponent: Component) {
    var _value_parser: ValueParser
    private val _factory = BeanFactory()
    private val _symbol_table: MutableMap<String, Any> = HashMap() // Indexed by symbol ID
    private val _behaviour_links: MutableList<BehaviourLink> = ArrayList()
    private val _event_links: MutableList<EventLink> = ArrayList()
    private val _macro_table = MacroExpander()
    private var _anim: Animation? = null // The animation currently being parsed

    /**
     * Constructs an AnimationParser that parses an XML file located
     * at <var>doc_url</var> and is to be displayed on the Component
     * <var>view</var>.
     *
     * @param doc_url The URL of the XML document to be parsed.
     * @param view    The Component on which the Animation is to be displayed.
     */
    init {
        _value_parser = ValueParser(documentURL)
        _factory.addCategory(CATEGORY_SCENE, "", "", true)
        _factory.addPackage(CATEGORY_SCENE, PKG_SCENE)
        _factory.addCategory(CATEGORY_BEHAVIOUR, "", "", true)
        _factory.addPackage(CATEGORY_BEHAVIOUR, PKG_BEHAVIOUR)
    }

    /**
     * Constructs an AnimationParser that parses an XML file stored in
     * a file and is to be displayed on the Component <var>view</var>.
     *
     * @param file The file containing the XML document to be parsed.
     * @param view The Component on which the Animation is to be displayed.
     */
    constructor(file: File, view: Component) : this(file.toURL(), view)

    /**
     * Registers a package in the system class loader to be searched for classes
     * of scene-graph node.
     *
     *
     * Names of scene bean types are translated into class names by capitalising
     * their first letter and then searching for a class with that name
     * in the packages registered by the `addScenePackage` functions.
     * Packages are searched in order of registration, with the package
     * `uk.ac.ic.doc.scenebeans` being searched first.
     * Packages of beans can also be registered within an XML document using
     * the `<?scenebeans...?>` processing instruction.
     *
     * @param pkg The name of the package containing the SceneBean classes.
     */
    fun addScenePackage(pkg: String) {
        _factory.addPackage(CATEGORY_SCENE, pkg)
    }

    /**
     * Registers a package in the given class loader to be searched for classes
     * of scene-graph node.
     *
     *
     * Names of scene bean types are translated into class names by capitalising
     * their first letter and then searching for a class with that name
     * in the packages registered by the `addScenePackage` functions.
     * Packages are searched in order of registration, with the package
     * `uk.ac.ic.doc.scenebeans` being searched first.
     * Packages of beans can also be registered within an XML document using
     * the `<?scenebeans...?>` processing instruction.
     *
     * @param l   The ClassLoader used to load the package.
     * @param pkg The name of the package containing the SceneBean classes.
     */
    fun addScenePackage(l: ClassLoader, pkg: String) {
        _factory.addPackage(CATEGORY_SCENE, l, pkg)
    }

    /**
     * Registers a package in the given class loader to be searched for classes
     * of behaviour bean.
     *
     *
     * Names of behaviour algorithms are translated into class names by
     * capitalising their first letter and then searching for a class with that
     * name in the packages registered by the `addScenePackage`
     * functions.
     * Packages are searched in order of registration, with the package
     * `uk.ac.ic.doc.scenebeans` being searched first.
     * Packages of beans can also be registered within an XML document using
     * the `<?scenebeans...?>` processing instruction.
     *
     * @param pkg The name of the package containing the SceneBean classes.
     */
    fun addBehaviourPackage(pkg: String) {
        _factory.addPackage(CATEGORY_BEHAVIOUR, pkg)
    }

    /**
     * Registers a package in the system class loader to be searched for classes
     * of behaviour bean.
     *
     *
     * Names of behaviour algorithms are translated into class names by
     * capitalising their first letter and then searching for a class with that
     * name in the packages registered by the `addScenePackage`
     * functions.
     * Packages are searched in order of registration, with the package
     * `uk.ac.ic.doc.scenebeans` being searched first.
     * Packages of beans can also be registered within an XML document using
     * the `<?scenebeans...?>` processing instruction.
     *
     * @param l   The ClassLoader used to load the package.
     * @param pkg The name of the package containing the SceneBean classes.
     */
    fun addBehaviourPackage(l: ClassLoader?, pkg: String) {
        _factory.addPackage(CATEGORY_BEHAVIOUR, pkg)
    }

    /**
     * Parses the data identified by the URL passed to the parser's
     * constructor into a DOM document model and then translates that
     * document model into a Animation.
     *
     * @throws IOException             An I/O error occurred while reading the XML document.
     * @throws AnimationParseException The XML file contained invalid information.  It may, for example,
     * be malformed or contain elements or attributes not understood by
     * the parser.
     */
    @Throws(IOException::class, AnimationParseException::class)
    fun parseAnimation(): Animation {
        return try {
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(File(documentURL.toURI()))
            doc.documentElement.normalize()
            translateDocument(doc)
        } catch (ex: SAXParseException) {
            throw AnimationParseException(ex.systemId + " line  " + ex.lineNumber + ": " + ex.message)
        } catch (ex: SAXException) {
            throw AnimationParseException("failed to parse XML: " + ex.message)
        } catch (e: ParserConfigurationException) {
            throw IOException(e)
        } catch (e: URISyntaxException) {
            throw IOException(e)
        }
    }

    @Throws(AnimationParseException::class)
    fun translateDocument(doc: Document): Animation {
        translateProcessingInstructions(doc)
        val e = doc.documentElement
        if (e.tagName != Tag.ANIMATION) {
            throw AnimationParseException("invalid document type")
        }
        _anim = Animation()
        setAnimationDimensions(e, _anim!!)
        val nodes = e.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is Element) {
                translateElement(node)
            }
        }

        /*  Return the Animation to the caller, marking the currently parsed
         *  sprite as null to indicate the end of parsing.
         */
        val anim = _anim as Animation
        _anim = null
        return anim
    }

    @Throws(AnimationParseException::class)
    fun translateProcessingInstructions(d: Document) {
        val nodes = d.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is ProcessingInstruction) {
                translateProcessingInstruction(node)
            }
        }
    }

    @Throws(AnimationParseException::class)
    fun translateProcessingInstruction(pi: ProcessingInstruction) {
        if (pi.target != PI_TARGET) {
            return
        }
        var codebase: String? = null
        var category: String? = null
        var pkg_name: String? = null
        try {
            val r = PushbackReader(StringReader(pi.data))
            while (trim(r)) {
                val tag = parseTag(r)
                val value = parseValue(r)
                if (tag == PI_CODEBASE) {
                    codebase = value
                } else if (tag == PI_CATEGORY) {
                    category = value
                } else if (tag == PI_PACKAGE) {
                    pkg_name = value
                } else {
                    throw AnimationParseException("unknown element \"" + tag + "\" in processing instruction")
                }
            }
        } catch (ex: IOException) {
            throw AnimationParseException("failed to parse processing instruction: " + ex.message)
        }
        if (category == null) {
            throw AnimationParseException("category not specified in processing instruction")
        } else if (pkg_name == null) {
            throw AnimationParseException("package not specified in processing instruction")
        }
        if (codebase == null) {
            _factory.addPackage(category, pkg_name)
        } else {
            try {
                val loader: ClassLoader = URLClassLoader(arrayOf(URL(documentURL, codebase)))
                _factory.addPackage(category, loader, pkg_name)
            } catch (ex: MalformedURLException) {
                throw AnimationParseException("malformed URL in codebase of processing instruction: " + ex.message)
            }
        }
    }

    @Throws(AnimationParseException::class, IOException::class)
    private fun parseTag(r: PushbackReader): String {
        val buf = StringBuffer()
        var ch: Int
        while (true) {
            ch = r.read()
            if (ch == -1) {
                throw AnimationParseException("malformed processing instruction")
            } else if (ch == '='.code) {
                return buf.toString()
            } else {
                buf.append(ch.toChar())
            }
        }
    }

    @Throws(AnimationParseException::class, IOException::class)
    private fun parseValue(r: PushbackReader): String {
        expect(r, '\"')
        val buf = StringBuffer()
        var ch: Int
        while (true) {
            ch = r.read()
            if (ch == -1) {
                throw AnimationParseException("malformed processing instruction")
            } else if (ch == '\"'.code) {
                return buf.toString()
            } else {
                buf.append(ch.toChar())
            }
        }
    }

    @Throws(IOException::class)
    private fun trim(r: PushbackReader): Boolean {
        var ch: Int
        do {
            ch = r.read()
            if (ch == -1) {
                return false
            }
        } while (Character.isWhitespace(ch.toChar()))
        r.unread(ch)
        return true
    }

    @Throws(AnimationParseException::class, IOException::class)
    private fun expect(r: PushbackReader, expected_ch: Char) {
        val ch = r.read()
        if (ch != expected_ch.code) {
            throw AnimationParseException("malformed processing exception")
        }
    }

    @Throws(AnimationParseException::class)
    private fun setAnimationDimensions(e: Element, s: Animation) {
        try {
            val width_str = getOptionalAttribute(e, Attr.WIDTH)
            val height_str = getOptionalAttribute(e, Attr.HEIGHT)
            _anim!!.width = ExprUtil.evaluate(width_str)
            _anim!!.height = ExprUtil.evaluate(height_str)
        } catch (ex: NumberFormatException) {
            throw AnimationParseException("invalid dimension: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun translateElement(elt: Element) {
        val type = elt.tagName
        if (type == Tag.BEHAVIOUR) {
            translateBehaviour(elt)
        } else if (type == Tag.SEQ) {
            translateSeq(elt)
        } else if (type == Tag.CO) {
            translateCo(elt)
        } else if (type == Tag.COMMAND) {
            translateCommand(elt)
        } else if (type == Tag.EVENT) {
            translateEvent(elt)
        } else if (type == Tag.DEFINE) {
            translateDefine(elt)
        } else if (type == Tag.DRAW) {
            translateDraw(elt)
        } else if (type == Tag.FORALL) {
            parseForall(elt, object : ForallParser {
                @Throws(AnimationParseException::class)
                override fun parse(e: Element) {
                    translateElement(e)
                }
            })
        } else {
            throw AnimationParseException("invalid element \"" + type + "\"")
        }
    }

    @Throws(AnimationParseException::class)
    fun parseForall(elt: Element, child_parser: ForallParser) {
        val `var` = getRequiredAttribute(elt, Attr.VAR)
        val values = getRequiredAttribute(elt, Attr.VALUES)
        var sep = getOptionalAttribute(elt, Attr.SEP)
        if (sep == null) {
            sep = " \n\t"
        }
        val tok = StringTokenizer(values, sep)
        while (tok.hasMoreTokens()) {
            addMacro(`var`, tok.nextToken())
            val children = elt.childNodes
            for (i in 0 until children.length) {
                val child_node = children.item(i)
                if (child_node is Element) {
                    val child_elt = child_node
                    if (child_elt.tagName == Tag.FORALL) {
                        parseForall(child_elt, child_parser)
                    } else {
                        child_parser.parse(child_elt)
                    }
                }
            }
            removeMacro(`var`)
        }
    }

    /*-----------------------------------------------------------------------
     *  Forall: macro definition and iteration
     */
    @Throws(AnimationParseException::class)
    fun translateBehaviour(elt: Element) {
        val behaviour = createBehaviour(elt)
        if (behaviour is Activity) {
            optionalStartActivity(elt, behaviour)
        }
    }

    /*-----------------------------------------------------------------------
     *  Behaviour
     */
    @Throws(AnimationParseException::class)
    fun translateSeq(elt: Element) {
        val seq = createSequentialActivity(elt)
        optionalStartActivity(elt, seq)
    }

    @Throws(AnimationParseException::class)
    fun translateCo(elt: Element) {
        val co = createConcurrentActivity(elt)
        optionalStartActivity(elt, co)
    }

    @Throws(AnimationParseException::class)
    fun createSequentialActivity(elt: Element): SequentialActivity {
        val seq = SequentialActivity()
        val event_name = getOptionalAttribute(elt, Attr.EVENT)
        if (event_name != null) {
            seq.activityName = event_name
        }
        createSubActivities(seq, elt)
        putOptionalSymbol(elt, seq)
        return seq
    }

    @Throws(AnimationParseException::class)
    fun createConcurrentActivity(elt: Element): ConcurrentActivity {
        val co = ConcurrentActivity()
        val event_name = getOptionalAttribute(elt, Attr.EVENT)
        if (event_name != null) {
            co.activityName = event_name
        }
        createSubActivities(co, elt)
        putOptionalSymbol(elt, co)
        return co
    }

    @Throws(AnimationParseException::class)
    fun createSubActivities(ca: CompositeActivity, elt: Element) {
        val children = elt.childNodes
        for (i in 0 until children.length) {
            val child_node = children.item(i)
            if (child_node is Element) {
                createSubActivity(ca, child_node)
            }
        }
    }

    @Throws(AnimationParseException::class)
    fun createSubActivity(ca: CompositeActivity, elt: Element) {
        val elt_type = elt.tagName
        if (elt_type == Tag.FORALL) {
            parseForall(elt, object : ForallParser {
                @Throws(AnimationParseException::class)
                override fun parse(e: Element) {
                    createSubActivity(ca, e)
                }
            })
        } else if (elt_type == Tag.BEHAVIOUR) {
            val behaviour = createBehaviour(elt)
            if (behaviour is Activity && behaviour.isFinite) {
                ca.addActivity((behaviour as Activity?)!!)
            } else {
                throw AnimationParseException(elt.tagName + " elements can only contain finite behaviours")
            }
        } else if (elt_type == Tag.CO) {
            ca.addActivity(createConcurrentActivity(elt))
        } else if (elt_type == Tag.SEQ) {
            ca.addActivity(createSequentialActivity(elt))
        } else {
            throw AnimationParseException("invalid element $elt_type")
        }
    }

    @Throws(AnimationParseException::class)
    fun createBehaviour(elt: Element): Any? {
        return try {
            val algorithm = getRequiredAttribute(elt, Attr.ALGORITHM)
            val event = getOptionalAttribute(elt, Attr.EVENT)
            val behaviour = _factory.newBean(CATEGORY_BEHAVIOUR, algorithm)
            val behaviour_info = BeanUtil.getBeanInfo(behaviour)
            if (event != null) {
                if (behaviour is Activity) {
                    BeanUtil.setProperty(behaviour, behaviour_info, PROPERTY_ACTIVITY_NAME, event, _value_parser)
                } else {
                    throw AnimationParseException("only activities report completion events")
                }
            }
            initialiseParameters(behaviour, behaviour_info, elt)
            putOptionalSymbol(elt, behaviour)
            behaviour
        } catch (ex: RuntimeException) {
            throw ex
        } catch (ex: Exception) {
            throw AnimationParseException("could not create behaviour: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun optionalStartActivity(elt: Element, a: Activity) {
        val state = getOptionalAttribute(elt, Attr.STATE)
        if (state != null && state == Value.STARTED) {
            _anim!!.addActivity(a)
        }
    }

    @Throws(AnimationParseException::class)
    fun translateCommand(elt: Element) {
        val name = getRequiredAttribute(elt, Attr.NAME)
        if (_anim!!.getCommand(name) != null) {
            throw AnimationParseException("a command named \"" + name + "\" has already been defined")
        }
        val cmd = createCompositeCommand(elt)!!
        _anim!!.addCommand(name, cmd)
    }

    /*-----------------------------------------------------------------------
     *  Commands and events
     */
    @Throws(AnimationParseException::class)
    fun translateEvent(elt: Element) {
        val object_id = getRequiredAttribute(elt, Attr.OBJECT)
        val event_name = getRequiredAttribute(elt, Attr.EVENT)
        val bean = getSymbol(object_id)
        val cmd = createCompositeCommand(elt)
        val invoker = EventInvoker(event_name, cmd)
        BeanUtil.bindEventListener(invoker, bean)
        _event_links.add(EventLink(bean, object_id, invoker))
    }

    @Throws(AnimationParseException::class)
    fun createCompositeCommand(elt: Element): Command? {
        val sub_cmds = elt.childNodes
        val cmd = CompositeCommand()
        if (sub_cmds.length == 0) {
            throw AnimationParseException("empty command body")
        }
        for (i in 0 until sub_cmds.length) {
            val child_node = sub_cmds.item(i)
            if (child_node is Element) {
                val child_elt = child_node
                if (child_elt.tagName == Tag.FORALL) {
                    parseForall(child_elt, object : ForallParser {
                        @Throws(AnimationParseException::class)
                        override fun parse(e: Element) {
                            cmd.addCommand(createSubCommand(e))
                        }
                    })
                } else {
                    cmd.addCommand(createSubCommand(child_elt))
                }
            }
        }
        return if (cmd.commandCount == 1) {
            cmd.getCommand(0)
        } else {
            cmd
        }
    }

    @Throws(AnimationParseException::class)
    fun createSubCommand(elt: Element): Command {
        val tag = elt.tagName
        return if (tag == Tag.START) {
            createStartCommand(elt)
        } else if (tag == Tag.STOP) {
            createStopCommand(elt)
        } else if (tag == Tag.RESET) {
            createResetCommand(elt)
        } else if (tag == Tag.SET) {
            createSetCommand(elt)
        } else if (tag == Tag.INVOKE) {
            createInvokeCommand(elt)
        } else if (tag == Tag.ANNOUNCE) {
            createAnnounceCommand(elt)
        } else {
            throw AnimationParseException("unexpected element type \"" + tag + "\"")
        }
    }

    @Throws(AnimationParseException::class)
    fun createStartCommand(elt: Element): Command {
        val name = getRequiredAttribute(elt, Attr.BEHAVIOUR)
        val bean = getSymbol(name)
        return if (bean is Activity) {
            val activity = bean
            var runner = activity.activityRunner
            if (runner == null) {
                runner = _anim
            }
            StartActivityCommand(activity, runner)
        } else {
            throw AnimationParseException("symbol \"$name\" does not refer to an activity")
        }
    }

    @Throws(AnimationParseException::class)
    fun createStopCommand(elt: Element): Command {
        val name = getRequiredAttribute(elt, Attr.BEHAVIOUR)
        val bean = getSymbol(name)
        return if (bean is Activity) {
            StopActivityCommand(bean)
        } else {
            throw AnimationParseException("symbol \"$name\" does not refer to an activity")
        }
    }

    @Throws(AnimationParseException::class)
    fun createResetCommand(elt: Element): Command {
        val name = getRequiredAttribute(elt, Attr.BEHAVIOUR)
        val bean = getSymbol(name)
        return if (bean is Activity) {
            ResetActivityCommand(bean)
        } else {
            throw AnimationParseException("symbol \"$name\" does not refer to an activity")
        }
    }

    @Throws(AnimationParseException::class)
    fun createSetCommand(elt: Element): Command {
        val symbol = getRequiredAttribute(elt, Attr.OBJECT)
        val param_str = getRequiredAttribute(elt, Attr.PARAM)
        val value_str = getRequiredAttribute(elt, Attr.VALUE)
        val bean = getSymbol(symbol)
        val info = BeanUtil.getBeanInfo(bean)
        val pd = BeanUtil.getPropertyDescriptor(info, param_str)
        val value = _value_parser.newObject(pd!!.propertyType, value_str)
        val set_method = pd.writeMethod
        return SetParameterCommand(bean, set_method, value)
    }

    @Throws(AnimationParseException::class)
    fun createInvokeCommand(elt: Element): Command {
        val cmd_name = getRequiredAttribute(elt, Attr.COMMAND)
        val obj_name = getOptionalAttribute(elt, Attr.OBJECT)
        val anim: Animation?
        anim = if (obj_name == null) {
            _anim
        } else {
            val sym = getSymbol(obj_name)
            if (sym is Animation) {
                sym
            } else {
                throw AnimationParseException("symbol \"" + obj_name + "\" does not refer to an animation")
            }
        }
        val cmd = anim!!.getCommand(cmd_name)
        return cmd ?: throw AnimationParseException("command \"$cmd_name\" not supported by animation")
    }

    @Throws(AnimationParseException::class)
    fun createAnnounceCommand(elt: Element): Command {
        val event_name = getRequiredAttribute(elt, Attr.EVENT)
        _anim!!.addEventName(event_name)
        return AnnounceCommand(_anim, event_name)
    }

    @Throws(AnimationParseException::class)
    fun translateDefine(elt: Element) {
        val sg = createDrawNode(elt)!!
        putOptionalSymbol(elt, sg)
    }

    /*-----------------------------------------------------------------------
     *  Graphical elements
     */
    @Throws(AnimationParseException::class)
    fun translateDraw(elt: Element) {
        val sg = createDrawNode(elt)!!
        putOptionalSymbol(elt, sg)
        _anim!!.addSubgraph(sg)
    }

    @Throws(AnimationParseException::class)
    fun createDrawNode(elt: Element): SceneGraph? {
        return minimise(createChildren(elt))
    }

    @Throws(AnimationParseException::class)
    fun createSceneGraph(elt: Element): SceneGraph? {
        val elt_type = elt.tagName
        val sg = if (elt_type == Tag.DRAW) {
            createDrawNode(elt)
        } else if (elt_type == Tag.TRANSFORM) {
            createTransformNode(elt)
        } else if (elt_type == Tag.STYLE) {
            createStyleNode(elt)
        } else if (elt_type == Tag.INPUT) {
            createInputNode(elt)
        } else if (elt_type == Tag.COMPOSE) {
            createComposeNode(elt)
        } else if (elt_type == Tag.INST) {
            createInstNode(elt)
        } else if (elt_type == Tag.INCLUDE) {
            createIncludeNode(elt)
        } else if (elt_type == Tag.PRIMITIVE) {
            createPrimitiveNode(elt)
        } else {
            throw AnimationParseException("unknown scene-graph type \"" + elt_type + "\"")
        }

        //putOptionalSymbol( elt, sg );
        return sg
    }

    @Throws(AnimationParseException::class)
    fun createChildren(elt: Element): Layered {
        val layers = Layered()
        createChildren(layers, elt)
        return layers
    }

    @Throws(AnimationParseException::class)
    fun createChildren(composite: CompositeNode?, elt: Element): CompositeNode? {
        val nodes = elt.childNodes
        for (i in 0 until nodes.length) {
            val node = nodes.item(i)
            if (node is Element) {
                val child = nodes.item(i) as Element
                val child_type = child.tagName
                if (child_type == Tag.FORALL) {
                    parseForall(child, object : ForallParser {
                        @Throws(AnimationParseException::class)
                        override fun parse(e: Element) {
                            composite!!.addSubgraph(createSceneGraph(e)!!)
                        }
                    })
                } else if (!(child_type == Tag.PARAM || child_type == Tag.ANIMATE)) {
                    composite!!.addSubgraph(createSceneGraph(child)!!)
                }
            }
        }
        if (composite!!.subgraphCount == 0) {
            throw AnimationParseException("no layers in composite")
        }
        return composite
    }

    @Throws(AnimationParseException::class)
    fun minimise(composite: CompositeNode): SceneGraph? {
        return if (composite.subgraphCount == 0) {
            throw AnimationParseException("no layers in composite")
        } else if (composite.subgraphCount == 1) {
            composite.getSubgraph(0)
        } else {
            composite
        }
    }

    @Throws(AnimationParseException::class)
    fun createTransformNode(elt: Element): SceneGraph? {
        val type = getRequiredAttribute(elt, Attr.TYPE)
        val bean = try {
            newSceneBean(type) as Transform?
        } catch (ex: ClassCastException) {
            throw AnimationParseException("$type is not a transform node")
        }
        putOptionalSymbol(elt, bean!!)
        val sg = minimise(createChildren(elt))
        bean!!.transformedGraph = sg
        initialiseParameters(bean, BeanUtil.getBeanInfo(bean), elt)
        return bean
    }

    @Throws(AnimationParseException::class)
    fun createStyleNode(elt: Element): SceneGraph? {
        val type = getRequiredAttribute(elt, Attr.TYPE)
        val bean = try {
            newSceneBean(type) as Style?
        } catch (ex: ClassCastException) {
            throw AnimationParseException("$type is not a style node")
        }
        putOptionalSymbol(elt, bean!!)
        val sg = minimise(createChildren(elt))
        bean!!.styledGraph = sg
        initialiseParameters(bean, BeanUtil.getBeanInfo(bean), elt)
        return bean
    }

    @Throws(AnimationParseException::class)
    fun createInputNode(elt: Element): SceneGraph? {
        val type = getRequiredAttribute(elt, Attr.TYPE)
        val bean = try {
            newSceneBean(type) as Input?
        } catch (ex: ClassCastException) {
            throw AnimationParseException("$type is not an input node")
        }
        putOptionalSymbol(elt, bean!!)
        val sg = minimise(createChildren(elt))
        bean!!.sensitiveGraph = sg
        initialiseParameters(bean, BeanUtil.getBeanInfo(bean), elt)
        return bean
    }

    @Throws(AnimationParseException::class)
    fun createComposeNode(elt: Element): SceneGraph? {
        val type = getRequiredAttribute(elt, Attr.TYPE)
        val comp = try {
            newSceneBean(type) as CompositeNode?
        } catch (ex: ClassCastException) {
            throw AnimationParseException("$type is not a composite node")
        }
        putOptionalSymbol(elt, comp!!)
        createChildren(comp, elt)
        initialiseParameters(comp, BeanUtil.getBeanInfo(comp), elt)
        return comp
    }

    @Throws(AnimationParseException::class)
    fun createInstNode(elt: Element): SceneGraph {
        val src = getRequiredAttribute(elt, Attr.OBJECT)
        val link = getSymbol(src)
        return if (link is SceneGraph) {
            link
        } else {
            throw AnimationParseException("link target \"" + src + "\" does not refer to a " + "scene-graph node")
        }
    }

    @Throws(AnimationParseException::class)
    fun createIncludeNode(elt: Element): SceneGraph {
        val src_str = getRequiredAttribute(elt, Attr.SRC)
        return try {
            val inc_url = URL(documentURL, src_str)
            val inc_parser = XMLAnimationParser(inc_url, viewComponent)

            /*  Add all macros to the parser
             */
            val params = elt.childNodes
            for (i in 0 until params.length) {
                val node = params.item(i)
                if (node is Element) {
                    val e = params.item(i) as Element
                    if (e.tagName != Tag.PARAM) {
                        throw AnimationParseException(
                                "only " + Tag.PARAM + " tags are allowed in a " + elt.tagName + " node")
                    }
                    val name = getRequiredAttribute(e, Attr.NAME)
                    val value = getRequiredAttribute(e, Attr.VALUE)
                    inc_parser.addMacro(name, value)
                }
            }

            /*  Parse the Animation
             */
            val inc_anim = inc_parser.parseAnimation()

            /*  Embed the included Animation
             */_anim!!.addActivity(inc_anim)
            putOptionalSymbol(elt, inc_anim)
            inc_anim
        } catch (ex: MalformedURLException) {
            throw AnimationParseException("invalid URL " + src_str + ": " + ex.message)
        } catch (ex: IOException) {
            throw AnimationParseException("failed to include animation " + src_str + ": " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun createPrimitiveNode(elt: Element): SceneGraph {
        val type = getRequiredAttribute(elt, Attr.TYPE)
        val drawn = getOptionalAttribute(elt, Attr.DRAWN)
        val bean = newSceneBean(type) as? SceneGraph ?: throw AnimationParseException(
                "type \"" + type + "\" is not a SceneGraph class")
        val info = BeanUtil.getBeanInfo(bean)
        initialiseParameters(bean, info, elt)
        putOptionalSymbol(elt, bean)
        return bean
    }

    @Throws(AnimationParseException::class)
    fun newSceneBean(type: String): Any? {
        return try {
            _factory.newBean(CATEGORY_SCENE, type)
        } catch (ex: Exception) {
            throw AnimationParseException("failed to create scene bean: " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun initialiseParameters(bean: Any?, bean_elt: Element) {
        initialiseParameters(bean, BeanUtil.getBeanInfo(bean), bean_elt)
    }

    /*-----------------------------------------------------------------------
     *  Methods for processing the "param" and "animate" elements that are
     *  contained in many different elements.
     */
    @Throws(AnimationParseException::class)
    fun initialiseParameters(bean: Any?, info: BeanInfo?, bean_elt: Element) {
        val params = bean_elt.childNodes
        for (i in 0 until params.length) {
            val node = params.item(i)
            if (node is Element) {
                initialiseParameter(bean, info, node)
            }
        }
    }

    @Throws(AnimationParseException::class)
    fun initialiseParameter(bean: Any?, info: BeanInfo?, param_elt: Element) {
        if (param_elt.tagName == Tag.PARAM) {
            setParameter(bean, info, param_elt)
        } else if (param_elt.tagName == Tag.ANIMATE) {
            animateParameter(bean, param_elt)
        } else if (param_elt.tagName == Tag.FORALL) {
            parseForall(param_elt, object : ForallParser {
                @Throws(AnimationParseException::class)
                override fun parse(e: Element) {
                    initialiseParameter(bean, info, e)
                }
            })
        }
    }

    /**
     * Sets a single parameter of the object 'bean' from the attributes
     * of the "param" element 'param_elt'.
     */
    @Throws(AnimationParseException::class)
    fun setParameter(bean: Any?, info: BeanInfo?, param_elt: Element) {
        val param_name = getRequiredAttribute(param_elt, Attr.NAME)
        val index_str = getOptionalAttribute(param_elt, Attr.INDEX)
        val value_str = getRequiredAttribute(param_elt, Attr.VALUE)
        if (index_str == null) {
            BeanUtil.setProperty(bean, info, param_name, value_str, _value_parser)
        } else {
            val index: Int
            index = try {
                Math.floor(ExprUtil.evaluate(index_str)).toInt()
            } catch (ex: IllegalArgumentException) {
                throw AnimationParseException("invalid property index: " + ex.message)
            }
            BeanUtil.setIndexedProperty(bean, info, param_name, index, value_str, _value_parser)
        }
    }

    /**
     * Binds a behaviour to a parameter so that the parameter's value is
     * updated whenever the behaviour is simulated.  A single behaviour
     * can update multiple parameters and a single parameter can have
     * multiple behaviours, although the effect is undefined if more
     * than one of those behaviours is being simulated at the same time.
     */
    @Throws(AnimationParseException::class)
    fun animateParameter(bean: Any?, anim_elt: Element) {
        val param_name = getRequiredAttribute(anim_elt, Attr.PARAM)
        val index_str = getOptionalAttribute(anim_elt, Attr.INDEX)
        val behaviour_id = getRequiredAttribute(anim_elt, Attr.BEHAVIOUR)
        var facet_id = getOptionalAttribute(anim_elt, Attr.FACET)
        val behaviour: Any?
        val facet: Any?
        val behaviour_listener: Any
        if (index_str == null) {
            behaviour_listener = newBehaviourAdapter(bean, param_name)
        } else {
            val index: Int
            index = try {
                index_str.toInt()
            } catch (ex: NumberFormatException) {
                throw AnimationParseException("invalid property index: " + ex.message)
            }
            behaviour_listener = newIndexedBehaviourAdapter(bean, param_name, index)
        }
        behaviour = getSymbol(behaviour_id)
        if (facet_id != null) {
            facet_id += "Facet"
            facet = BeanUtil.getProperty(behaviour, facet_id)
        } else {
            facet = behaviour
        }
        BeanUtil.bindEventListener(behaviour_listener, facet)
        _behaviour_links.add(
                BehaviourLink(behaviour, behaviour_id, facet, facet_id, bean, behaviour_listener, param_name))
    }

    @Throws(AnimationParseException::class)
    fun newBehaviourAdapter(bean: Any?, param_name: String): Any {
        val bean_class: Class<*> = bean!!.javaClass
        val method_name = adapterMethodName(param_name)
        return try {
            val method = bean_class.getMethod(method_name, *arrayOfNulls(0))
            method.invoke(bean, *arrayOfNulls(0))
        } catch (ex: Exception) {
            throw AnimationParseException(
                    "could not create adapter for parameter \"" + param_name + "\": " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun newIndexedBehaviourAdapter(bean: Any?, param_name: String, idx: Int): Any {
        val bean_class: Class<*> = bean!!.javaClass
        val method_name = "new" + param_name[0].uppercaseChar() + param_name.substring(1) + "Adapter"
        return try {
            val method = bean_class.getMethod(method_name, Integer.TYPE)
            method.invoke(bean, idx)
        } catch (ex: Exception) {
            throw AnimationParseException(
                    "could not create adapter for parameter \"" + param_name + "\": " + ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun adapterMethodName(param_name: String): String {
        return "new" + param_name[0].uppercaseChar() + param_name.substring(1) + "Adapter"
    }

    @Throws(AnimationParseException::class)
    fun putOptionalSymbol(elt: Element, bean: Any) {
        val symbol = getOptionalAttribute(elt, Attr.ID)
        symbol?.let { putSymbol(it, bean) }
    }/*-----------------------------------------------------------------------
     *  General utility methods to access parsing state
     */

    /**
     * Put a named bean into the symbol table.  This allows an application
     * to make its user-interface elements and other objects available as
     * behaviours to XML documents.
     *
     * @param symbol The name of the symbol.
     * @param bean   The value of the symbol.
     * @throws AnimationParseException The name of the symbol is already defined in the symbol table.
     */
    @Throws(AnimationParseException::class)
    fun putSymbol(symbol: String, bean: Any) {
        if (_symbol_table.containsKey(symbol)) {
            throw AnimationParseException("duplicate definition of symbol \"$symbol\"")
        }
        _symbol_table[symbol] = bean
    }

    /**
     * Looks up an object (scene-graph node or behaviour) in the symbol
     * table, indexed by it's XML name (given by the the `id` tag
     * attribute.
     *
     * @param symbol The name of the symbol.
     * @return The value of the symbol.
     * @throws AnimationParseException The symbol is not defined in the symbol table.
     */
    @Throws(AnimationParseException::class)
    fun getSymbol(symbol: String): Any? {
        return if (_symbol_table.containsKey(symbol)) {
            _symbol_table[symbol]
        } else {
            throw AnimationParseException("symbol \"$symbol\" has not been defined")
        }
    }

    val symbols: Map<*, *>
        /**
         * Returns an immutable view of the symbol table.
         *
         * @return An immutable map, indexed by string symbol name.
         */
        get() = Collections.unmodifiableMap(_symbol_table)
    val behaviourLinks: Collection<*>
        /**
         * Returns an immutable view of thelinks between behaviours and
         * animated beans.
         *
         * @return A Collection of
         * [BehaviourLink]
         * objects.
         */
        get() = Collections.unmodifiableList(_behaviour_links)
    val eventLinks: Collection<*>
        /**
         * Returns an immutable view of the links between event sources and
         * commands invoked in response to events from those sources.
         *
         * @return A Collection of
         * [EventLink] objects.
         */
        get() = Collections.unmodifiableList(_event_links)

    @Throws(AnimationParseException::class)
    fun getRequiredAttribute(e: Element, attr: String?): String {
        return try {
            val s = XMLUtil.getRequiredAttribute(e, attr)
            _macro_table.expandMacros(s)
        } catch (ex: MacroException) {
            throw AnimationParseException(ex.message)
        }
    }

    @Throws(AnimationParseException::class)
    fun getOptionalAttribute(e: Element, attr: String?): String? {
        return try {
            var s = XMLUtil.getOptionalAttribute(e, attr)
            if (s != null) {
                s = _macro_table.expandMacros(s)
            }
            s
        } catch (ex: MacroException) {
            throw AnimationParseException(ex.message)
        }
    }

    /**
     * Adds a macro to the parser.  XML attribute values are macro-expanded
     * before the parser uses them to translate the document into an Animation.
     * Macros allow Animation documents to be parameterised and paramaters
     * to be passed to the parser from user input or the command line.
     *
     *
     * Macro expansion is *textual*:  it does not take the syntactic
     * structure (such as expression syntax) of the expanded string into
     * account.  Be careful when expanding macros in strings with a syntactic
     * structure; you may, for example, need to enclose macros in brackes
     * inside expressions.
     *
     * @param name  The name of the macro.
     * @param value The value of the macro.
     */
    @Throws(AnimationParseException::class)
    fun addMacro(name: String, value: String) {
        try {
            _macro_table.addMacro(name!!, value)
        } catch (ex: MacroException) {
            throw AnimationParseException(ex.message)
        }
    }

    /**
     * Removes a macro from the parser.
     *
     * @param name The name of the macro to remove.
     */
    fun removeMacro(name: String?) {
        _macro_table.removeMacro(name)
    }

    interface ForallParser {
        @Throws(AnimationParseException::class)
        fun parse(child: Element)
    }

    companion object {
        private const val PROPERTY_ACTIVITY_NAME = "activityName"
        private const val PI_TARGET = "scenebeans"
        private const val PI_CODEBASE = "codebase"
        private const val PI_CATEGORY = "category"
        private const val PI_PACKAGE = "package"

        /*  Bean categories and packages
     */
        private const val CATEGORY_SCENE = "scene"
        private const val PKG_SCENE = "uk.ac.ic.doc.scenebeans"
        private const val CATEGORY_BEHAVIOUR = "behaviour"
        private const val PKG_BEHAVIOUR = "uk.ac.ic.doc.scenebeans.behaviour"
    }
}