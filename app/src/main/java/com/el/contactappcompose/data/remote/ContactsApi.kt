package com.el.contactappcompose.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ContactsApi {
    @GET("api/")
    suspend fun getRandomLists(
     @Query("page") page: Int = 1,
     @Query("results") pageSize: Int = 25,
     @Query("inc") inc: String = "gender,name,picture,phone,cell,id,email"
    ) : ContactsListsDto

}