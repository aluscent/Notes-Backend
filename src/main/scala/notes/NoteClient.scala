package notes

import notes.Note
import cats.effect.unsafe.implicits.global
import cats.effect.{ExitCode, IO, IOApp}
import io.circe.syntax.EncoderOps
import org.http4s.{Method, Request}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import io.circe.generic.auto._
import org.http4s.circe._


object Methods {
  def addNote(client: Client[IO], note: Note): IO[String] =
    client.expect[String](
      req = Request[IO](uri = uri"http://localhost:8080/notes", method = Method.POST)
        .withEntity(note.asJson)
    )

//  def getNote(client: Client[IO], title: String): IO[String] =
//    client.expect[String](req = Request[IO](uri = uri"http://localhost:8080/notes/$title", method = Method.GET))

  def getNotes(client: Client[IO]): IO[String] =
    client.expect[String](req = Request[IO](uri = uri"http://localhost:8080/notes", method = Method.GET))

  def removeNote(client: Client[IO]): IO[String] =
    client.expect[String](req = Request[IO](uri = uri"http://localhost:8080/notes", method = Method.GET))
}


object AddNote extends IOApp {
  import Methods.addNote

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO]
      .resource.use(addNote(_, Note("test 1", "this is supposed to be a test")).flatMap(IO.println))
      .as(ExitCode.Success)
}

object GetNotes extends IOApp {
  import Methods.getNotes

  override def run(args: List[String]): IO[ExitCode] =
    BlazeClientBuilder[IO]
      .resource.use(getNotes(_).flatMap(IO.println))
      .as(ExitCode.Success)
}

//object GetNote extends IOApp {
//  import Methods.getNote
//
//  override def run(args: List[String]): IO[ExitCode] =
//    BlazeClientBuilder[IO]
//      .resource.use(getNote(_, "sample title").flatMap(IO.println))
//      .as(ExitCode.Success)
//}