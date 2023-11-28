package com.makebodywell.bodywell.model

data class Drug(
   var id: Int = 0,
   var type: String = "",
   var name: String = "",
   var amount: String = "",
   var unit: String = "",
   var period: String = "",
   var startDate: String = "",
   var endDate: String = "",
   var count: Int = 0
)
