package com.hanasmarcin.kivracoffee.utils

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Extension function for parsing json into class using Gson library
 */
inline fun <reified T> Context.jsonToClass(@RawRes resourceId: Int): T {
    val itemType = object : TypeToken<T>() {}.type
    return Gson().fromJson(
        resources.openRawResource(resourceId).bufferedReader().use { it.readText() },
        itemType
    )
}

