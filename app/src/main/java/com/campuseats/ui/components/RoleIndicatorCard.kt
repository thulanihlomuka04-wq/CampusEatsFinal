package com.campuseats.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.security.UserRole
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.ui.theme.CampusOrange

@Composable
fun RoleIndicatorCard(
    role: UserRole,
    modifier: Modifier = Modifier
) {
    val (bgColor, tintColor) = when (role) {
        UserRole.STUDENT -> Color(0xFFE8F5E9) to CampusGreen
        UserRole.VENDOR -> Color(0xFFFFF3E0) to CampusOrange
        UserRole.ADMIN -> Color(0xFFEDE7F6) to Color(0xFF5E35B1)
    }

    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, tintColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ROLE: ${role.name}",
                color = tintColor,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }
    }
}
