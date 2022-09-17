/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package lepus.logger

/**
 *
 * @param fileName
 *   File Name
 * @param enclosureName
 *   A string that includes the package name and file name.
 * @param packageName
 *   Package Name
 * @param lineNumber
 *   The number of lines in the file containing classes, objects, etc.
 */
case class ExecLocation(
  fileName:      String,
  enclosureName: String,
  packageName:   String,
  lineNumber:    Int
)
