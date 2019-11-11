package org.sumona

import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper

import scala.util.Try

object JsonUtil {
  val mapper = new ObjectMapper() with ScalaObjectMapper
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  def toJson(value: Any): String = {
    mapper.writeValueAsString(value)
  }

  def fromJson[T](json: String)(implicit m: Manifest[T]): T = {
    mapper.readValue[T](json)
  }
}

case class ModelObject(field: String, anotherField: String)

object ModelObject {
  def fromJson(toParse: String): Try[ModelObject] = {
    Try(JsonUtil.fromJson(toParse))
  }
}

