package com.wecreatex.template.domain.person

import com.wecreatex.utils.time.Date
import com.wecreatex.utils.transport.Result
import com.wecreatex.utils.time.TimeUtils.parseDate

case class Person(
  id: String,
  name: String, 
  surname: String, 
  gender: Option[String], 
  birthDate: Date)

object Person {
  
  def apply(
    id: String,
    name: String, 
    surname: String, 
    dayOfBirth: Int, 
    monthOfBirth: Int, 
    yearOfBirth: Int, 
    gender: Option[String]): Result[Person] = {
    val birthDate = parseDate(yearOfBirth, monthOfBirth, dayOfBirth)
    birthDate.map(Person(id, name, surname, gender, _))
  }
  
}