package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import coil.compose.AsyncImage
import com.example.data.model.DesignEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    user: UserEntity?,
    onNavigateOrders: () -> Unit,
    onNavigateFavorites: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Text("الملف الشخصي (My Profile)", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkNavy)

        Spacer(modifier = Modifier.height(16.dp))

        // Profile User Header
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().testTag("profile_user_card")
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(LightPurple100),
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.avatar.isNullOrBlank()) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = DeepPurple700, modifier = Modifier.size(36.dp))
                    } else {
                        AsyncImage(model = user?.avatar, contentDescription = null, modifier = Modifier.fillMaxSize())
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(user?.name ?: "زائر كاستم وير", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                    Text(user?.email ?: "guest@customwear.com", fontSize = 12.sp, color = TextGray)
                    Text("الدور: ${user?.role ?: "CUSTOMER"}", fontSize = 11.sp, color = DeepPurple700, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Options List
        ProfileMenuRow("طلباتي السابقة وتتبع الشحن", Icons.Default.LocalShipping, onNavigateOrders, "profile_orders_menu")
        ProfileMenuRow("تصميماتي القابلة للحفظ", Icons.Default.Favorite, onNavigateFavorites, "profile_favorites_menu")
        ProfileMenuRow("سوق التصميمات والفنون", Icons.Default.Palette, onNavigateFavorites, "profile_marketplace_menu")

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.9f)),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("profile_logout_button")
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("تسجيل الخروج (Logout)")
        }
    }
}

@Composable
private fun ProfileMenuRow(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, testTag: String) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = DeepPurple700)
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkNavy, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun DesignMarketplaceScreen(designs: List<DesignEntity>, onApplyDesign: (DesignEntity) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        Text("سوق التصميمات (Design Marketplace) 🎨", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DarkNavy)
        Text("تصفح أعمال المصممين المحليين وطبقها فوراً على ملابسك", fontSize = 12.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(designs) { design ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        AsyncImage(
                            model = design.imageUrl,
                            contentDescription = design.name,
                            modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp))
                        )
                        Text(design.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                        Text("بواسطة ${design.designerName}", fontSize = 10.sp, color = TextGray)
                        Button(
                            onClick = { onApplyDesign(design) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
                            modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                        ) {
                            Text("طبّق على ملابسك", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
