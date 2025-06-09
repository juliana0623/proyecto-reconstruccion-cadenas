import ArbolSufijos.*
import Oraculo.*

import scala.annotation.tailrec     // Importa el tipo Oraculo y la función crearOraculo

package object ReconstCadenas {

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Ingenuo (2.3.1 del enunciado).
   */
  def reconstruirCadenaIngenuo(n: Int, o: Oraculo): Seq[Char] = {
    def generarCadenas(longitud: Int): LazyList[List[Char]] = {
      if (longitud == 0) LazyList(List.empty)
      else for {
        cadena <- generarCadenas(longitud - 1)
        letra <- alfabeto
      } yield letra :: cadena
    }
    generarCadenas(n).find(o).getOrElse(Seq.empty)
  }


  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Mejorado (2.3.2 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 (donde s = s1.s2) también son subsecuencias de S.
   */
  def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char] = {
    val alfabetoLazy = alfabeto.to(LazyList)
    val sc0 = LazyList(Vector.empty[Char])

    val scK = (1 to n).foldLeft(sc0) { (scAnterior, _) =>
      for {
        w <- scAnterior
        letra <- alfabetoLazy
        nuevo = w :+ letra
        if o(nuevo)
      } yield nuevo
    }

    scK.find(_.length == n).getOrElse(Seq.empty)
  }

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo (2.3.3 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   */
  def reconstruirCadenaTurbo(n: Int, o: Oraculo): Seq[Char] = {
    def generarCandidatos(SC: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      for {
        s1 <- SC
        s2 <- SC
      } yield s1 ++ s2
    }

    @tailrec
    def reconstruir(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      val candidatas = generarCandidatos(SC).filter(o)
      val S = candidatas.head

      if (S.length == n) S
      else reconstruir(2 * k, candidatas)
    }

    val SC1 = alfabeto.map(c => Seq(c)).filter(o)
    reconstruir(2, SC1)
  }


  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo Mejorada (2.3.4 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa el filtro para ir más rápido.
   */
  def reconstruirCadenaTurboMejorada(n: Int, o: Oraculo): Seq[Char] = {
    def filtrar(SC: Seq[Seq[Char]], k: Int): Seq[Seq[Char]] = {
      val conjunto = SC.toSet
      for {
        s1 <- SC
        s2 <- SC
        s = s1 ++ s2
        if s.sliding(k).toSet.forall(conjunto.contains)
      } yield s
    }

    @tailrec
    def reconstruir(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      val candidatas = filtrar(SC, k / 2).filter(o)
      val S = candidatas.head

      if (S.length == n) S
      else {
        reconstruir(2 * k, candidatas)
      }
    }

    val SC1 = alfabeto.map(c => Seq(c)).filter(o)
    reconstruir(2, SC1)
  }


  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo Acelerada (2.3.5 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa árboles de sufijos para guardar Seq[Seq[Char]] (SCk) y el filtro modificado.
   */
  def reconstruirCadenaTurboAcelerada(n: Int, o: Oraculo): Seq[Char] = {
    def filtrar(SC: Seq[Seq[Char]], k: Int): Seq[Seq[Char]] = {
      val trie = arbolDeSufijos(SC)
      val combinaciones = for {
        s1 <- SC
        s2 <- SC
      } yield s1 ++ s2

      combinaciones.filter { s =>
        s.sliding(k).toSet.forall(sub => pertenece(sub, trie))
      }
    }

    @tailrec
    def reconstruir(k: Int, SCi: Seq[Seq[Char]]): Seq[Char] = {
      val candidatas = filtrar(SCi, k / 2).filter(o)
      val S = candidatas.head

      if (S.length == n) S
      else reconstruir(2 * k, candidatas)
    }
    val SC1 = alfabeto.map(c => Seq(c)).filter(o)
    reconstruir(2, SC1)
  }

}