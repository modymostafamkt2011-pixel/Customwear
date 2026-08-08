package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomWearTopBar(
    currentScreen: AppScreen,
    currentRole: String,
    cartItemCount: Int,
    onNavigate: (AppScreen) -> Unit,
    onSwitchRole: (String) -> Unit
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clickable { onNavigate(AppScreen.HOME) }
                    .testTag("brand_logo_button")
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepPurple700),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Checkroom,
                        contentDescription = "Logo",
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row {
                        Text(
                            text = "CUSTOM",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = DarkNavy
                        )
                        Text(
                            text = "WEAR",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = DeepPurple700
                        )
                    }
                    Text(
                        text = "لبسك على مزاجك",
                        fontSize = 10.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right Actions: Role Switcher Demo Badge & Cart / Search
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Role Selector Chip for Project Demo Presentation
                Box {
                    AssistChip(
                        onClick = { showRoleMenu = true },
                        label = {
                            Text(
                                text = when (currentRole) {
                                    "SELLER" -> "البائع"
                                    "ADMIN" -> "المدير"
                                    else -> "المشتري"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepPurple700
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (currentRole) {
                                    "SELLER" -> Icons.Default.Storefront
                                    "ADMIN" -> Icons.Default.AdminPanelSettings
                                    else -> Icons.Default.Person
                                },
                                contentDescription = "Role",
                                tint = DeepPurple700,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = LightPurple100
                        ),
                        modifier = Modifier.testTag("demo_role_chip")
                    )

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Customer (عميل)") },
                            onClick = {
                                showRoleMenu = false
                                onSwitchRole("CUSTOMER")
                                onNavigate(AppScreen.HOME)
                            },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Seller Dashboard (بائع)") },
                            onClick = {
                                showRoleMenu = false
                                onSwitchRole("SELLER")
                                onNavigate(AppScreen.SELLER_DASHBOARD)
                            },
                            leadingIcon = { Icon(Icons.Default.Storefront, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Admin Panel (مدير النظام)") },
                            onClick = {
                                showRoleMenu = false
                                onSwitchRole("ADMIN")
                                onNavigate(AppScreen.ADMIN_PANEL)
                            },
                            leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null) }
                        )
                    }
                }

                // AI Assistant Icon
                IconButton(
                    onClick = { onNavigate(AppScreen.AI_ASSISTANT) },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(LightCyan100)
                        .testTag("topbar_ai_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = CyanDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Cart Icon with Badge
                IconButton(
                    onClick = { onNavigate(AppScreen.CART) },
                    modifier = Modifier.testTag("topbar_cart_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge(
                                    containerColor = CyanDark,
                                    contentColor = Color.White
                                ) {
                                    Text(text = cartItemCount.toString(), fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ShoppingCart,
                            contentDescription = "Cart",
                            tint = DarkNavy
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomWearBottomNavBar(
    currentScreen: AppScreen,
    currentRole: String,
    onNavigate: (AppScreen) -> Unit
) {
    if (currentScreen == AppScreen.SPLASH || currentScreen == AppScreen.ONBOARDING) return

    Surface(
        color = Color.White,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            if (currentRole == "SELLER") {
                NavBarItem(
                    label = "الرئيسية",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { onNavigate(AppScreen.HOME) }
                )
                NavBarItem(
                    label = "لوحة التحكم",
                    icon = Icons.Outlined.Dashboard,
                    selectedIcon = Icons.Filled.Dashboard,
                    selected = currentScreen == AppScreen.SELLER_DASHBOARD,
                    onClick = { onNavigate(AppScreen.SELLER_DASHBOARD) }
                )
                NavBarItem(
                    label = "إضافة منتج",
                    icon = Icons.Outlined.AddBox,
                    selectedIcon = Icons.Filled.AddBox,
                    selected = currentScreen == AppScreen.SELLER_ADD_PRODUCT,
                    onClick = { onNavigate(AppScreen.SELLER_ADD_PRODUCT) }
                )
                NavBarItem(
                    label = "الملف الشخصي",
                    icon = Icons.Outlined.Person,
                    selectedIcon = Icons.Filled.Person,
                    selected = currentScreen == AppScreen.PROFILE,
                    onClick = { onNavigate(AppScreen.PROFILE) }
                )
            } else if (currentRole == "ADMIN") {
                NavBarItem(
                    label = "الرئيسية",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { onNavigate(AppScreen.HOME) }
                )
                NavBarItem(
                    label = "لوحة المدير",
                    icon = Icons.Outlined.AdminPanelSettings,
                    selectedIcon = Icons.Filled.AdminPanelSettings,
                    selected = currentScreen == AppScreen.ADMIN_PANEL,
                    onClick = { onNavigate(AppScreen.ADMIN_PANEL) }
                )
                NavBarItem(
                    label = "المتجر",
                    icon = Icons.Outlined.Storefront,
                    selectedIcon = Icons.Filled.Storefront,
                    selected = currentScreen == AppScreen.CATALOG,
                    onClick = { onNavigate(AppScreen.CATALOG) }
                )
                NavBarItem(
                    label = "الملف",
                    icon = Icons.Outlined.Person,
                    selectedIcon = Icons.Filled.Person,
                    selected = currentScreen == AppScreen.PROFILE,
                    onClick = { onNavigate(AppScreen.PROFILE) }
                )
            } else {
                // CUSTOMER NAVIGATION
                NavBarItem(
                    label = "الرئيسية",
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { onNavigate(AppScreen.HOME) },
                    testTag = "nav_home"
                )
                NavBarItem(
                    label = "اكتشف",
                    icon = Icons.Outlined.Explore,
                    selectedIcon = Icons.Filled.Explore,
                    selected = currentScreen == AppScreen.CATALOG,
                    onClick = { onNavigate(AppScreen.CATALOG) },
                    testTag = "nav_explore"
                )

                // Emphasized Custom Build Center Action Button
                Box(
                    modifier = Modifier
                        .offset(y = (-12).dp)
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DeepPurple700)
                        .border(3.dp, Color.White, CircleShape)
                        .clickable { onNavigate(AppScreen.BUILDER) }
                        .testTag("nav_create_builder_cta"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Custom Clothing",
                        tint = CyanAccent,
                        modifier = Modifier.size(32.dp)
                    )
                }

                NavBarItem(
                    label = "طلباتي",
                    icon = Icons.Outlined.LocalShipping,
                    selectedIcon = Icons.Filled.LocalShipping,
                    selected = currentScreen == AppScreen.ORDER_TRACKING,
                    onClick = { onNavigate(AppScreen.ORDER_TRACKING) },
                    testTag = "nav_orders"
                )
                NavBarItem(
                    label = "حسابي",
                    icon = Icons.Outlined.Person,
                    selectedIcon = Icons.Filled.Person,
                    selected = currentScreen == AppScreen.PROFILE,
                    onClick = { onNavigate(AppScreen.PROFILE) },
                    testTag = "nav_profile"
                )
            }
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String = ""
) {
    val activeColor = DeepPurple700
    val inactiveColor = TextGray

    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) selectedIcon else icon,
            contentDescription = label,
            tint = if (selected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) activeColor else inactiveColor
        )
    }
}
