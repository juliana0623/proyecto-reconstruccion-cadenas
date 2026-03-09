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
    def generarCadenas(longitud: Int): LazyList[List[Char]] = {
      if (longitud == 0) LazyList(List.empty)
      else if (longitud > umbral) {
        val tareas = alfabeto.map { letra =>
          task {
            generarCadenas(longitud - 1).map(letra :: _)
          }
        }
        tareas.map(_.join()).reduceLeft(_ #::: _)
      } else {
        for {
          cadena <- generarCadenas(longitud - 1)
          letra  <- alfabeto
        } yield letra :: cadena
      }
    }
    generarCadenas(n).find(o).getOrElse(Seq.empty)
  }

  def reconstruirCadenaIngenuoParIterator(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    val candidatos =
      (1 to n).foldLeft(Iterator(Seq.empty[Char])) { (acc, _) =>
        val accList = acc.toVector
        if (accList.size > umbral) {
          val (izq, der) = accList.splitAt(accList.size / 2)

          val (resI, resD) = parallel(
            () => for {
              prefix <- izq.iterator
              letra <- alfabeto.iterator
            } yield prefix :+ letra,
            () => for {
              prefix <- der.iterator
              letra <- alfabeto.iterator
            } yield prefix :+ letra
          )
          resI() ++ resD()
        } else {
          for {
            prefix <- accList.iterator
            letra <- alfabeto.iterator
          } yield prefix :+ letra
        }
      }

    candidatos.find(o).getOrElse(Seq.empty)
  }

  /**
   * Versión paralela de reconstruirCadenaMejorado.
   * Recibe un umbral, la longitud n y el oráculo.
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa paralelismo de tareas y/o datos.
   */
  def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char] = {
    val sc0: Seq[Seq[Char]] = Seq(Vector.empty[Char])

    val scK = (1 to n).foldLeft(sc0) { (scAnterior, _) =>
      if (scAnterior.size > umbral) {
        // Paralelismo de datos: distribuimos la expansión de prefijos entre hilos
        (for {
          w     <- scAnterior.par
          letra <- alfabeto
          nuevo = w :+ letra
          if o(nuevo)
        } yield nuevo).seq
      } else {
        // Versión secuencial para tamaños pequeños (por debajo del umbral)
        for {
          w     <- scAnterior
          letra <- alfabeto
          nuevo = w :+ letra
          if o(nuevo)
        } yield nuevo
      }
    }

    scK.find(_.length == n).getOrElse(Seq.empty)
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
