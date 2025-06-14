package com.dicoding.intermediate_satu.data.response

import com.google.gson.annotations.SerializedName

data class GetStoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem>,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
){
	fun isNullOrEmpty(): Boolean {
		return listStory.isEmpty()
	}
}

data class ListStoryItem(

	@field:SerializedName("photoUrl")
	val photoUrl: String,

	@field:SerializedName("createdAt")
	val createdAt: String,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("lon")
	val lon: Any?,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lat")
	val lat: Any?
)
