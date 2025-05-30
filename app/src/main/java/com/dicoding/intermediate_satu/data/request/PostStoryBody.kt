package com.dicoding.intermediate_satu.data.request

import okhttp3.MultipartBody

data class PostStoryBody(
    val description: String,
    val photo: MultipartBody.Part,
)
