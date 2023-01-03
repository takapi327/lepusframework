/** This file is part of the Lepus Framework. For the full copyright and license information, please view the LICENSE
  * file that was distributed with this source code.
  */

package lepus.app.session

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import org.specs2.mutable.Specification

case class User(name: String)
object User:
  def default: User = User("Lepus")

object SessionStorageTest extends Specification:

  val user: User = User.default

  val emptyStorageIO: IO[SessionStorage[IO, User]] = SessionStorage.default[IO, User]()

  val storageId: SessionIdentifier = emptyStorageIO.flatMap(_.sessionId).unsafeRunSync()

  val nonEmptyStorageIO: IO[SessionStorage[IO, User]] =
    for
      storage <- SessionStorage.default[IO, User]()
      _       <- storage.modify(storageId, _ => (Some(user), ()))
    yield storage

  "Testing the SessionStorage" should {
    "By default, session storage is empty." in {
      val result = for
        storage <- emptyStorageIO
        id      <- storage.sessionId
        userOpt <- storage.get(id)
      yield userOpt.isEmpty

      result.unsafeRunSync()
    }

    "Session storage values can be retrieved using the same identifier used for storage storage as the key." in {
      val result = for
        storage <- nonEmptyStorageIO
        userOpt <- storage.get(storageId)
      yield userOpt.isDefined and userOpt.contains(user)

      result.unsafeRunSync()
    }

    "If the identifier at the time of storage is different from that at the time of retrieval, the value retrieved from the session is None." in {
      val result = for
        storage <- nonEmptyStorageIO
        id      <- storage.sessionId
        userOpt <- storage.get(id)
      yield userOpt.isEmpty

      result.unsafeRunSync()
    }

    "Using the same identifier as when storing, the session storage value can be updated." in {
      val result = for
        storage <- nonEmptyStorageIO
        _       <- storage.modify(storageId, userOpt => (userOpt.map(_.copy(name = "Updated")), ()))
        userOpt <- storage.get(storageId)
      yield userOpt.isDefined and !userOpt.contains(user) and userOpt.map(_.name).contains("Updated")

      result.unsafeRunSync()
    }

    "When data exists in session storage, storing data with a new identifier does not erase the data already stored." in {
      val result = for
        storage <- nonEmptyStorageIO
        id      <- storage.sessionId
        _       <- storage.modify(id, _ => (Some(User("new")), ()))
        old     <- storage.get(storageId)
        userOpt <- storage.get(id)
      yield old.isDefined and old.contains(user) and userOpt.isDefined and userOpt.map(_.name).contains("new")

      result.unsafeRunSync()
    }
  }
