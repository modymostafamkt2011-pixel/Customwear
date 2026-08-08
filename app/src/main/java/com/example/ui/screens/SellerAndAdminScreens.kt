package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.theme.*

@Composable
fun SellerDashboardScreen(
    orders: List<OrderEntity>,
    onUpdateOrderStatus: (orderId: String, newStatus: String) -> Unit,
    onNavigateToAddProduct: () -> Unit
) {
    val totalOrders = orders.size
    val pendingOrders = orders.count { it.status == "PENDING" }
    val completedOrders = orders.count { it.status == "DELIVERED" }
    val revenue = orders.sumOf { it.totalPrice }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("لوحة تحكّم البائع (Seller Dashboard) 🏬", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkNavy)
                Text("متابعة الطلبات وتحديث مراحل التصنيع", fontSize = 11.sp, color = TextGray)
            }
            Button(
                onClick = onNavigateToAddProduct,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة منتج", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("إجمالي الأرباح", "${revenue.toInt()} EGP", DeepPurple700, Modifier.weight(1f))
            StatCard("الطلبات المعلقة", pendingOrders.toString(), WarningOrange, Modifier.weight(1f))
            StatCard("المكتملة", completedOrders.toString(), SuccessGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("إدارة طلبات المصنع والورشة:", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavy)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(orders) { order ->
                SellerOrderCard(order = order, onUpdateOrderStatus = onUpdateOrderStatus)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = TextGray)
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color, modifier = Modifier.padding(top = 2.dp))
        }
    }
}

@Composable
private fun SellerOrderCard(order: OrderEntity, onUpdateOrderStatus: (orderId: String, newStatus: String) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().testTag("seller_order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.id, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${order.totalPrice.toInt()} EGP", fontWeight = FontWeight.Bold, color = DeepPurple700)
            }
            Text("العميل: ${order.customerName} (${order.city})", fontSize = 12.sp, color = TextGray)
            Text("الحالة الحالية: ${order.status}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)

            Spacer(modifier = Modifier.height(8.dp))

            Text("تحديث حالة التصنيع والطلب:", fontSize = 10.sp, color = TextGray)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val statuses = listOf("ACCEPTED", "MANUFACTURING", "READY", "SHIPPED", "DELIVERED")
                statuses.forEach { st ->
                    AssistChip(
                        onClick = { onUpdateOrderStatus(order.id, st) },
                        label = { Text(st.take(4), fontSize = 9.sp) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (order.status == st) DeepPurple700 else LightPurple100,
                            labelColor = if (order.status == st) Color.White else DeepPurple700
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun SellerAddProductScreen(
    onAddProduct: (name: String, category: String, basePrice: Double, description: String, imageUrl: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("T-Shirts") }
    var priceText by remember { mutableStateOf("250") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text("إضافة منتج جديد للمتجر ➕", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("اسم المنتج") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("seller_add_product_name")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it },
            label = { Text("السعر الأساسي بالجنيه (EGP)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("seller_add_product_price")
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("وصف المنتج والخامات المتاحة") },
            minLines = 3,
            modifier = Modifier.fillMaxWidth().testTag("seller_add_product_desc")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val price = priceText.toDoubleOrNull() ?: 250.0
                if (name.isNotBlank()) {
                    onAddProduct(name, category, price, description, imageUrl)
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier.fillMaxWidth().height(50.dp).testTag("seller_save_product_button")
        ) {
            Text("نشر المنتج في الكتالوج (Publish Product)")
        }
    }
}

@Composable
fun AdminPanelScreen(
    orders: List<OrderEntity>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text("لوحة إدارة المنصة (CustomWear Admin) 🛡️", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard("إجمالي المستخدمين", "1,240", DeepPurple700, Modifier.weight(1f))
            StatCard("البائعين المعتمدين", "42", CyanDark, Modifier.weight(1f))
            StatCard("إجمالي المعاملات", "${orders.sumOf { it.totalPrice }.toInt()} EGP", SuccessGreen, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().testTag("admin_stats_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("حالة السيرفر والمنصة:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("• قواعد البيانات: PostgreSQL / Room Active", fontSize = 12.sp, color = TextGray)
                Text("• المصادقة والأمان: Supabase Auth Ready", fontSize = 12.sp, color = TextGray)
                Text("• محرك الذكاء الاصطناعي: Gemini API Connected", fontSize = 12.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}
