package take_care.parser
import take_care.codes._
import scala.util.parsing.combinator._
import scala.collection.mutable.HashMap


abstract class ParseU() extends RegexParsers {	

////Gen blocks
	private var idZ : Int = 1		
	private var mapZ :HashMap[String,Int] = new HashMap()
	mapZ += ("0" -> 0)	//the block 0 is reserved, so we start from 1

	private var array_block_ordered_by_idZ :Array[Block_code] = Array()
	
	def mapMeBro(str:String) = {
		mapZ += (str -> idZ)
		idZ = idZ + 1	
		}
	
	def getBlocks():Array[Block_code] = { return array_block_ordered_by_idZ }
	def setBlocks(tl:Array[Block_code]):Unit = { array_block_ordered_by_idZ=tl }
	def getHashs():HashMap[String,Int] = { return mapZ}
	def getID():Int ={ return idZ }
	var codes_now:List[Block_code] = List(new Block_code(0))	//first block code reserved	


////Gen functions	
	private var idZ_FF : Int = 0
	private var mapZ_FF :HashMap[String,Int] = new HashMap()
	private var array_block_ordered_by_idZ_FF :Array[Page_function] = Array()	
	
	def mapMeBro_FF(str:String) = {
		mapZ_FF += (str -> idZ_FF)
		idZ_FF =idZ_FF + 1
		}		
	
	def getBlocks_FF():Array[Page_function] = { return array_block_ordered_by_idZ_FF}
	def setBlocks_FF(lt:Array[Page_function]):Unit = { array_block_ordered_by_idZ_FF = lt }
	def getHashs_FF():HashMap[String,Int] = { return mapZ_FF}
	def getID_FF():Int ={ return idZ_FF }
	var functionList:List[Page_function] = List()


	private var ff_to_track:Array[Int] = Array()
	
	def get_ff_to_track():Array[Int] ={ return ff_to_track}
	
	def set_ff_To_track(src2:String):Unit ={
		var tmp :List[Int] = List()
		for (ff <- src2.trim.split(" +")){
			for (i <- 0 to (array_block_ordered_by_idZ_FF.length - 1)){
				val name_f = array_block_ordered_by_idZ_FF(i).get_name()
				if (name_f == ff)
					tmp = i::tmp
			}
		}
		ff_to_track = tmp.reverse.distinct.toArray
	}


////Runtime vars	
	var mutual_FF:Int = 0
	var first_index_FF:Int=0

	var type_jump:Int = 0
	var start_offset:Long = 0
	var mutual:Int = 0
	var counter:Long = 0
	var last_address:Long = 0

	override def skipWhitespace() = false
	def hex2Dec(str:String):Long = str.toList.map("0123456789abcdef".indexOf(_).toLong).reduceLeft(_ * 16L + _)

	def to_test 
	
}
