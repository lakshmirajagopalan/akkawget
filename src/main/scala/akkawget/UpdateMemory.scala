package akkawget

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import breeze.util.BloomFilter

object UpdateMemory {
  def apply(bf: BloomFilter[String])(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) = {
    Flow[String].map(url => bf.+=(url))
  }
}
