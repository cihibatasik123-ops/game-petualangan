package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.GoldAmberPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateDark800
import com.example.ui.theme.SlateDark900
import com.example.ui.viewmodel.GameScreen
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppNavigation()
            }
        }
    }
}

@Composable
fun MainAppNavigation(
    viewModel: GameViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle back button when not on Map screen
    if (currentScreen != GameScreen.MAP && currentScreen != GameScreen.BATTLE) {
        BackHandler {
            viewModel.navigateTo(GameScreen.MAP)
        }
    }

    if (currentScreen == GameScreen.BATTLE) {
        BattleScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    containerColor = SlateDark800,
                    contentColor = GoldAmberPrimary,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == GameScreen.MAP,
                        onClick = { viewModel.navigateTo(GameScreen.MAP) },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Peta") },
                        label = { Text("Peta", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldAmberPrimary,
                            indicatorColor = GoldAmberPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_map")
                    )

                    NavigationBarItem(
                        selected = currentScreen == GameScreen.HERO,
                        onClick = { viewModel.navigateTo(GameScreen.HERO) },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Pahlawan") },
                        label = { Text("Pahlawan", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldAmberPrimary,
                            indicatorColor = GoldAmberPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_hero")
                    )

                    NavigationBarItem(
                        selected = currentScreen == GameScreen.AI_GUIDE,
                        onClick = { viewModel.navigateTo(GameScreen.AI_GUIDE) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Resi AI") },
                        label = { Text("Resi AI", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldAmberPrimary,
                            indicatorColor = GoldAmberPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_ai_guide")
                    )

                    NavigationBarItem(
                        selected = currentScreen == GameScreen.IMAGE_STUDIO,
                        onClick = { viewModel.navigateTo(GameScreen.IMAGE_STUDIO) },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Studio Gambar") },
                        label = { Text("Studio Seni", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E1300),
                            selectedTextColor = GoldAmberPrimary,
                            indicatorColor = GoldAmberPrimary,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_item_image_studio")
                    )
                }
            },
            containerColor = SlateDark900
        ) { innerPadding ->
            when (currentScreen) {
                GameScreen.MAP -> MapScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                GameScreen.HERO -> HeroScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                GameScreen.AI_GUIDE -> AiGuideScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                GameScreen.IMAGE_STUDIO -> ImageStudioScreen(viewModel = viewModel, modifier = Modifier.padding(innerPadding))
                GameScreen.BATTLE -> Unit // Handled above
            }
        }
    }
}
