package take_care.parser
import take_care.codes._
import scala.collection.mutable.{ HashMap, ListBuffer}

trait Archit_parser {

var edges :ListBuffer[Edges] = new ListBuffer[Edges]()
var hashThem:HashMap[String,Int] = new HashMap()
var arrBlock:Array[Block_code] = Array()
var hashThem_FF:HashMap[String,Int] = new HashMap()
var arrBlock_FF:Array[Page_function] = Array()
var tracked_FF:Array[Int] = Array()


def setVisited(index:Int):Int = {
	if((index >= 0) &  (index < arrBlock.length)){
	 arrBlock(index).setVisited()
	 return 0
	 }
	else 
	 return 1
}

def set(mm:ParseU):Unit ={
	hashThem = mm.getHashs()
	arrBlock = mm.getBlocks()
	hashThem_FF = mm.getHashs_FF()
	arrBlock_FF = mm.getBlocks_FF()
	tracked_FF = mm.get_ff_to_track()
	
}

//mngmnt process - pid
def getPid(str:String):String
def getBaseAddress(str:String, other:String):String
def kill(pid:String):Int
def pause_proc(pid:String):Unit
def resume_proc(pid:String):Unit

//mngmnt executable - file
def getExecutable(eseguibile:String, filename:String):Unit 
def file_cmd(eseguibile:String):Int	//which means type of file (e.g. in widnwos will be dll,exe,etc..)

//mngmnt trace intel pt
def start_tracing(pid:String, comand_path:String, state:Int, whatever:AnyRef):Unit



//Cold template to operate in
def routine_1(args:Array[String]):Int	//Gen blocks, trace some
def routine_2():Int	//Read edges of the graph, give coverage, gives function hitted
}
