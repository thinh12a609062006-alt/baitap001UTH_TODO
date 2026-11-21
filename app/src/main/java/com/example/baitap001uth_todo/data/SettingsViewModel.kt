package com.example.baitap001uth_todo.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ThemeMode { LIGHT, DARK, AUTO }
enum class AccentOption { BLUE, GREEN, ORANGE, PURPLE }

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val accent: AccentOption = AccentOption.BLUE,
    val fontSize: Float = 0.3f,
    val remind: Boolean = true,
    val sound: Boolean = true,
    val dailySummary: Boolean = false,
    val language: String = "Tiếng Việt"
)

class SettingsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    fun onThemeMode(v: ThemeMode) = _state.update { it.copy(themeMode = v) }
    fun onAccent(v: AccentOption) = _state.update { it.copy(accent = v) }
    fun onFontSize(v: Float) = _state.update { it.copy(fontSize = v) }
    fun onRemind(v: Boolean) = _state.update { it.copy(remind = v) }
    fun onSound(v: Boolean) = _state.update { it.copy(sound = v) }
    fun onDailySummary(v: Boolean) = _state.update { it.copy(dailySummary = v) }
}