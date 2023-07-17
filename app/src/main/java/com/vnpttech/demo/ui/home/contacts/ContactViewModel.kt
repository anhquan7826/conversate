package com.vnpttech.demo.ui.home.contacts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.vnpttech.demo.constants.AddContactStatus
import com.vnpttech.demo.data.AppContainer
import com.vnpttech.demo.constants.LoadingStatus
import com.vnpttech.demo.data.CacheData
import com.vnpttech.demo.model.ContactRequest
import com.vnpttech.demo.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContactViewModel(private val container: AppContainer) : ViewModel() {
    private val _uiState = MutableStateFlow(ContactState(status = LoadingStatus.Loading))
    val uiState: StateFlow<ContactState> = _uiState.asStateFlow()

    private var _contacts = mutableStateListOf<User>()
    val contacts: List<User>
        get() {
            return if (filter.isEmpty()) {
                _contacts
            } else {
                _contacts.filter {
                    it.displayName.contains(
                        Regex(filter.trim(), RegexOption.IGNORE_CASE)
                    )
                }
            }
        }

    var contactRequests = mutableStateListOf<ContactRequest>()
    var pendingContactDelete by mutableStateOf<User?>(null)
    var emailField by mutableStateOf("")
    var messageField by mutableStateOf("")

    var toggleFilter by mutableStateOf(false)
    var filter by mutableStateOf("")

    suspend fun loadContacts() {
        _uiState.update {
            it.copy(
                status = LoadingStatus.Loading
            )
        }
        _contacts.clear()
        contactRequests.clear()
        _contacts.addAll(container.contacts.getContacts())
        contactRequests.addAll(container.contacts.getContactRequests().toMutableList())
        _uiState.update {
            it.copy(
                status = LoadingStatus.Loaded,
                requestAvailable = contactRequests.isNotEmpty(),
                requestSize = contactRequests.size
            )
        }
    }

    fun onFilterChange(value: String) {
        filter = value
    }

    fun onFilterToggle() {
        filter = ""
        toggleFilter = !toggleFilter
    }

    fun onContactDeletePending(contact: User) {
        pendingContactDelete = contact
    }

    suspend fun onContactDeleteAccept() {
        container.contacts.removeContact(pendingContactDelete!!.email)
        _contacts.removeIf { it.email == pendingContactDelete!!.email }
        _uiState.update {
            it.copy(deletedContactEmail = pendingContactDelete!!.email)
        }
        pendingContactDelete = null
    }

    fun onContactDeleteCancel() {
        pendingContactDelete = null
    }

    suspend fun requestOperation(request: ContactRequest, accept: Boolean) {
        contactRequests.remove(request)
        container.contacts.contactRequestOperation(request.user.email, accept)
        _uiState.update {
            it.copy(
                requestAvailable = contactRequests.isNotEmpty(), requestSize = contactRequests.size
            )
        }
    }

    suspend fun sendContactRequest() {
        if (emailField == CacheData.user?.email || container.users.getUserFromEmail(emailField) == null) {
            _uiState.update {
                it.copy(
                    addContactStatus = AddContactStatus.Invalid
                )
            }
        } else {
            container.contacts.sendContactRequest(emailField, messageField)
            _uiState.update {
                it.copy(addContactStatus = AddContactStatus.Done)
            }
        }
    }

    fun onLeavingAddContactScreen() {
        emailField = ""
        messageField = ""
        _uiState.update {
            it.copy(addContactStatus = AddContactStatus.None)
        }
    }

    fun onEmailChange(value: String) {
        emailField = value.trim()
        _uiState.update {
            it.copy(addContactStatus = AddContactStatus.None)
        }
    }

    fun onMessageChange(value: String) {
        messageField = value
    }
}