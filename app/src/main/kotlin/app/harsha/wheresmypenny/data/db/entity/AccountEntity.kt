package app.harsha.wheresmypenny.data.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "accounts",
    indices = [Index(value = ["label"], unique = true)],
)
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    /** Stored as "BANK", "CASH", or "CREDIT_CARD" */
    val accountType: String,
    /** Non-negative. For CREDIT_CARD: initial debt owed on openingDate. */
    val openingBalanceCents: Long = 0,
    /** ISO 8601 calendar date: YYYY-MM-DD */
    val openingDate: String,
    val isActive: Boolean = true,
)
