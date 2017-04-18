package akkawget

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._


object Main {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("akkawget")
    implicit val materializer = ActorMaterializer()

    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._
      val startUrl = Source(List("https://www.walmart.com/"))
      val allUrls = builder.add(Merge[String](2))
//      val allHtmls = builder.add(Broadcast[String](2))

      startUrl ~> allUrls ~> Fetch() ~> Extract() ~> allUrls
      ClosedShape
    }).run()
  }
}
