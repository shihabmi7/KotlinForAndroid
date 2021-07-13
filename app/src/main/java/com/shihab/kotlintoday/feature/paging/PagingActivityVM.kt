package com.shihab.kotlintoday.feature.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.shihab.kotlintoday.rest.RetrofitClient
import kotlinx.coroutines.flow.Flow

class PagingActivityVM : ViewModel() {

    fun getListData(): Flow<PagingData<CharacterData>> {
        val pager = Pager(
            config = PagingConfig(pageSize = 20, maxSize = 200),
            pagingSourceFactory =
            { CharacterPagingSource(RetrofitClient.getAPIInterface()) }).flow.cachedIn(
            viewModelScope
        )
        return pager
    }
}