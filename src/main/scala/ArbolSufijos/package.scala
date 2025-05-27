import scala.annotation.tailrec

package object ArbolSufijos {

  abstract class Trie
  case class Nodo(car: Char, marcada: Boolean, hijos: List[Trie]) extends Trie
  case class Hoja(car: Char, marcada: Boolean) extends Trie

  def raiz(t: Trie): Char = {
    t match {
      case Nodo(c, _, _) => c
      case Hoja(c, _)    => c
    }
  }

  def cabezas(t: Trie): Seq[Char] = {
    t match {
      case Nodo(_, _, hijos) => hijos.map(t=>raiz(t))
      case Hoja(c, _)        => Seq[Char](c)
    }
  }

  @tailrec
  def pertenece(s: Seq[Char], t: Trie): Boolean = {
    (s, t) match {
      case (Nil, Nodo(_, marcada, _)) => marcada
      case (Nil, Hoja(_, marcada))    => marcada
      case (c +: cs, Nodo(_, _, hijos)) =>
        val hijosEncontrados = hijos.filter(hijo => raiz(hijo) == c)
        if (hijosEncontrados.isEmpty) {
          false
        } else {
          pertenece(cs, hijosEncontrados.head)
        }
      case (c +: cs, Hoja(car_hoja, _)) =>
        c == car_hoja && cs.isEmpty
      case _ => false
    }
  }

  def adicionar(s: Seq[Char], t: Trie): Trie = {
    def construirNuevaRama(s_actual_rama: Seq[Char], char_actual_rama: Char): Trie = {
      s_actual_rama match {
        case Nil => Hoja(char_actual_rama, true)
        case d +: ds => Nodo(char_actual_rama, false, List(construirNuevaRama(ds, d)))
      }
    }

    (s, t) match {
      case (Nil, Nodo(car_nodo, _, hijos_nodo)) => Nodo(car_nodo, true, hijos_nodo)
      case (Nil, Hoja(car_hoja, _))             => Hoja(car_hoja, true)

      case (c +: cs, Nodo(car_nodo, marcada_nodo, hijos_nodo)) =>
        val (hijosCoincidentes, otrosHijos) = hijos_nodo.partition(hijo => raiz(hijo) == c)
        val ramaProcesada = if (cabezas(t).contains(c)) {
          adicionar(cs, hijosCoincidentes.head)
        } else {
          construirNuevaRama(cs, c)
        }
        Nodo(car_nodo, marcada_nodo, ramaProcesada :: otrosHijos)
      case (c +: cs, Hoja(car_hoja, marcada_hoja)) =>
        Nodo(car_hoja, marcada_hoja, List(construirNuevaRama(cs, c)))
    }
  }

  def adicionarr(s: Seq[Char], t: Trie): Trie = {

    def construirRama(s: Seq[Char]): Trie = {
      s match {
        case s1 +: Seq() => Hoja(s1, true)
        case s1 +: ss => Nodo(s1, false, List(construirRama(ss)))
      }
    }

    if (pertenece(s, t)) {
      t
    } else {

      (s, t) match {

        case (s1 +: ss, Hoja(c, m)) => Nodo(c, m, List(construirRama(s1 +: ss)))

        case (s1 +: ss, Nodo(c, m, hijos)) =>
          if (cabezas(t).contains(s1)) {
            val nuevosHijos = hijos.map(hijo =>
              if (raiz(hijo) == s1) {
                adicionarr(ss, hijo)
              }
              else hijo)
            Nodo(c, m, nuevosHijos)
          } else {
            Nodo(c, m, hijos ++ List(construirRama(s1 +: ss)))
          }

        case (Seq(), Nodo(c, m, hijos)) => Nodo(c, true, hijos)

        case (Seq(), t) => t

      }
    }
  }

  def arbolDeSufijos(ss: Seq[Seq[Char]]): Trie = {
    @tailrec
    def insertarSufijos(seqsRestantes: Seq[Seq[Char]], trieAcumulado: Trie): Trie = {
      seqsRestantes match {
        case Nil => trieAcumulado
        case seqActual +: colaSeqs =>
          val sufijos = seqActual.tails.filter(_.nonEmpty)
          val trieConSufijosActuales = sufijos.foldLeft(trieAcumulado)((accTrie, suf) => adicionar(suf, accTrie))
          insertarSufijos(colaSeqs, trieConSufijosActuales)
      }
    }
    insertarSufijos(ss, Nodo('_', false, Nil))
  }
}