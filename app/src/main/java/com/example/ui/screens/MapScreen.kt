package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdventureLevel
import com.example.ui.components.StarRatingBar
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    val levels = viewModel.gameLevels
    var selectedLevelForPreview by remember { mutableStateOf<AdventureLevel?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🗺️ Peta Petualangan",
                            fontWeight = FontWeight.Bold,
                            color = GoldAmberPrimary
                        )
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFF261D0C),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(text = "🪙", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${profile.gold} Emas",
                                fontWeight = FontWeight.Bold,
                                color = GoldAmberPrimary,
                                fontSize = 13.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateDark900)
            )
        },
        containerColor = SlateDark900
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Hero Status Bar Card
            item {
                HeroQuickStatusCard(profile = profile)
            }

            item {
                Text(
                    text = "Daftar Wilayah & Level",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
                Text(
                    text = "Taklukkan monster penjaga untuk membuka wilayah berikutnya dan mengklaim pusaka legendaris!",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }

            items(levels) { level ->
                val isUnlocked = profile.unlockedLevelIds.contains(level.id)
                val stars = profile.levelStars[level.id] ?: 0

                LevelItemCard(
                    level = level,
                    isUnlocked = isUnlocked,
                    stars = stars,
                    playerLevel = profile.level,
                    onClick = {
                        if (isUnlocked) {
                            selectedLevelForPreview = level
                        }
                    }
                )
            }
        }

        // Level Story Preview Modal Dialog
        selectedLevelForPreview?.let { level ->
            AlertDialog(
                onDismissRequest = { selectedLevelForPreview = null },
                containerColor = SlateDark800,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = level.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Level ${level.id}: ${level.name}",
                                fontWeight = FontWeight.Bold,
                                color = GoldAmberPrimary,
                                fontSize = 18.sp
                            )
                            Text(
                                text = level.regionName,
                                fontSize = 12.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = level.storyIntro,
                            fontSize = 13.sp,
                            color = TextPrimaryLight
                        )
                        Divider(color = DarkCardStroke)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Musuh Penjaga:",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                                Text(
                                    text = level.enemy.name,
                                    fontWeight = FontWeight.Bold,
                                    color = RubyRed,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${level.enemy.title} • Elemen: ${level.enemy.element}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "HP Musuh: ${level.enemy.maxHp}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = "ATK: ${level.enemy.attack} | DEF: ${level.enemy.defense}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        Surface(
                            color = DarkCardSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Text(text = "🎁 Hadiah: +${level.enemy.expReward} XP", fontSize = 12.sp, color = ArcaneCyan)
                                Text(text = "+${level.enemy.goldReward} Emas", fontSize = 12.sp, color = GoldAmberPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val lvl = selectedLevelForPreview
                            selectedLevelForPreview = null
                            if (lvl != null) {
                                viewModel.startBattle(lvl.id)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                        modifier = Modifier.testTag("start_battle_button")
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Mulai Pertarungan!", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedLevelForPreview = null }) {
                        Text(text = "Tutup", color = TextSecondaryLight)
                    }
                }
            )
        }
    }
}

@Composable
fun HeroQuickStatusCard(profile: com.example.data.model.PlayerProfile) {
    Surface(
        color = DarkCardSurface,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
        modifier = Modifier.fillMaxWidth().testTag("hero_quick_status_card")
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
                            .background(Brush.radialGradient(listOf(GoldAmberPrimary, SlateDark900))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🧝‍♂️", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = profile.name,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Level ${profile.level} • ${profile.battlesWon} Kemenangan",
                            color = GoldAmberPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        color = Color(0xFF201315),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "⚔️ ${profile.totalAttack}",
                            color = RubyRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        color = Color(0xFF0F1E2E),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "🛡️ ${profile.totalDefense}",
                            color = ArcaneCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatProgressBar(
                    label = "HP",
                    currentValue = profile.currentHp,
                    maxValue = profile.maxHp,
                    barColor = RubyRed,
                    modifier = Modifier.weight(1f)
                )
                StatProgressBar(
                    label = "MP",
                    currentValue = profile.currentMp,
                    maxValue = profile.maxMp,
                    barColor = ManaBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            StatProgressBar(
                label = "XP Menuju Level Berikutnya",
                currentValue = profile.currentXp,
                maxValue = profile.maxXp,
                barColor = EmeraldGreen
            )
        }
    }
}

@Composable
fun LevelItemCard(
    level: AdventureLevel,
    isUnlocked: Boolean,
    stars: Int,
    playerLevel: Int,
    onClick: () -> Unit
) {
    val borderColor = if (isUnlocked) {
        if (stars > 0) EmeraldGreen.copy(alpha = 0.6f) else GoldAmberPrimary.copy(alpha = 0.6f)
    } else DarkCardStroke.copy(alpha = 0.3f)

    Surface(
        color = if (isUnlocked) DarkCardSurface else SlateDark800.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_card_${level.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level badge / icon
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isUnlocked) Brush.linearGradient(listOf(Color(0xFF283548), Color(0xFF161F30)))
                        else Brush.linearGradient(listOf(Color(0xFF1A1A1A), Color(0xFF111111)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text(text = level.iconEmoji, fontSize = 28.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Terkunci",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Level ${level.id}: ${level.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isUnlocked) TextPrimaryLight else Color.Gray
                    )
                    if (isUnlocked && stars > 0) {
                        StarRatingBar(stars = stars)
                    }
                }

                Text(
                    text = "${level.regionName} • Musuh: ${level.enemy.name}",
                    fontSize = 12.sp,
                    color = if (isUnlocked) ArcaneCyan else Color.DarkGray
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = level.description,
                    fontSize = 11.sp,
                    maxLines = 2,
                    color = if (isUnlocked) TextSecondaryLight else Color.Gray.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        color = if (playerLevel >= level.recommendedLevel) Color(0xFF0F291E) else Color(0xFF2E190F),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Rekomendasi Lv. ${level.recommendedLevel}",
                            fontSize = 10.sp,
                            color = if (playerLevel >= level.recommendedLevel) EmeraldGreen else GoldAmberSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (isUnlocked) {
                        Text(
                            text = "Ketuk untuk Masuk ▶",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAmberPrimary
                        )
                    } else {
                        Text(
                            text = "Kalahkan level sebelumnya",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
