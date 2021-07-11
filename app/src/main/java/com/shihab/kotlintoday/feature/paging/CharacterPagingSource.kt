package com.shihab.kotlintoday.feature.paging

import android.net.Uri
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.shihab.kotlintoday.rest.IMyAPI

class CharacterPagingSource(val apiService: IMyAPI): PagingSource<Int, CharacterData>() {

    override fun getRefreshKey(state: PagingState<Int, CharacterData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterData> {
        return try {
            val nextPage = params.key ?: FIRST_PAGE_INDEX
            val response = apiService.getDataFromAPI(nextPage)

            var nextPageNumber: Int? = null
            if(response.info.next != null) {
                val uri = Uri.parse(response.info.next)
                val nextPageQuery = uri.getQueryParameter("page")
                nextPageNumber = nextPageQuery?.toInt()
            }
            var prevPageNumber: Int? = null
            if(response.info.prev != null) {
                val uri = Uri.parse(response.info.prev)
                val prevPageQuery = uri.getQueryParameter("page")

                prevPageNumber = prevPageQuery?.toInt()
            }

            LoadResult.Page(data = response.results,
                prevKey = prevPageNumber,
                nextKey = nextPageNumber)
        }
        catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
    companion object {
        private const val FIRST_PAGE_INDEX = 1
    }
}