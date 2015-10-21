package monocle.function

import monocle.function.fields._
import monocle.std.tuple2._
import monocle.{Iso, Optional, Prism}

import scala.annotation.implicitNotFound

/**
 * Typeclass that defines a [[Prism]] between an `S` and its head `A` and tail `S`
 * @tparam S source of [[Prism]] and tail of [[Prism]] target
 * @tparam A head of [[Prism]] target, `A` is supposed to be unique for a given `S`
 */
@implicitNotFound("Could not find an instance of Cons[${S},${A}], please check Monocle instance location policy to " +
  "find out which import is necessary")
trait Cons[S, A] extends Serializable {
  def cons: Prism[S, (A, S)]

  def headOption: Optional[S, A] = cons composeLens first
  def tailOption: Optional[S, S] = cons composeLens second
}

object Cons extends ConsFunctions {
  /** lift an instance of [[Cons]] using an [[Iso]] */
  def fromIso[S, A, B](iso: Iso[S, A])(implicit ev: Cons[A, B]): Cons[S, B] = new Cons[S, B] {
    override def cons: Prism[S, (B, S)] =
      iso composePrism ev.cons composeIso iso.reverse.second
  }
}


trait ConsFunctions {
  final def cons[S, A](implicit ev: Cons[S, A]): Prism[S, (A, S)] = ev.cons

  final def headOption[S, A](implicit ev: Cons[S, A]): Optional[S, A] = ev.headOption
  final def tailOption[S, A](implicit ev: Cons[S, A]): Optional[S, S] = ev.tailOption

  @deprecated("use headOption", since = "1.1.0")
  final def headMaybe[S, A](implicit ev: Cons[S, A]): Optional[S, A] = ev.headOption
  @deprecated("use tailOption", since = "1.1.0")
  final def tailMaybe[S, A](implicit ev: Cons[S, A]): Optional[S, S] = ev.tailOption

  /** append an element to the head */
  final def _cons[S, A](head: A, tail: S)(implicit ev: Cons[S, A]): S =
    ev.cons.reverseGet((head, tail))

  /** deconstruct an S between its head and tail */
  final def _uncons[S, A](s: S)(implicit ev: Cons[S, A]): Option[(A, S)] =
    ev.cons.getOption(s)
}