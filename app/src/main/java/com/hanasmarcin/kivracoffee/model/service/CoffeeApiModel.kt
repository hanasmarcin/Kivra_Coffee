package com.hanasmarcin.kivracoffee.model.service

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class that represents objects from API response
 */
@Parcelize
data class CoffeeApiModel(
    @SerializedName("id") val id: Long,
    @SerializedName("uid") val uid: String,
    @SerializedName("blend_name") val blendName: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("variety") val variety: String,
    @SerializedName("notes") val notes: String,
    @SerializedName("intensifier") val intensifier: String
): Parcelable