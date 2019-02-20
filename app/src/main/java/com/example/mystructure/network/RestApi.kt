package com.example.mybase.network

import com.example.mystructure.model.Loan
import retrofit2.Call
import retrofit2.http.GET

interface RestApi {
    @GET("api/v1/loan_activity/1")
    fun getLoan(): Call<List<Loan>>

}