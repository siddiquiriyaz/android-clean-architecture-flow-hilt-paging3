package com.premitivekey.cleanarchitecturedemo.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.premitivekey.cleanarchitecturedemo.domain.model.CreateUserRequest
import com.premitivekey.cleanarchitecturedemo.domain.model.User
import com.premitivekey.cleanarchitecturedemo.domain.usecase.CreateUserUseCase
import com.premitivekey.cleanarchitecturedemo.presentation.base.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUserViewModel @Inject constructor(private val createUserUseCase: CreateUserUseCase) : ViewModel() {

    private val _createUserState = MutableStateFlow<UiState<User>>(UiState.Idle)
    val createUserState: StateFlow<UiState<User>> = _createUserState

    fun createUser(firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            createUserUseCase.execute(CreateUserRequest(firstName, lastName, email))
                .onStart { _createUserState.value = UiState.Loading }
                .catch { e -> _createUserState.value = UiState.Error(e.message ?: "Unknown error") }
                .collect { user -> _createUserState.value = UiState.Success(user) }
        }
    }

    fun resetState() {
        _createUserState.value = UiState.Idle
    }

}