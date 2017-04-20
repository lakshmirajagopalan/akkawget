package akkawget

import java.net.URI

import akka.stream.scaladsl.{Flow, Source}
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.immutable.Seq

object Extract {
  def apply() = {
    Flow[(String, String)].flatMapMerge(20, { case (url, content) =>
      try {
        val elements = Jsoup.parse(content).select("a")
        val urls: Seq[String] = elements
          .select("a")
          .map(element => new URI(url).resolve(Option(element.attr("href")).getOrElse("")).toString)
          .distinct.take(20).toList

        Source(urls)
      }catch {
        case ex => ex.printStackTrace(); Source.empty
      }
    })
  }
}

