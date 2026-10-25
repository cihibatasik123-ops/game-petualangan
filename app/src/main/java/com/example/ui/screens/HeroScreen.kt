package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.InventoryItem
import com.example.data.model.ItemRarity
import com.example.data.model.ItemType
import com.example.ui.components.RarityBadge
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeroScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.playerProfile.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Karakter & Tas, 1: Toko Pengelana
    var shopFeedbackMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "🧝‍♂️ Pahlawan & Inventaris",
                        fontWeight = FontWeight.Bold,
                        color = GoldAmberPrimary
                    )
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
                                text = "${profile.gold}",
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = SlateDark800,
                contentColor = GoldAmberPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Karakter & Perlengkapan", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Toko Pengelana", fontWeight = FontWeight.Bold) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
                ) {
                    // Profile Overview Card
                    item {
                        Surface(
                            color = DarkCardSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                            modifier = Modifier.fillMaxWidth().testTag("hero_profile_overview_card")
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(Brush.radialGradient(listOf(GoldAmberPrimary, SlateDark900))),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🧝‍♂️", fontSize = 32.sp)
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column {
                                        Text(
                                            text = profile.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = TextPrimaryLight
                                        )
                                        Text(
                                            text = "Tingkat Pahlawan: Level ${profile.level}",
                                            fontSize = 13.sp,
                                            color = GoldAmberPrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Total ${profile.battlesWon} Kemenangan Tempur",
                                            fontSize = 11.sp,
                                            color = TextSecondaryLight
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    StatSummaryBox(
                                        title = "Serangan",
                                        value = "${profile.totalAttack}",
                                        color = RubyRed,
                                        icon = "⚔️",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatSummaryBox(
                                        title = "Pertahanan",
                                        value = "${profile.totalDefense}",
                                        color = ArcaneCyan,
                                        icon = "🛡️",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatSummaryBox(
                                        title = "HP Maks",
                                        value = "${profile.maxHp}",
                                        color = EmeraldGreen,
                                        icon = "❤️",
                                        modifier = Modifier.weight(1f)
                                    )
                                    StatSummaryBox(
                                        title = "MP Maks",
                                        value = "${profile.maxMp}",
                                        color = ManaBlue,
                                        icon = "💧",
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                StatProgressBar(
                                    label = "Pengalaman (XP)",
                                    currentValue = profile.currentXp,
                                    maxValue = profile.maxXp,
                                    barColor = EmeraldGreen
                                )
                            }
                        }
                    }

                    // Equipped Items Deck
                    item {
                        Text(
                            text = "Pusaka Terpasang",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryLight
                        )
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            EquipSlotCard(
                                title = "Senjata Utama",
                                item = profile.equippedWeapon,
                                placeholder = "Belum memasang senjata",
                                onUnequip = { viewModel.unequipItem(ItemType.WEAPON) }
                            )
                            EquipSlotCard(
                                title = "Zirah Perlindungan",
                                item = profile.equippedArmor,
                                placeholder = "Belum memasang zirah pelindung",
                                onUnequip = { viewModel.unequipItem(ItemType.ARMOR) }
                            )
                            EquipSlotCard(
                                title = "Relik Bertuah",
                                item = profile.equippedRelic,
                                placeholder = "Belum memasang relik jimat",
                                onUnequip = { viewModel.unequipItem(ItemType.RELIC) }
                            )
                        }
                    }

                    // Inventory Items Deck
                    item {
                        Text(
                            text = "Isi Tas Inventaris (${profile.inventory.size} Macam)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimaryLight
                        )
                    }

                    if (profile.inventory.isEmpty()) {
                        item {
                            Surface(
                                color = DarkCardSurface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Tas inventaris masih kosong. Taklukkan level monster atau kunjungi Toko Pengelana untuk membeli perlengkapan!",
                                    color = TextSecondaryLight,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(profile.inventory) { item ->
                            val isEquipped = item.id == profile.equippedWeapon?.id ||
                                    item.id == profile.equippedArmor?.id ||
                                    item.id == profile.equippedRelic?.id

                            InventoryItemRow(
                                item = item,
                                isEquipped = isEquipped,
                                onEquip = { viewModel.equipItem(item) }
                            )
                        }
                    }
                }
            } else {
                // SHOP TAB
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 24.dp)
                ) {
                    item {
                        Surface(
                            color = Color(0xFF2A1C0E),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldAmberPrimary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "🏪 Warung Pengelana Rimba",
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAmberPrimary,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Gunakan keping emas hasil menaklukkan level untuk membeli persediaan ramuan herbal dan senjata tempa sakti!",
                                    fontSize = 12.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }

                    shopFeedbackMessage?.let { msg ->
                        item {
                            Surface(
                                color = EmeraldGreen.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = msg,
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    items(viewModel.shopItems) { shopItem ->
                        Surface(
                            color = DarkCardSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("shop_item_${shopItem.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = shopItem.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = TextPrimaryLight
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        RarityBadge(shopItem.rarity)
                                    }
                                    Text(
                                        text = shopItem.description,
                                        fontSize = 11.sp,
                                        color = TextSecondaryLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Harga: 🪙 ${shopItem.valueGold} Emas",
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAmberPrimary,
                                        fontSize = 12.sp
                                    )
                                }

                                Button(
                                    onClick = {
                                        val success = viewModel.buyItem(shopItem)
                                        shopFeedbackMessage = if (success) {
                                            "Berhasil membeli ${shopItem.name}!"
                                        } else {
                                            "Emas tidak mencukupi untuk membeli ini!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "Beli", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatSummaryBox(
    title: String,
    value: String,
    color: Color,
    icon: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = SlateDark800,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 16.sp)
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                color = color,
                fontSize = 14.sp
            )
            Text(
                text = title,
                fontSize = 9.sp,
                color = TextSecondaryLight
            )
        }
    }
}

@Composable
fun EquipSlotCard(
    title: String,
    item: InventoryItem?,
    placeholder: String,
    onUnequip: () -> Unit
) {
    Surface(
        color = DarkCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (item != null) ArcaneCyan.copy(alpha = 0.5f) else DarkCardStroke),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = ArcaneCyan,
                    fontWeight = FontWeight.SemiBold
                )
                if (item != null) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = "${item.description} • ${if (item.attackBonus > 0) "+${item.attackBonus} ATK" else "+${item.defenseBonus} DEF"}",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                } else {
                    Text(
                        text = placeholder,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (item != null) {
                OutlinedButton(
                    onClick = onUnequip,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RubyRed),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(text = "Lepas", fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun InventoryItemRow(
    item: InventoryItem,
    isEquipped: Boolean,
    onEquip: () -> Unit
) {
    Surface(
        color = DarkCardSurface,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isEquipped) ArcaneCyan.copy(alpha = 0.6f) else DarkCardStroke),
        modifier = Modifier.fillMaxWidth().testTag("inventory_item_${item.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name + if (item.quantity > 1) " (x${item.quantity})" else "",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = TextPrimaryLight
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    RarityBadge(item.rarity)
                }

                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )

                if (item.attackBonus > 0 || item.defenseBonus > 0) {
                    Text(
                        text = "Bonus: ${if (item.attackBonus > 0) "+${item.attackBonus} ATK " else ""}${if (item.defenseBonus > 0) "+${item.defenseBonus} DEF" else ""}",
                        fontSize = 11.sp,
                        color = ArcaneCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (item.type != ItemType.POTION) {
                if (isEquipped) {
                    Surface(
                        color = ArcaneCyanDark.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "✓ Terpasang",
                            color = ArcaneCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(text = "Pasang", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
