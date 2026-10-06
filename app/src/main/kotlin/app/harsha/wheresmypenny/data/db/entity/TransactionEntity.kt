package app.harsha.wheresmypenny.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["fromAccountId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["toAccountId"],
            onDelete = ForeignKey.RESTRICT,
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index("fromAccountId"),
        Index("toAccountId"),
        Index("categoryId"),
        Index("date"),
        Index("transactionType"),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    /** ISO 8601 calendar date: YYYY-MM-DD. Stored as entered; no timezone conversion. */
    val date: String,
    /** "EXPENSE", "INCOME", or "TRANSFER" */
    val transactionType: String,
    /** Always positive. Stored in minor currency units (e.g. euro cents). Never Float/Double. */
    val amountCents: Long,
    val fromAccountId: Long? = null,
    val toAccountId: Long? = null,
    val categoryId: Long? = null,
    val description: String = "",
)
