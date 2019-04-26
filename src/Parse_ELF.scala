package take_care.parser
import take_care.codes._


class Parse_ELF() extends ParseU {	
		
	def spazio :Parser[String]= """[ ]+""".r | """\t""".r	//Dipende da dove copi. se fai da shell > i spazi fra offset ed bin sono \t
	def offset :Parser[Long] = spazio ~ """[0-9a-f]+""".r ~ ":" ^^ {case i1 ~ str ~ i2 => hex2Dec(str)}
	def codice_bin = """[a-f0-9][a-f0-9]""".r
	def argomento = """[#.$a-zA-Z,()_>% @<+:*0-9x-]+""".r
	def opcode = """(?!j\w{1,4}|call\w{0,1}|ret)(\w{1,6})""".r
	
	def jump_opcode = opcode_jmp|opcode_jccc|opcode_call| opcode_ret 	//old value was """(j\w{1,4}|call\w{0,1}|ret)""".r
	def opcode_jmp = """jmp\w{0,1}""".r ^^ {_ => type_jump = 0 }
	def opcode_jccc= """j\w{1,4}""".r ^^ {_ => type_jump = 1 }
	def opcode_call= """call\w{0,1}""".r ^^ {_ => type_jump = 2}
	def opcode_ret = """ret\w{0,1}""".r ^^ {_ => type_jump = 3}

	def riga  = 
	 offset <~ spazio <~ repsep(codice_bin," ") <~ rep(spazio) <~ opcode <~ opt(spazio) <~ opt(argomento) ^^ {
		case off => 
			counter += 1
			last_address = off
			if(mutual == 0) {
				mutual=1; start_offset=off
				if(mutual_FF == 0){
					mutual_FF=1; 
					first_index_FF=getID()
					}
				}
		}
			
	def rigaJump  = offset ~ spazio ~ repsep(codice_bin," ") ~ rep(spazio) ~ jump_opcode ~ opt(spazio) ~ opt(argomento) ^^ {
		case off ~ i1 ~ i2 ~ i3 ~ i4 ~ i5 ~ argz => 
			val jsZ = new Block_code(off, counter + 1)
			jsZ.setStart(start_offset)
			jsZ.setJump(type_jump)
			codes_now = (jsZ)::codes_now
			last_address = off
			counter=0
			mutual=0
			start_offset=0
			if(type_jump != 3){
			    argz match {
			        case Some(fine) => {
			                        val lul = get_jump_to(fine)
			                        if(lul > 0)
			                            jsZ.setJumpTo(lul)
                    }
                    case None => ()
                }
			}
			mapMeBro(off.toHexString)
	}

    def get_jump_to(str:String):Long = {
        	val ret = str.split("[ ]+")
        	for(elem <- ret)
                if ( elem.trim.matches("^[0-9a-fA-F]+$") ){
                    return ( elem.toList.map("0123456789abcdef".indexOf(_).toLong).reduceLeft(_ * 16L + _) )
                }
            return 0L
    }
		
	def riga_continuos = offset <~ spazio <~ repsep(codice_bin," ") <~ rep(spazio) ^^ {off => last_address = off}	

	def righe = riga | rigaJump | riga_continuos
	
	def block = repsep(righe, "\n") ^^ {_ => () }
	
	def function_name = 
	 opt("""\n""".r) ~ """[0]+""".r ~ """[a-f1-9][a-f0-9]*""".r ~ opt(spazio) ~ """<[a-zA-Z_@+.0-9-]*>""".r ~ """:""".r ~ """\n""".r ~ block ^^{
	   case i1 ~ i2 ~ address_start ~ i3 ~ name ~ i4 ~ i5 ~ blocchi =>
		var last_index_FF=getID()
		if (mutual != 0){
			val jsZ = new Block_code(last_address, counter)
			jsZ.setStart(start_offset)
			jsZ.setJump(-1)
			codes_now = (jsZ)::codes_now
			counter=0
			mutual=0
			start_offset=0
			mapMeBro(last_address.toHexString)
		}
		val ff = new Page_function( name, hex2Dec(address_start), last_address)
		ff.set_Index_Block(first_index_FF, last_index_FF )
		functionList = (ff)::functionList
		mutual_FF=0
		mutual = 0
		first_index_FF=0
		mapMeBro_FF(name)
	}
	
	def functions = repsep(function_name,"\n") ^^ {_ => 
		setBlocks_FF( (functionList.reverse).toArray)
		setBlocks( (codes_now.reverse).toArray);
		() }
			
	def junk = """(?!Disassembly of section \.text:)(.)*""".r
	def text_to_start = opt(repsep(junk, "\n")) ~> """\nDisassembly of section \.text:""".r ~> "\n" ~> functions 
	
	def to_test = text_to_start
}
