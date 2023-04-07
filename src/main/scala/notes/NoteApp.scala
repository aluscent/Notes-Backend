package notes

import cats.effect.{ExitCode, IO, IOApp}
import doobie.util.transactor.Transactor
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.CORS

object NoteApp extends IOApp {
  //Define the data structures to store the notes
  implicit val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    "jdbc:postgresql:root",
    "root",
    "docker"
  )

  //Define the endpoints
  private val dsl = new Http4sDsl[IO] {}

  import DatabaseLayer._
  import dsl._

  private val noteService = HttpRoutes.of[IO] {
    case GET -> Root / "notes" =>
      for {
        list <- findAllNotes
        resp <- Ok(list.toString())
      } yield resp

    case GET -> Root / "notes" / title =>
      findNoteByTitle(title).flatMap {
        case Some(value) => Ok(value.toString)
        case None => NotFound()
      }

    case req@POST -> Root / "notes" =>
      import Decoders.NoteDecoders._
      for {
        note <- req.as[Note]
        status <- insertIntoNotes(note)
        resp <- if (status > 0) Ok(s"Inserted $status records.") else NotFound()
      } yield resp

    case DELETE -> Root / "notes" / title =>
      for {
        status <- removeNote(title)
        resp <- if (status > 0) Ok(s"Inserted $status records.") else NotFound()
      } yield resp

    case _ => NotFound()
  }

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(1234, "localhost")
      .withHttpApp(CORS(Router("/" -> noteService).orNotFound))
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
