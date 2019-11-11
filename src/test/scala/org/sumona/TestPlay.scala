package org.sumona

import org.scalatest.{FlatSpec, Matchers}

import scala.util.{Failure, Try}

/**
  * Created by uroutsu on 11/7/16.
  */
class TestPlay extends FlatSpec with Matchers {

  val messages_to_test = List("a","b","c")

  //Convenience so we're not always constructing this guy
  trait ImplicitErrorHandler {
    implicit val errorHandler = new ErrorHandler {
      override def handleErrors(messages: String*): Unit = println(messages)
    }
  }

  "Straight function with error handler" should "do something" in new ImplicitErrorHandler{
    val handler: (String) => Try[ModelObject] = MessageHandler.tryToRead((str) => ModelObject.fromJson(str), errorHandler)_
    messages_to_test.map(message => handler(message)).foreach(obj => obj.isFailure should be (true))
  }

  "Straight function with implicit errorHandler" should "do something too" in new ImplicitErrorHandler {
    val handler: String => Try[ModelObject] = ImplicitlyObjectAutobahnMessageHandler.tryToRead((str) => ModelObject.fromJson(str))_
    messages_to_test.map(message => handler(message)).foreach(obj => obj.isFailure should be (true))

  }

  "Mixin autobahnMessageHandler" should "do it too" in  {

    val handler = new ModelObjectHandler with PrintLineErrorHandler
    val differentHandler = new ModelObjectHandler with StoreToS3ErrorHandler

    messages_to_test.map(handler.tryToRead(_)).foreach(obj => obj.isFailure should be (true))
    messages_to_test.map(differentHandler.tryToRead(_)).foreach(obj => obj.isFailure should be (true))

  }

  "Class constructor with implicit error handler" should "do it too" in new ImplicitErrorHandler {
    val handler = new ClassyModelObjectHandler

    messages_to_test.map(handler.tryToRead(_)).foreach(obj => obj.isFailure should be (true))
  }

}
