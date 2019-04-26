package take_care.codes

class Block_code(start_offsetI: Long, end_offsetI: Long, numb_istrI: Long) extends AnyRef{

	private var start_offset:Long = start_offsetI
	private val end_offset:Long = end_offsetI
	private var numb_istr:Long = numb_istrI
	private var jump_to:Long = 0L
	private var visited:Int = 0

	
	private var type_jump:Int = 0		//0 jmp, 1 jcc, 2call, 3 ret, -1 end of function

	def this(end:Long, numb:Long) = this(end,end,numb)
	def this(end:Long) = this(end,0)
	
	def setVisited() = this.visited = this.visited + 1
	def setJump(jJ:Int) = type_jump = jJ
	def setStart(start:Long) = if(start > 0  )this.start_offset = start
	def setNumb(numb:Long) = this.numb_istr = numb

	def getVisited():Int =  return visited
	def getNumb():Long = return this.numb_istr
	def getStart():Long ={
		val n:Long = start_offset
		return n
	}
	def getEnd():Long ={
		val n:Long = end_offset
		return n
	}
	
	def compare(next:Block_code):Long = getStart - next.getStart
	def equals(next:Block_code):Boolean = this.compare(next) == 0

	override def toString():String = {
	  var retZ = ("Block"+" <" + start_offset.toString + "," + end_offset.toString + ">" + " (" + numb_istr.toString +", "+type_jump+ ")")
	  return retZ
	}  
	
	def isContainedIstr(offset_istruzione:Long):Boolean =
		return ( (offset_istruzione >= start_offset) && (offset_istruzione <= end_offset))

    def setJumpTo(jj:Long) = if( jj > 0L  ) this.jump_to = jj
    def getJumpTo():Long = return this.jump_to

}
