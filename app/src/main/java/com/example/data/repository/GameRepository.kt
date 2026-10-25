package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class GameRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("adventure_game_prefs", Context.MODE_PRIVATE)

    private val _playerProfile = MutableStateFlow(loadPlayerProfile())
    val playerProfile: StateFlow<PlayerProfile> = _playerProfile.asStateFlow()

    private val _battleState = MutableStateFlow<BattleState?>(null)
    val battleState: StateFlow<BattleState?> = _battleState.asStateFlow()

    val availableSkills = listOf(
        PlayerSkill(
            id = "skill_slash",
            name = "Tebasan Kilat",
            mpCost = 15,
            damageMultiplier = 1.6f,
            description = "Tebasan cepat dengan energi petir yang menghasilkan 160% damage fisik.",
            requiredLevel = 1
        ),
        PlayerSkill(
            id = "skill_heal",
            name = "Aura Suci",
            mpCost = 20,
            damageMultiplier = 0f,
            description = "Memanggil cahaya spiritual untuk memulihkan 55 poin HP pemain.",
            requiredLevel = 2
        ),
        PlayerSkill(
            id = "skill_fire",
            name = "Hantaman Badai",
            mpCost = 30,
            damageMultiplier = 2.4f,
            description = "Menghempaskan gelombang angin dahsyat menghasilkan 240% damage.",
            requiredLevel = 3
        ),
        PlayerSkill(
            id = "skill_ultimate",
            name = "Amukan Naga Pusaka",
            mpCost = 45,
            damageMultiplier = 3.5f,
            description = "Jurus pamungkas naga sakti yang meluluhlantakkan pertahanan musuh (350% damage).",
            requiredLevel = 4
        )
    )

    val gameLevels: List<AdventureLevel> = listOf(
        AdventureLevel(
            id = 1,
            name = "Hutan Bambu Rimba",
            regionName = "Lembah Permulaan",
            description = "Hutan asri berpohon bambu lebat tempat berkumpulnya siluman kera yang mengganggu para musafir.",
            storyIntro = "Kamu melangkahkan kaki pertama ke rimba mistis. Suara gemerisik bambu menyambut kehadiranmu, lalu seekor Siluman Kera melompat dari dahan!",
            recommendedLevel = 1,
            iconEmoji = "🎋",
            enemy = Enemy(
                id = "enemy_1",
                name = "Siluman Kera Liar",
                title = "Penguasa Bambu",
                maxHp = 90,
                currentHp = 90,
                attack = 13,
                defense = 5,
                element = "Tanah",
                iconDescription = "Kera bertaring tajam dengan cakar berbisa",
                expReward = 80,
                goldReward = 70,
                dropItem = InventoryItem(
                    id = "item_sword_bamboo",
                    name = "Pedang Bambu Kuning",
                    type = ItemType.WEAPON,
                    rarity = ItemRarity.COMMON,
                    description = "Pedang ringan yang ditempa dari bambu tua bertuah.",
                    attackBonus = 9,
                    valueGold = 45
                )
            )
        ),
        AdventureLevel(
            id = 2,
            name = "Rawa Beracun Kalimaya",
            regionName = "Rawa Mistis",
            description = "Lumpur hisap dan kabut ungu beracun yang dijaga oleh predator berkepala ganda.",
            storyIntro = "Kabut tebal berbau belerang menyelimuti rawa. Dari air keruh, riak ombak besar menandakan kedatangan ular rawa bertaring tajam!",
            recommendedLevel = 2,
            iconEmoji = "🐍",
            enemy = Enemy(
                id = "enemy_2",
                name = "Ular Rawa Kalimaya",
                title = "Pemangsa Rawa",
                maxHp = 160,
                currentHp = 160,
                attack = 20,
                defense = 8,
                element = "Racun",
                iconDescription = "Ular raksasa dengan sisik berkilau racun mematikan",
                expReward = 160,
                goldReward = 130,
                dropItem = InventoryItem(
                    id = "item_armor_crocodile",
                    name = "Zirah Sisik Rawa",
                    type = ItemType.ARMOR,
                    rarity = ItemRarity.RARE,
                    description = "Zirah kokoh dari sisik predator rawa yang menahan tebasan.",
                    defenseBonus = 12,
                    valueGold = 90
                )
            )
        ),
        AdventureLevel(
            id = 3,
            name = "Reruntuhan Kuno Majapahit",
            regionName = "Candi Bertuah",
            description = "Reruntuhan candi kuno peninggalan kerajaan agung tempat artefak suci terkunci di balik pelindung gaib.",
            storyIntro = "Relief bebatuan candi menyala saat kakimu menapak altar. Patung batu raksasa tiba-tiba hidup dan mengacungkan gada batu!",
            recommendedLevel = 3,
            iconEmoji = "🏛️",
            enemy = Enemy(
                id = "enemy_3",
                name = "Penjaga Candi Bertuah",
                title = "Golem Penjaga Altar",
                maxHp = 270,
                currentHp = 270,
                attack = 29,
                defense = 16,
                element = "Batu",
                iconDescription = "Golem bebatuan andesit berukir aksara Jawa kuno berkekuatan magis",
                expReward = 280,
                goldReward = 220,
                dropItem = InventoryItem(
                    id = "item_relic_garuda",
                    name = "Jimat Garuda Sakti",
                    type = ItemType.RELIC,
                    rarity = ItemRarity.EPIC,
                    description = "Jimat berlambang burung garuda yang mengalirkan kekuatan leluhur.",
                    attackBonus = 16,
                    defenseBonus = 10,
                    valueGold = 200
                )
            )
        ),
        AdventureLevel(
            id = 4,
            name = "Kawah Lahar Merapi",
            regionName = "Gua Kristal Lava",
            description = "Gua bawah tanah dengan sungai lahar membara dan kristal api yang memancarkan hawa panas luar biasa.",
            storyIntro = "Hawa panas menyengat wajahmu. Dari dalam kubangan lahar menyala, bangkit sosok raksasa berwujud kobaran api murni!",
            recommendedLevel = 4,
            iconEmoji = "🌋",
            enemy = Enemy(
                id = "enemy_4",
                name = "Elemental Lahar Membara",
                title = "Jiwa Api Kawah",
                maxHp = 390,
                currentHp = 390,
                attack = 38,
                defense = 22,
                element = "Api",
                iconDescription = "Makhluk api magma dengan sulur api membakar",
                expReward = 420,
                goldReward = 360,
                dropItem = InventoryItem(
                    id = "item_sword_keris",
                    name = "Keris Surya Brahmana",
                    type = ItemType.WEAPON,
                    rarity = ItemRarity.EPIC,
                    description = "Keris lekuk sembilan yang ditempa dengan meteor api membara.",
                    attackBonus = 30,
                    valueGold = 350
                )
            )
        ),
        AdventureLevel(
            id = 5,
            name = "Puncak Tahta Halilintar",
            regionName = "Kuil Puncak Langit",
            description = "Puncak gunung tertinggi di atas awan, medan pertarungan terakhir melawan Naga Badai Antaboga.",
            storyIntro = "Petir menyambar langit kelam. Di puncak menara awan emas, seekor Naga Agung bersayap badai menatapmu dengan mata menyala!",
            recommendedLevel = 5,
            iconEmoji = "🐉",
            enemy = Enemy(
                id = "enemy_5",
                name = "Naga Badai Antaboga",
                title = "Dewa Penguasa Badai",
                maxHp = 560,
                currentHp = 560,
                attack = 49,
                defense = 28,
                element = "Petir & Angin",
                iconDescription = "Naga pusaka legendaris Nusantara pelindung tahta langit",
                expReward = 900,
                goldReward = 800,
                dropItem = InventoryItem(
                    id = "item_armor_dewata",
                    name = "Mahkota Pusaka Dewata",
                    type = ItemType.RELIC,
                    rarity = ItemRarity.LEGENDARY,
                    description = "Mahkota pusaka peninggalan para dewata tertinggi yang memberi anugerah tak terkalahkan.",
                    attackBonus = 38,
                    defenseBonus = 26,
                    valueGold = 1000
                )
            )
        )
    )

    // BATTLE SYSTEM IMPLEMENTATION
    fun startBattle(levelId: Int) {
        val level = gameLevels.firstOrNull { it.id == levelId } ?: return
        val currentProfile = _playerProfile.value
        val initialLogs = listOf(
            CombatLog(level.storyIntro, CombatLogType.INFO),
            CombatLog("Pertarungan dimulai! ${level.enemy.name} siap menyerang!", CombatLogType.INFO)
        )

        _battleState.value = BattleState(
            level = level,
            playerCurrentHp = currentProfile.currentHp,
            playerMaxHp = currentProfile.maxHp,
            playerCurrentMp = currentProfile.currentMp,
            playerMaxMp = currentProfile.maxMp,
            enemyCurrentHp = level.enemy.maxHp,
            enemyMaxHp = level.enemy.maxHp,
            isPlayerTurn = true,
            isDefending = false,
            isBattleOver = false,
            isVictory = false,
            rewardsClaimed = false,
            logs = initialLogs
        )
    }

    fun executePlayerAttack() {
        val current = _battleState.value ?: return
        if (!current.isPlayerTurn || current.isBattleOver) return

        val profile = _playerProfile.value
        val isCrit = Random.nextFloat() < 0.22f
        val critMultiplier = if (isCrit) 1.5f else 1.0f
        val rawDamage = (profile.totalAttack * (0.9f + Random.nextFloat() * 0.25f) * critMultiplier).toInt()
        val actualDamage = (rawDamage - current.level.enemy.defense / 2).coerceAtLeast(8)

        val newEnemyHp = (current.enemyCurrentHp - actualDamage).coerceAtLeast(0)
        val logs = current.logs.toMutableList()

        if (isCrit) {
            logs.add(CombatLog("⚡ CRITICAL HIT! Kamu menebas ${current.level.enemy.name} dengan keras seharga $actualDamage Damage!", CombatLogType.PLAYER_CRIT))
        } else {
            logs.add(CombatLog("🗡️ Kamu menyerang ${current.level.enemy.name} menghasilkan $actualDamage Damage.", CombatLogType.PLAYER_HIT))
        }

        if (newEnemyHp <= 0) {
            logs.add(CombatLog("🏆 Musuh tumbang! Kemenangan mutlak untukmu, Pahlawan!", CombatLogType.VICTORY))
            _battleState.value = current.copy(
                enemyCurrentHp = 0,
                isBattleOver = true,
                isVictory = true,
                logs = logs
            )
            onBattleVictory(current.level)
            return
        }

        // Enemy turn
        _battleState.value = current.copy(
            enemyCurrentHp = newEnemyHp,
            isPlayerTurn = false,
            isDefending = false,
            logs = logs
        )

        executeEnemyTurn()
    }

    fun executePlayerSkill(skill: PlayerSkill) {
        val current = _battleState.value ?: return
        if (!current.isPlayerTurn || current.isBattleOver) return
        if (current.playerCurrentMp < skill.mpCost) {
            val logs = current.logs.toMutableList()
            logs.add(CombatLog("⚠️ Mana tidak cukup untuk mengeluarkan ${skill.name}!", CombatLogType.INFO))
            _battleState.value = current.copy(logs = logs)
            return
        }

        val profile = _playerProfile.value
        val remainingMp = current.playerCurrentMp - skill.mpCost
        val logs = current.logs.toMutableList()

        if (skill.id == "skill_heal") {
            val healAmount = 55
            val newHp = (current.playerCurrentHp + healAmount).coerceAtMost(current.playerMaxHp)
            logs.add(CombatLog("✨ Kamu merapalkan ${skill.name}! Memulihkan $healAmount HP (Sisa MP: $remainingMp).", CombatLogType.HEAL))

            _battleState.value = current.copy(
                playerCurrentHp = newHp,
                playerCurrentMp = remainingMp,
                isPlayerTurn = false,
                isDefending = false,
                logs = logs
            )
            executeEnemyTurn()
            return
        }

        // Offensive Skill
        val isCrit = Random.nextFloat() < 0.25f
        val critMult = if (isCrit) 1.6f else 1.0f
        val rawDamage = (profile.totalAttack * skill.damageMultiplier * (0.95f + Random.nextFloat() * 0.2f) * critMult).toInt()
        val actualDamage = (rawDamage - current.level.enemy.defense / 3).coerceAtLeast(15)

        val newEnemyHp = (current.enemyCurrentHp - actualDamage).coerceAtLeast(0)
        logs.add(CombatLog("🔥 Kamu melepaskan ${skill.name}! Menghantam ${current.level.enemy.name} dengan dahsyat seharga $actualDamage Damage!", CombatLogType.SKILL))

        if (newEnemyHp <= 0) {
            logs.add(CombatLog("🏆 ${current.level.enemy.name} terlempar dan hancur lebur! Kemenangan milikmu!", CombatLogType.VICTORY))
            _battleState.value = current.copy(
                enemyCurrentHp = 0,
                playerCurrentMp = remainingMp,
                isBattleOver = true,
                isVictory = true,
                logs = logs
            )
            onBattleVictory(current.level)
            return
        }

        _battleState.value = current.copy(
            enemyCurrentHp = newEnemyHp,
            playerCurrentMp = remainingMp,
            isPlayerTurn = false,
            isDefending = false,
            logs = logs
        )

        executeEnemyTurn()
    }

    fun executePlayerDefend() {
        val current = _battleState.value ?: return
        if (!current.isPlayerTurn || current.isBattleOver) return

        val logs = current.logs.toMutableList()
        val mpRestored = 8
        val newMp = (current.playerCurrentMp + mpRestored).coerceAtMost(current.playerMaxMp)
        logs.add(CombatLog("🛡️ Kamu mengambil posisi bertahan! Pertahanan meningkat drastis & memulihkan $mpRestored MP.", CombatLogType.DEFEND))

        _battleState.value = current.copy(
            isDefending = true,
            playerCurrentMp = newMp,
            isPlayerTurn = false,
            logs = logs
        )

        executeEnemyTurn()
    }

    fun useBattlePotion(item: InventoryItem) {
        val current = _battleState.value ?: return
        if (!current.isPlayerTurn || current.isBattleOver) return

        val profile = _playerProfile.value
        val inv = profile.inventory.toMutableList()
        val idx = inv.indexOfFirst { it.id == item.id }
        if (idx == -1) return

        val currentItem = inv[idx]
        if (currentItem.quantity > 1) {
            inv[idx] = currentItem.copy(quantity = currentItem.quantity - 1)
        } else {
            inv.removeAt(idx)
        }

        val logs = current.logs.toMutableList()
        var newHp = current.playerCurrentHp
        var newMp = current.playerCurrentMp

        if (item.hpHeal > 0) {
            newHp = (newHp + item.hpHeal).coerceAtMost(current.playerMaxHp)
            logs.add(CombatLog("🧪 Menggunakan ${item.name}! Memulihkan ${item.hpHeal} HP.", CombatLogType.HEAL))
        }
        if (item.mpHeal > 0) {
            newMp = (newMp + item.mpHeal).coerceAtMost(current.playerMaxMp)
            logs.add(CombatLog("🧪 Menggunakan ${item.name}! Memulihkan ${item.mpHeal} MP.", CombatLogType.HEAL))
        }

        _playerProfile.value = profile.copy(inventory = inv)
        savePlayerProfile(_playerProfile.value)

        _battleState.value = current.copy(
            playerCurrentHp = newHp,
            playerCurrentMp = newMp,
            isPlayerTurn = false,
            isDefending = false,
            logs = logs
        )

        executeEnemyTurn()
    }

    private fun executeEnemyTurn() {
        val current = _battleState.value ?: return
        if (current.isBattleOver) return

        val profile = _playerProfile.value
        val enemy = current.level.enemy

        val defenseFactor = if (current.isDefending) profile.totalDefense * 2 else profile.totalDefense
        val enemyRawDamage = (enemy.attack * (0.85f + Random.nextFloat() * 0.35f)).toInt()
        val damageTaken = (enemyRawDamage - defenseFactor / 2).coerceAtLeast(6)

        val newPlayerHp = (current.playerCurrentHp - damageTaken).coerceAtLeast(0)
        val logs = current.logs.toMutableList()

        logs.add(CombatLog("💥 ${enemy.name} melancarkan serangan! Memberikanmu $damageTaken Damage.", CombatLogType.ENEMY_HIT))

        if (newPlayerHp <= 0) {
            logs.add(CombatLog("💀 Kamu telah kehabisan tenaga! Pertarungan berakhir dengan kekalahan.", CombatLogType.DEFEAT))
            _battleState.value = current.copy(
                playerCurrentHp = 0,
                isBattleOver = true,
                isVictory = false,
                logs = logs
            )
            // Update profile with remaining HP of at least 1 so hero is not permanently dead
            _playerProfile.value = profile.copy(currentHp = (profile.maxHp * 0.3f).toInt())
            savePlayerProfile(_playerProfile.value)
            return
        }

        _battleState.value = current.copy(
            playerCurrentHp = newPlayerHp,
            isPlayerTurn = true,
            isDefending = false,
            logs = logs
        )
    }

    private fun onBattleVictory(level: AdventureLevel) {
        val profile = _playerProfile.value
        val enemy = level.enemy

        // Calculate stars
        val currentBattle = _battleState.value
        val hpPercentage = if (currentBattle != null) {
            currentBattle.playerCurrentHp.toFloat() / currentBattle.playerMaxHp.toFloat()
        } else 0.5f

        val stars = when {
            hpPercentage >= 0.75f -> 3
            hpPercentage >= 0.35f -> 2
            else -> 1
        }

        val updatedStars = profile.levelStars.toMutableMap()
        val previousStars = updatedStars[level.id] ?: 0
        if (stars > previousStars) {
            updatedStars[level.id] = stars
        }

        // Unlock next level
        val updatedUnlocked = profile.unlockedLevelIds.toMutableSet()
        if (level.id + 1 <= gameLevels.size) {
            updatedUnlocked.add(level.id + 1)
        }

        // Rewards
        var newGold = profile.gold + enemy.goldReward
        var newXp = profile.currentXp + enemy.expReward
        var newLevel = profile.level
        var newMaxXp = profile.maxXp
        var newMaxHp = profile.maxHp
        var newMaxMp = profile.maxMp
        var newBaseAtk = profile.baseAttack
        var newBaseDef = profile.baseDefense

        // Level up check
        while (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp = (newMaxXp * 1.5f).toInt()
            newMaxHp += 25
            newMaxMp += 10
            newBaseAtk += 5
            newBaseDef += 3
        }

        // Inventory drop
        val newInventory = profile.inventory.toMutableList()
        enemy.dropItem?.let { drop ->
            val existingIdx = newInventory.indexOfFirst { it.id == drop.id }
            if (existingIdx != -1) {
                val existing = newInventory[existingIdx]
                newInventory[existingIdx] = existing.copy(quantity = existing.quantity + 1)
            } else {
                newInventory.add(drop)
            }
        }

        val updatedProfile = profile.copy(
            level = newLevel,
            currentXp = newXp,
            maxXp = newMaxXp,
            currentHp = newMaxHp,
            maxHp = newMaxHp,
            currentMp = newMaxMp,
            maxMp = newMaxMp,
            gold = newGold,
            baseAttack = newBaseAtk,
            baseDefense = newBaseDef,
            inventory = newInventory,
            unlockedLevelIds = updatedUnlocked,
            levelStars = updatedStars,
            battlesWon = profile.battlesWon + 1
        )

        _playerProfile.value = updatedProfile
        savePlayerProfile(updatedProfile)
    }

    fun endBattle() {
        _battleState.value = null
    }

    fun equipItem(item: InventoryItem) {
        val profile = _playerProfile.value
        val updated = when (item.type) {
            ItemType.WEAPON -> profile.copy(equippedWeapon = item)
            ItemType.ARMOR -> profile.copy(equippedArmor = item)
            ItemType.RELIC -> profile.copy(equippedRelic = item)
            ItemType.POTION -> profile
        }
        _playerProfile.value = updated
        savePlayerProfile(updated)
    }

    fun unequipItem(type: ItemType) {
        val profile = _playerProfile.value
        val updated = when (type) {
            ItemType.WEAPON -> profile.copy(equippedWeapon = null)
            ItemType.ARMOR -> profile.copy(equippedArmor = null)
            ItemType.RELIC -> profile.copy(equippedRelic = null)
            ItemType.POTION -> profile
        }
        _playerProfile.value = updated
        savePlayerProfile(updated)
    }

    fun buyShopItem(item: InventoryItem): Boolean {
        val profile = _playerProfile.value
        if (profile.gold < item.valueGold) return false

        val newInventory = profile.inventory.toMutableList()
        val existingIdx = newInventory.indexOfFirst { it.id == item.id }
        if (existingIdx != -1) {
            val existing = newInventory[existingIdx]
            newInventory[existingIdx] = existing.copy(quantity = existing.quantity + 1)
        } else {
            newInventory.add(item.copy(quantity = 1))
        }

        val updated = profile.copy(
            gold = profile.gold - item.valueGold,
            inventory = newInventory
        )
        _playerProfile.value = updated
        savePlayerProfile(updated)
        return true
    }

    // PERSISTENCE
    private fun loadPlayerProfile(): PlayerProfile {
        val jsonStr = prefs.getString("profile_data", null) ?: return createInitialProfile()
        return try {
            val json = JSONObject(jsonStr)
            val name = json.optString("name", "Pendekar Nusantara")
            val level = json.optInt("level", 1)
            val currentXp = json.optInt("currentXp", 0)
            val maxXp = json.optInt("maxXp", 100)
            val currentHp = json.optInt("currentHp", 120)
            val maxHp = json.optInt("maxHp", 120)
            val currentMp = json.optInt("currentMp", 50)
            val maxMp = json.optInt("maxMp", 50)
            val gold = json.optInt("gold", 150)
            val baseAttack = json.optInt("baseAttack", 18)
            val baseDefense = json.optInt("baseDefense", 10)
            val battlesWon = json.optInt("battlesWon", 0)

            val unlockedLevels = mutableSetOf<Int>()
            val unlockedArr = json.optJSONArray("unlockedLevels")
            if (unlockedArr != null) {
                for (i in 0 until unlockedArr.length()) {
                    unlockedLevels.add(unlockedArr.getInt(i))
                }
            } else {
                unlockedLevels.add(1)
            }

            val levelStars = mutableMapOf<Int, Int>()
            val starsObj = json.optJSONObject("levelStars")
            starsObj?.keys()?.forEach { key ->
                levelStars[key.toInt()] = starsObj.getInt(key)
            }

            PlayerProfile(
                name = name,
                level = level,
                currentXp = currentXp,
                maxXp = maxXp,
                currentHp = currentHp,
                maxHp = maxHp,
                currentMp = currentMp,
                maxMp = maxMp,
                gold = gold,
                baseAttack = baseAttack,
                baseDefense = baseDefense,
                unlockedLevelIds = unlockedLevels,
                levelStars = levelStars,
                battlesWon = battlesWon,
                inventory = createInitialInventory()
            )
        } catch (e: Exception) {
            createInitialProfile()
        }
    }

    private fun savePlayerProfile(profile: PlayerProfile) {
        val json = JSONObject()
        json.put("name", profile.name)
        json.put("level", profile.level)
        json.put("currentXp", profile.currentXp)
        json.put("maxXp", profile.maxXp)
        json.put("currentHp", profile.currentHp)
        json.put("maxHp", profile.maxHp)
        json.put("currentMp", profile.currentMp)
        json.put("maxMp", profile.maxMp)
        json.put("gold", profile.gold)
        json.put("baseAttack", profile.baseAttack)
        json.put("baseDefense", profile.baseDefense)
        json.put("battlesWon", profile.battlesWon)

        val unlockedArr = JSONArray()
        profile.unlockedLevelIds.forEach { unlockedArr.put(it) }
        json.put("unlockedLevels", unlockedArr)

        val starsObj = JSONObject()
        profile.levelStars.forEach { (k, v) -> starsObj.put(k.toString(), v) }
        json.put("levelStars", starsObj)

        prefs.edit().putString("profile_data", json.toString()).apply()
    }

    private fun createInitialProfile(): PlayerProfile {
        val initInventory = createInitialInventory()
        val startingWeapon = initInventory.firstOrNull { it.id == "item_starting_blade" }
        return PlayerProfile(
            name = "Pendekar Arya",
            level = 1,
            currentXp = 0,
            maxXp = 100,
            currentHp = 120,
            maxHp = 120,
            currentMp = 50,
            maxMp = 50,
            gold = 180,
            baseAttack = 18,
            baseDefense = 10,
            equippedWeapon = startingWeapon,
            inventory = initInventory,
            unlockedLevelIds = setOf(1),
            levelStars = emptyMap()
        )
    }

    private fun createInitialInventory(): List<InventoryItem> {
        return listOf(
            InventoryItem(
                id = "item_starting_blade",
                name = "Belati Baja Pengelana",
                type = ItemType.WEAPON,
                rarity = ItemRarity.COMMON,
                description = "Senjata pertama bekal berpetualang yang tajam dan handal.",
                attackBonus = 6,
                valueGold = 25,
                quantity = 1
            ),
            InventoryItem(
                id = "potion_hp_small",
                name = "Ramuan Ginseng Merah",
                type = ItemType.POTION,
                rarity = ItemRarity.COMMON,
                description = "Ramuan herbal penambah stamina yang memulihkan 45 HP.",
                hpHeal = 45,
                valueGold = 20,
                quantity = 3
            ),
            InventoryItem(
                id = "potion_mp_small",
                name = "Embun Tirta Suci",
                type = ItemType.POTION,
                rarity = ItemRarity.COMMON,
                description = "Tetesan air suci pegunungan yang memulihkan 30 MP.",
                mpHeal = 30,
                valueGold = 25,
                quantity = 2
            )
        )
    }
}
