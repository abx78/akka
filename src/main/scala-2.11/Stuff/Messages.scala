package Stuff

trait MessageBase {
 def content:String
}
case class SimpleMessage (content:String) extends MessageBase
case class DoSomeWork(content:String) extends MessageBase
case class DoMoreWork(content:String) extends MessageBase
case class DoEvenMore(content:String) extends MessageBase
case class Result(result1:String,result2:String,result3:String)