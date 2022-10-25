/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.immutable.ListMap

import lepus.core.generic.Schema

import lepus.swagger.model.Reference

class SchemaToReference(nameMapList: Option[ListMap[Schema.Name, Schema[?]]]):
  def map(name: Schema.Name): Option[Reference] =
    nameMapList.flatMap(_.get(name)) match
      case Some(_) => Some(Reference(s"#/components/schemas/${ name.shortName }"))
      case None    => None
