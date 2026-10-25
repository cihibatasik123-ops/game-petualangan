package com.example.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageStudioScreen(
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSize by viewModel.selectedImageSize.collectAsState()
    val isLoading by viewModel.isImageLoading.collectAsState()
    val generatedBitmap by viewModel.generatedImage.collectAsState()
    val errorMessage by viewModel.imageError.collectAsState()
    val history by viewModel.imageHistory.collectAsState()

    var promptInput by remember {
        mutableStateOf("Pedang Keris legendaris bercahaya petir emas dan biru di atas altar batu candi kuno, fantasy adventure 8k wallpaper")
    }

    val sizeOptions = listOf(
        Pair("1K", "1K (1024x1024)"),
        Pair("2K", "2K (2048x2048)"),
        Pair("4K", "4K (4096x4096)")
    )

    val presetPrompts = listOf(
        "Naga Antaboga bersayap petir di puncak gunung berawan emas",
        "Reruntuhan candi kuno bercahaya kristal ungu mistis di tengah hutan",
        "Pendekar Nusantara berzirah pusaka emas menghadapi golem lava",
        "Gua kristal berkilauan dengan sungai magma cair bercahaya merah"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = GoldAmberPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Studio Gambar Fantasi",
                                fontWeight = FontWeight.Bold,
                                color = GoldAmberPrimary,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Model: gemini-3-pro-image-preview",
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
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 28.dp)
        ) {
            // Affordance Section: Image Size Selector (1K, 2K, 4K)
            item {
                Surface(
                    color = DarkCardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                    modifier = Modifier.fillMaxWidth().testTag("image_size_selector_container")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Resolusi Gambar:",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(Pilih ukuran 1K, 2K, atau 4K)",
                                fontSize = 11.sp,
                                color = ArcaneCyan
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            sizeOptions.forEach { (sizeKey, label) ->
                                val isSelected = selectedSize == sizeKey
                                Surface(
                                    color = if (isSelected) GoldAmberPrimary else SlateDark800,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isSelected) GoldAmberSecondary else DarkCardStroke
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { viewModel.setImageSize(sizeKey) }
                                        .testTag("size_option_$sizeKey")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = sizeKey,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = if (isSelected) Color(0xFF1E1300) else TextPrimaryLight
                                        )
                                        Text(
                                            text = if (sizeKey == "1K") "1024 px" else if (sizeKey == "2K") "2048 px" else "4096 px",
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color(0xFF452B00) else TextSecondaryLight
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Prompt Input Card
            item {
                Surface(
                    color = DarkCardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Deskripsi Visual Adegan / Monster:",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = promptInput,
                            onValueChange = { promptInput = it },
                            placeholder = { Text("Tuliskan gambaran pemandangan petualanganmu...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(95.dp)
                                .testTag("image_prompt_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldAmberPrimary,
                                unfocusedBorderColor = DarkCardStroke,
                                focusedContainerColor = SlateDark800,
                                unfocusedContainerColor = SlateDark800,
                                focusedTextColor = TextPrimaryLight,
                                unfocusedTextColor = TextPrimaryLight
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Preset Fantasi Cepat:",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(presetPrompts) { preset ->
                                Surface(
                                    color = SlateDark800,
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                                    modifier = Modifier.clickable { promptInput = preset }
                                ) {
                                    Text(
                                        text = preset.take(30) + "...",
                                        fontSize = 10.sp,
                                        color = ArcaneCyan,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = { viewModel.generateAdventureImage(promptInput) },
                            enabled = promptInput.isNotBlank() && !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldAmberPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("generate_image_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.Black,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Menghasilkan Gambar $selectedSize (30-60s)...",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold
                                )
                            } else {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Hasilkan Gambar ($selectedSize)",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Error Display
            errorMessage?.let { error ->
                item {
                    Surface(
                        color = Color(0xFF2E1215),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RubyRed.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth().testTag("image_error_card")
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RubyRed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = error,
                                color = RubyRed,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Image Preview Result
            item {
                Text(
                    text = "Hasil Visual Gambar:",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight,
                    fontSize = 15.sp
                )
            }

            item {
                Surface(
                    color = DarkCardSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 280.dp)
                        .testTag("image_result_card")
                ) {
                    if (generatedBitmap != null) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Image(
                                bitmap = generatedBitmap!!.asImageBitmap(),
                                contentDescription = "Hasil Gambar Petualangan",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = GoldAmberPrimary.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Resolusi: $selectedSize",
                                        color = GoldAmberPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = "Model: gemini-3-pro-image-preview",
                                    fontSize = 10.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    } else if (isLoading) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(color = GoldAmberPrimary)
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Sedang merender ilustrasi $selectedSize...",
                                fontWeight = FontWeight.Bold,
                                color = GoldAmberPrimary,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Menggunakan Gemini 3 Pro Image (memerlukan 30-60 detik)",
                                color = TextSecondaryLight,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = DarkCardStroke,
                                modifier = Modifier.size(54.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Pilih ukuran (1K, 2K, 4K) dan tekan tombol untuk menghasilkan ilustrasi petualangan!",
                                color = TextSecondaryLight,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            // History List
            if (history.isNotEmpty()) {
                item {
                    Text(
                        text = "Riwayat Pembuatan Gambar (${history.size})",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        fontSize = 14.sp
                    )
                }

                items(history) { item ->
                    Surface(
                        color = DarkCardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardStroke),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = ArcaneCyanDark.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.imageSize,
                                    color = ArcaneCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.prompt,
                                fontSize = 11.sp,
                                color = TextPrimaryLight,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
