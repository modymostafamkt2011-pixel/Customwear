package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DesignEntity
import com.example.data.model.ProductEntity
import com.example.data.model.SellerEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun HomeScreen(
    products: List<ProductEntity>,
    designs: List<DesignEntity>,
    sellers: List<SellerEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onStartCustomizingClick: () -> Unit,
    onToggleFavorite: (String) -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    val categories = listOf("الكل", "T-Shirts", "Polo", "Shirts", "Hoodies", "Sweatshirts")
    var activeCategory by remember { mutableStateOf("الكل") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Search Bar Section
        PaddingValues(horizontal = 16.dp, vertical = 12.dp).let {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("ابحث عن قطعة أو تصميم جديد...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                trailingIcon = {
                    IconButton(onClick = onNavigateToAiAssistant) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = CyanDark)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = DeepPurple700,
                    unfocusedBorderColor = BorderGray
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it)
                    .testTag("home_search_input")
            )
        }

        // Hero Banner CTA Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(DeepPurple700, DeepPurple900)
                    )
                )
                .clickable { onStartCustomizingClick() }
                .padding(20.dp)
                .testTag("home_hero_banner_cta")
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
                Surface(
                    color = CyanAccent.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "منصة التفصيل الأولى 🇪🇬",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "صمم القطعة اللي في دماغك.",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    lineHeight = 28.sp
                )

                Text(
                    text = "لبسك على مزاجك.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanAccent,
                    modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                )

                Button(
                    onClick = onStartCustomizingClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanDark,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("home_hero_build_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Brush,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ابدأ التصميم الآن", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            // Decorative Graphic
            Icon(
                imageVector = Icons.Default.Checkroom,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(130.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 20.dp, y = 20.dp)
            )
        }

        // Category Horizontal Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = activeCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        activeCategory = category
                        onSelectCategory(category)
                    },
                    label = { Text(category, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DeepPurple700,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        // Section Title: Featured Products
        SectionHeader(
            title = "أحدث المنتجات القابلة والتفصيل",
            actionText = "عرض الكل",
            onActionClick = onNavigateToCatalog
        )

        // Featured Products Horizontal List
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products.take(6)) { product ->
                ProductCardItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onToggleFavorite = { onToggleFavorite(product.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AI Fashion Assistant Banner CTA
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onNavigateToAiAssistant() },
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = LightCyan100)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyanDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "المساعد الذكي للموضة ✨",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = DarkNavy
                    )
                    Text(
                        text = "اكتب بالعامية اللي في خيالك وسيقوم AI باختيار القطعة واللون والخامة فوراً!",
                        fontSize = 12.sp,
                        color = TextGray
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = CyanDark
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section Title: Trending Designs
        SectionHeader(
            title = "تصميمات شائعة من الفنانين",
            actionText = "سوق التصميمات",
            onActionClick = onNavigateToCatalog
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(designs.take(5)) { design ->
                DesignCardItem(design = design, onStartCustomizingClick = onStartCustomizingClick)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Popular Sellers
        SectionHeader(
            title = "أفضل ورش ومصانع القماش المحلي",
            actionText = "",
            onActionClick = {}
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sellers.take(5)) { seller ->
                SellerCardItem(seller = seller)
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkNavy
        )
        if (actionText.isNotEmpty()) {
            Text(
                text = actionText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DeepPurple700,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}

@Composable
fun ProductCardItem(
    product: ProductEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(160.dp)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF1F3F5))
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = DeepPurple700,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DarkNavy,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.sellerName,
                    fontSize = 10.sp,
                    color = TextGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${product.basePrice.toInt()} EGP",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = DeepPurple700
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = product.rating.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DesignCardItem(design: DesignEntity, onStartCustomizingClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onStartCustomizingClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = design.imageUrl,
                contentDescription = design.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color.LightGray)
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = design.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = DarkNavy
                )
                Text(
                    text = "بواسطة ${design.designerName}",
                    fontSize = 10.sp,
                    color = TextGray
                )
                Text(
                    text = "+${design.price.toInt()} EGP للتصميم",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanDark,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun SellerCardItem(seller: SellerEntity) {
    Card(
        modifier = Modifier.width(180.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightPurple100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = DeepPurple700
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = seller.storeName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = DarkNavy,
                    maxLines = 1
                )
                Text(
                    text = seller.location,
                    fontSize = 10.sp,
                    color = TextGray,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(10.dp)
                    )
                    Text(
                        text = " ${seller.rating}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
