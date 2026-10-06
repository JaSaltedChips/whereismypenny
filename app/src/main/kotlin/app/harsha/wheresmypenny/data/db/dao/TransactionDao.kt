package app.harsha.wheresmypenny.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import app.harsha.wheresmypenny.data.db.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("""
        SELECT * FROM transactions
        ORDER BY date DESC, id DESC
        LIMIT :limit OFFSET :offset
    """)
    fun observePage(limit: Int = 100, offset: Int = 0): Flow<List<TransactionEntity>>

    @Query("""
        SELECT * FROM transactions
        WHERE (:dateFrom IS NULL OR date >= :dateFrom)
          AND (:dateTo   IS NULL OR date <= :dateTo)
          AND (:type     IS NULL OR transactionType = :type)
          AND (:accountId IS NULL OR fromAccountId = :accountId OR toAccountId = :accountId)
        ORDER BY date DESC, id DESC
    """)
    fun observeFiltered(
        dateFrom: String? = null,
        dateTo: String? = null,
        type: String? = null,
        accountId: Long? = null,
    ): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    /** All transactions for balance calculation — no pagination. */
    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<TransactionEntity>

    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun delete(id: Long)

    /** Duplicate check: same date, type, amount, and accounts. */
    @Query("""
        SELECT id FROM transactions
        WHERE date = :date
          AND transactionType = :type
          AND amountCents = :amountCents
          AND (fromAccountId IS :fromId OR (fromAccountId IS NULL AND :fromId IS NULL))
          AND (toAccountId   IS :toId   OR (toAccountId   IS NULL AND :toId   IS NULL))
    """)
    suspend fun findDuplicates(
        date: String,
        type: String,
        amountCents: Long,
        fromId: Long?,
        toId: Long?,
    ): List<Long>
}
