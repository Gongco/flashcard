package com.example.flashcard.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashcard.data.local.AppDatabase
import com.example.flashcard.data.repository.FlashcardRepository
import com.example.flashcard.model.Deck
import com.example.flashcard.model.Flashcard
import com.example.flashcard.model.ReviewQuality
import com.example.flashcard.model.Screen
import com.example.flashcard.model.calculateNextReview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class FlashcardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val repository = FlashcardRepository(database.userDao(), database.deckDao(), database.flashcardDao())

    // ID của user đang đăng nhập (dùng làm khóa ngoại cho Deck)
    private val _currentUserId = MutableStateFlow(0L)

    private val _currentUserName = MutableStateFlow("")
    val currentUserNameState: StateFlow<String> = _currentUserName

    val decks: StateFlow<List<Deck>> = _currentUserId.flatMapLatest { userId ->
        repository.decksByOwner(userId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val allCards: StateFlow<List<Flashcard>> = repository.cards.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val cards: StateFlow<List<Flashcard>> = allCards.combine(decks) { cardList, userDecks ->
        val userDeckIds = userDecks.map { it.id }.toSet()
        cardList.filter { it.deckId in userDeckIds }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val dueCards: StateFlow<List<Flashcard>> = cards.map { cardList ->
        val now = System.currentTimeMillis()
        cardList.filter { it.nextReviewAt <= now }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    var currentScreen by mutableStateOf(Screen.LOGIN)
        private set

    var currentUserName by mutableStateOf("")
        private set

    private var currentUserId by mutableLongStateOf(0L)

    var selectedDeckId by mutableLongStateOf(0L)
        private set

    var reviewSelectedDeckOnly by mutableStateOf(false)
        private set

    var loginMessage by mutableStateOf("")
        private set

    var registerMessage by mutableStateOf("")
        private set

    fun login(name: String, password: String) {
        if (name.isBlank() || password.isBlank()) {
            loginMessage = "Vui lòng nhập tên người dùng và mật khẩu."
            return
        }
        val trimmedName = name.trim()
        val trimmedPassword = password.trim()

        viewModelScope.launch {
            val user = repository.findUser(trimmedName)
            if (user == null) {
                loginMessage = "Tài khoản không tồn tại. Vui lòng đăng ký."
                return@launch
            }
            // So sánh mật khẩu bằng hash thay vì plain text
            if (!repository.verifyPassword(user, trimmedPassword)) {
                loginMessage = "Mật khẩu không đúng cho tài khoản này."
                return@launch
            }
            loginMessage = ""
            registerMessage = ""
            currentUserName = trimmedName
            currentUserId = user.id
            _currentUserName.value = trimmedName
            _currentUserId.value = user.id
            currentScreen = Screen.HOME
            seedStarterDeckIfNeeded()
        }
    }

    fun register(name: String, password: String, passwordConfirm: String) {
        if (name.isBlank() || password.isBlank() || passwordConfirm.isBlank()) {
            registerMessage = "Vui lòng điền đầy đủ các thông tin."
            return
        }
        val trimmedName = name.trim()
        val trimmedPassword = password.trim()
        val trimmedConfirm = passwordConfirm.trim()

        if (trimmedPassword != trimmedConfirm) {
            registerMessage = "Mật khẩu xác nhận không khớp."
            return
        }

        if (trimmedPassword.length < 4) {
            registerMessage = "Mật khẩu phải từ 4 ký tự trở lên."
            return
        }

        viewModelScope.launch {
            // register() giờ trả về userId (>0) hoặc -1 nếu tên đã tồn tại
            val userId = repository.register(trimmedName, trimmedPassword)
            if (userId > 0) {
                registerMessage = ""
                loginMessage = ""
                currentUserName = trimmedName
                currentUserId = userId
                _currentUserName.value = trimmedName
                _currentUserId.value = userId
                currentScreen = Screen.HOME
                seedStarterDeckIfNeeded()
            } else {
                registerMessage = "Tên người dùng đã tồn tại."
            }
        }
    }

    fun logout() {
        currentUserName = ""
        currentUserId = 0L
        _currentUserName.value = ""
        _currentUserId.value = 0L
        selectedDeckId = 0L
        loginMessage = ""
        registerMessage = ""
        currentScreen = Screen.LOGIN
    }

    fun navigateTo(screen: Screen) {
        loginMessage = ""
        registerMessage = ""
        currentScreen = screen
    }

    fun openDeck(deckId: Long) {
        selectedDeckId = deckId
        currentScreen = Screen.DECK_DETAIL
    }

    fun startDueReview() {
        reviewSelectedDeckOnly = false
        currentScreen = Screen.REVIEW
    }

    fun startDeckReview() {
        reviewSelectedDeckOnly = true
        currentScreen = Screen.REVIEW
    }

    fun addDeck(name: String, description: String, category: String, language: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val deckId = repository.saveDeck(
                Deck(
                    ownerId = currentUserId,
                    name = name.trim(),
                    description = description.trim(),
                    category = category.trim().ifBlank { "General" },
                    language = language.trim().ifBlank { "Any" }
                )
            )
            selectedDeckId = deckId
            currentScreen = Screen.DECK_DETAIL
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
            if (selectedDeckId == deck.id) {
                selectedDeckId = 0L
                currentScreen = Screen.HOME
            }
        }
    }

    fun updateDeck(deck: Deck, name: String, description: String, category: String, language: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.saveDeck(
                deck.copy(
                    name = name.trim(),
                    description = description.trim(),
                    category = category.trim().ifBlank { "General" },
                    language = language.trim().ifBlank { "Any" }
                )
            )
        }
    }

    fun addCard(frontText: String, backText: String, note: String) {
        val deckId = selectedDeckId
        if (deckId == 0L || frontText.isBlank() || backText.isBlank()) return
        viewModelScope.launch {
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = frontText.trim(),
                    backText = backText.trim(),
                    note = note.trim()
                )
            )
            currentScreen = Screen.DECK_DETAIL
        }
    }

    fun deleteCard(card: Flashcard) {
        viewModelScope.launch {
            repository.deleteCard(card)
        }
    }

    fun updateCard(card: Flashcard, frontText: String, backText: String, note: String) {
        if (frontText.isBlank() || backText.isBlank()) return
        viewModelScope.launch {
            repository.updateCard(
                card.copy(
                    frontText = frontText.trim(),
                    backText = backText.trim(),
                    note = note.trim(),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun recordReview(card: Flashcard, quality: ReviewQuality) {
        val now = System.currentTimeMillis()
        val updated = card.calculateNextReview(quality, now)
        viewModelScope.launch {
            repository.updateCard(updated)
        }
    }

    private fun seedStarterDeckIfNeeded() {
        viewModelScope.launch {
            val count = repository.getDeckCountByOwner(currentUserId)
            if (count > 0) return@launch
            val deckId = repository.saveDeck(
                Deck(
                    ownerId = currentUserId,
                    name = "Kotlin Basics",
                    description = "Starter cards for general study",
                    category = "Programming",
                    language = "English"
                )
            )
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = "What is a data class?",
                    backText = "A Kotlin class designed to hold data and generate common functions automatically.",
                    note = "Examples: equals, hashCode, toString, copy."
                )
            )
            repository.addCard(
                Flashcard(
                    deckId = deckId,
                    frontText = "ViewModel is used for what?",
                    backText = "It stores UI state and survives configuration changes.",
                    note = "Useful in Jetpack Compose apps."
                )
            )
        }
    }
}
