package object ArbolSufijos {

  // Definiendo la estructura para los tries (árboles de prefijos/sufijos)
  abstract class Trie
  case class Nodo(car: Char, marcada: Boolean, hijos: List[Trie]) extends Trie
  case class Hoja(car: Char, marcada: Boolean) extends Trie

  /**
   * Devuelve el carácter almacenado en la raíz de un Trie.
   * Para un Nodo, es el carácter del nodo.
   * Para una Hoja, es el carácter de la hoja.
   */
  def raiz(t: Trie): Char = {
    t match {
      case Nodo(c, _, _) => c
      case Hoja(c, _) => c
    }
  }

  /**
   * Devuelve una secuencia de caracteres representando las cabezas de los hijos directos de un Nodo.
   * Para una Hoja, devuelve una secuencia con su propio carácter.
   */
  def cabezas(t: Trie): Seq[Char] = {
    t match {
      case Nodo(_, _, lt) => lt.map(t => raiz(t))
      case Hoja(c, _) => Seq[Char](c)
    }
  }

  /**
   * Devuelve true si la secuencia s es reconocida por el trie t, y false si no.
   */
  def pertenece(s: Seq[Char], t: Trie): Boolean = {
    // Implementación de la función pertenece
    ??? // Implementar lógica aquí
  }

  /**
   * Adiciona una secuencia s (de uno o más caracteres) a un trie t.
   * Devuelve el nuevo trie con la secuencia adicionada.
   */
  def adicionar(s: Seq[Char], t: Trie): Trie = {
    // Implementación de la función adicionar
    ??? // Implementar lógica aquí
  }

  /**
   * Dada una secuencia no vacía de secuencias (ss),
   * devuelve el árbol de sufijos asociado a esas secuencias.
   */
  def arbolDeSufijos(ss: Seq[Seq[Char]]): Trie = {
    // Implementación de la función arbolDeSufijos
    ??? // Implementar lógica aquí
  }
}