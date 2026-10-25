package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.RarityBadge
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BattleScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val battleState by viewModel.battleState.collectAsState()
    val profile by viewModel.playerProfile.collectAsState()
    val skills = viewModel.availableSkills
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showSkillSheet by remember { mutableStateOf(false) }
    var showPotionDialog by remember { mutableStateOf(false) }
    var showSurrenderConfirm by remember { mutableStateOf(false) }

    // Intercept hardware and gesture back navigation
    BackHandler {
        if (battleState?.isBattleOver == true) {
            viewModel.exitBattle()
        } else {
            showSurrenderConfirm = true
        }
    }

    // Auto-scroll combat logs to newest
    LaunchedEffect(battleState?.logs?.size) {
        battleState?.logs?.let { logs ->
            if (logs.isNotEmpty()) {
                listState.animateScrollToItem(logs.size - 1)
            }
        }
    }

    if (battleState == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SlateDark900),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Memuat medan pertempuran...", color = TextSecondaryLight)
        }
        return
    }

    val battle = battleState!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Level ${battle.level.id}: ${battle.level.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GoldAmberPrimary
                        )
                        Text(
                            text = battle.level.regionName,
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (battle.isBattleOver) {
                                viewModel.exitBattle()
                            } else {
                                showSurrenderConfirm = true
                            }
                        },
                        modifier = Modifier.testTag("battle_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali ke Peta",
                            tint = TextPrimaryLight
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BattleArenaBg)
            )
        },
        containerColor = BattleArenaBg
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ENEMY CARD
            Surface(
                color = DarkCardSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (battle.enemyCurrentHp <= 0) Color.Gray else RubyRed.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("enemy_status_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(RubyRed.copy(alpha = 0.4f), Color(0xFF1E1010)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = battle.level.iconEmoji, fontSize = 28.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = battle.level.enemy.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = RubyRed
                                )
                                Text(
                                    text = "${battle.level.enemy.title} • Elemen: ${battle.level.enemy.element}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Surface(
                            color = Color(0xFF2E1515),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ATK: ${battle.level.enemy.attack}",
                                color = RubyRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    StatProgressBar(
                        label = "HP Musuh",
                        currentValue = battle.enemyCurrentHp,
                        maxValue = battle.enemyMaxHp,
                        barColor = RubyRed,
                        tag = "enemy_hp_bar"
                    )
                }
            }

            // PLAYER CARD
            Surface(
                color = DarkCardSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, if (battle.isDefending) ArcaneCyan else DarkCardStroke),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_combat_card")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Brush.radialGradient(listOf(GoldAmberPrimary.copy(alpha = 0.5f), Color(0xFF161F30)))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "🧝‍♂️", fontSize = 24.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = profile.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = "Level ${profile.level} • ATK: ${profile.totalAttack} | DEF: ${if (battle.isDefending) profile.totalDefense * 2 else profile.totalDefense}",
                                    fontSize = 11.sp,
                                    color = if (battle.isDefending) ArcaneCyan else TextSecondaryLight,
                                    fontWeight = if (battle.isDefending) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        if (battle.isDefending) {
                            Surface(
                                color = ArcaneCyanDark,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🛡️ Bertahan",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        StatProgressBar(
                            label = "HP",
                            currentValue = battle.playerCurrentHp,
                            maxValue = battle.playerMaxHp,
                            barColor = EmeraldGreen,
                            modifier = Modifier.weight(1f),
                            tag = "player_hp_bar"
                        )
                        StatProgressBar(
                            label = "MP",
                            currentValue = battle.playerCurrentMp,
                            maxValue = battle.playerMaxMp,
                            barColor = ManaBlue,
                            modifier = Modifier.weight(1f),
                            tag = "player_mp_bar"
                        )
                    }
                }
            }

            // COMBAT LOG SCROLL
            Surface(
                color = Color(0xFF070B14),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke.copy(alpha = 0.5f)),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(battle.logs) { log ->
                        val textColor = when (log.type) {
                            CombatLogType.INFO -> TextSecondaryLight
                            CombatLogType.PLAYER_HIT -> TextPrimaryLight
                            CombatLogType.PLAYER_CRIT -> GoldAmberPrimary
                            CombatLogType.ENEMY_HIT -> RubyRed
                            CombatLogType.HEAL -> EmeraldGreen
                            CombatLogType.SKILL -> ArcaneCyan
                            CombatLogType.DEFEND -> ManaBlue
                            CombatLogType.VICTORY -> AccentGold
                            CombatLogType.DEFEAT -> RubyRed
                        }

                        Text(
                            text = log.message,
                            fontSize = 12.sp,
                            color = textColor,
                            fontWeight = if (log.type == CombatLogType.PLAYER_CRIT || log.type == CombatLogType.VICTORY) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ACTION CONTROLS
            if (!battle.isBattleOver) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.attack() },
                            enabled = battle.isPlayerTurn,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("attack_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFF261800))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Serang", color = Color(0xFF261800), fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showSkillSheet = true },
                            enabled = battle.isPlayerTurn,
                            colors = ButtonDefaults.buttonColors(containerColor = ArcaneCyanDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("skill_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Jurus Sakti", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.defend() },
                            enabled = battle.isPlayerTurn,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ArcaneCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("defend_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Bertahan (+MP)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showPotionDialog = true },
                            enabled = battle.isPlayerTurn,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldGreen),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("potion_action_button")
                        ) {
                            Icon(imageVector = Icons.Default.LocalHospital, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Tas Ramuan", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Battle Ended Action Bar
                Button(
                    onClick = { viewModel.exitBattle() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (battle.isVictory) GoldAmberPrimary else ArcaneCyan
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("finish_battle_button")
                ) {
                    Text(
                        text = if (battle.isVictory) "🎉 Kemenangan! Lanjutkan Petualangan" else "Kembali ke Peta Petualangan",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        // Skill Selection Sheet
        if (showSkillSheet) {
            AlertDialog(
                onDismissRequest = { showSkillSheet = false },
                containerColor = SlateDark800,
                title = {
                    Text(text = "Pilih Jurus Sakti", fontWeight = FontWeight.Bold, color = GoldAmberPrimary)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        skills.forEach { skill ->
                            val canUse = battle.playerCurrentMp >= skill.mpCost && profile.level >= skill.requiredLevel
                            Surface(
                                color = if (canUse) DarkCardSurface else Color(0xFF141720),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (canUse) ArcaneCyan.copy(alpha = 0.5f) else Color.DarkGray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("skill_item_${skill.id}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = skill.name,
                                                fontWeight = FontWeight.Bold,
                                                color = if (canUse) TextPrimaryLight else Color.Gray,
                                                fontSize = 14.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = ManaBlue.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "${skill.mpCost} MP",
                                                    color = ManaBlue,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = skill.description,
                                            fontSize = 11.sp,
                                            color = TextSecondaryLight
                                        )
                                        if (profile.level < skill.requiredLevel) {
                                            Text(
                                                text = "Terbuka di Level ${skill.requiredLevel}",
                                                fontSize = 10.sp,
                                                color = RubyRed
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            showSkillSheet = false
                                            viewModel.useSkill(skill)
                                        },
                                        enabled = canUse,
                                        colors = ButtonDefaults.buttonColors(containerColor = ArcaneCyanDark),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(text = "Pakai", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSkillSheet = false }) {
                        Text(text = "Batal", color = TextSecondaryLight)
                    }
                }
            )
        }

        // Potion Bag Dialog
        if (showPotionDialog) {
            val potions = profile.inventory.filter { it.type == ItemType.POTION }
            AlertDialog(
                onDismissRequest = { showPotionDialog = false },
                containerColor = SlateDark800,
                title = {
                    Text(text = "Tas Ramuan", fontWeight = FontWeight.Bold, color = EmeraldGreen)
                },
                text = {
                    if (potions.isEmpty()) {
                        Text(
                            text = "Tas ramuanmu kosong! Beli ramuan herbal di menu Pahlawan & Toko.",
                            color = TextSecondaryLight,
                            fontSize = 13.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            potions.forEach { potion ->
                                Surface(
                                    color = DarkCardSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${potion.name} (x${potion.quantity})",
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimaryLight,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = potion.description,
                                                fontSize = 11.sp,
                                                color = TextSecondaryLight
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                showPotionDialog = false
                                                viewModel.usePotion(potion)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text(text = "Teguk", fontSize = 12.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPotionDialog = false }) {
                        Text(text = "Tutup", color = TextSecondaryLight)
                    }
                }
            )
        }

        // Surrender Confirmation Dialog
        if (showSurrenderConfirm) {
            AlertDialog(
                onDismissRequest = { showSurrenderConfirm = false },
                containerColor = SlateDark800,
                title = {
                    Text(text = "Mundur dari Medan Tempur?", fontWeight = FontWeight.Bold, color = RubyRed)
                },
                text = {
                    Text(
                        text = "Jika kamu mundur sekarang, pertarungan akan dianggap kalah dan musuh belum dapat ditaklukkan.",
                        color = TextPrimaryLight
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showSurrenderConfirm = false
                            viewModel.exitBattle()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RubyRed)
                    ) {
                        Text(text = "Ya, Mundur", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSurrenderConfirm = false }) {
                        Text(text = "Lanjut Tempur", color = TextSecondaryLight)
                    }
                }
            )
        }

        // VICTORY CELEBRATION MODAL
        if (battle.isBattleOver && battle.isVictory) {
            val stars = profile.levelStars[battle.level.id] ?: 3
            AlertDialog(
                onDismissRequest = { /* Require button click */ },
                containerColor = SlateDark800,
                title = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "🏆", fontSize = 42.sp)
                        Text(
                            text = "Kemenangan Gemilang!",
                            fontWeight = FontWeight.Bold,
                            color = GoldAmberPrimary,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StarRatingBar(stars = stars, modifier = Modifier.scale(1.2f))
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Kamu telah menaklukkan ${battle.level.enemy.name} dan membebaskan ${battle.level.regionName}!",
                            fontSize = 13.sp,
                            color = TextPrimaryLight,
                            textAlign = TextAlign.Center
                        )

                        Surface(
                            color = DarkCardSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmberPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Hadiah Perolehan:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = GoldAmberPrimary
                                )
                                Text(
                                    text = "✨ +${battle.level.enemy.expReward} Pengalaman (XP)",
                                    fontSize = 13.sp,
                                    color = ArcaneCyan
                                )
                                Text(
                                    text = "🪙 +${battle.level.enemy.goldReward} Keping Emas",
                                    fontSize = 13.sp,
                                    color = GoldAmberPrimary
                                )
                                battle.level.enemy.dropItem?.let { drop ->
                                    Divider(color = DarkCardStroke, modifier = Modifier.padding(vertical = 4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🎁 Jatuhan Pusaka: ", fontSize = 12.sp, color = TextSecondaryLight)
                                        RarityBadge(drop.rarity)
                                    }
                                    Text(
                                        text = "${drop.name} (+${if (drop.attackBonus > 0) "${drop.attackBonus} ATK" else "${drop.defenseBonus} DEF"})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimaryLight
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.exitBattle() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("claim_rewards_button")
                    ) {
                        Text(text = "Klaim Hadiah & Selesai", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}
