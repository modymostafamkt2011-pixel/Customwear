package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.theme.*

@Composable
fun OrderTrackingScreen(
    orders: List<OrderEntity>,
    trackedOrderId: String?,
    onReorder: (orderId: String) -> Unit,
    onStartNewOrder: () -> Unit
) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TextGray, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("لا توجد طلبات سابقة حتى الآن", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onStartNewOrder) { Text("ابدأ تصميم طلبك الأول") }
            }
        }
        return
    }

    val selectedOrder = remember(orders, trackedOrderId) {
        if (trackedOrderId != null) orders.find { it.id == trackedOrderId } ?: orders.first()
        else orders.first()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text("تتبع ومتابعة التصنيع والطلب 🚚", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Order Summary Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().testTag("order_tracking_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("رقم الطلب: ${selectedOrder.id}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = DarkNavy)
                    Text("${selectedOrder.totalPrice.toInt()} EGP", fontWeight = FontWeight.Black, fontSize = 15.sp, color = DeepPurple700)
                }

                Text("المصنع المُنَفِذ: ${selectedOrder.sellerName}", fontSize = 12.sp, color = TextGray)

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text("مراحل التصنيع والتوصيل:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkNavy)

                Spacer(modifier = Modifier.height(10.dp))

                // Timeline
                val stages = listOf(
                    "PENDING" to "تم تسجيل الطلب",
                    "ACCEPTED" to "تم قبول الطلب من الورشة",
                    "MANUFACTURING" to "جاري القص والتطريز والطباعة 🧵",
                    "READY" to "تم تجهيز وسحب القطعة",
                    "SHIPPED" to "جاري الشحن للتوصيل 🚚",
                    "DELIVERED" to "تم الاستلام بنجاح 🎉"
                )

                val currentStageIndex = stages.indexOfFirst { it.first == selectedOrder.status }.let { if (it < 0) 2 else it }

                stages.forEachIndexed { index, stage ->
                    val isCompleted = index <= currentStageIndex
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isCompleted) DeepPurple700 else BorderGray),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = stage.second,
                            fontSize = 12.sp,
                            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCompleted) DarkNavy else TextGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { onReorder(selectedOrder.id) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("order_again_reorder_button")
                ) {
                    Icon(Icons.Default.Replay, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة طلب نفس التصميم (Order Again)")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("الطلبات السابقة الأخرى:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(orders) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(order.id, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(order.sellerName, fontSize = 11.sp, color = TextGray)
                        }
                        Text("${order.totalPrice.toInt()} EGP", fontWeight = FontWeight.Bold, color = DeepPurple700)
                    }
                }
            }
        }
    }
}
