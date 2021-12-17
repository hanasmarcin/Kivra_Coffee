package com.hanasmarcin.kivracoffee.model.repository

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hanasmarcin.kivracoffee.R
import com.hanasmarcin.kivracoffee.utils.Flags
import com.hanasmarcin.kivracoffee.utils.jsonToClass
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.parcelize.Parcelize
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository class for getting flag images from coded image in assets
 */
@Singleton
class CountryRepository @Inject constructor(
    private val flags: Flags,
    @ApplicationContext context: Context
) {

    private val countryList: List<CountryModel> = context.jsonToClass(R.raw.country_codes)

    fun getFromName(name: String): BitmapDrawable? {
        return countryList.firstOrNull { it.name.contains(name, ignoreCase = true) }?.let {
            flags.forCountry(it.code)
        }
    }
}

@Parcelize
data class CountryModel(
    @SerializedName("name") val name: String,
    @SerializedName("dial_code") val dialCode: String,
    @SerializedName("code") val code: String
) : Parcelable