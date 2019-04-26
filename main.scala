import take_care.parser._
import take_care.codes._
import scala.io.StdIn.{readLine,readInt}

object TakeCare {

var strategy_archit : Archit_parser = null

def main(args:Array[String]) :Unit= {

	println("We asume you are using ubuntu")
	strategy_archit = new Archit_parser_Linux
	if( strategy_archit.routine_1(args) == 0 ){
		readLine("# Press enter when all the stuff has be done\n")
		strategy_archit.routine_2()
	}
return 
 }
}
