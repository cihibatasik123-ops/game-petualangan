package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Psychology
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
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiGuideScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val selectedModel by viewModel.selectedChatModel.collectAsState()
    val isLoading by viewModel.isChatLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val modelOptions = listOf(
        Triple("gemini-3.8-flash", "Gemini 3.8 Flash", "Umum / Cerdas"),
        Triple("gemini-4-argon", "Gemini 4 Argon", "Tugas Kompleks"),
        Triple("gemini-3.1-flash-lite", "Gemini 3.1 Lite", "Respon Kilat")
    )

    val quickQuestions = listOf(
        "Bagaimana strategi mengalahkan Naga Badai di level 5?",
        "Jelaskan taktik menghemat Mana saat bertarung",
        "Ceritakan legenda Keris Surya Brahmana",
        "Beri teka-teki petualangan mistis nusantara"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Brush.radialGradient(listOf(GoldAmberPrimary, SlateDark900))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🧙‍♂️", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Resi Pusaka (Gemini AI)",
                                fontWeight = FontWeight.Bold,
                                color = GoldAmberPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Pemandu Spiritual & Ahli Strategi Petualangan",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
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
            // Model Selector Bar
            Surface(
                color = SlateDark800,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                    Text(
                        text = "Pilih Model Gemini:",
                        fontSize = 11.sp,
                        color = TextSecondaryLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(modelOptions) { (modelId, label, role) ->
                            val isSelected = selectedModel == modelId
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setChatModel(modelId) },
                                label = {
                                    Column {
                                        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        Text(text = role, fontSize = 9.sp, color = if (isSelected) Color(0xFF1E1300) else TextSecondaryLight)
                                    }
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldAmberPrimary,
                                    selectedLabelColor = Color(0xFF1E1300),
                                    containerColor = DarkCardSurface,
                                    labelColor = TextPrimaryLight
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (isSelected) GoldAmberPrimary else DarkCardStroke,
                                    enabled = true,
                                    selected = isSelected
                                ),
                                modifier = Modifier.testTag("model_chip_$modelId")
                            )
                        }
                    }
                }
            }

            // Quick Question Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickQuestions) { q ->
                    Surface(
                        color = DarkCardSurface,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ArcaneCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.clickable {
                            inputText = q
                            viewModel.sendChatMessage(q)
                            inputText = ""
                        }
                    ) {
                        Text(
                            text = q,
                            fontSize = 11.sp,
                            color = ArcaneCyan,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Message Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(msg = msg)
                }

                if (isLoading) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = GoldAmberPrimary,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sang Resi sedang menatap cakrawala kebatinan...",
                                fontSize = 12.sp,
                                color = GoldAmberPrimary
                            )
                        }
                    }
                }
            }

            // Chat Input Box
            Surface(
                color = SlateDark800,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Tanyakan taktik pada Sang Resi...", fontSize = 13.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_field"),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldAmberPrimary,
                            unfocusedBorderColor = DarkCardStroke,
                            focusedContainerColor = DarkCardSurface,
                            unfocusedContainerColor = DarkCardSurface,
                            focusedTextColor = TextPrimaryLight,
                            unfocusedTextColor = TextPrimaryLight
                        ),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isLoading) {
                                val textToSend = inputText
                                inputText = ""
                                viewModel.sendChatMessage(textToSend)
                            }
                        },
                        enabled = inputText.isNotBlank() && !isLoading,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (inputText.isNotBlank() && !isLoading) GoldAmberPrimary else Color.DarkGray)
                            .testTag("send_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Kirim Pesan",
                            tint = if (inputText.isNotBlank() && !isLoading) Color(0xFF1E1300) else Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isUser = msg.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(GoldAmberPrimary, SlateDark900))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧙‍♂️", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) ArcaneCyanDark.copy(alpha = 0.85f) else DarkCardSurface,
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (isUser) 14.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 14.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) ArcaneCyan else DarkCardStroke
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!isUser) {
                    Text(
                        text = "Sang Resi Pusaka",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAmberPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = msg.text,
                    fontSize = 13.sp,
                    color = TextPrimaryLight,
                    lineHeight = 18.sp
                )
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(ArcaneCyan, SlateDark900))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🧝‍♂️", fontSize = 18.sp)
            }
        }
    }
}
