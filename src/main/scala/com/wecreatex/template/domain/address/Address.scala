package com.wecreatex.template.domain.address

import com.wecreatex.utils.time.Date
import com.wecreatex.utils.time.TimeUtils.parseDate
import com.wecreatex.utils.transport.Result

case class Address(
  id: String,
  street: String,
  number: Int,
  city: String,
  country: String)