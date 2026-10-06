package app.harsha.wheresmypenny.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import app.harsha.wheresmypenny.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY transactionType, name")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE transactionType = :type ORDER BY name")
    fun observeByType(type: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert
    suspend fun insert(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    /** Delete only if not referenced by any transaction and not a system category. */
    @Query("DELETE FROM categories WHERE id = :id AND isSystem = 0")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM transactions WHERE categoryId = :id")
    suspend fun transactionCount(id: Long): Int
}
