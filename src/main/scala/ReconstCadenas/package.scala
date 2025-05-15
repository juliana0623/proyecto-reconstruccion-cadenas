import ArbolSufijos._ // Importa las definiciones de ArbolSufijos si es necesario
import Oraculo._     // Importa el tipo Oraculo y la función crearOraculo

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
    // Implementación de la función reconstruirCadenaTurbo
    // Asegurarse que n es potencia de 2 según el enunciado.
    ??? // Implementar lógica aquí
  }

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo Mejorada (2.3.4 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa el filtro para ir más rápido.
   */
  def reconstruirCadenaTurboMejorada(n: Int, o: Oraculo): Seq[Char] = {
    // Implementación de la función reconstruirCadenaTurboMejorada
    // Necesitará implementar la función Filtrar auxiliar.
    ??? // Implementar lógica aquí
  }

  /**
   * Recibe la longitud de la secuencia que hay que reconstruir (n, potencia de 2) y un oráculo para esa secuencia,
   * y devuelve la secuencia reconstruida.
   * Algoritmo Turbo Acelerada (2.3.5 del enunciado).
   * Usa la propiedad de que si s <= S, entonces s1 y s2 también son subsecuencias de S.
   * Usa árboles de sufijos para guardar Seq[Seq[Char]] (SCk) y el filtro modificado.
   */
  def reconstruirCadenaTurboAcelerada(n: Int, o: Oraculo): Seq[Char] = {
    // Implementación de la función reconstruirCadenaTurboAcelerada
    // Necesitará usar las funciones del paquete ArbolSufijos.
    ??? // Implementar lógica aquí
  }
}