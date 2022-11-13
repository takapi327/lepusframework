/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus

package object database:
  type DatabaseCF[T] = DataSource ?=> T
