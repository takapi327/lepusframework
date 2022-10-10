/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.database

trait DoobieQueryHelper:
  def table: String

  def select(params: String*): LepusQuery.Select = LepusQuery.select(table, params*)
  def insert(params: String*): LepusQuery.Insert = LepusQuery.insert(table, params*)
