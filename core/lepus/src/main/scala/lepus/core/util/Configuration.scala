/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.core.util

import java.time.{ Duration => JavaDuration }

import scala.jdk.CollectionConverters._
import scala.concurrent.duration.{ Duration, FiniteDuration, _ }

import com.typesafe.config._

case class Configuration(config: Config) {
  def get[A](path: String)(implicit loader: ConfigLoader[A]): A =
    loader.load(config, path)
}

object Configuration {

  def load(
    classLoader:    ClassLoader,
    directSettings: Map[String, String]
  ): Configuration = {
    try {
      val directConfig: Config = ConfigFactory.parseMap(directSettings.asJava)
      val config:       Config = ConfigFactory.load(classLoader, directConfig)
      Configuration(config)
    } catch {
      case e: ConfigException => throw new Exception
    }
  }

  def load(): Configuration = Configuration(ConfigFactory.load())
}

trait ConfigLoader[A] { self =>
  def load(config: Config, path: String): A
  def map[B](f: A => B): ConfigLoader[B] = new ConfigLoader[B] {
    def load(config: Config, path: String): B = {
      f(self.load(config, path))
    }
  }
}

object ConfigLoader {
  def apply[A](f: Config => String => A): ConfigLoader[A] =
    new ConfigLoader[A] {
      override def load(config: Config, path: String): A =
        f(config)(path)
    }

  implicit val string:         ConfigLoader[String]           = ConfigLoader(_.getString)
  implicit val int:            ConfigLoader[Int]              = ConfigLoader(_.getInt)
  implicit val long:           ConfigLoader[Long]             = ConfigLoader(_.getLong)
  implicit val number:         ConfigLoader[Number]           = ConfigLoader(_.getNumber)
  implicit val double:         ConfigLoader[Double]           = ConfigLoader(_.getDouble)
  implicit val bytes:          ConfigLoader[ConfigMemorySize] = ConfigLoader(_.getMemorySize)
  implicit val finiteDuration: ConfigLoader[FiniteDuration]   = ConfigLoader(_.getDuration).map(_.toNanos.nanos)
  implicit val javaDuration:   ConfigLoader[JavaDuration]     = ConfigLoader(_.getDuration)
  implicit val scalaDuration: ConfigLoader[Duration] = ConfigLoader(config =>
    path =>
      if (config.getIsNull(path)) {
        Duration.Inf
      } else {
        config.getDuration(path).toNanos.nanos
      }
  )

  implicit val seqBoolean: ConfigLoader[Seq[Boolean]] =
    ConfigLoader(_.getBooleanList).map(_.asScala.map(_.booleanValue).asInstanceOf[Seq[Boolean]])
  implicit val seqInt:    ConfigLoader[Seq[Int]]    = ConfigLoader(_.getIntList).map(_.asScala.map(_.toInt).asInstanceOf[Seq[Int]])
  implicit val seqLong:   ConfigLoader[Seq[Long]]   = ConfigLoader(_.getDoubleList).map(_.asScala.map(_.longValue).asInstanceOf[Seq[Long]])
  implicit val seqNumber: ConfigLoader[Seq[Number]] = ConfigLoader(_.getNumberList).map(_.asScala.asInstanceOf[Seq[Number]])
  implicit val seqDouble: ConfigLoader[Seq[Double]] = ConfigLoader(_.getDoubleList).map(_.asScala.map(_.doubleValue).asInstanceOf[Seq[Double]])
  implicit val seqString: ConfigLoader[Seq[String]] = ConfigLoader(_.getStringList).map(_.asScala.asInstanceOf[Seq[String]])
  implicit val seqBytes: ConfigLoader[Seq[ConfigMemorySize]] = ConfigLoader(_.getMemorySizeList).map(_.asScala.asInstanceOf[Seq[ConfigMemorySize]])
  implicit val seqFinite: ConfigLoader[Seq[FiniteDuration]] =
    ConfigLoader(_.getDurationList).map(_.asScala.map(_.toNanos.nanos).asInstanceOf[Seq[FiniteDuration]])
  implicit val seqJavaDuration: ConfigLoader[Seq[JavaDuration]] = ConfigLoader(_.getDurationList).map(_.asScala.asInstanceOf[Seq[JavaDuration]])
  implicit val seqScalaDuration: ConfigLoader[Seq[Duration]] =
    ConfigLoader(_.getDurationList).map(_.asScala.map(_.toNanos.nanos).asInstanceOf[Seq[Duration]])

  implicit val config:           ConfigLoader[Config]             = ConfigLoader(_.getConfig)
  implicit val configObject:     ConfigLoader[ConfigObject]       = ConfigLoader(_.getObject)
  implicit val configList:       ConfigLoader[ConfigList]         = ConfigLoader(_.getList)
  implicit val seqConfig:        ConfigLoader[Seq[Config]]        = ConfigLoader(_.getConfigList).map(_.asScala.asInstanceOf[Seq[Config]])
  implicit val configuration:    ConfigLoader[Configuration]      = config.map(Configuration(_))
  implicit val seqConfiguration: ConfigLoader[Seq[Configuration]] = seqConfig.map(_.map(Configuration(_)))

  implicit def optionA[A](implicit loader: ConfigLoader[A]): ConfigLoader[Option[A]] =
    new ConfigLoader[Option[A]] {
      override def load(config: Config, path: String): Option[A] =
        (config.hasPath(path) && !config.getIsNull(path)) match {
          case true  => Some(loader.load(config, path))
          case false => None
        }
    }
}
