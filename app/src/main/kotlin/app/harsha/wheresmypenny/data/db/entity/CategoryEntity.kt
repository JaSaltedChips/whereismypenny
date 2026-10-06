package app.harsha.wheresmypenny.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name", "transactionType"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    /** "EXPENSE" or "INCOME" */
    val transactionType: String,
    /** True for the built-in "Refund" category; prevents accidental deletion. */
    val isSystem: Boolean = false,
)
