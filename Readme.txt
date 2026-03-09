PROYECTO: Reconstrucción de Cadenas de ADN en Scala
==============================================

1. Descripción general
----------------------
Este proyecto implementa distintas soluciones funcionales (secuenciales y paralelas)
al **Problema de Reconstrucción de Cadenas (PRC)** descrito en el enunciado del curso.

La idea es reconstruir una cadena de ADN desconocida S, de longitud N, sobre el
alfabeto {a, c, g, t}, utilizando únicamente un **oráculo** Ψ_S que responde si una
secuencia s es o no subcadena de S.

Se implementan:
- Soluciones funcionales secuenciales (ingenua, mejorada, turbo, turbo mejorada,
  turbo acelerada con árboles de sufijos).
- Versiones paralelas de varias de estas soluciones usando **paralelismo de datos**
  y **paralelismo de tareas**.
- Un programa de medición de tiempos usando **ScalaMeter**, que permite comparar
  el desempeño secuencial vs paralelo y generar archivos CSV con los resultados.


2. Estructura de paquetes y archivos
------------------------------------
La estructura principal en `src/main/scala` es:

- `Oraculo\package.scala`
  - Define:
    - `val alfabeto = Seq('a', 'c', 'g', 't')`
    - `type Oraculo = Seq[Char] => Boolean`
    - `def crearOraculo(delay: Int)(c: Seq[Char]): Oraculo`
  - Esta función modela el oráculo que responde si una secuencia es subcadena de la
    cadena objetivo, simulando el costo del experimento con `Thread.sleep(delay)`.

- `ArbolSufijos\package.scala`
  - Define la estructura de **trie** y los árboles de sufijos:
    - `abstract class Trie`
    - `case class Nodo(car: Char, marcada: Boolean, hijos: List[Trie])`
    - `case class Hoja(car: Char, marcada: Boolean)`
    - `def raiz(t: Trie): Char`
    - `def cabezas(t: Trie): Seq[Char]`
    - `def pertenece(s: Seq[Char], t: Trie): Boolean`
    - `def adicionar(s: Seq[Char], t: Trie): Trie`
    - `def arbolDeSufijos(ss: Seq[Seq[Char]]): Trie`
  - Estas funciones permiten representar y consultar de forma eficiente conjuntos de
    cadenas mediante árboles de sufijos.

- `ReconstCadenas\package.scala`
  - Implementa las versiones **secuenciales** de los algoritmos:
    - `def reconstruirCadenaIngenuo(n: Int, o: Oraculo): Seq[Char]`
      - Implementa la solución ingenua (2.3.1): prueba todas las cadenas de longitud N.
    - `def reconstruirCadenaMejorado(n: Int, o: Oraculo): Seq[Char]`
      - Implementa la solución mejorada (2.3.2) construyendo conjuntos SC_k de
        candidatas y filtrando con el oráculo.
    - `def reconstruirCadenaTurbo(n: Int, o: Oraculo): Seq[Char]`
      - Implementa la versión turbo (2.3.3), duplicando longitud k y combinando SC_k/2.
    - `def reconstruirCadenaTurboMejorada(n: Int, o: Oraculo): Seq[Char]`
      - Implementa la turbo mejorada (2.3.4) con la función de filtrado que evita
        generar cadenas que ya pueden descartarse sin consultar al oráculo.
    - `def reconstruirCadenaTurboAcelerada(n: Int, o: Oraculo): Seq[Char]`
      - Implementa la versión turbo acelerada (2.3.5), usando árboles de sufijos para
        representar SC_k y acelerar el filtrado de candidatas.

- `common\package.scala`
  - Proporciona primitivas de **paralelismo de tareas**:
    - Infraestructura basada en `ForkJoinPool` y `RecursiveTask`.
    - Funciones:
      - `def task[T](body: => T): ForkJoinTask[T]`
      - `def parallel[A, B](taskA: => A, taskB: => B): (A, B)`
      - `def parallel[A, B, C, D](...)`
  - Se utiliza para implementar paralelismo explícito en las versiones paralelas.

- `ReconstCadenasPar\package.scala`
  - Implementa las versiones **paralelas** de los algoritmos:
    - `def reconstruirCadenaIngenuoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Usa generación recursiva de cadenas y `task` para dividir el trabajo cuando
        la profundidad supera el umbral.
    - `def reconstruirCadenaIngenuoParIterator(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Variante que usa `Iterator` y `common.parallel` para dividir el espacio de
        búsqueda en dos mitades.
    - `def reconstruirCadenaMejoradoPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Versión paralela de la solución mejorada: cuando el tamaño de SC_k supera el
        umbral, se usa `.par` (paralelismo de datos) para expandir las candidatas en
        paralelo; para tamaños pequeños se usa la versión secuencial.
    - `def reconstruirCadenaTurboPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Paraleliza la generación de combinaciones SC_k/2 · SC_k/2 y el filtrado por
        el oráculo usando `.par` según el umbral.
    - `def reconstruirCadenaTurboMejoradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Versión paralela del turbo mejorado, combinando:
        - Filtrado de candidatos con conocimiento de SC_k.
        - Paralelismo de datos para generación y filtrado cuando el tamaño supera
          el umbral.
    - `def reconstruirCadenaTurboAceleradaPar(umbral: Int)(n: Int, o: Oraculo): Seq[Char]`
      - Versión paralela de la turbo acelerada:
        - Usa `arbolDeSufijos` para representar SC_k.
        - Paraleliza la verificación de subcadenas en el trie y la consulta al oráculo
          cuando el número de combinaciones es grande.

- `TestRunner\package.scala`
  - Programa principal (`object TestRunner extends App`) para:
    - Generar una secuencia aleatoria de ADN de longitud n = 2^k.
    - Crear el oráculo correspondiente con `crearOraculo`.
    - Medir tiempos con **org.scalameter**:
      - Versión secuencial: `reconstruirCadenaTurboMejorada`.
      - Versión paralela: `reconstruirCadenaTurboMejoradaPar`.
    - Calcular aceleración = T_secuencial / T_paralelo.
    - Imprimir tabla de resultados por cada k.
    - Guardar resultados en un archivo CSV `resultados_acelerada_YYYYMMDD_HHMMSS.csv`
      en la raíz del proyecto.


3. Dependencias y compilación
------------------------------
El proyecto usa **sbt** y Scala 3:

- Archivo `build.sbt`:
  - `scalaVersion := "3.3.6"`
  - Dependencias principales:
    - `"com.storm-enroute" %% "scalameter-core" % "0.21"` (vía `cross` para Scala 2.13).
    - `"org.scala-lang.modules" %% "scala-parallel-collections" % "1.2.0"`.
    - `"org.scalameta" %% "munit" % "1.1.0" % Test`.

Requisitos:
- Tener instalado:
  - Java (JDK 8+).
  - sbt.

Para compilar el proyecto:
1. Abrir una terminal en la carpeta raíz del proyecto.
2. Ejecutar:
   - `sbt compile`


4. Cómo ejecutar los algoritmos
-------------------------------
### 4.1. Ejecución desde la consola de Scala o desde código

Ejemplo para usar cualquier versión `reconstruirCadenaXXX`:

1. Crear una cadena objetivo:
   - Por ejemplo: `"gaatccagat".toList`
2. Crear un oráculo:
   - `val or = Oraculo.crearOraculo(1)(sec)`
3. Llamar al algoritmo deseado:
   - Secuencial ingenuo:
     - `ReconstCadenas.reconstruirCadenaIngenuo(sec.length, or)`
   - Secuencial mejorado:
     - `ReconstCadenas.reconstruirCadenaMejorado(sec.length, or)`
   - Turbo / turbo mejorada / turbo acelerada:
     - `ReconstCadenas.reconstruirCadenaTurbo(sec.length, or)`
     - `ReconstCadenas.reconstruirCadenaTurboMejorada(sec.length, or)`
     - `ReconstCadenas.reconstruirCadenaTurboAcelerada(sec.length, or)`
   - Versiones paralelas (ejemplo con umbral = 1000):
     - `ReconstCadenasPar.reconstruirCadenaIngenuoPar(1000)(sec.length, or)`
     - `ReconstCadenasPar.reconstruirCadenaMejoradoPar(1000)(sec.length, or)`
     - `ReconstCadenasPar.reconstruirCadenaTurboPar(1000)(sec.length, or)`
     - `ReconstCadenasPar.reconstruirCadenaTurboMejoradaPar(1000)(sec.length, or)`
     - `ReconstCadenasPar.reconstruirCadenaTurboAceleradaPar(1000)(sec.length, or)`


5. Cómo ejecutar las mediciones de desempeño (ScalaMeter)
---------------------------------------------------------
El programa principal `TestRunner` ya está configurado para:
- Probar valores de k (por defecto, en el código: de 12 a 13).
- Para cada k:
  - Generar una cadena aleatoria de longitud n = 2^k.
  - Medir tiempo secuencial con `reconstruirCadenaTurboMejorada`.
  - Medir tiempo paralelo con `reconstruirCadenaTurboMejoradaPar`.
  - Imprimir en consola los tiempos y la aceleración.
  - Guardar los resultados en un archivo CSV en la raíz del proyecto.

Para ejecutarlo:
1. Desde la raíz del proyecto, en una terminal:
   - `sbt run`
2. Seleccionar `TestRunner` si sbt pregunta qué objeto `main` ejecutar.

Los archivos CSV generados tienen nombres del estilo:
- `resultados_acelerada_YYYYMMDD_HHMMSS.csv`

Puede abrirlos con cualquier editor de texto, Excel, LibreOffice, etc., para
construir tablas comparativas y gráficas de aceleración.


6. Notas sobre paralelismo y umbral
-----------------------------------
- El parámetro `umbral` en las funciones paralelas controla a partir de qué tamaño
  de conjunto de candidatas o combinaciones se activa el paralelismo:
  - Para tamaños de entrada pequeños, se ejecuta de forma secuencial (menor
    sobrecarga).
  - Para tamaños grandes, se usa:
    - `.par` sobre colecciones (paralelismo de datos).
    - `task` y `parallel` (paquete `common`) para paralelismo de tareas.
- Se recomienda experimentar con diferentes valores de `umbral` para encontrar
  el mejor compromiso entre sobrecarga de creación de tareas y aceleración obtenida.


7. Relación con los resultados de aprendizaje del curso
-------------------------------------------------------
Este proyecto evidencia:
- Uso de **programación funcional** en Scala:
  - Uso intensivo de colecciones inmutables, recursión, funciones de orden superior,
    `LazyList`, `Iterator`, `foldLeft`, `for-comprehensions`, etc.
- Combinación de programación funcional y **paralelismo**:
  - Paralelismo de datos (`.par`) y de tareas (`task`, `parallel`) para acelerar
    algoritmos expresados en estilo funcional.
- Implementación y uso de **árboles de sufijos** para mejorar el desempeño de los
  algoritmos turbo.
- Medición y análisis de desempeño con **ScalaMeter**, generando datos cuantitativos
  para comparar soluciones secuenciales y paralelas.

Con esto, el proyecto cubre los puntos del enunciado relacionados con:
- Modelado funcional del problema.
- Diseño e implementación de soluciones secuenciales y concurrentes.
- Uso de estructuras de datos adecuadas (tries y árboles de sufijos).
- Evaluación comparativa de desempeño y análisis de aceleración.

