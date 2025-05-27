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
    // Implementación de la función reconstruirCadenaIngenuo
    ??? // Implementar lógica aquí
  }

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Mejorado (2.3.2 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 (donde s = s1.s2) también son subsecuencias de S.
   */
  def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char] = {
    // Implementación de la función reconstruirCadenaMejorado
    ??? // Implementar lógica aquí
  }

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo (2.3.3 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   */
  def reconstruirCadenaTurbo(n: Int, o: Oraculo): Seq[Char] = {
    def generarCandidatos(SC: Seq[Seq[Char]]): Seq[Seq[Char]] = for {
      s1 <- SC
      s2 <- SC
    } yield s1 ++ s2

    @tailrec
    def iterar(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      if (k > n) Seq()
      else {
        val candidatos = generarCandidatos(SC)
        val SCfiltrado = candidatos.filter(w => o(w))
        val SClongitudN = SCfiltrado.find(w => w.length == n)
        if (SClongitudN.nonEmpty) {
          SClongitudN.head
        } else {
          iterar(2 * k, SCfiltrado)
        }
      }
    }

    val SC1: Seq[Seq[Char]] = alfabeto.map(c => Seq(c))
    iterar(2, SC1)
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
      val candidatos = for {
        s1 <- SC
        s2 <- SC
        s = s1 ++ s2
        if (1 until k).forall(i => SC.contains(s.slice(i, i + k)))
      } yield s
      candidatos
    }

    @tailrec
    def iterar(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      if (k > n) Seq()
      else {
        val candidatos = filtrar(SC, k/2)
        val SCfiltrado = candidatos.filter(w => o(w))
        val SClongitudN = SCfiltrado.find(w => w.length == n)
        if(SClongitudN.nonEmpty){
          SClongitudN.head
        }else{
          iterar(2 * k, SCfiltrado)
        }
      }
    }
    val SC1: Seq[Seq[Char]] = alfabeto.map(c => Seq(c))
    iterar(2, SC1)
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
      val combinaciones = for (s1 <- SC; s2 <- SC) yield s1 ++ s2
      combinaciones.filter(s => s.sliding(k).forall(sub => pertenece(sub, trie)))
    }

    @tailrec
    def iterar(k: Int, SC: Seq[Seq[Char]]): Seq[Char] = {
      if (k > n) Seq()
      else {
        val candidatos = filtrar(SC, k/2)
        val SCfiltrado = candidatos.filter(w => o(w))
        val SClongitudN = SCfiltrado.find(w => w.length == n)
        if(SClongitudN.nonEmpty){
          SClongitudN.head
        }else{
          iterar(2 * k, SCfiltrado)
        }
      }
    }
    val SC1 = alfabeto.map(c => Seq(c)).filter(o)
    iterar(2, SC1)
  }
}