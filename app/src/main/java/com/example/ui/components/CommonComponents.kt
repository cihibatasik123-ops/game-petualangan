package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ItemRarity
import com.example.ui.theme.*

@Composable
fun StatProgressBar(
    label: String,
    currentValue: Int,
    maxValue: Int,
    barColor: Color,
    modifier: Modifier = Modifier,
    tag: String = "stat_bar"
) {
    val progress = if (maxValue > 0) (currentValue.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progressAnim")

    Column(modifier = modifier.testTag(tag)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondaryLight
            )
            Text(
                text = "$currentValue / $maxValue",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimaryLight
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFF232D3F))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(5.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
fun StarRatingBar(
    stars: Int,
    maxStars: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..maxStars) {
            if (i <= stars) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Bintang $i",
                    tint = AccentGold,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Bintang Kosong",
                    tint = Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun RarityBadge(rarity: ItemRarity) {
    val (bgColor, textColor) = when (rarity) {
        ItemRarity.COMMON -> Color(0xFF64748B) to Color.White
        ItemRarity.RARE -> ArcaneCyanDark to Color.White
        ItemRarity.EPIC -> Color(0xFF8B5CF6) to Color.White
        ItemRarity.LEGENDARY -> GoldAmberPrimary to Color(0xFF261900)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = rarity.name,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
