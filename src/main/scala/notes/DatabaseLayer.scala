package notes

import cats.effect.IO
import doobie.implicits._
import doobie.util.transactor.Transactor
import doobie.util.update.Update
import doobie.{HC, HPS}

object DatabaseLayer {
  def findAllNotes(implicit xa: Transactor[IO]): IO[List[Note]] = {
    val query = sql"select title, body from notes".query[(String, String)].map { case (t, b) => Note(t, b) }
    query.to[List].transact(xa)
  }

  def findNoteByTitle(title: String)(implicit xa: Transactor[IO]): IO[Option[Note]] = {
    val query = sql"select title, body from notes where title like '%' || $title || '%'"
      .query[(String, String)].map { case (t, b) => Note(t, b) }
    query.option.transact(xa)
  }

  def findNotesByTitleLowLevel(title: String)(implicit xa: Transactor[IO]): IO[Option[Note]] = {
    val queryString = "select title, body from notes where title like '%' || ? || '%'"
    HC.stream[(String, String)](
      queryString,
      HPS.set(title),
      10)
      .map { case (t, b) => Note(t, b) }
      .compile
      .toList
      .map(_.headOption)
      .transact(xa)
  }

  def findAllNotesAsStream(implicit xa: Transactor[IO]): IO[List[Note]] =
    sql"select title, body from notes"
      .query[(String, String)]
      .map { case (t, b) => Note(t, b) }
      .stream
      .compile
      .toList
      .transact(xa)

  def insertIntoNotes(note: Note)(implicit xa: Transactor[IO]): IO[Int] = {
    val queryString = sql"insert into notes (title, body) values (${note.title}, ${note.body})".update
    queryString.run.transact(xa)
  }

  def insertIntoNotesLowLevel(note: Note)(implicit xa: Transactor[IO]): IO[Int] = {
    val queryString = "insert into notes (title, body) values (?, ?)"
    Update[Note](queryString).run(note).transact(xa)
  }

  def multipleInsertsIntoNotes(noteList: List[Note])(implicit xa: Transactor[IO]): IO[Int] = {
    val queryString = "insert into notes (title, body) values (?, ?)"
    Update[Note](queryString)
      .updateMany(noteList)
      .transact(xa)
  }

  def removeNote(title: String)(implicit xa: Transactor[IO]): IO[Int] = {
    val queryString = "delete from notes where title = ?"
    Update[String](queryString).run(title).transact(xa)
  }
}
