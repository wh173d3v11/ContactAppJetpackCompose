package com.el.contactappcompose.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ContactDao {

    @Upsert
    suspend fun upsertAll(lists: List<ContactEntity>)

    @Query("SELECT * FROM contactentity")
    fun pagingSource(): PagingSource<Int, ContactEntity>

    @Query("DELETE FROM contactentity")
    suspend fun clearAll()

}