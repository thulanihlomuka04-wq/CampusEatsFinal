package com.campuseats.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.campuseats.data.local.entity.OrderStatus
import com.campuseats.ui.components.CampusEatsTopBar
import com.campuseats.ui.components.StatusBadge
import com.campuseats.ui.theme.CampusGreen
import com.campuseats.ui.theme.CampusOrange
import com.campuseats.utils.CurrencyFormatter
import com.campuseats.utils.DateUtils

@Composable
fun OrderStatusScreen(
    orderId: String,
    viewModel: StudentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val order by viewModel.getOrderById(orderId).collectAsState(initial = null)
    val orderItems by viewModel.getOrderItems(orderId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            CampusEatsTopBar(
                title = "Order Tracking",
                canNavigateBack = true,
                onNavigateBack = onNavigateBack
            )
        }
    ) { padding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val ord = order!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = ord.orderNumber,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Vendor: ${ord.vendorName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Placed at ${DateUtils.formatDateTime(ord.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        StatusBadge(status = ord.status)

                        Spacer(modifier = Modifier.height(20.dp))

                        // Status Progress Representation
                        if (ord.status == OrderStatus.REJECTED) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Cancel,
                                        contentDescription = "Rejected",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "ORDER REJECTED",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "The vendor was unable to accept this order. Please browse other campus food options or contact the vendor.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    }
                                }
                            }
                        } else {
                            // 5-Stage Step Indicator: PLACED -> ACCEPTED -> PREPARING -> READY -> COLLECTED
                            OrderStatusTimeline(currentStatus = ord.status)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Pickup PIN section
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "CAMPUS PICKUP PIN",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = ord.pickupPin,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 6.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Show this 4-digit code at the vendor counter",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Items list
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Items Ordered",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        orderItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.quantity}x  ${item.foodName}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = CurrencyFormatter.format(item.subtotal),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Paid:", fontWeight = FontWeight.Bold)
                            Text(
                                text = CurrencyFormatter.format(ord.totalAmount),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToHome,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Home")
                    }
                    Button(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Order History")
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusTimeline(
    currentStatus: OrderStatus,
    modifier: Modifier = Modifier
) {
    val steps = listOf(
        OrderStatus.PLACED to "Placed",
        OrderStatus.ACCEPTED to "Accepted",
        OrderStatus.PREPARING to "Preparing",
        OrderStatus.READY to "Ready",
        OrderStatus.COLLECTED to "Collected"
    )

    val currentStepIndex = steps.indexOfFirst { it.first == currentStatus }.let {
        if (it == -1) 0 else it
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, (status, label) ->
                val isCompleted = index <= currentStepIndex
                val isCurrent = index == currentStepIndex

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isCurrent -> MaterialTheme.colorScheme.primary
                                    isCompleted -> CampusGreen
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted && !isCurrent) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = label,
                        fontSize = 10.sp,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isCompleted -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center
                    )
                }

                if (index < steps.size - 1) {
                    val linePassed = index < currentStepIndex
                    Box(
                        modifier = Modifier
                            .weight(0.5f)
                            .height(3.dp)
                            .background(
                                if (linePassed) CampusGreen
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
        }
    }
}
