package com.example.aapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

data class FreshWrapper(
    @Json(name="list")
    val list: List<FreshData>
)
@Entity(
    tableName = "Fresh",
    foreignKeys = arrayOf(
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = SaveItem::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("saveId")
        )
    )
)
data class FreshData(
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null,
    var saveId: Long? =null,

    @Json(name = "lclas_nm")
    var lname: String,
    @Json(name = "mlsfc_nm")
    var mname: String,
    @Json(name = "std_prdlst_nm")
    var sname: String,
    @Json(name = "std_unit_new_nm")
    var uname: String,
    @Json(name = "std_qlity_new_nm")
    var grade: String,
    @Json(name = "cpr_nm")
    var cprName: String,
    @Json(name = "sbid_pric_max")
    var maxPrice: String,
    @Json(name = "sbid_pric_min")
    var minPrice: String,
    @Json(name = "sbid_pric_avg")
    var avgPrice: String,
    @Json(name = "delng_gy")
    var tradeAmt: String
)
@Entity(tableName = "SaveItem")
data class SaveItem(
    @PrimaryKey(autoGenerate = true)
    var id: Long? =null,
    var saveTitle: String
)