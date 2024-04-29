package com.el.contactappcompose.di

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.el.contactappcompose.data.local.ContactDatabase
import com.el.contactappcompose.data.local.ContactEntity
import com.el.contactappcompose.data.remote.ContactRemoteMediator
import com.el.contactappcompose.data.remote.ContactsApi
import com.el.contactappcompose.data.repo.LocalContactRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContactsDatabase(@ApplicationContext context: Context): ContactDatabase {
        return Room.databaseBuilder(context, ContactDatabase::class.java, "contacts.db").build()
    }

    @Provides
    @Singleton
    fun provideLocalContactsRepo(@ApplicationContext context: Context): LocalContactRepo {
        return LocalContactRepo(context)
    }

    // https://randomuser.me/api/?page=2&results=25&inc=gender,name,picture,phone,cell,id,email
    const val BASE_URL = "https://randomuser.me"

    @Provides
    @Singleton
    fun provideContactsApi(): ContactsApi {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(httpClient)
            .build()
            .create()
    }

    @OptIn(ExperimentalPagingApi::class)
    @Provides
    @Singleton
    fun provideContactPager(
        contactDatabase: ContactDatabase,
        contactsApi: ContactsApi
    ): Pager<Int, ContactEntity> {
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                initialLoadSize = 25,
                enablePlaceholders = false
            ),
            remoteMediator = ContactRemoteMediator(
                contactDb = contactDatabase,
                contactApi = contactsApi
            ),
            pagingSourceFactory = {
                contactDatabase.dao.pagingSource()
            }
        )
    }

}