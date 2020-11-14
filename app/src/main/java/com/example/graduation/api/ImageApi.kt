package com.example.graduation.api

import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImageApi {

    @Multipart
    @POST("/users/")
    fun uploadImage(@Part("user_id") userId: String, @Part image: MultipartBody.Part)
        : Single<String>
}