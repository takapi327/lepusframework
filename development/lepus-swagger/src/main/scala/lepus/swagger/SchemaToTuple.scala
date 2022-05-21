/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.swagger

import scala.collection.mutable.ListBuffer

import lepus.router.model.{ Schema, SchemaType }

class SchemaToTuple {
  def apply(schema: Schema[_]): List[(Schema.Name, Schema[_])] = {
    val thisSchema = schema.name match {
      case Some(name) => List(name -> schema)
      case None       => Nil
    }
    val propertySchemas = schema match {
      case Schema(SchemaType.SArray(s), _, _, _)    => apply(s)
      case Schema(s: SchemaType.Entity[_], _, _, _) => s.fields.flatMap(v => apply(v.schema))
      case _                                         => List.empty
    }
    thisSchema ++ propertySchemas
  }
}

object SchemaToTuple {

  def unique(tuples: Iterable[(Schema.Name, Schema[_])]): Iterable[(Schema.Name, Schema[_])] = {
    val uniques: collection.mutable.Set[Schema.Name]   = collection.mutable.Set()
    val result:  ListBuffer[(Schema.Name, Schema[_])] = ListBuffer()
    tuples.foreach(tuple => {
      if (!uniques.contains(tuple._1)) {
        uniques.add(tuple._1)
        result += tuple
      }
    })
    result.toList
  }
}
