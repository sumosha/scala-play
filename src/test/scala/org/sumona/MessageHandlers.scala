package org.sumona

import scala.util.{Failure, Success, Try}

//TODO: how do abstract out the cartesian product of parsers and errorhandlers to create a nice, fluent syntax like new UserAssessmentResultParser with JsonParsing with XYZErrorHandler

trait ErrorHandler {
  def handleErrors(messages: String*): Unit //maybe take a throwable too
}

object MessageHandler {

  def tryToRead[T](parse: String => Try[T], errorHandler: ErrorHandler)(message: String): Try[T] = {
    val tried = parse(message)
    tried match {
      case Failure(ex) => errorHandler.handleErrors(s"${this.getClass.getName} messed up.", ex.getMessage)
    }
    tried
  }

}

// Mixin style. Downside is you have a lots of traits, some that only partially implement the root trait. This seems SRP violation. However, the compiler will complain if your mixins don't add up to the sum of required implementations.
trait TraityMessageHandler {
  this: ErrorHandler =>
  type T

  val parser: String => Try[T]

  def tryToRead[T](message: String) = {
    val tried = parser(message)
    tried match {
      case Failure(ex) => handleErrors(s"${this.getClass.getName} messed up.", ex.getMessage)
    }
    tried
  }
}

trait ModelObjectHandler extends TraityMessageHandler with ErrorHandler {
  type T = ModelObject
  override val parser = (str: String) => ModelObject.fromJson(str)
}

trait PrintLineErrorHandler extends ErrorHandler {
  override def handleErrors(messages: String*) = println("println guy:" + messages.mkString("\n"))
}

trait StoreToS3ErrorHandler extends ErrorHandler {
  override def handleErrors(messages: String*) = println(s"store to s3: $messages") //in the real world this would actually store to s3 or so we think
}

//Downside is implicit loading is hard to track down. Upside of this is you only require it for the method in question (as opposed to class level)
object ImplicitlyObjectAutobahnMessageHandler {

  def tryToRead[T](parse: String => Try[T])(message: String)(implicit errorHandler: ErrorHandler) = {
    val tried = parse(message)
    tried match {
      case Failure(ex) => errorHandler.handleErrors(s"${this.getClass.getName} messed up.", ex.getMessage)
    }
    tried
  }
}

//Downside is implicit loading is hard to track down. Additionally, every subclass is required to take an implicit errorHandler in the constructor. It can't construct it. I haven't found the workaround yet, but it probably is messy.
abstract class ClassyMessageHandler[T](implicit errorHandler: ErrorHandler) {
  val parser: String => Try[T]

  def tryToRead[T](message: String) = {
    val tried = parser(message)
    tried match {
      case Failure(ex) => errorHandler.handleErrors(s"${this.getClass.getName} messed up.", ex.getMessage)
    }
    tried
  }
}

//see, I need this constructor arg all the time now
class ClassyModelObjectHandler(implicit errorHandler: ErrorHandler) extends ClassyMessageHandler[ModelObject] {
  override val parser = (str: String) => ModelObject.fromJson(str)
}
