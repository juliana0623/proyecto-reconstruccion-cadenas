import Oraculo.*
import ArbolSufijos.*
import common.*

import scala.annotation.tailrec

import scala.collection.parallel.CollectionConverters.* // Para usar .par en colecciones estándar

package object ReconstCadenasPar {

  //Ahora vienen las versiones paralelas

  /**
   * Versión paralela de reconstruirCadenaIngenuo.
   * Recibe un umbral, la longitud n y el oráculo.
   * Usa paralelismo de tareas.
   */
  def reconstruirCadenaIngenuoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Implementación de la función reconstruirCadenaIngenuoPar
    ??? // Implementar lógica aquí
  }

  /**
   * Versión paralela de reconstruirCadenaMejorado.
   * Recibe un umbral, la longitud n y el oráculo.
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa paralelismo de tareas y/o datos.
   */
  def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    // Implementación de la función reconstruirCadenaMejoradoPar
    ??? // Implementar lógica aquí
  }

  /**
   * Versión paralela de reconstruirCadenaTurbo.
   * Recibe un umbral, la longitud n (potencia de 2) y el oráculo.
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa paralelismo de tareas y/o datos.
   */
  def reconstruirCadenaTurboPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    def generarCandidatos(SC: Seq[Seq[Char]]): Seq[Seq[Char]] = {
      if (SC.length > umbral) {
        (for {
          s1 <- SC.par
          s2 <- SC
        } yield s1 ++ s2).seq
      } else {
        for {
          s1 <- SC
          s2 <- SC
        }
        yield s1 ++ s2
      }
    }

    @tailrec
    def reconstruir(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {

      val combinacionesGeneradas = generarCandidatos(SC)

      val candidatas = if (combinacionesGeneradas.length > umbral) {
        combinacionesGeneradas.par.filter(o).seq
      } else {
        combinacionesGeneradas.filter(o)
      }

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
   * Versión paralela de reconstruirCadenaTurboMejorada.
   * Recibe un umbral, la longitud n (potencia de 2) y el oráculo.
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa paralelismo de datos (.par) y tareas (common) de manera optimizada.
   */
  def reconstruirCadenaTurboMejoradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    def filtrarPar(SC: Seq[Seq[Char]], k: Int): Seq[Seq[Char]] = {
      val conjunto = SC.toSet
      if (SC.size > umbral) {
        (for {
          s1 <- SC.par
          s2 <- SC
          s = s1 ++ s2
          if s.sliding(k).forall(conjunto.contains)
        } yield s).seq
      } else {
        for {
          s1 <- SC
          s2 <- SC
          s = s1 ++ s2
          if s.sliding(k).forall(conjunto.contains)
        } yield s
      }
    }

    @tailrec
    def reconstruir(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      val candidatos = filtrarPar(SC, k / 2)

      val candidatosValidos =
        if (candidatos.size > umbral) candidatos.par.filter(o).seq
        else candidatos.filter(o)

      val S = candidatosValidos.head
      if (S.length == n) S
      else {
        reconstruir(2 * k, candidatosValidos)
      }
    }

    val SC1 = alfabeto.map(c => Seq(c)).filter(o)
    reconstruir(2, SC1)
  }

  /**
   * Versión paralela de reconstruirCadenaTurboAcelerada.
   * Recibe un umbral, la longitud n (potencia de 2) y el oráculo.
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa árboles de sufijos para guardar Seq[Seq[Char]].
   * Usa paralelismo de tareas y/o datos.
   */
  def reconstruirCadenaTurboAceleradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    def filtrarPar(SC: Seq[Seq[Char]], k: Int): Seq[Seq[Char]] = {
      val trie = arbolDeSufijos(SC)
      val combinaciones = for {
        s1 <- SC
        s2 <- SC
      } yield s1 ++ s2

      if (combinaciones.length > umbral) {
        combinaciones.par.filter { s =>
          s.sliding(k).forall(sub => pertenece(sub, trie))
        }.seq
      } else {
        combinaciones.filter { s =>
          s.sliding(k).forall(sub => pertenece(sub, trie))
        }
      }
    }

    @tailrec
    def reconstruir(k: Int, SCi: Seq[Seq[Char]]): Seq[Char] = {
      val validas = filtrarPar(SCi, k / 2)

      val candidatas = if (validas.length > umbral) {
        validas.par.filter(o).seq
      } else {
        validas.filter(o)
      }

      val S = candidatas.head
      if (S.length == n) S
      else {
        reconstruir(2*k, candidatas)
      }
    }

    val sc1 = alfabeto.map(c => Seq(c)).filter(o)
    reconstruir(2, sc1)
  }
}
