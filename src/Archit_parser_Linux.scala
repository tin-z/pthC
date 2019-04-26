package take_care.parser
import take_care.codes._
import scala.collection.mutable.{ HashMap, MutableList}

import scala.io.StdIn.{readLine,readInt}
import scala.io.Source
import scala.sys.process.{Process, ProcessIO}
import sys.process._
import sys.process.stringSeqToProcess

class Archit_parser_Linux extends Archit_parser {

 private var base_address:Long = 0L
 private var max_length:Long = 0L



//####	Linux_scripts
def getPid(name_process:String):String = {
	return  ("""./scripts/get_pid.sh """ + name_process).!!
 }

def getBaseAddress(pid:String, percorso_comando:String):String = {
	return ( """./scripts/get_baseAdd.sh """ + pid + " " + percorso_comando ).!!
 }

def kill(pid:String):Int = {
	val r = ("kill " + pid).!
	printf("Killato\n")
	return r
 }

def getExecutable(eseguibile:String, filename:String):Unit = {
	 ("""./scripts/obj.sh """ + eseguibile + " " + filename ).!
 }

def file_cmd(eseguibile:String):Int = {	//useless
	val ret:String = ("""file """ + eseguibile).!!
	if( ret.indexOf("shared object") > 0){
		return 1
	}
	else if( ret.indexOf("executable") > 0){
		return 0
	}
	return 2
 }

def pause_proc(pid:String):Unit={
	("""kill -TSTP """ + pid).!
 }

def resume_proc(pid:String):Unit={
	("""kill -CONT """ + pid).!
 }

def hex2Dec(str:String):Long ={ 
	str.toList.map("0123456789abcdef".indexOf(_).toLong).reduceLeft(_ * 16L + _)
 }

def start_tracing(pid:String, comand_path:String, state:Int,whatever:AnyRef):Unit={

	val default_number_mpages:String=" 16384"	//which means, Dimension_TOPA_buffer = 2^n * page_size. 16384 * 4KB = 64MB
	val switch_output_size:String= " 60M"	
	var str=""	
	var cmd = Array(
		( "sudo ./scripts/trace.sh " + pid + default_number_mpages + switch_output_size + " " + comand_path ),
		( "sudo ./scripts/trace_without_filter.sh " + pid + default_number_mpages + switch_output_size + " " + comand_path ),
		"sudo ./scripts/trace_2.sh\n" ,
		( "sudo perf record -m," + default_number_mpages.trim +" -e intel_pt// " + "--switch-output=" + switch_output_size.trim + " " + comand_path )
	)
	var msg = Array(
		"For some reason ( scala && posix) can't launch more than one process so you must exec\nthis command from to another shell under the same dir:",
		"IMPORTANT:if it dosn't work or the program gives us 0% cod cov, launch instead:"		
	)

	var helps=Array(
			"\n\t###Help_help\n",
			"\th/H - to get help",
			"\tb/B - to stop tracing",
			"\ti/I - to get more info about the process",
			"\n"
	)
	var info=Array(
			"\n\t###Info_info\n", 
			"\tto exit correctly:\n\t\tfirst you must stop the perf process next you can press 'b'",
			"\terror perf report :\n\t\tif you have some perf data with errors the reason could be cause\n\t\tyou have much threshold on cpu running process so try close all other windows except what needed\n\t\tincrease default_number_mpages (must be multiple of 2^n cause wold be mapped into pages size\n\t\tdecrease switch_out_size (remember this size must be <= 75% of the buffer size)",
			"\n"
	)
	
	if (state == 0) {	//The process to trace was launched with scala
		println(msg(0) + "\n" + cmd(0) + "\n\n" + msg(1) + "\n" + cmd(1) + "\n")
		readLine("If u have some old perf.data under the dir ./record take backup of them cause will be delected\nwhen you did all the stuff, press enter to continue\n# ")
		resume_proc(pid)
		
		println("###\n#\n#\n#\n\n")
		printf("#Pres 'h' to get help, 'b' or 'B' to stop tracing\n#\n#\n")
		
		while(str != "B" ){
			str = readLine("# ").toUpperCase
			if(str == "B"){
				if(readLine("Are you sure?\n#y(dafult)/n: ").toUpperCase == "N")
					str=""
			}
			else if(str == "H")
				helps.foreach(println)
			else if(str == "I")
				info.foreach(println)
			else 
				println("..Wrong command, press h if you need help")
		}
	}
	else if (state == 1){	//The process was allready launched
		println(msg(0) + "\n\n"+ cmd(1))
	}
	else {			//The manual process
		println("\n###\n#Manual mode......\nopen another terminal like superuser and:\n" +
		"0)under ./record dir, laun: rm *\n" +
		"1)under ./record dir, launch:\n" + cmd(3) + "\n" +
		"2)go back from ./record and launch:\n./scripts/trace_without_pid.sh 0 " + comand_path +
		"3)under ./ dir, launch:\nsudo ./scripts/trace_2.sh\n" )
	}
	
	println("\n*if you stopped first the perf record process and next pressed b, launch: ")
	println(cmd(2))
 }



var input:HashMap[String,String] = HashMap(
	"-i" -> "./input_outputs/input.txt",
	"-o" -> "./log.txt",
	"-c" -> "",
	"-p" -> "",
	"-P" -> "0",
	"-f" -> "./input_outputs/function_list.txt",
	"-m" -> "./input_outputs/config_memory.txt"
 )

def validate_input(args:Array[String]):Unit={

	var index = 0
	while(true){
		try{
			input( args(index) )
			val k = args(index)
			index += 1
			input += (k -> args(index).trim)
			println(k + " -> " + args(index).trim)
			index += 1
		} catch {
			case tri:NoSuchElementException => { throw new IllegalArgumentException()}
			case tri:ArrayIndexOutOfBoundsException => {
				val keys = input.keys.toList
				if( keys.length != (keys diff input.values.toList).length)
					throw new IllegalArgumentException()
				return
			}
		}
	}
 }

def routine_1(args:Array[String]):Int={	
	try{
		validate_input(args)
	} catch {
		case tri:IllegalArgumentException => {	
			println("# Wrong format input...")
			println("usage: scala TakeCare -c <command_name> -p <complete_path_command_name>  [ -o <log> -P <pid> ]")
			return 1
			}
	}

	val filename = input("-i")
	val percorso_comando = input("-p")
	val eseguibile = input("-c")
	val pid_process_custom = input("-P")
	val functions_touched = input("-f")
	
	getExecutable(percorso_comando, filename)	
	val src = scala.io.Source.fromFile(filename)
	val lines = src.mkString
	val p = new Parse_ELF()

	p.parse(p.text_to_start, lines) match {
		case p.Success(s,_) => 
		case x => println(x.toString)
	}
	src.close()
/*
	for (element <- p.getBlocks){
		println(element)
	}
	for (element <- p.getBlocks_FF){
		println(element)
	}
*/
	var tmp = scala.io.Source.fromFile(functions_touched).getLines.toArray
	val src2 = tmp(0)
	p.set_ff_To_track( src2 )

	set(p)
	printf("### Done creation of blocks.\n")

/*	some tests hash map*/
	val (idx1, idx2) = arrBlock_FF(0).get_Index()
	println(idx1)
	println(idx2)
	println(arrBlock(idx1))
	println(arrBlock(idx2))
	println( arrBlock(0).toString() )//check the 0 block

	if(readLine("### Manual mode tracing? N(default)/y?\n# ").toUpperCase == "Y"){
		start_tracing("0", percorso_comando, 2, null)
	}
	else{
		if(pid_process_custom == "0"){
			val pb = Process(percorso_comando)
			val pio = new ProcessIO(_ => (), _ => (), _ => ())
			pb.run(pio)
			pause_proc( getPid(eseguibile) )
			val pid_process =  getPid( eseguibile ).trim

			printf("### process is running with pid:%s baseadd:%s\nNow we start tracing\n", pid_process, getBaseAddress( pid_process, percorso_comando) )

			start_tracing(pid_process, percorso_comando, 0, null)
			kill(pid_process)
			printf("### Check all is killed \n")
		}
		else 
			start_tracing(pid_process_custom, percorso_comando, 1, null)
	}
	return 0
 }
 

 
def print_coverage(){
	var coverage:Double = ((arrBlock.toList.filter(_.getVisited() > 0) ).length - 1).toDouble / ( arrBlock.length - 1).toDouble
	println("Instruction visited: " + ((arrBlock.toList.filter(_.getVisited() > 0) ).length - 1).toString + " out of: " + (arrBlock.length - 1).toString )	
	printf("The coverage is: ") 
	println(f"$coverage%1.2f")
 }

def resolve_conflict(address_l:Long):Long={
	var indx_r = address_l
	while( !(hashThem.contains(indx_r.toHexString)) ){
		indx_r += 1
	}
	return indx_r
 }

def print_function_hit(){
	println("#\n#\nFunction hitted:")
	for(index <- tracked_FF){
		val point_to_ff = arrBlock_FF(index)
		println(point_to_ff.toString)
		val (fromZ,toZ) = point_to_ff.get_Index()
		for(index_block <- fromZ to toZ){
			val point_to_block = arrBlock(index_block)
			println(point_to_block.toString + " #hit by:"+ point_to_block.getVisited())
		}
		println()
	
	}
 }





def set_coverage(){
	
	var src2 = scala.io.Source.fromFile( input("-m") ).getLines.toArray
	val tmp = src2(0).split(" ")
	var diff_map_address = true
	//var tmp = readLine("insert base address\n# ")

	if(tmp(3).trim == "n"){
		base_address= hex2Dec( tmp(0).trim )
		diff_map_address=false
	}
	else {
		base_address= hex2Dec( tmp(1).trim )
	}
	//tmp = readLine("insert max length\n# ")
	
	max_length = base_address + hex2Dec( tmp(2) )
	var ret = (("./scripts/posix_scala_conflict.sh 0").!!).split(" ")
	if(ret(0) == "1"){
		println("None input file trace")
		return
	}

	val dir = "input_outputs/"
	var precZ = 0
	var fromZ = 0	
	setVisited(0)

	//if( readLine("the base address differ from the original address y(default)/n\n# ").toUpperCase != "N"){
	if(diff_map_address){
		for( output_now <- ret ){
			println("file: " + dir + output_now)
			for (line <- Source.fromFile((dir + output_now.trim)).getLines){
				//println(line)
				val line2 = line.trim.split("=>")
				if ( line2.length < 2){	//error intercepted, reset to init state, rarely happens (trace error)
					precZ = 0
					fromZ = 0
				}
				else{
					line2(0) = line2(0).trim
					line2(1) = line2(1).trim
					if (( hex2Dec(line2(0) ) <= max_length) && ( hex2Dec(line2(0)) >= base_address )){
						try { 
							fromZ = hashThem( (hex2Dec(line2(0)) - base_address).toHexString ) 
						}
						catch { 
							case tri:NoSuchElementException => {
								fromZ = hashThem((resolve_conflict( hex2Dec(line2(0)) - base_address )).toHexString)
								}
							}
						if( precZ != 0){
							for (follow_the_path <- (precZ) to (fromZ) by 1){
								setVisited(follow_the_path)	
							}
						}
						else {
							setVisited(fromZ)
						}
					}
					if (( hex2Dec(line2(1)) <= max_length) && ( hex2Dec(line2(1)) >= base_address)){
						try {
							 precZ = hashThem( (hex2Dec(line2(1)) - base_address).toHexString )
						}
						catch { 
							case tri:NoSuchElementException => {
								precZ = hashThem((resolve_conflict( hex2Dec(line2(1)) - base_address )).toHexString)
								}
							}
					}
					else {
						precZ = 0
					}
				}
			}
		}
	}
	else {
		for( output_now <- ret ){
			println("file: " + dir + output_now)
			for (line <- Source.fromFile((dir + output_now.trim)).getLines){
				//println(line)
				val line2 = line.trim.split("=>")
				if ( line2.length < 2){	//error intercepted, reset to init state, rarely happens (trace error)
					precZ = 0
					fromZ = 0
				}
				else{
					line2(0) = line2(0).trim
					line2(1) = line2(1).trim
					if (( hex2Dec(line2(0) ) <= max_length) && ( hex2Dec(line2(0)) >= base_address )){
						try { 
							fromZ = hashThem( (hex2Dec(line2(0))).toHexString ) 
						}
						catch { 
							case tri:NoSuchElementException => {
								fromZ = hashThem((resolve_conflict( hex2Dec(line2(0)) )).toHexString)
								}
							}
						if( precZ != 0){
							for (follow_the_path <- (precZ) to (fromZ) by 1){
								setVisited(follow_the_path)	
							}
						}
						else {
							setVisited(fromZ)
						}
					}
					if (( hex2Dec(line2(1)) <= max_length) && ( hex2Dec(line2(1)) >= base_address)){
						try {
							 precZ = hashThem( (hex2Dec(line2(1)) ).toHexString )
						}
						catch { 
							case tri:NoSuchElementException => {
								precZ = hashThem((resolve_conflict( hex2Dec(line2(1)) )).toHexString)
								}
							}
					}
					else {
						precZ = 0
					}
				}
			}
		}
	}
 }		

def routine_2():Int={
	set_coverage()
	print_coverage()
	print_function_hit()
	return 0
 }


/*	to construct CFG use :
		- the method resolve_conflict
		- the field Basic_block.jump_to
		- the hashmap 
*/

}
