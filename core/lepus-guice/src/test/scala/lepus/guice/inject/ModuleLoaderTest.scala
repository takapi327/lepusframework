/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.guice.inject

import org.specs2.mutable.Specification

object ModuleLoaderTest extends Specification:

  class LepusModuleTest

  "Testing the ModuleLoader" should {

    "Successfully read class name of enabled module from config" in {
      ModuleLoader.enableds must beAnInstanceOf[Seq[String]]
    }

    "Class name of enabled module retrieved from config matches the specified number" in {
      ModuleLoader.enableds.length === 5
    }

    "The class name of the enabled module retrieved from config matches the specified one" in {
      ModuleLoader.enableds containsSlice Seq("Module1", "Module2", "Module3", "Module4", "Module5")
    }

    "Successfully read class name of disabled module from config" in {
      ModuleLoader.disableds must beAnInstanceOf[Seq[String]]
    }

    "Class name of disabled module retrieved from config matches the specified number" in {
      ModuleLoader.disableds.length === 2
    }

    "The class name of the disabled module retrieved from config matches the specified one" in {
      ModuleLoader.disableds containsSlice Seq("Module1", "Module2")
    }

    "The number of moduleClassNames is the number of enableds minus disableds" in {
      ModuleLoader.moduleClassNames.toSeq.length === 3
    }

    "The value of moduleClassNames is enableds minus disableds" in {
      ModuleLoader.moduleClassNames.toSeq containsSlice Seq("Module3", "Module4", "Module5")
    }
  }
