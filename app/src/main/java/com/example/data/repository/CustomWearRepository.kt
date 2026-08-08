package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID

class CustomWearRepository private constructor(context: Context) {

    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "customwear_database"
    ).fallbackToDestructiveMigration().build()

    val dao = db.appDao()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedSampleDataIfEmpty()
        }
    }

    private suspend fun seedSampleDataIfEmpty() {
        val user = dao.getUserByEmail("demo@customwear.com")
        if (user == null) {
            // Seed Default Users
            val customer = UserEntity(
                id = "user_cust_1",
                name = "أحمد محمود (Ahmed)",
                email = "customer@customwear.com",
                phone = "+201012345678",
                role = "CUSTOMER",
                avatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200"
            )
            val sellerUser1 = UserEntity(
                id = "user_seller_1",
                name = "مصنع القاهرة للغزل (Cairo Textiles)",
                email = "seller1@customwear.com",
                phone = "+201223344556",
                role = "SELLER",
                avatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200"
            )
            val sellerUser2 = UserEntity(
                id = "user_seller_2",
                name = "ورشة أليكس ستايل (Alex Style Studio)",
                email = "seller2@customwear.com",
                phone = "+201112223334",
                role = "SELLER"
            )
            val adminUser = UserEntity(
                id = "user_admin_1",
                name = "مدير المنصة (CustomWear Admin)",
                email = "admin@customwear.com",
                phone = "+201000000000",
                role = "ADMIN"
            )

            dao.insertUser(customer)
            dao.insertUser(sellerUser1)
            dao.insertUser(sellerUser2)
            dao.insertUser(adminUser)

            // Seed 5 Sellers
            val sellers = listOf(
                SellerEntity("seller_1", "user_seller_1", "Cairo Wear Factory", "متخصصون في الملابس القطنية الفاخرة والتطريز المخصص", 4.9f, "القاهرة - Nasr City", true),
                SellerEntity("seller_2", "user_seller_2", "Alex Style Studio", "أفضل تصميمات الهودي والستريت وير في الإسكندرية", 4.8f, "الإسكندرية - Smouha", true),
                SellerEntity("seller_3", "user_seller_3", "Nile Stitch & Print", "طباعة دقيقة عالي الجودة وتجهيز كميات سريعة", 4.7f, "الجيزة - Dokki", true),
                SellerEntity("seller_4", "user_seller_4", "Urban Cotton Egypt", "خامات قطنية 100% غزل مصري فاخر", 4.9f, "طنطا - Tanta", true),
                SellerEntity("seller_5", "user_seller_5", "Delta Custom Apparel", "تشكيلة واسعة من البولو والقمصان المطبوعة", 4.6f, "المنصورة - Mansoura", true)
            )
            sellers.forEach { dao.insertSeller(it) }

            // Seed Fabrics
            val fabrics = listOf(
                FabricEntity("fab_1", "Cotton (قطن 100%)", "ناعم ومناسب للاستخدام اليومي والحرارة المصرية.", 0.0),
                FabricEntity("fab_2", "Pique (بيكية)", "مسامي ممتاز لتيشيرتات البولو الأنيقة.", 30.0),
                FabricEntity("fab_3", "Linen (كتان نقي)", "خفيف وبارد جداً للمواسم الصيفية والقمصان.", 50.0),
                FabricEntity("fab_4", "Polyester Blend (بوليستر رياضي)", "سريع الجفاف ومناسب للأنشطة الرياضية.", 15.0),
                FabricEntity("fab_5", "Cotton Blend (مخلوط فخم)", "يقاوم الانكماش ويتحمل الغسيل المتكرر.", 20.0)
            )
            dao.insertFabrics(fabrics)

            // Seed Colors
            val colors = listOf(
                ColorEntity("col_1", "أسود (Black)", "#121212", 0.0),
                ColorEntity("col_2", "أبيض (White)", "#FFFFFF", 0.0),
                ColorEntity("col_3", "كحلي (Navy)", "#1A237E", 0.0),
                ColorEntity("col_4", "رمادي (Gray)", "#757575", 0.0),
                ColorEntity("col_5", "أحمر (Red)", "#D32F2F", 0.0),
                ColorEntity("col_6", "أخضر (Green)", "#2E7D32", 0.0),
                ColorEntity("col_7", "أزرق سماوي (Blue)", "#1976D2", 0.0),
                ColorEntity("col_8", "بيج (Beige)", "#F5F5DC", 0.0)
            )
            dao.insertColors(colors)

            // Seed 10 Products
            val products = listOf(
                ProductEntity("prod_1", "seller_1", "Cairo Wear Factory", "Oversized Cotton T-Shirt", "تيشيرت أوفر سايز قطني فاخر بتطريز محلي", "T-Shirts", 200.0, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=500", 4.9f),
                ProductEntity("prod_2", "seller_2", "Alex Style Studio", "Vintage Streetwear Hoodie", "هودي مريح بخامة ثقيلة مقاسات واسعة", "Hoodies", 500.0, "https://images.unsplash.com/photo-1556306535-0f09a537f0a3?w=500", 4.9f),
                ProductEntity("prod_3", "seller_3", "Nile Stitch & Print", "Classic Pique Polo Shirt", "تيشيرت بولو بيكية أنيق للعمل والجامعة", "Polo", 300.0, "https://images.unsplash.com/photo-1581655353564-df123a1eb820?w=500", 4.7f),
                ProductEntity("prod_4", "seller_4", "Urban Cotton Egypt", "Minimalist Casual Linen Shirt", "قميص كتان خفيف ومريح بألوان متناسقة", "Shirts", 380.0, "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=500", 4.8f),
                ProductEntity("prod_5", "seller_1", "Cairo Wear Factory", "Cozy Fleece Sweatshirt", "سويت شيرت مبطن ومثالي للطقس البارد", "Sweatshirts", 450.0, "https://images.unsplash.com/photo-1620799140408-edc6dcb6d633?w=500", 4.8f),
                ProductEntity("prod_6", "seller_5", "Delta Custom Apparel", "Graphic Printed Tee", "تيشيرت أبيض بطباعة ديجيتال زاهية", "T-Shirts", 220.0, "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500", 4.6f),
                ProductEntity("prod_7", "seller_2", "Alex Style Studio", "Zip-Up Urban Hoodie", "هودي بسوستة خامة قطنية ممتازة", "Hoodies", 550.0, "https://images.unsplash.com/photo-1509967419530-da38b4704bc6?w=500", 4.9f),
                ProductEntity("prod_8", "seller_3", "Nile Stitch & Print", "Slim Fit Premium Shirt", "قميص سليم فيت مخصص للمناسبات", "Shirts", 400.0, "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?w=500", 4.7f),
                ProductEntity("prod_9", "seller_4", "Urban Cotton Egypt", "Sports Performance Polo", "بولو رياضي مرن يتحمل الحرارة والأنشطة", "Polo", 320.0, "https://images.unsplash.com/photo-1625910513413-5fc2e6241bfa?w=500", 4.8f),
                ProductEntity("prod_10", "seller_5", "Delta Custom Apparel", "Crewneck Winter Sweatshirt", "سويت شيرت كلاسيكي بألوان مميزة", "Sweatshirts", 430.0, "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?w=500", 4.7f)
            )
            products.forEach { dao.insertProduct(it) }

            // Seed 10 Designs
            val designs = listOf(
                DesignEntity("des_1", "designer_1", "Karim Art", "Minimalist Horus Eye", "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=300", 25.0, 310, 4.9f),
                DesignEntity("des_2", "designer_2", "Nour Graphic", "Arabic Calligraphy - الأمل", "https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=300", 20.0, 420, 5.0f),
                DesignEntity("des_3", "designer_3", "Cairo Synth", "Retro Pyramids Neon", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=300", 30.0, 190, 4.8f),
                DesignEntity("des_4", "designer_1", "Karim Art", "Geometric Lotus Flower", "https://images.unsplash.com/photo-1541701494587-cb58502866ab?w=300", 15.0, 250, 4.7f),
                DesignEntity("des_5", "designer_4", "Salma Visuals", "Urban Streets Cairo typography", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=300", 25.0, 180, 4.9f),
                DesignEntity("des_6", "designer_2", "Nour Graphic", "Pharaonic Cat Line Art", "https://images.unsplash.com/photo-1536924940846-227afb31e2a5?w=300", 20.0, 290, 4.8f),
                DesignEntity("des_7", "designer_5", "Omar Studio", "Abstract Waves Art", "https://images.unsplash.com/photo-1500462895327-1337ee254a50?w=300", 15.0, 140, 4.6f),
                DesignEntity("des_8", "designer_3", "Cairo Synth", "Cyberpunk Sphinx", "https://images.unsplash.com/photo-1563089145-599997674d42?w=300", 35.0, 350, 4.9f),
                DesignEntity("des_9", "designer_4", "Salma Visuals", "Coffee & Code Minimalist", "https://images.unsplash.com/photo-1517842645767-c639042777db?w=300", 18.0, 210, 4.7f),
                DesignEntity("des_10", "designer_5", "Omar Studio", "Vintage Nile Sunset", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=300", 22.0, 160, 4.8f)
            )
            designs.forEach { dao.insertDesign(it) }

            // Seed 5 Sample Orders
            val custom1 = CustomizationEntity(
                id = "cust_sample_1",
                userId = "user_cust_1",
                productId = "prod_1",
                productName = "Oversized Cotton T-Shirt",
                clothingType = "T-Shirt",
                fabricName = "Cotton (قطن 100%)",
                fabricPrice = 0.0,
                fit = "Oversized",
                colorName = "أسود (Black)",
                colorHex = "#121212",
                size = "L",
                customText = "CustomWear 2026",
                textColorHex = "#00E5FF",
                fontFamily = "Sans",
                designPosition = "Front",
                quantity = 2,
                totalPrice = 480.0
            )
            dao.insertCustomization(custom1)

            val order1 = OrderEntity(
                id = "CW-100291",
                userId = "user_cust_1",
                customerName = "أحمد محمود",
                sellerId = "seller_1",
                sellerName = "Cairo Wear Factory",
                totalPrice = 520.0,
                deliveryFee = 40.0,
                status = "MANUFACTURING",
                paymentMethod = "Cash on Delivery",
                address = "15 شارع النصر، مدينة نصر",
                phone = "+201012345678",
                city = "القاهرة"
            )
            val orderItem1 = OrderItemEntity(
                id = "item_1",
                orderId = "CW-100291",
                customizationId = "cust_sample_1",
                productName = "Oversized Cotton T-Shirt",
                summary = "T-Shirt • Cotton • Oversized • Black • L • Text: 'CustomWear 2026'",
                quantity = 2,
                price = 240.0
            )
            dao.insertOrder(order1)
            dao.insertOrderItems(listOf(orderItem1))

            val order2 = OrderEntity(
                id = "CW-100292",
                userId = "user_cust_1",
                customerName = "أحمد محمود",
                sellerId = "seller_2",
                sellerName = "Alex Style Studio",
                totalPrice = 590.0,
                deliveryFee = 40.0,
                status = "ACCEPTED",
                paymentMethod = "Mock Online Payment",
                address = "شارع كورنيش النيل، الدقي",
                phone = "+201012345678",
                city = "الجيزة"
            )
            dao.insertOrder(order2)

            val order3 = OrderEntity(
                id = "CW-100293",
                userId = "user_cust_1",
                customerName = "أحمد محمود",
                sellerId = "seller_3",
                sellerName = "Nile Stitch & Print",
                totalPrice = 340.0,
                deliveryFee = 40.0,
                status = "SHIPPED",
                paymentMethod = "Cash on Delivery",
                address = "شارع الجلاء، طنطا",
                phone = "+201012345678",
                city = "الغربية"
            )
            dao.insertOrder(order3)

            val order4 = OrderEntity(
                id = "CW-100294",
                userId = "user_cust_1",
                customerName = "سارة حسن",
                sellerId = "seller_1",
                sellerName = "Cairo Wear Factory",
                totalPrice = 490.0,
                deliveryFee = 40.0,
                status = "DELIVERED",
                paymentMethod = "Cash on Delivery",
                address = "شارع 9، المعادي",
                phone = "+201119876543",
                city = "القاهرة"
            )
            dao.insertOrder(order4)

            val order5 = OrderEntity(
                id = "CW-100295",
                userId = "user_cust_1",
                customerName = "عمر فاروق",
                sellerId = "seller_2",
                sellerName = "Alex Style Studio",
                totalPrice = 880.0,
                deliveryFee = 40.0,
                status = "READY",
                paymentMethod = "Mock Online Payment",
                address = "شارع الفؤاد، الإسكندرية",
                phone = "+201225554433",
                city = "الإسكندرية"
            )
            dao.insertOrder(order5)

            // Seed Reviews
            val reviews = listOf(
                ReviewEntity("rev_1", "user_cust_1", "أحمد محمود", "prod_1", "seller_1", 5.0f, "الخامة ممتازة والتطريز ثقيل ونظيف جداً! صممت التيشيرت ووصل بنفس الشكل بالضبط."),
                ReviewEntity("rev_2", "user_2", "سارة عادل", "prod_2", "seller_2", 4.8f, "الهودي دافي جداً واللون الأوفسايز مظبوط. شكراً كاستم وير."),
                ReviewEntity("rev_3", "user_3", "محمد علي", "prod_3", "seller_3", 4.7f, "البولو خامة بيكية نظيفة وتتحمل الغسيل.")
            )
            reviews.forEach { dao.insertReview(it) }
        }
    }

    companion object {
        @Volatile private var instance: CustomWearRepository? = null

        fun getInstance(context: Context): CustomWearRepository {
            return instance ?: synchronized(this) {
                instance ?: CustomWearRepository(context).also { instance = it }
            }
        }
    }
}
