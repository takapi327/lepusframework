/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import lepus.core.util.Configuration

/** Object to retrieve the module you want to enable in your application from a config string
  */
object ModuleLoader:

  private val config: Configuration = Configuration.load()

  private val ENABLED_MODULES:  String = "lepus.modules.enabled"
  private val DISABLED_MODULES: String = "lepus.modules.disabled"

  private[lepus] val enableds:  Seq[String] = config.get[Option[Seq[String]]](ENABLED_MODULES).getOrElse(Seq.empty)
  private[lepus] val disableds: Seq[String] = config.get[Option[Seq[String]]](DISABLED_MODULES).getOrElse(Seq.empty)

  private[lepus] val moduleClassNames: Set[String] = enableds.toSet -- disableds

  def load(): Seq[Any] =
    moduleClassNames.map(className => constructModule(className)).toSeq

  private def constructModule[T](className: String, args: AnyRef*): T =
    val argTypes    = args.map(_.getClass)
    val cls         = ClassLoader.getSystemClassLoader.loadClass(className)
    val constructor = cls.getConstructor(argTypes: _*)
    constructor.setAccessible(true)
    constructor.newInstance(args: _*).asInstanceOf[T]
