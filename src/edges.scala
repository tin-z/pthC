package take_care.codes
import scala.collection.mutable.{HashMap, Queue}


class Edges(){

 private var map:HashMap[Int,Int] = new HashMap()
 private var edges:Array[(Int,Queue[Int])] = Array() 
 private var pointernow:Int = 0
 map += (0 -> 0)
 edges :+= (0,Queue[Int]())	//0 is the entry point

 def getLast_index_id():Int=
 {
 	var (index_pt, coda ) = edges(pointernow)
 	return index_pt
 }

 def addA(id_from_pt:Int) = {
	val id = id_from_pt
	try { 
		val m = map( id )	//find if allready exists in my map
		var (index_pt, coda ) = edges(pointernow)
		coda.enqueue( m )
		pointernow = m

	} catch {
		case ex : java.util.NoSuchElementException => {
			val m = edges.length
			map += (id -> m )
			edges :+= ( id_from_pt, Queue[Int]() )
			var (index_pt, coda ) = edges(pointernow)
			coda.enqueue( m )
			pointernow = m
		}
	}
 }

//now had done all the stuff to create CFG_I, we must get it ready to be used.
 def close_edge() = {
 	var pointernow:Int = 0
 }
 

def next():Int = {
 var (index_pt, coda ) = edges(pointernow)
 pointernow = coda.dequeue
 var (index_pt2, coda2 ) = edges(pointernow)
 return index_pt2
 }

}
