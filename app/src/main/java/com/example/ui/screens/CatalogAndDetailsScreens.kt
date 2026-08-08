package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FabricEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.*

@Composable
fun CatalogScreen(
    products: List<ProductEntity>,
    fabrics: List<FabricEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onToggleFavorite: (String) -> Unit
) {
    var sortBy by remember { mutableStateOf("Popularity") }
    var selectedFabric by remember { mutableStateOf("All") }

    val filteredProducts = remember(products, searchQuery, selectedCategory, selectedFabric, sortBy) {
        products.filter { p ->
            val matchSearch = searchQuery.isBlank() || p.name.contains(searchQuery, ignoreCase = true) || p.category.contains(searchQuery, ignoreCase = true)
            val matchCategory = selectedCategory == "All" || selectedCategory == "الكل" || p.category.contains(selectedCategory, ignoreCase = true)
            matchSearch && matchCategory
        }.sortedBy {
            when (sortBy) {
                "Price Low-High" -> it.basePrice
                "Price High-Low" -> -it.basePrice
                else -> -it.rating.toDouble()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(bottom = 80.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "كتالوج الملابس والقماش 🧵",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = DarkNavy
            )

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("ابحث بالاسم أو الفئة...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .testTag("catalog_search_input")
            )
        }

        // Filters Horizontal Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            val categories = listOf("الكل", "T-Shirts", "Polo", "Shirts", "Hoodies", "Sweatshirts")
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { onSelectCategory(cat) },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }

        // Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredProducts) { product ->
                ProductCardItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onToggleFavorite = { onToggleFavorite(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductDetailsScreen(
    product: ProductEntity?,
    fabrics: List<FabricEntity>,
    onStartCustomizing: (ProductEntity) -> Unit,
    onBackClick: () -> Unit
) {
    if (product == null) return

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .verticalScroll(scrollState)
            .padding(bottom = 90.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.White)
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkNavy
                    )
                    Text(
                        text = "يبدأ من ${product.basePrice.toInt()} EGP",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = DeepPurple700
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = product.sellerName,
                        fontSize = 13.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = " ${product.rating} (42 تقييم)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavy
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Text(
                    text = "الوصف والمواصفات",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkNavy
                )
                Text(
                    text = product.description,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                Text(
                    text = "الخامات المتاحة للتفصيل:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = DarkNavy
                )

                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    fabrics.take(4).forEach { fab ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• ${fab.name}", fontSize = 13.sp, color = DarkNavy)
                            Text(
                                text = if (fab.additionalPrice > 0) "+${fab.additionalPrice.toInt()} EGP" else "أساسي",
                                fontSize = 12.sp,
                                color = DeepPurple700,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onStartCustomizing(product) },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("details_start_customize_button")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "خصص وتفصيل هذه القطعة (Customize)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
