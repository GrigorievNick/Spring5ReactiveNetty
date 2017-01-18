package scala

object Person {
  def apply(name: String, age: Int): Person = new Person(name, age)
}

class Person(var name: String, var age: Int) {
  def getName: String = {
    this.name
  }

  def getAge: Int = {
    this.age
  }

  def setAge(age: Int) = {
    this.age = age
  }

  def setName(name: String) = {
    this.name = name
  }

  override def toString: String = {
    s"Person{name='$name'age='$age'}"
  }
}