package com.el.contactappcompose.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.el.contactappcompose.data.local.ContactDatabase
import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.toContactEntity
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ContactRemoteMediator(
    private val contactDb: ContactDatabase,
    private val contactApi: ContactsApi
) : RemoteMediator<Int, ContactEntity>() {

    private val totalPageWantToFetch = 100


    private var currentPage = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ContactEntity>
    ): MediatorResult {
        return try {
            Log.d("dinesh", "loadType : ${loadType.name}")
            //finding next page
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if(lastItem == null) {
                        1
                    } else {
                        currentPage
                    }
                }
            }

            currentPage += 1

            val contactsList = contactApi.getRandomLists(
                page = loadKey,
                pageSize = state.config.pageSize
            )

            contactDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    contactDb.dao.clearAll()
                }
                val contactEntities = contactsList.results.map { it.toContactEntity() }
                contactDb.dao.upsertAll(contactEntities)
            }
            MediatorResult.Success(
                endOfPaginationReached = loadKey >= totalPageWantToFetch
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return super.initialize()
    }
}