package com.dicoding.intermediate_satu.data.network

import com.dicoding.intermediate_satu.data.request.LoginBody
import com.dicoding.intermediate_satu.data.request.RegisterBody
import com.dicoding.intermediate_satu.data.response.GetStoryResponse
import com.dicoding.intermediate_satu.data.response.LoginResponse
import com.dicoding.intermediate_satu.data.response.PostStoryResponse
import com.dicoding.intermediate_satu.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("register")
    suspend fun register(
        @Body registerBody: RegisterBody
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body loginBody: LoginBody
    ): LoginResponse

    @Multipart
    @POST("stories")
    suspend fun postStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody?,
        @Part("lon") lon: RequestBody?
    ): PostStoryResponse

    @GET("stories")
    suspend fun getStory(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Header("Authorization") token: String
    ): GetStoryResponse

    @GET("stories")
    suspend fun getStoryWithLocation(
        @Query("location") location: Int = 1,
        @Header("Authorization") token: String
    ): GetStoryResponse

}