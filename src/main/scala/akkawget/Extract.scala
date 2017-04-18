package akkawget

import java.net.URI

import akka.stream.scaladsl.{Flow, Source}
import org.jsoup.Jsoup

import scala.collection.JavaConversions._
import scala.collection.immutable.Seq

object Extract {
  def apply() = {
    Flow[(String, String)].flatMapMerge(0,{ case (url, content) => {
      val urls: Seq[String] = Jsoup.parse(content)
        .select("a")
        .map(element => new URI(url).resolve(Option(element.attr("href")).getOrElse("")).toString)
        .toList
      Source(urls)
    }})
  }
}
