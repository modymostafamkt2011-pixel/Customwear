package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole { CUSTOMER, SELLER, ADMIN }

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: String, // CUSTOMER, SELLER, ADMIN
    val avatar: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "sellers")
data class SellerEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val storeName: String,
    val description: String,
    val rating: Float,
    val location: String,
    val approved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val sellerName: String,
    val name: String,
    val description: String,
    val category: String, // T-Shirt, Polo, Shirt, Hoodie, Sweatshirt
    val basePrice: Double, // in EGP
    val imageUrl: String,
    val rating: Float = 4.8f,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fabrics")
data class FabricEntity(
    @PrimaryKey val id: String,
    val name: String, // Cotton, Pique, Linen, Polyester, Cotton Blend
    val description: String,
    val additionalPrice: Double
)

@Entity(tableName = "colors")
data class ColorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val hexCode: String,
    val additionalPrice: Double = 0.0
)

@Entity(tableName = "designs")
data class DesignEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val designerName: String,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val uses: Int = 120,
    val rating: Float = 4.9f,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "customizations")
data class CustomizationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val clothingType: String,
    val fabricName: String,
    val fabricPrice: Double,
    val fit: String, // Regular, Oversized, Slim, Relaxed
    val colorName: String,
    val colorHex: String,
    val size: String, // XS, S, M, L, XL, XXL
    val customText: String = "",
    val textColorHex: String = "#FFFFFF",
    val fontFamily: String = "Sans",
    val designPosition: String = "Front", // Front, Back, Left Chest, Right Chest, Sleeve
    val designImageUrl: String = "",
    val designPrice: Double = 0.0,
    val quantity: Int = 1,
    val totalPrice: Double,
    val previewImageUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val customizationId: String,
    val quantity: Int
)

enum class OrderStatus {
    PENDING, ACCEPTED, MANUFACTURING, READY, SHIPPED, DELIVERED, CANCELLED
}

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val customerName: String,
    val sellerId: String,
    val sellerName: String,
    val totalPrice: Double,
    val deliveryFee: Double = 40.0,
    val status: String, // PENDING, ACCEPTED, MANUFACTURING, READY, SHIPPED, DELIVERED
    val paymentMethod: String, // Cash on Delivery, Mock Online Payment
    val address: String,
    val phone: String,
    val city: String = "Cairo",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val customizationId: String,
    val productName: String,
    val summary: String,
    val quantity: Int,
    val price: Double
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val productId: String,
    val sellerId: String,
    val rating: Float,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String
)
