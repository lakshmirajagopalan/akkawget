package akkawget

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import scala.concurrent.ExecutionContext.Implicits.global

object Fetch {
  val MAX_SIZE = 10485760
  def apply()(implicit actorSystem : ActorSystem, materializer: ActorMaterializer): Flow[String, (String, String), Unit] = {
    val http = Http(actorSystem)
    Flow[String].mapAsync(1)(url => {
      println("Fetching " + url)
      http.singleRequest(HttpRequest(uri = url)).map(url ->)
    }).mapAsync(1){
      case (url, response) => response.entity.withSizeLimit(MAX_SIZE).dataBytes
                  .runFold("")((sofar, buffer) => sofar + new String(buffer.toArray)).map(url ->)
    }
  }
}
