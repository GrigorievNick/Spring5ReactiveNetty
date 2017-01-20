package scala

import com.fasterxml.jackson.annotation.JsonProperty

import scala.beans.BeanProperty

case class Person(@JsonProperty("name") @BeanProperty name: String, @JsonProperty("age") @BeanProperty age: Int)