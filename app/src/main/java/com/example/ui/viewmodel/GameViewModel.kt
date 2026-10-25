package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.model.*
import com.example.data.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class GameScreen {
    MAP,
    BATTLE,
    HERO,
    AI_GUIDE,
    IMAGE_STUDIO
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val repository = GameRepository(application)
    val playerProfile: StateFlow<PlayerProfile> = repository.playerProfile
    val battleState: StateFlow<BattleState?> = repository.battleState
    val gameLevels: List<AdventureLevel> = repository.gameLevels
    val availableSkills: List<PlayerSkill> = repository.availableSkills

    private val _currentScreen = MutableStateFlow(GameScreen.MAP)
    val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

    // Shop items
    val shopItems = listOf(
        InventoryItem(
            id = "potion_hp_shop",
            name = "Ramuan Herbal Merapi",
            type = ItemType.POTION,
            rarity = ItemRarity.COMMON,
            description = "Memulihkan 70 HP seketika saat dalam genting.",
            hpHeal = 70,
            valueGold = 45
        ),
        InventoryItem(
            id = "potion_mp_shop",
            name = "Air Kolam Semeru",
            type = ItemType.POTION,
            rarity = ItemRarity.COMMON,
            description = "Memulihkan 45 MP untuk merapalkan jurus sakti.",
            mpHeal = 45,
            valueGold = 50
        ),
        InventoryItem(
            id = "sword_iron_shop",
            name = "Pedang Baja Tempa Tiga",
            type = ItemType.WEAPON,
            rarity = ItemRarity.RARE,
            description = "Ditempa oleh pandai besi ulung dari lereng gunung (ATK +18).",
            attackBonus = 18,
            valueGold = 180
        ),
        InventoryItem(
            id = "armor_iron_shop",
            name = "Zirah Rantai Sakti",
            type = ItemType.ARMOR,
            rarity = ItemRarity.RARE,
            description = "Baju zirah logam kuat penangkal panah dan cakar siluman (DEF +16).",
            defenseBonus = 16,
            valueGold = 210
        )
    )

    // Chatbot State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_welcome",
                sender = MessageSender.AI_GUIDE,
                text = "Salam pahlawan! Aku adalah Sang Resi Pusaka, penjaga ilmu kuno dan penuntun tapak petualanganmu di Rimba Legenda. Tanyakan padaku taktik mengalahkan monster, rahasia tiap level, atau cara membangkitkan pusaka sakti!",
                modelName = "gemini-3.8-flash"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _selectedChatModel = MutableStateFlow("gemini-3.8-flash")
    val selectedChatModel: StateFlow<String> = _selectedChatModel.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    // Image Generation State
    private val _selectedImageSize = MutableStateFlow("1K") // 1K, 2K, 4K
    val selectedImageSize: StateFlow<String> = _selectedImageSize.asStateFlow()

    private val _isImageLoading = MutableStateFlow(false)
    val isImageLoading: StateFlow<Boolean> = _isImageLoading.asStateFlow()

    private val _generatedImage = MutableStateFlow<Bitmap?>(null)
    val generatedImage: StateFlow<Bitmap?> = _generatedImage.asStateFlow()

    private val _imageError = MutableStateFlow<String?>(null)
    val imageError: StateFlow<String?> = _imageError.asStateFlow()

    private val _imageHistory = MutableStateFlow<List<GeneratedArt>>(emptyList())
    val imageHistory: StateFlow<List<GeneratedArt>> = _imageHistory.asStateFlow()

    fun navigateTo(screen: GameScreen) {
        _currentScreen.value = screen
    }

    // Combat methods
    fun startBattle(levelId: Int) {
        repository.startBattle(levelId)
        _currentScreen.value = GameScreen.BATTLE
    }

    fun attack() {
        repository.executePlayerAttack()
    }

    fun useSkill(skill: PlayerSkill) {
        repository.executePlayerSkill(skill)
    }

    fun defend() {
        repository.executePlayerDefend()
    }

    fun usePotion(item: InventoryItem) {
        repository.useBattlePotion(item)
    }

    fun exitBattle() {
        repository.endBattle()
        _currentScreen.value = GameScreen.MAP
    }

    // Hero / Equipment methods
    fun equipItem(item: InventoryItem) {
        repository.equipItem(item)
    }

    fun unequipItem(type: ItemType) {
        repository.unequipItem(type)
    }

    fun buyItem(item: InventoryItem): Boolean {
        return repository.buyShopItem(item)
    }

    // Chatbot methods
    fun setChatModel(model: String) {
        _selectedChatModel.value = model
    }

    fun sendChatMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank() || _isChatLoading.value) return

        val userMsg = ChatMessage(
            id = "user_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = trimmed
        )
        val updatedList = _chatMessages.value + userMsg
        _chatMessages.value = updatedList

        _isChatLoading.value = true
        viewModelScope.launch {
            try {
                val currentModel = _selectedChatModel.value
                val reply = GeminiApiClient.sendChatMessage(
                    history = updatedList,
                    userMessage = trimmed,
                    modelName = currentModel
                )
                val aiMsg = ChatMessage(
                    id = "ai_${System.currentTimeMillis()}",
                    sender = MessageSender.AI_GUIDE,
                    text = reply,
                    modelName = currentModel
                )
                _chatMessages.value = _chatMessages.value + aiMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    id = "ai_err_${System.currentTimeMillis()}",
                    sender = MessageSender.AI_GUIDE,
                    text = "Resi Pusaka memejamkan mata: 'Maaf pahlawan, kabut kosmik mengaburkan pandangan: ${e.localizedMessage}'"
                )
                _chatMessages.value = _chatMessages.value + errorMsg
            } finally {
                _isChatLoading.value = false
            }
        }
    }

    // Image Studio methods
    fun setImageSize(size: String) {
        _selectedImageSize.value = size
    }

    fun generateAdventureImage(prompt: String) {
        val trimmed = prompt.trim()
        if (trimmed.isBlank() || _isImageLoading.value) return

        _isImageLoading.value = true
        _imageError.value = null

        viewModelScope.launch {
            try {
                val size = _selectedImageSize.value
                val bitmap = GeminiApiClient.generateImage(
                    prompt = trimmed,
                    imageSize = size,
                    modelName = "gemini-3-pro-image-preview"
                )
                _generatedImage.value = bitmap
                val artItem = GeneratedArt(
                    id = "art_${System.currentTimeMillis()}",
                    prompt = trimmed,
                    imageSize = size
                )
                _imageHistory.value = listOf(artItem) + _imageHistory.value
            } catch (e: Exception) {
                _imageError.value = e.localizedMessage ?: "Gagal memproses gambar."
            } finally {
                _isImageLoading.value = false
            }
        }
    }
}
