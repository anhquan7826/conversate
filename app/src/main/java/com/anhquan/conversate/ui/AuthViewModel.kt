package com.anhquan.conversate.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.anhquan.conversate.data.AppContainer
import com.anhquan.conversate.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(
    private val container: AppContainer,
    token: String,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthState.IDLE)
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(token)
            .requestEmail().build()

    fun logout(context: Context) {
        Firebase.auth.signOut()
        GoogleSignIn.getClient(context, gso).revokeAccess()
        _uiState.update { AuthState.IDLE }
    }

    fun onAuthResult(result: AuthResult) {
        container.users.setCurrentUserData(
            User(
                displayName = result.user!!.displayName.toString(),
                email = result.user!!.email.toString(),
                avatar = result.user!!.photoUrl.toString()
            )
        )
        _uiState.update { AuthState.SUCCEEDED }
    }

    fun onAuthError(exception: ApiException) {
        _uiState.update { AuthState.FAILED }
    }

    fun onRoutePopped() {
        _uiState.update { AuthState.IDLE }
    }
}

enum class AuthState {
    IDLE, SUCCEEDED, FAILED
}