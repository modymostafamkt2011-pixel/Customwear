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
import com.example.data.model.CartItemEntity
import com.example.ui.theme.*

@Composable
fun CartScreen(
    cartItems: List<CartItemEntity>,
    onRemoveItem: (String) -> Unit,
    onProceedToCheckout: () -> Unit,
    onStartNewCustomization: () -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "سلة التسوق فارغة حالياً!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkNavy
                )
                Text(
                    text = "قم بتفصيل وتصميم قطعتك الأولى الآن واستمتع بملابس فريدة.",
                    fontSize = 13.sp,
                    color = TextGray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                )
                Button(
                    onClick = onStartNewCustomization,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700)
                ) {
                    Text("صمم قطعتك الآن")
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text("سلة الملابس المخصصة 🛒", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(cartItems) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().testTag("cart_item_${item.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(LightPurple100, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Checkroom, contentDescription = null, tint = DeepPurple700)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text("قطعـة ملابس مخصصة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavy)
                            Text("الكمية: ${item.quantity}", fontSize = 12.sp, color = TextGray)
                        }

                        IconButton(onClick = { onRemoveItem(item.id) }) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Remove", tint = Color.Red)
                        }
                    }
                }
            }
        }

        Button(
            onClick = onProceedToCheckout,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("cart_proceed_checkout_button")
        ) {
            Text("المتابعة لإتمام الشراء (Proceed to Checkout)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
fun CheckoutScreen(
    onPlaceOrder: (address: String, phone: String, city: String, paymentMethod: String) -> Unit
) {
    var name by remember { mutableStateOf("أحمد محمود") }
    var phone by remember { mutableStateOf("+201012345678") }
    var address by remember { mutableStateOf("15 شارع النصر، مدينة نصر") }
    var city by remember { mutableStateOf("القاهرة") }
    var paymentMethod by remember { mutableStateOf("Cash on Delivery") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text("إتمام الطلب والدفع 📦", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("الاسم الكامل") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("رقم الهاتف") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("عنوان التوصيل التفصيلي") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("checkout_address_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("المدينة / المحافظة") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("checkout_city_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("طريقة الدفع:", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = paymentMethod == "Cash on Delivery",
                onClick = { paymentMethod = "Cash on Delivery" },
                label = { Text("الدفع عند الاستلام (COD)") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = paymentMethod == "Mock Online Payment",
                onClick = { paymentMethod = "Mock Online Payment" },
                label = { Text("دفع إلكتروني تجريبي") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (address.isNotBlank() && phone.isNotBlank()) {
                    onPlaceOrder(address, phone, city, paymentMethod)
                }
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("checkout_place_order_button")
        ) {
            Text("تأكيد وتأكيد الطلب (Place Order)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
