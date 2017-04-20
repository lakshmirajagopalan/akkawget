package akkawget

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import breeze.util.BloomFilter
import org.apache.commons.validator.UrlValidator

object FilterUrls {
  val urlValidator = new UrlValidator()
  val invalidComponents = List("blog", "faq", "corporate", "terms-of-use", "account", "login", "story", "business")

  def isValidUrl(url: String) = urlValidator.isValid(url) && !invalidComponents.exists(url.contains)
  def seenBefore(bf: BloomFilter[String])(url: String) = bf.contains(url)

  def apply(bf: BloomFilter[String])(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) = {
    Flow[String].filter(isValidUrl).filterNot(seenBefore(bf))
  }
}
