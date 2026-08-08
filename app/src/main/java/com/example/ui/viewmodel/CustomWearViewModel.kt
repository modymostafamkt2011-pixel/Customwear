package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AiAssistantService
import com.example.data.api.AiCustomizationRecommendation
import com.example.data.model.*
import com.example.data.repository.CustomWearRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    LOGIN,
    REGISTER,
    HOME,
    CATALOG,
    PRODUCT_DETAILS,
    BUILDER,
    CART,
    CHECKOUT,
    ORDER_TRACKING,
    PROFILE,
    FAVORITES,
    DESIGN_MARKETPLACE,
    AI_ASSISTANT,
    SELLER_DASHBOARD,
    SELLER_ADD_PRODUCT,
    ADMIN_PANEL
}

// Active Customization State inside the Builder
data class BuilderState(
    val productId: String = "prod_1",
    val productName: String = "Oversized Cotton T-Shirt",
    val clothingType: String = "T-Shirt", // T-Shirt, Polo, Shirt, Hoodie, Sweatshirt
    val fabricName: String = "Cotton (قطن 100%)",
    val fabricPrice: Double = 0.0,
    val fit: String = "Oversized", // Regular, Oversized, Slim, Relaxed
    val colorName: String = "أسود (Black)",
    val colorHex: String = "#121212",
    val size: String = "L", // XS, S, M, L, XL, XXL
    val customText: String = "CustomWear",
    val textColorHex: String = "#00E5FF",
    val fontFamily: String = "Sans",
    val designPosition: String = "Front", // Front, Back, Left Chest, Right Chest, Sleeve
    val designImageUrl: String = "",
    val designPrice: Double = 0.0,
    val quantity: Int = 1,
    val basePrice: Double = 200.0,
    val currentStep: Int = 1
) {
    val printingPrice: Double
        get() = if (customText.isNotEmpty() || designImageUrl.isNotEmpty()) 50.0 else 0.0

    val customizationFee: Double
        get() = designPrice + (if (fit == "Oversized") 20.0 else 0.0)

    val deliveryFee: Double = 40.0

    val unitPrice: Double
        get() = basePrice + fabricPrice + printingPrice + customizationFee

    val totalPrice: Double
        get() = unitPrice * quantity
}

class CustomWearViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CustomWearRepository.getInstance(application)
    private val dao = repository.dao

    // Current State
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentRole = MutableStateFlow("CUSTOMER") // CUSTOMER, SELLER, ADMIN
    val currentRole: StateFlow<String> = _currentRole.asStateFlow()

    // Builder State
    private val _builderState = MutableStateFlow(BuilderState())
    val builderState: StateFlow<BuilderState> = _builderState.asStateFlow()

    // Selected Product for details
    private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
    val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

    // Active Order for tracking
    private val _trackedOrderId = MutableStateFlow<String?>(null)
    val trackedOrderId: StateFlow<String?> = _trackedOrderId.asStateFlow()

    // Search and Filter for catalog
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All")
    val selectedFabricFilter = MutableStateFlow("All")
    val sortBy = MutableStateFlow("Popularity") // Popularity, Price Low-High, Price High-Low, Rating

    // Data Flows from Room DB
    val allProducts: StateFlow<List<ProductEntity>> = dao.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allFabrics: StateFlow<List<FabricEntity>> = dao.getAllFabrics()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allColors: StateFlow<List<ColorEntity>> = dao.getAllColors()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allDesigns: StateFlow<List<DesignEntity>> = dao.getAllDesigns()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allSellers: StateFlow<List<SellerEntity>> = dao.getApprovedSellers()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val cartItems = _currentUser.flatMapLatest { user ->
        if (user != null) dao.getCartItems(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userOrders = _currentUser.flatMapLatest { user ->
        if (user != null) dao.getOrdersByUser(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sellerOrders = dao.getAllOrders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userFavorites = _currentUser.flatMapLatest { user ->
        if (user != null) dao.getFavorites(user.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // AI Assistant state
    val aiPromptInput = MutableStateFlow("")
    val aiLoading = MutableStateFlow(false)
    val aiRecommendation = MutableStateFlow<AiCustomizationRecommendation?>(null)

    // User Message feedback snackbar
    val userMessage = MutableStateFlow<String?>(null)

    init {
        // Auto load demo customer on init
        viewModelScope.launch {
            val demoUser = dao.getUserByEmail("customer@customwear.com")
            if (demoUser != null) {
                _currentUser.value = demoUser
                _currentRole.value = demoUser.role
            }
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun switchRole(role: String) {
        _currentRole.value = role
        viewModelScope.launch {
            val email = when (role) {
                "SELLER" -> "seller1@customwear.com"
                "ADMIN" -> "admin@customwear.com"
                else -> "customer@customwear.com"
            }
            val user = dao.getUserByEmail(email)
            if (user != null) {
                _currentUser.value = user
            }
        }
    }

    fun login(email: String, role: String) {
        viewModelScope.launch {
            var user = dao.getUserByEmail(email)
            if (user == null) {
                user = UserEntity(
                    id = "user_${UUID.randomUUID().toString().take(6)}",
                    name = email.substringBefore("@").replaceFirstChar { it.uppercase() },
                    email = email,
                    phone = "+201000000000",
                    role = role
                )
                dao.insertUser(user)
            }
            _currentUser.value = user
            _currentRole.value = user.role
            userMessage.value = "تم تسجيل الدخول بنجاح"
            if (role == "SELLER") navigateTo(AppScreen.SELLER_DASHBOARD)
            else if (role == "ADMIN") navigateTo(AppScreen.ADMIN_PANEL)
            else navigateTo(AppScreen.HOME)
        }
    }

    fun register(name: String, email: String, phone: String, role: String) {
        viewModelScope.launch {
            val user = UserEntity(
                id = "user_${UUID.randomUUID().toString().take(6)}",
                name = name,
                email = email,
                phone = phone,
                role = role
            )
            dao.insertUser(user)
            if (role == "SELLER") {
                val seller = SellerEntity(
                    id = "seller_${UUID.randomUUID().toString().take(6)}",
                    userId = user.id,
                    storeName = "$name Store",
                    description = "متجر متألق لصناعة أحدث صيحات الموضة المخصصة",
                    rating = 5.0f,
                    location = "القاهرة"
                )
                dao.insertSeller(seller)
            }
            _currentUser.value = user
            _currentRole.value = role
            userMessage.value = "تم إنشاء الحساب بنجاح!"
            if (role == "SELLER") navigateTo(AppScreen.SELLER_DASHBOARD)
            else navigateTo(AppScreen.HOME)
        }
    }

    fun selectProduct(product: ProductEntity) {
        _selectedProduct.value = product
        val basePrice = when (product.category) {
            "T-Shirts" -> 200.0
            "Polo" -> 300.0
            "Shirts" -> 380.0
            "Hoodies" -> 500.0
            "Sweatshirts" -> 450.0
            else -> product.basePrice
        }
        _builderState.value = BuilderState(
            productId = product.id,
            productName = product.name,
            clothingType = product.category.removeSuffix("s"),
            basePrice = basePrice,
            currentStep = 1
        )
    }

    // Builder State Controls
    fun updateBuilderType(type: String) {
        val basePrice = when (type) {
            "T-Shirt" -> 200.0
            "Polo" -> 300.0
            "Shirt" -> 380.0
            "Hoodie" -> 500.0
            "Sweatshirt" -> 450.0
            else -> 200.0
        }
        _builderState.value = _builderState.value.copy(clothingType = type, basePrice = basePrice)
    }

    fun updateBuilderFabric(fabric: FabricEntity) {
        _builderState.value = _builderState.value.copy(
            fabricName = fabric.name,
            fabricPrice = fabric.additionalPrice
        )
    }

    fun updateBuilderFit(fit: String) {
        _builderState.value = _builderState.value.copy(fit = fit)
    }

    fun updateBuilderColor(color: ColorEntity) {
        _builderState.value = _builderState.value.copy(
            colorName = color.name,
            colorHex = color.hexCode
        )
    }

    fun updateBuilderSize(size: String) {
        _builderState.value = _builderState.value.copy(size = size)
    }

    fun updateBuilderDesign(text: String, textColorHex: String, position: String, design: DesignEntity? = null) {
        _builderState.value = _builderState.value.copy(
            customText = text,
            textColorHex = textColorHex,
            designPosition = position,
            designImageUrl = design?.imageUrl ?: _builderState.value.designImageUrl,
            designPrice = design?.price ?: _builderState.value.designPrice
        )
    }

    fun updateBuilderQuantity(qty: Int) {
        if (qty in 1..99) {
            _builderState.value = _builderState.value.copy(quantity = qty)
        }
    }

    fun nextBuilderStep() {
        val current = _builderState.value.currentStep
        if (current < 9) {
            _builderState.value = _builderState.value.copy(currentStep = current + 1)
        }
    }

    fun prevBuilderStep() {
        val current = _builderState.value.currentStep
        if (current > 1) {
            _builderState.value = _builderState.value.copy(currentStep = current - 1)
        }
    }

    // Size Assistant Logic
    fun calculateRecommendedSize(heightCm: Int, weightKg: Int, fitPreference: String): String {
        val bmi = weightKg / ((heightCm / 100.0) * (heightCm / 100.0))
        var size = when {
            weightKg < 55 -> "S"
            weightKg in 55..68 -> "M"
            weightKg in 69..82 -> "L"
            weightKg in 83..95 -> "XL"
            else -> "XXL"
        }
        if (fitPreference == "Oversized" && size != "XXL") {
            size = when (size) {
                "S" -> "M"
                "M" -> "L"
                "L" -> "XL"
                "XL" -> "XXL"
                else -> size
            }
        }
        return size
    }

    // Add Customization to Cart
    fun saveCustomizationAndAddToCart() {
        val state = _builderState.value
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val customization = CustomizationEntity(
                id = "cust_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                productId = state.productId,
                productName = state.productName,
                clothingType = state.clothingType,
                fabricName = state.fabricName,
                fabricPrice = state.fabricPrice,
                fit = state.fit,
                colorName = state.colorName,
                colorHex = state.colorHex,
                size = state.size,
                customText = state.customText,
                textColorHex = state.textColorHex,
                fontFamily = state.fontFamily,
                designPosition = state.designPosition,
                designImageUrl = state.designImageUrl,
                designPrice = state.designPrice,
                quantity = state.quantity,
                totalPrice = state.totalPrice
            )
            dao.insertCustomization(customization)

            val cartItem = CartItemEntity(
                id = "cart_${UUID.randomUUID().toString().take(8)}",
                userId = user.id,
                customizationId = customization.id,
                quantity = state.quantity
            )
            dao.insertCartItem(cartItem)

            userMessage.value = "تمت إضافة التصميم الخاص بك إلى السلة!"
            navigateTo(AppScreen.CART)
        }
    }

    fun removeCartItem(cartItemId: String) {
        viewModelScope.launch {
            dao.deleteCartItem(cartItemId)
            userMessage.value = "تمت إزالة القطعة من السلة"
        }
    }

    // Checkout & Place Order
    fun placeOrder(address: String, phone: String, city: String, paymentMethod: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val items = cartItems.value
            if (items.isEmpty()) return@launch

            var grandTotal = 0.0
            val orderId = "CW-${(100000..999999).random()}"

            items.forEach { cartItem ->
                val cust = dao.getCustomizationById(cartItem.customizationId)
                if (cust != null) {
                    grandTotal += cust.totalPrice
                    val orderItem = OrderItemEntity(
                        id = "item_${UUID.randomUUID().toString().take(6)}",
                        orderId = orderId,
                        customizationId = cust.id,
                        productName = cust.productName,
                        summary = "${cust.clothingType} • ${cust.fabricName} • ${cust.fit} • ${cust.colorName} • Size: ${cust.size} • Text: '${cust.customText}'",
                        quantity = cartItem.quantity,
                        price = cust.totalPrice
                    )
                    dao.insertOrderItems(listOf(orderItem))
                }
            }

            val order = OrderEntity(
                id = orderId,
                userId = user.id,
                customerName = user.name,
                sellerId = "seller_1",
                sellerName = "Cairo Wear Factory",
                totalPrice = grandTotal + 40.0,
                deliveryFee = 40.0,
                status = "PENDING",
                paymentMethod = paymentMethod,
                address = address,
                phone = phone,
                city = city
            )

            dao.insertOrder(order)
            dao.clearCart(user.id)

            _trackedOrderId.value = orderId
            userMessage.value = "تم تسجيل طلبك بنجاح! جاري التجهيز."
            navigateTo(AppScreen.ORDER_TRACKING)
        }
    }

    // Order Again / Reorder
    fun reorderPreviousOrder(orderId: String) {
        viewModelScope.launch {
            val items = dao.getOrderItems(orderId)
            val user = _currentUser.value ?: return@launch
            if (items.isNotEmpty()) {
                val item = items.first()
                val cust = dao.getCustomizationById(item.customizationId)
                if (cust != null) {
                    val newCust = cust.copy(id = "cust_${UUID.randomUUID().toString().take(8)}", createdAt = System.currentTimeMillis())
                    dao.insertCustomization(newCust)
                    val newCart = CartItemEntity(
                        id = "cart_${UUID.randomUUID().toString().take(8)}",
                        userId = user.id,
                        customizationId = newCust.id,
                        quantity = newCust.quantity
                    )
                    dao.insertCartItem(newCart)
                    userMessage.value = "تمت إعادة إضافة تصميمك السابق للطلب إلى السلة!"
                    navigateTo(AppScreen.CART)
                }
            }
        }
    }

    // Toggle Favorite
    fun toggleFavorite(productId: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val isFav = userFavorites.value.any { it.productId == productId }
            if (isFav) {
                dao.removeFavorite(user.id, productId)
                userMessage.value = "تمت الإزالة من المفضلة"
            } else {
                dao.insertFavorite(FavoriteEntity("fav_${UUID.randomUUID().toString().take(6)}", user.id, productId))
                userMessage.value = "تم الحفظ في المفضلة!"
            }
        }
    }

    // Seller status update
    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            dao.updateOrderStatus(orderId, newStatus)
            userMessage.value = "تم تحديث حالة الطلب إلى $newStatus"
        }
    }

    // Seller Product Add
    fun addSellerProduct(name: String, category: String, basePrice: Double, description: String, imageUrl: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val product = ProductEntity(
                id = "prod_${UUID.randomUUID().toString().take(6)}",
                sellerId = user.id,
                sellerName = user.name,
                name = name,
                description = description,
                category = category,
                basePrice = basePrice,
                imageUrl = if (imageUrl.isBlank()) "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500" else imageUrl,
                rating = 5.0f
            )
            dao.insertProduct(product)
            userMessage.value = "تم إضافـة المنتج بنجاح إلى المَتجر!"
            navigateTo(AppScreen.SELLER_DASHBOARD)
        }
    }

    // AI Assistant Execute
    fun submitAiPrompt() {
        val prompt = aiPromptInput.value.trim()
        if (prompt.isEmpty()) return
        aiLoading.value = true
        viewModelScope.launch {
            val rec = AiAssistantService.analyzeStylePrompt(prompt)
            aiRecommendation.value = rec
            aiLoading.value = false
        }
    }

    fun applyAiRecommendation(rec: AiCustomizationRecommendation) {
        val basePrice = when (rec.clothingType) {
            "T-Shirt" -> 200.0
            "Polo" -> 300.0
            "Shirt" -> 380.0
            "Hoodie" -> 500.0
            "Sweatshirt" -> 450.0
            else -> 200.0
        }
        _builderState.value = BuilderState(
            clothingType = rec.clothingType,
            fabricName = rec.fabric,
            fit = rec.fit,
            colorName = rec.colorName,
            colorHex = rec.colorHex,
            customText = rec.customText,
            basePrice = basePrice,
            currentStep = 7 // Go directly to dynamic preview
        )
        userMessage.value = "تم تطبيق الخيارات الموصى بها من المساعد الذكي!"
        navigateTo(AppScreen.BUILDER)
    }

    fun clearUserMessage() {
        userMessage.value = null
    }
}
