package com.hanasmarcin.kivracoffee.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hanasmarcin.kivracoffee.model.service.CoffeeApiModel
import kotlinx.parcelize.Parcelize
import kotlin.random.Random

@Entity
@Parcelize
data class CoffeeModel(
    @PrimaryKey val uid: String,
    val fetchTimestamp: Long,
    val blendName: String,
    val origin: String,
    val country: String,
    val variety: String,
    val notes: List<String>,
    val intensifier: String,
    val flagRotation: Float = Random.nextDouble(-5.0, 5.0).toFloat()
) : Parcelable {
    companion object {
        fun fromApiModel(
            coffeeApiModel: CoffeeApiModel,
            fetchTimestamp: Long = System.currentTimeMillis()
        ): CoffeeModel = with(coffeeApiModel) {
            CoffeeModel(
                uid,
                fetchTimestamp,
                blendName,
                origin,
                origin.split(", ").last(),
                variety,
                notes.split(", "),
                intensifier
            )
        }
    }
}