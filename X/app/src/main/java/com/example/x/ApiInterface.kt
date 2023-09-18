package com.example.x

import com.example.x.company_dataClasses.CompanyApi
import com.example.x.launches_DataClasses.LaunchesApiItem
import com.example.x.rocket_dataClasses.RocketDataApiItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {

    @GET("launches")
    fun getLaunches(): Call<List<LaunchesApiItem>>

    @GET("company")
    fun getCompany(): Call<CompanyApi>

    @GET("rockets")
    fun getRockets(): Call<List<RocketDataApiItem>>

}