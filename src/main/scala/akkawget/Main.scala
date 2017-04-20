package akkawget

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._

import breeze.util.BloomFilter

object Main {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem = ActorSystem("akkawget")
    implicit val materializer = ActorMaterializer()
    var bloomFilter = new BloomFilter[String](100, 30)
    RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[Unit] =>
      import GraphDSL.Implicits._
      val startUrl = Source(List("https://www.walmart.com", "https://www.amazon.com"))
      val allUrls = builder.add(Merge[String](2))
      val validUrls = builder.add(Broadcast[String](2))
      val allHtmls = builder.add(Broadcast[(String, String)](2))

      startUrl ~> allUrls ~> Fetch() ~> allHtmls ~> Extract() ~> FilterUrls(bloomFilter) ~> validUrls  ~> allUrls
      validUrls ~> UpdateMemory(bloomFilter).map(bf => bloomFilter = bf) ~> Sink.ignore
      allHtmls ~> FilterProductPages() ~> Sink.foreach(println)
      ClosedShape
    }).run()
  }
}
