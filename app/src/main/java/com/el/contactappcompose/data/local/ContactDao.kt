package com.el.contactappcompose.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Upsert
    suspend fun upsertAll(lists: List<ContactEntity>)

    @Query("SELECT * FROM contactentity")
    fun pagingSource(): PagingSource<Int, ContactEntity>

    @Query("DELETE FROM contactentity")
    suspend fun clearAll()

    @Query("SELECT * FROM contactentity WHERE firstName LIKE :query OR lastName LIKE :query")
    fun search(query: String): Flow<List<ContactEntity>>
}