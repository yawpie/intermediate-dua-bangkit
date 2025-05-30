package com.dicoding.intermediate_satu.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.intermediate_satu.data.preferences.SessionManager
import com.dicoding.intermediate_satu.data.response.ListStoryItem
import javax.inject.Inject

class StoryPagingSource @Inject constructor(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) :
    PagingSource<Int, ListStoryItem>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getStory(position, params.loadSize,
                "Bearer ${sessionManager.getTokenOnce()!!}")
            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isNullOrEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
//        return try {
//            // Fallback to 0 as the initial "page" (offset)
//            val position = params.key ?: 0
//
//            // Fetch the complete data
//            val allStories = apiService.getStory("Bearer ${sessionManager.getTokenOnce()!!}").listStory
//
//            // Compute the subset (or "page") to return
//            val start = position
//            val end = (position + params.loadSize).coerceAtMost(allStories.size)
//            val pageStories = allStories.subList(start, end)
//
//            // Determine next/prev keys
//            val nextKey = if (end == allStories.size) null else position + params.loadSize
//            val prevKey = if (position == 0) null else position - params.loadSize
//
//            LoadResult.Page(
//                data = pageStories,
//                prevKey = prevKey,
//                nextKey = nextKey
//            )
//        } catch (exception: Exception) {
//            LoadResult.Error(exception)
//        }
    }

}