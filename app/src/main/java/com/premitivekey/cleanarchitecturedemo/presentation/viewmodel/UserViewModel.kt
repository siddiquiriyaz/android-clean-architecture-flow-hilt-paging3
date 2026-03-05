package com.premitivekey.cleanarchitecturedemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premitivekey.cleanarchitecturedemo.domain.model.User
import com.premitivekey.cleanarchitecturedemo.domain.usecase.GetUsersUseCase
import com.premitivekey.cleanarchitecturedemo.presentation.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val getUsersUseCase: GetUsersUseCase) : ViewModel() {

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState

    // separate flag just for bottom loader visibility
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    private val accumulatedUsers = mutableListOf<User>()
    private var currentPage = 1
    private var totalPages = 1
    private var isLoading = false

    val isLastPage get() = currentPage > totalPages

    init {
        loadUsers()
    }

    fun loadUsers() {
        if (isLoading || isLastPage) return
        isLoading = true

        // show bottom loader only when it's NOT the first page
        if (accumulatedUsers.isNotEmpty()) {
            _isLoadingMore.value = true
        }

        viewModelScope.launch {
            getUsersUseCase.execute(page = currentPage)
                .catch { e ->
                    isLoading = false
                    _isLoadingMore.value = false
                    _usersState.value = UiState.Error(e.message ?: "Unknown error")
                }
                .collect { pagedUsers ->
                    accumulatedUsers.addAll(pagedUsers.users)
                    totalPages = pagedUsers.totalPages
                    currentPage++
                    isLoading = false
                    _isLoadingMore.value = false
                    _usersState.value = UiState.Success(accumulatedUsers.toList())
                }
        }
    }

    fun refresh() {
        accumulatedUsers.clear()
        currentPage = 1
        totalPages = 1
        isLoading = false
        _isLoadingMore.value = false
        _usersState.value = UiState.Loading
        loadUsers()
    }

}