import Oraculo._
import ReconstCadenas._
import ReconstCadenasPar._
import ArbolSufijos._
import scala.util.Random

val delayOraculo = 0

def generarSecuencia(longitud: Int): Seq[Char] = {
  val rnd = new Random()
  Seq.fill(longitud)(alfabeto(rnd.nextInt(alfabeto.length)))
}

val sec1 = generarSecuencia(math.pow(2, 3).toInt)

val lon = sec1.length
val or1=crearOraculo(delayOraculo)(sec1)
reconstruirCadenaTurboMejoradaPar(2)(lon, or1)
