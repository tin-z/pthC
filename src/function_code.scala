package take_care.codes

class Page_function(nameI:String, start_offsetI:Long, end_offsetI:Long) extends AnyRef{
	private val name_f = nameI
	private val start_offset:Long = start_offsetI
	private val end_offset :Long= end_offsetI
	private var index_block:(Int,Int) = (0,0)

	def getStart():Long ={
		val n:Long = start_offset
		return n
	}

	def set_Index_Block(first:Int,last:Int) = { 
		if(first < last)
			index_block=(first,last) 
		else
			index_block=(first,first)
	}
	
	def compare(next:Page_function):Long = getStart - next.getStart
	def equals(next:Page_function):Boolean = this.compare(next) == 0

	override def toString():String = {
		val (from,to) = index_block
		return name_f +" <" + start_offset.toString + "," + end_offset.toString + "> " + " ("+from.toString+", "+to.toString+")"
	}
	
	def get_Index():(Int,Int) = {
		return index_block
	}

	def get_name():String={
		return name_f
	}
	def isContainedIstr(offset_istruzione:Long):Boolean =
		return ( (offset_istruzione >= start_offset) && (offset_istruzione <= end_offset) )	
}
