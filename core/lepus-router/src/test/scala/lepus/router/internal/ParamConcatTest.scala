/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.router.internal

import org.scalatest.flatspec.AnyFlatSpec

class ParamConcatTest extends AnyFlatSpec:
  "ParamConcat" should "compile" in {
    assertCompiles("""
      summon[ParamConcat.Aux[Tuple1[String], (Long, Double), (String, Long, Double)]]
      summon[ParamConcat.Aux[(Long, Double), Tuple1[String], (Long, Double, String)]]
      summon[ParamConcat.Aux[Tuple1[String], Tuple1[Int], (String, Int)]]

      // tuple > 1
      summon[ParamConcat.Aux[(String, Int), (Long, Double), (String, Int, Long, Double)]]

      // single & tuple
      summon[ParamConcat.Aux[String, (Long, Double), (String, Long, Double)]]
      summon[ParamConcat.Aux[(Long, Double), String, (Long, Double, String)]]

      // single & single
      summon[ParamConcat.Aux[String, Long, (String, Long)]]

      // unit & unit
      summon[ParamConcat.Aux[Unit, Unit, Unit]]

      // unit & tuple
      summon[ParamConcat.Aux[Unit, (Long, Double), (Long, Double)]]
      summon[ParamConcat.Aux[(Long, Double), Unit, (Long, Double)]]

      // unit & single
      summon[ParamConcat.Aux[Unit, Int, Int]]
      summon[ParamConcat.Aux[Int, Unit, Int]]

      // without aux
      summon[ParamConcat[Tuple1[String], (Long, Double)]]
      summon[ParamConcat[(String, Int), (Long, Double)]]
      summon[ParamConcat[String, (Long, Double)]]
      summon[ParamConcat[Unit, (Long, Double)]]
      summon[ParamConcat[Unit, Int]]
    """)
  }