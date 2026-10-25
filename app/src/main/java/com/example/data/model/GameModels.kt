package com.example.data.model

enum class ItemRarity {
    COMMON,
    RARE,
    EPIC,
    LEGENDARY
}

enum class ItemType {
    WEAPON,
    ARMOR,
    RELIC,
    POTION
}

data class InventoryItem(
    val id: String,
    val name: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val description: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val hpHeal: Int = 0,
    val mpHeal: Int = 0,
    val valueGold: Int = 10,
    val quantity: Int = 1
)

data class PlayerSkill(
    val id: String,
    val name: String,
    val mpCost: Int,
    val damageMultiplier: Float,
    val description: String,
    val requiredLevel: Int = 1
)

data class Enemy(
    val id: String,
    val name: String,
    val title: String,
    val maxHp: Int,
    val currentHp: Int,
    val attack: Int,
    val defense: Int,
    val element: String,
    val iconDescription: String,
    val expReward: Int,
    val goldReward: Int,
    val dropItem: InventoryItem? = null
)

data class AdventureLevel(
    val id: Int,
    val name: String,
    val regionName: String,
    val description: String,
    val storyIntro: String,
    val recommendedLevel: Int,
    val enemy: Enemy,
    val iconEmoji: String = "⚔️"
)

data class PlayerProfile(
    val name: String = "Pendekar Nusantara",
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 100,
    val currentHp: Int = 120,
    val maxHp: Int = 120,
    val currentMp: Int = 50,
    val maxMp: Int = 50,
    val gold: Int = 150,
    val baseAttack: Int = 18,
    val baseDefense: Int = 10,
    val equippedWeapon: InventoryItem? = null,
    val equippedArmor: InventoryItem? = null,
    val equippedRelic: InventoryItem? = null,
    val inventory: List<InventoryItem> = emptyList(),
    val unlockedLevelIds: Set<Int> = setOf(1),
    val levelStars: Map<Int, Int> = emptyMap(),
    val battlesWon: Int = 0
) {
    val totalAttack: Int
        get() = baseAttack + (equippedWeapon?.attackBonus ?: 0) + (equippedRelic?.attackBonus ?: 0)

    val totalDefense: Int
        get() = baseDefense + (equippedArmor?.defenseBonus ?: 0) + (equippedRelic?.defenseBonus ?: 0)
}

enum class CombatLogType {
    INFO,
    PLAYER_HIT,
    PLAYER_CRIT,
    ENEMY_HIT,
    HEAL,
    SKILL,
    DEFEND,
    VICTORY,
    DEFEAT
}

data class CombatLog(
    val message: String,
    val type: CombatLogType
)

data class BattleState(
    val level: AdventureLevel,
    val playerCurrentHp: Int,
    val playerMaxHp: Int,
    val playerCurrentMp: Int,
    val playerMaxMp: Int,
    val enemyCurrentHp: Int,
    val enemyMaxHp: Int,
    val isPlayerTurn: Boolean = true,
    val isDefending: Boolean = false,
    val isBattleOver: Boolean = false,
    val isVictory: Boolean = false,
    val rewardsClaimed: Boolean = false,
    val logs: List<CombatLog> = emptyList()
)

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val modelName: String = ""
)

enum class MessageSender {
    USER,
    AI_GUIDE
}

data class GeneratedArt(
    val id: String,
    val prompt: String,
    val imageSize: String,
    val base64Data: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
