package myStream

import akka.stream.scaladsl.Source

import scala.concurrent.Future

trait MyStream[+T] {
  def head: T
  def tail: MyStream[T]
  def map[U](fn: T => U): MyStream[U]
  def filter[U](fn: T => Boolean): MyStream[T]
  def foldLeft[U](zero: U)(fn: (U, T) => U): Future[U]
}
class Cons[T](val head: T, rest: => MyStream[T]) extends MyStream[T] {
  override def tail: MyStream[T] = rest
  override def map[U](fn: (T) => U): MyStream[U] = new Cons(fn(head), tail.map(fn))
  override def filter[U](fn: (T) => Boolean): MyStream[T] = if(fn(head)) new Cons(head, tail.filter(fn)) else tail.filter(fn)
  override def foldLeft[U](zero: U)(fn: (U, T) => U): Future[U] = rest.foldLeft(fn(zero, head))(fn)
}

case object Empty extends MyStream[Nothing] {
  override def head: Nothing = throw new Exception()
  override def tail: MyStream[Nothing] = throw new Exception()
  override def map[U](fn: (Nothing) => U): MyStream[U] = Empty
  override def filter[U](fn: (Nothing) => Boolean): MyStream[Nothing] = Empty
  override def foldLeft[U](zero: U)(fn: (U, Nothing) => U): Future[U] = Future.successful(zero)
}

object Main extends App{
  val myStream: MyStream[Int] = new Cons(1, new Cons(2, new Cons(3, new Cons(4, Empty))))
  println(myStream.head)
  println(myStream.tail)
  println(myStream.map(_ + 1).tail.head)
  println(myStream.map(_ + 1).tail.tail)
  println(myStream.filter(_ % 2 == 0))
  println(myStream.filter(_ > 5))
  println(myStream.foldLeft(0)(_ + _))


  val stream: Stream[Int] = 1 #:: 2 #:: 3 #:: Stream.empty
  println(stream)
  println(stream.tail)
  println(stream.map(_+1))
  println(stream.filter(_ > 5))
  println(stream.foldLeft(0)(_ + _))

}
