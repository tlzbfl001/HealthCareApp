package kr.bodywell.health.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
   var string1: String = "",
   var int1: Int = 0,
   var unit1: String = "",
   var string2: String = "",
   var int2: Int = 0,
   var unit2: String = "",
   var string3: String = "",
   var int3: Int = 0,
   var unit3: String = "",
   var string4: String = "",
   var int4: Int = 0,
   var unit4: String = "",
   var int5: Int = 0
) : Parcelable