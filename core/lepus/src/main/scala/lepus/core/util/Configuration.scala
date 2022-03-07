/**
 *  This file is part of the Lepus Framework.
 *  For the full copyright and license information,
 *  please view the LICENSE file that was distributed with this source code.
 */

package lepus.core.util

import scala.jdk.CollectionConverters._

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

trait ConfigLoader[A] {
  def load(config: Config, path: String): A
}

object ConfigLoader {
  def apply[A](f: Config => String => A): ConfigLoader[A] =
    new ConfigLoader[A] {
      override def load(config: Config, path: String): A =
        f(config)(path)
    }

  implicit val string: ConfigLoader[String] = ConfigLoader(_.getString)
  implicit val int:    ConfigLoader[Int]    = ConfigLoader(_.getInt)
  implicit val long:   ConfigLoader[Long]   = ConfigLoader(_.getLong)

  implicit def optionA[A](implicit loader: ConfigLoader[A]): ConfigLoader[Option[A]] =
    new ConfigLoader[Option[A]] {
      override def load(config: Config, path: String): Option[A] =
        (config.hasPath(path) && !config.getIsNull(path)) match {
          case true  => Some(loader.load(config, path))
          case false => None
        }
    }
}
