package com.laputa.common.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Author by xpl, Date on 2021/4/19.
 */
@Parcelize
data class Cat(val name: String, val age: Long):Parcelable