/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import lepus.core.util.Configuration

/** Object to retrieve the module you want to enable in your application from a config string
  */
object ModuleLoader:

  private val config: Configuration = Configuration.load()

  private val ENABLED_MODULES:  String = "lepus.modules.enable"
  private val DISABLED_MODULES: String = "lepus.modules.disable"

  private[lepus] val enables:  Seq[String] = config.get[Option[Seq[String]]](ENABLED_MODULES).getOrElse(Seq.empty)
  private[lepus] val disables: Seq[String] = config.get[Option[Seq[String]]](DISABLED_MODULES).getOrElse(Seq.empty)

  private[lepus] val moduleClassNames: Set[String] = enables.toSet -- disables

  /** Display a list of enabled modules in the console */
  println()
  println("      List of enabled modules")
  println()
  println("-------------------------------------------------")
  println(moduleClassNames.mkString("\n"))
  println("-------------------------------------------------")
  println()

  def load(): Seq[Any] =
    moduleClassNames.map(className => constructModule[Any](className)).toSeq

  private def constructModule[T](className: String, args: AnyRef*): T =
    val argTypes    = args.map(_.getClass)
    val cls         = ClassLoader.getSystemClassLoader.loadClass(className)
    val constructor = cls.getConstructor(argTypes: _*)
    constructor.setAccessible(true)
    constructor.newInstance(args: _*).asInstanceOf[T]
