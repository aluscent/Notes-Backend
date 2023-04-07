package notes.Decoders

import cats.effect.IO
import notes.Note
import io.circe.Decoder
import org.http4s.EntityDecoder
import org.http4s.circe.jsonOf

object NoteDecoders {
  implicit val noteDecoder: Decoder[Note] = Decoder.forProduct2("title", "body")(Note)
  implicit val noteEntityDecoder: EntityDecoder[IO, Note] = jsonOf[IO, Note]
}
