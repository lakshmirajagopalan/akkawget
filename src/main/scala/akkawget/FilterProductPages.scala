package akkawget

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

object FilterProductPages {
  val productAttributes = List("add to cart", "product details", "specifications", "cash on delivery", "free delivery", "in stock", "out of stock", "customer reviews")
  def apply()(implicit actorSystem : ActorSystem, materializer: ActorMaterializer) = {
    Flow[(String, String)].filterNot({case (url, html) => productAttributes.exists(html.contains)}).map(_._1)}
}
