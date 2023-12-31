package com.anhquan.conversate.ui.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.anhquan.conversate.constants.LoadingStatus
import com.anhquan.conversate.data.AppContainer
import com.anhquan.conversate.data.CacheData
import com.anhquan.conversate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(private val container: AppContainer): ViewModel() {
    private val _uiState = MutableStateFlow(LoadingStatus.Loading)
    val uiState: StateFlow<LoadingStatus> = _uiState.asStateFlow()

    var loaded by mutableStateOf(false)
    var user: User? by mutableStateOf(null)

    suspend fun loadUser(email: String) {
        if (!loaded) {
            _uiState.update {
                LoadingStatus.Loading
            }
            user = if (email == CacheData.user?.email) {
                CacheData.user
            } else {
                container.users.getUserFromEmail(email)
            }
            if (user != null) {
                _uiState.update {
                    LoadingStatus.Loaded
                }
            } else {
                _uiState.update {
                    LoadingStatus.Error
                }
            }
            loaded = true
        }
    }
}