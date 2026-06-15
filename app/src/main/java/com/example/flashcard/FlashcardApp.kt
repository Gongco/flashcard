package com.example.flashcard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.flashcard.model.Screen
import com.example.flashcard.ui.screens.AddCardScreen
import com.example.flashcard.ui.screens.DeckDetailScreen
import com.example.flashcard.ui.screens.HomeScreen
import com.example.flashcard.ui.screens.LoginScreen
import com.example.flashcard.ui.screens.RegisterScreen
import com.example.flashcard.ui.screens.ReviewScreen
import com.example.flashcard.ui.screens.StatsScreen
import com.example.flashcard.viewmodel.FlashcardViewModel

@Composable
fun FlashcardApp(viewModel: FlashcardViewModel) {
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()
    val isDark = viewModel.isDarkTheme ?: systemDark
    val decks by viewModel.decks.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val dueCards by viewModel.dueCards.collectAsState()
    val selectedDeck = decks.firstOrNull { it.id == viewModel.selectedDeckId }
    val selectedDeckCards = cards.filter { it.deckId == viewModel.selectedDeckId }
    val reviewCards = if (viewModel.reviewSelectedDeckOnly) selectedDeckCards else dueCards

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (viewModel.currentScreen) {
            Screen.LOGIN -> LoginScreen(
                message = viewModel.loginMessage,
                onLogin = viewModel::login,
                onNavigateToRegister = { viewModel.navigateTo(Screen.REGISTER) }
            )

            Screen.REGISTER -> RegisterScreen(
                message = viewModel.registerMessage,
                onRegister = viewModel::register,
                onNavigateToLogin = { viewModel.navigateTo(Screen.LOGIN) }
            )

            Screen.HOME -> HomeScreen(
                userName = viewModel.currentUserName,
                decks = decks,
                cards = cards,
                dueCards = dueCards,
                isDark = isDark,
                onToggleTheme = { viewModel.toggleTheme(systemDark) },
                onAddDeck = viewModel::addDeck,
                onUpdateDeck = viewModel::updateDeck,
                onDeleteDeck = viewModel::deleteDeck,
                onOpenDeck = viewModel::openDeck,
                onReviewClick = viewModel::startDueReview,
                onStatsClick = { viewModel.navigateTo(Screen.STATS) },
                onLogout = viewModel::logout
            )

            Screen.DECK_DETAIL -> DeckDetailScreen(
                deck = selectedDeck,
                cards = selectedDeckCards,
                onBackClick = { viewModel.navigateTo(Screen.HOME) },
                onAddCardClick = { viewModel.navigateTo(Screen.ADD_CARD) },
                onReviewClick = viewModel::startDeckReview,
                onUpdateDeck = viewModel::updateDeck,
                onDeleteDeck = viewModel::deleteDeck,
                onUpdateCard = viewModel::updateCard,
                onDeleteCard = viewModel::deleteCard
            )

            Screen.ADD_CARD -> AddCardScreen(
                onBackClick = { viewModel.navigateTo(Screen.DECK_DETAIL) },
                onSaveCard = viewModel::addCard
            )

            Screen.REVIEW -> ReviewScreen(
                cards = reviewCards,
                title = if (viewModel.reviewSelectedDeckOnly) {
                    selectedDeck?.name ?: "Ôn tập"
                } else {
                    "Ôn tập hôm nay"
                },
                onBackClick = {
                    viewModel.navigateTo(
                        if (viewModel.reviewSelectedDeckOnly) Screen.DECK_DETAIL else Screen.HOME
                    )
                },
                onStatsClick = { viewModel.navigateTo(Screen.STATS) },
                onReview = viewModel::recordReview
            )

            Screen.STATS -> StatsScreen(
                decks = decks,
                cards = cards,
                dueCards = dueCards,
                onBackClick = { viewModel.navigateTo(Screen.HOME) }
            )
        }
    }
}
