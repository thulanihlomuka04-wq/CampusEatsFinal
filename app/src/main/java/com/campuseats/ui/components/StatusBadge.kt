package com.campuseats.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.ui.theme.CampusOrange

@Composable
fun StatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PLACED -> Color(0xFFFFF3E0) to CampusOrange
        OrderStatus.ACCEPTED -> Color(0xFFE1F5FE) to Color(0xFF0288D1)
        OrderStatus.PREPARING -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
        OrderStatus.READY -> Color(0xFFE8F5E9) to CampusGreen
        OrderStatus.COLLECTED -> Color(0xFFECEFF1) to Color(0xFF455A64)
        OrderStatus.REJECTED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.getDisplayName(),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
