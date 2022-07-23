/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.core.util

import java.time.{ Duration => JavaDuration }

import scala.jdk.CollectionConverters.*
import scala.concurrent.duration.{ Duration, FiniteDuration, * }

import com.typesafe.config.*

case class Configuration(config: Config):
  def get[A](path: String)(using loader: ConfigLoader[A]): A =
    loader.load(config, path)

object Configuration:

  def load(
    classLoader:    ClassLoader,
    directSettings: Map[String, String]
  ): Configuration =
    try
      val directConfig: Config = ConfigFactory.parseMap(directSettings.asJava)
      val config:       Config = ConfigFactory.load(classLoader, directConfig)
      Configuration(config)
    catch
      case e: ConfigException => throw new Exception(e.getMessage)

  def load(): Configuration = Configuration(ConfigFactory.load())

end Configuration

trait ConfigLoader[A]:
  self =>

  def load(config: Config, path: String): A
  def map[B](f: A => B): ConfigLoader[B] = new ConfigLoader {
    def load(config: Config, path: String): B =
      f(self.load(config, path))
  }

end ConfigLoader

object ConfigLoader:
  def apply[A](f: Config => String => A): ConfigLoader[A] =
    new ConfigLoader[A] {
      override def load(config: Config, path: String): A =
        f(config)(path)
    }

  given ConfigLoader[String]           = ConfigLoader(_.getString)
  given ConfigLoader[Int]              = ConfigLoader(_.getInt)
  given ConfigLoader[Long]             = ConfigLoader(_.getLong)
  given ConfigLoader[Number]           = ConfigLoader(_.getNumber)
  given ConfigLoader[Double]           = ConfigLoader(_.getDouble)
  given ConfigLoader[Boolean]          = ConfigLoader(_.getBoolean)
  given ConfigLoader[ConfigMemorySize] = ConfigLoader(_.getMemorySize)
  given ConfigLoader[FiniteDuration]   = ConfigLoader(_.getDuration).map(_.toNanos.nanos)
  given ConfigLoader[JavaDuration]     = ConfigLoader(_.getDuration)
  given ConfigLoader[Duration]         = ConfigLoader(config => path =>
    if config.getIsNull(path) then
      Duration.Inf
    else
      config.getDuration(path).toNanos.nanos
  )

  given seqBoolean: ConfigLoader[Seq[Boolean]] =
    ConfigLoader(_.getBooleanList).map(_.asScala.map(_.booleanValue).asInstanceOf[Seq[Boolean]])
  given seqInt: ConfigLoader[Seq[Int]] =
    ConfigLoader(_.getIntList).map(_.asScala.map(_.toInt).asInstanceOf[Seq[Int]])
  given seqLong: ConfigLoader[Seq[Long]] =
    ConfigLoader(_.getDoubleList).map(_.asScala.map(_.longValue).asInstanceOf[Seq[Long]])
  given seqNumber: ConfigLoader[Seq[Number]] =
    ConfigLoader(_.getNumberList).map(_.asScala.asInstanceOf[Seq[Number]])
  given seqDouble: ConfigLoader[Seq[Double]] =
    ConfigLoader(_.getDoubleList).map(_.asScala.map(_.doubleValue).asInstanceOf[Seq[Double]])
  given seqString: ConfigLoader[Seq[String]] =
    ConfigLoader(_.getStringList).map(_.asScala.asInstanceOf[Seq[String]])
  given seqBytes: ConfigLoader[Seq[ConfigMemorySize]] =
    ConfigLoader(_.getMemorySizeList).map(_.asScala.asInstanceOf[Seq[ConfigMemorySize]])
  given seqFinite: ConfigLoader[Seq[FiniteDuration]] =
    ConfigLoader(_.getDurationList).map(_.asScala.map(_.toNanos.nanos).asInstanceOf[Seq[FiniteDuration]])
  given seqJavaDuration: ConfigLoader[Seq[JavaDuration]] =
    ConfigLoader(_.getDurationList).map(_.asScala.asInstanceOf[Seq[JavaDuration]])
  given seqScalaDuration: ConfigLoader[Seq[Duration]] =
    ConfigLoader(_.getDurationList).map(_.asScala.map(_.toNanos.nanos).asInstanceOf[Seq[Duration]])
  given seqConfig: ConfigLoader[Seq[Config]] =
    ConfigLoader(_.getConfigList).map(_.asScala.asInstanceOf[Seq[Config]])
  given seqConfiguration: ConfigLoader[Seq[Configuration]] =
    summon[ConfigLoader[Seq[Config]]].map(_.map(Configuration(_)))

  given ConfigLoader[Config]        = ConfigLoader(_.getConfig)
  given ConfigLoader[ConfigObject]  = ConfigLoader(_.getObject)
  given ConfigLoader[ConfigList]    = ConfigLoader(_.getList)
  given ConfigLoader[Configuration] = summon[ConfigLoader[Config]].map(Configuration(_))

  given [A](using loader: ConfigLoader[A]): ConfigLoader[Option[A]] with
    override def load(config: Config, path: String): Option[A] =
      if config.hasPath(path) && !config.getIsNull(path) then
        Some(loader.load(config, path))
      else
        None

  given [A](using loader: ConfigLoader[A]): ConfigLoader[Map[String, A]] with
    override def load(config: Config, path: String): Map[String, A] =
      val obj  = config.getObject(path)
      val conf = obj.toConfig
      obj.keySet().asScala.map { key =>
        key -> loader.load(conf, key)
      }.toMap

end ConfigLoader
