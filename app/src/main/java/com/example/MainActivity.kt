package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ProductEntity
import com.example.ui.components.CustomWearBottomNavBar
import com.example.ui.components.CustomWearTopBar
import com.example.ui.screens.*
import com.example.ui.theme.CustomWearTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.CustomWearViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CustomWearTheme {
                CustomWearAppContent()
            }
        }
    }
}

@Composable
fun CustomWearAppContent(
    viewModel: CustomWearViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val builderState by viewModel.builderState.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val trackedOrderId by viewModel.trackedOrderId.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()

    val products by viewModel.allProducts.collectAsStateWithLifecycle()
    val fabrics by viewModel.allFabrics.collectAsStateWithLifecycle()
    val colors by viewModel.allColors.collectAsStateWithLifecycle()
    val designs by viewModel.allDesigns.collectAsStateWithLifecycle()
    val sellers by viewModel.allSellers.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val userOrders by viewModel.userOrders.collectAsStateWithLifecycle()
    val sellerOrders by viewModel.sellerOrders.collectAsStateWithLifecycle()

    val aiPromptInput by viewModel.aiPromptInput.collectAsStateWithLifecycle()
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()
    val aiRecommendation by viewModel.aiRecommendation.collectAsStateWithLifecycle()

    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearUserMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.ONBOARDING) {
                CustomWearTopBar(
                    currentScreen = currentScreen,
                    currentRole = currentRole,
                    cartItemCount = cartItems.size,
                    onNavigate = { viewModel.navigateTo(it) },
                    onSwitchRole = { viewModel.switchRole(it) }
                )
            }
        },
        bottomBar = {
            CustomWearBottomNavBar(
                currentScreen = currentScreen,
                currentRole = currentRole,
                onNavigate = { viewModel.navigateTo(it) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "ScreenTransition") { screen ->
                when (screen) {
                    AppScreen.SPLASH -> SplashScreen(
                        onFinishSplash = { viewModel.navigateTo(AppScreen.ONBOARDING) }
                    )

                    AppScreen.ONBOARDING -> OnboardingScreen(
                        onGetStarted = { viewModel.navigateTo(AppScreen.HOME) }
                    )

                    AppScreen.LOGIN -> LoginScreen(
                        onLogin = { email, role -> viewModel.login(email, role) },
                        onNavigateToRegister = { viewModel.navigateTo(AppScreen.REGISTER) }
                    )

                    AppScreen.REGISTER -> RegisterScreen(
                        onRegister = { name, email, phone, role -> viewModel.register(name, email, phone, role) },
                        onNavigateToLogin = { viewModel.navigateTo(AppScreen.LOGIN) }
                    )

                    AppScreen.HOME -> HomeScreen(
                        products = products,
                        designs = designs,
                        sellers = sellers,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        onSelectCategory = { viewModel.selectedCategory.value = it },
                        onProductClick = {
                            viewModel.selectProduct(it)
                            viewModel.navigateTo(AppScreen.PRODUCT_DETAILS)
                        },
                        onStartCustomizingClick = { viewModel.navigateTo(AppScreen.BUILDER) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onNavigateToAiAssistant = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                        onNavigateToCatalog = { viewModel.navigateTo(AppScreen.CATALOG) }
                    )

                    AppScreen.CATALOG -> CatalogScreen(
                        products = products,
                        fabrics = fabrics,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        selectedCategory = selectedCategory,
                        onSelectCategory = { viewModel.selectedCategory.value = it },
                        onProductClick = {
                            viewModel.selectProduct(it)
                            viewModel.navigateTo(AppScreen.PRODUCT_DETAILS)
                        },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )

                    AppScreen.PRODUCT_DETAILS -> ProductDetailsScreen(
                        product = selectedProduct,
                        fabrics = fabrics,
                        onStartCustomizing = { product ->
                            viewModel.selectProduct(product)
                            viewModel.navigateTo(AppScreen.BUILDER)
                        },
                        onBackClick = { viewModel.navigateTo(AppScreen.CATALOG) }
                    )

                    AppScreen.BUILDER -> CustomBuilderScreen(
                        builderState = builderState,
                        fabrics = fabrics,
                        colors = colors,
                        designs = designs,
                        onUpdateType = { viewModel.updateBuilderType(it) },
                        onUpdateFabric = { viewModel.updateBuilderFabric(it) },
                        onUpdateFit = { viewModel.updateBuilderFit(it) },
                        onUpdateColor = { viewModel.updateBuilderColor(it) },
                        onUpdateSize = { viewModel.updateBuilderSize(it) },
                        onUpdateDesign = { text, color, pos, design -> viewModel.updateBuilderDesign(text, color, pos, design) },
                        onUpdateQuantity = { viewModel.updateBuilderQuantity(it) },
                        onNextStep = { viewModel.nextBuilderStep() },
                        onPrevStep = { viewModel.prevBuilderStep() },
                        onCalculateRecommendedSize = { height, weight, fit ->
                            viewModel.calculateRecommendedSize(height, weight, fit)
                        },
                        onAddToCart = { viewModel.saveCustomizationAndAddToCart() }
                    )

                    AppScreen.CART -> CartScreen(
                        cartItems = cartItems,
                        onRemoveItem = { viewModel.removeCartItem(it) },
                        onProceedToCheckout = { viewModel.navigateTo(AppScreen.CHECKOUT) },
                        onStartNewCustomization = { viewModel.navigateTo(AppScreen.BUILDER) }
                    )

                    AppScreen.CHECKOUT -> CheckoutScreen(
                        onPlaceOrder = { address, phone, city, paymentMethod ->
                            viewModel.placeOrder(address, phone, city, paymentMethod)
                        }
                    )

                    AppScreen.ORDER_TRACKING -> OrderTrackingScreen(
                        orders = userOrders,
                        trackedOrderId = trackedOrderId,
                        onReorder = { viewModel.reorderPreviousOrder(it) },
                        onStartNewOrder = { viewModel.navigateTo(AppScreen.BUILDER) }
                    )

                    AppScreen.PROFILE -> ProfileScreen(
                        user = currentUser,
                        onNavigateOrders = { viewModel.navigateTo(AppScreen.ORDER_TRACKING) },
                        onNavigateFavorites = { viewModel.navigateTo(AppScreen.DESIGN_MARKETPLACE) },
                        onLogout = { viewModel.navigateTo(AppScreen.LOGIN) }
                    )

                    AppScreen.DESIGN_MARKETPLACE -> DesignMarketplaceScreen(
                        designs = designs,
                        onApplyDesign = { design ->
                            viewModel.updateBuilderDesign(
                                text = builderState.customText,
                                textColorHex = builderState.textColorHex,
                                position = "Front",
                                design = design
                            )
                            viewModel.navigateTo(AppScreen.BUILDER)
                        }
                    )

                    AppScreen.AI_ASSISTANT -> AiAssistantScreen(
                        promptInput = aiPromptInput,
                        onPromptChange = { viewModel.aiPromptInput.value = it },
                        isLoading = aiLoading,
                        recommendation = aiRecommendation,
                        onSubmitPrompt = { viewModel.submitAiPrompt() },
                        onApplyRecommendation = { rec -> viewModel.applyAiRecommendation(rec) }
                    )

                    AppScreen.SELLER_DASHBOARD -> SellerDashboardScreen(
                        orders = sellerOrders,
                        onUpdateOrderStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) },
                        onNavigateToAddProduct = { viewModel.navigateTo(AppScreen.SELLER_ADD_PRODUCT) }
                    )

                    AppScreen.SELLER_ADD_PRODUCT -> SellerAddProductScreen(
                        onAddProduct = { name, cat, price, desc, url ->
                            viewModel.addSellerProduct(name, cat, price, desc, url)
                        }
                    )

                    AppScreen.ADMIN_PANEL -> AdminPanelScreen(
                        orders = sellerOrders
                    )

                    else -> HomeScreen(
                        products = products,
                        designs = designs,
                        sellers = sellers,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.searchQuery.value = it },
                        onSelectCategory = { viewModel.selectedCategory.value = it },
                        onProductClick = {
                            viewModel.selectProduct(it)
                            viewModel.navigateTo(AppScreen.PRODUCT_DETAILS)
                        },
                        onStartCustomizingClick = { viewModel.navigateTo(AppScreen.BUILDER) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onNavigateToAiAssistant = { viewModel.navigateTo(AppScreen.AI_ASSISTANT) },
                        onNavigateToCatalog = { viewModel.navigateTo(AppScreen.CATALOG) }
                    )
                }
            }
        }
    }
}
