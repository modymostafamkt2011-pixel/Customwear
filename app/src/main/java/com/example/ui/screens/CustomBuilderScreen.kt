package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ColorEntity
import com.example.data.model.DesignEntity
import com.example.data.model.FabricEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.BuilderState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomBuilderScreen(
    builderState: BuilderState,
    fabrics: List<FabricEntity>,
    colors: List<ColorEntity>,
    designs: List<DesignEntity>,
    onUpdateType: (String) -> Unit,
    onUpdateFabric: (FabricEntity) -> Unit,
    onUpdateFit: (String) -> Unit,
    onUpdateColor: (ColorEntity) -> Unit,
    onUpdateSize: (String) -> Unit,
    onUpdateDesign: (text: String, textColorHex: String, position: String, design: DesignEntity?) -> Unit,
    onUpdateQuantity: (Int) -> Unit,
    onNextStep: () -> Unit,
    onPrevStep: () -> Unit,
    onCalculateRecommendedSize: (heightCm: Int, weightKg: Int, fit: String) -> String,
    onAddToCart: () -> Unit
) {
    var showSizeAssistantDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(bottom = 80.dp)
    ) {
        // Step Indicator Progress Header
        BuilderProgressHeader(
            currentStep = builderState.currentStep,
            totalSteps = 9,
            onPrevStep = onPrevStep
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (builderState.currentStep) {
                1 -> Step1ClothingType(builderState.clothingType, onUpdateType, onNextStep)
                2 -> Step2Fabric(builderState.fabricName, fabrics, onUpdateFabric, onNextStep)
                3 -> Step3Fit(builderState.fit, onUpdateFit, onNextStep)
                4 -> Step4Color(builderState.colorHex, colors, onUpdateColor, onNextStep)
                5 -> Step5Size(
                    builderState.size,
                    onUpdateSize,
                    onOpenAssistant = { showSizeAssistantDialog = true },
                    onNext = onNextStep
                )
                6 -> Step6DesignAndText(
                    currentText = builderState.customText,
                    currentPosition = builderState.designPosition,
                    currentDesignUrl = builderState.designImageUrl,
                    designs = designs,
                    onUpdateDesign = onUpdateDesign,
                    onNext = onNextStep
                )
                7 -> Step7InteractivePreview(builderState = builderState, onNext = onNextStep)
                8 -> Step8PriceBreakdown(builderState = builderState, onNext = onNextStep)
                9 -> Step9SummaryAndAddToCart(
                    builderState = builderState,
                    onUpdateQuantity = onUpdateQuantity,
                    onAddToCart = onAddToCart
                )
            }
        }
    }

    if (showSizeAssistantDialog) {
        SizeAssistantDialog(
            onDismiss = { showSizeAssistantDialog = false },
            onCalculate = { height, weight ->
                val recommended = onCalculateRecommendedSize(height, weight, builderState.fit)
                onUpdateSize(recommended)
                showSizeAssistantDialog = false
            }
        )
    }
}

@Composable
private fun BuilderProgressHeader(currentStep: Int, totalSteps: Int, onPrevStep: () -> Unit) {
    val stepTitles = listOf(
        "نوع القطعة", "الخامة", "التفصيل (Fit)", "اللون", "المقاس", "التصميم والنص", "المعاينة الحية", "تفاصيل السعر", "إضافة للسلة"
    )

    Surface(
        color = Color.White,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentStep > 1) {
                        IconButton(onClick = onPrevStep, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Text(
                        text = "الخطوة $currentStep من $totalSteps: ${stepTitles.getOrElse(currentStep - 1) { "" }}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = DarkNavy
                    )
                }
                Text(
                    text = "${((currentStep / totalSteps.toFloat()) * 100).toInt()}%",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = DeepPurple700
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { currentStep / totalSteps.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = DeepPurple700,
                trackColor = BorderGray
            )
        }
    }
}

// STEP 1: Clothing Type
@Composable
private fun Step1ClothingType(selectedType: String, onSelect: (String) -> Unit, onNext: () -> Unit) {
    val types = listOf(
        "T-Shirt" to "تيشيرت كلاسيكي مريح",
        "Polo" to "بولو بيكيه أنيق",
        "Shirt" to "قميص صيفي وعصري",
        "Hoodie" to "هودي بخامة ثقيلة",
        "Sweatshirt" to "سويت شيرت شتوي"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("اختر نوع الملابس (Clothing Type)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("حدد الموديل الذي ترغب في تصميمه من البداية", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        types.forEach { (type, desc) ->
            val isSelected = selectedType == type
            Card(
                onClick = { onSelect(type) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) LightPurple100 else Color.White
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DeepPurple700)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("builder_type_$type")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) DeepPurple700 else LightPurple100),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Checkroom,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else DeepPurple700
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = type, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                        Text(text = desc, fontSize = 12.sp, color = TextGray)
                    }

                    if (isSelected) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DeepPurple700)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step1_next")
        ) {
            Text("متابعة للخامة (Next Step)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 2: Fabric
@Composable
private fun Step2Fabric(selectedFabricName: String, fabrics: List<FabricEntity>, onSelectFabric: (FabricEntity) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("اختر خامة القماش (Fabric Choice)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("اختر الملمس والوزن المناسب لاستخدامك", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        fabrics.forEach { fabric ->
            val isSelected = selectedFabricName == fabric.name
            Card(
                onClick = { onSelectFabric(fabric) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) LightPurple100 else Color.White
                ),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DeepPurple700)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("builder_fabric_${fabric.id}")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = fabric.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkNavy)
                        Text(
                            text = if (fabric.additionalPrice > 0) "+${fabric.additionalPrice.toInt()} EGP" else "مجاناً",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DeepPurple700
                        )
                    }
                    Text(text = fabric.description, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step2_next")
        ) {
            Text("متابعة للمقاس والتفصيل", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 3: Fit
@Composable
private fun Step3Fit(selectedFit: String, onSelectFit: (String) -> Unit, onNext: () -> Unit) {
    val fits = listOf(
        "Oversized" to "قصة واسعة مريحة جداً تناسب الموضة العصرية الشبابية.",
        "Regular" to "قصة كلاسيكية منتظمة تناسب الاستخدام اليومي.",
        "Slim" to "قصة مجسمة ومشدودة تفصل شكل الجسم.",
        "Relaxed" to "قصة مريحة فضفاضة خفيفة على الجسم."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("اختر قصة التفصيل (Fit)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("شكل ومقاس ارتداء الملابس على جسمك", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        fits.forEach { (fit, desc) ->
            val isSelected = selectedFit == fit
            Card(
                onClick = { onSelectFit(fit) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isSelected) LightPurple100 else Color.White),
                border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DeepPurple700)) else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .testTag("builder_fit_$fit")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = fit, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkNavy)
                    Text(text = desc, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step3_next")
        ) {
            Text("متابعة للون (Next)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 4: Color
@Composable
private fun Step4Color(selectedColorHex: String, colors: List<ColorEntity>, onSelectColor: (ColorEntity) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("اختر لون الملابس (Color Palette)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("اختر اللون الأساسي لقطعتك", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(20.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            items(colors) { colorEntity ->
                val isSelected = selectedColorHex.equals(colorEntity.hexCode, ignoreCase = true)
                val parsedColor = try {
                    Color(android.graphics.Color.parseColor(colorEntity.hexCode))
                } catch (e: Exception) {
                    Color.Black
                }

                Card(
                    onClick = { onSelectColor(colorEntity) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DeepPurple700)) else null,
                    modifier = Modifier.testTag("builder_color_${colorEntity.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(parsedColor)
                                .border(1.dp, BorderGray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = colorEntity.name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = DarkNavy
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step4_next")
        ) {
            Text("متابعة لاختيار المقاس (Size)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 5: Size & Size Assistant
@Composable
private fun Step5Size(
    selectedSize: String,
    onSelectSize: (String) -> Unit,
    onOpenAssistant: () -> Unit,
    onNext: () -> Unit
) {
    val sizes = listOf("XS", "S", "M", "L", "XL", "XXL")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("اختر المقاس (Size)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("اختر مقاسك المعتاد أو استخدم مساعد المقاس الذكي", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sizes.forEach { size ->
                val isSelected = selectedSize == size
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) DeepPurple700 else Color.White)
                        .border(1.dp, if (isSelected) DeepPurple700 else BorderGray, RoundedCornerShape(12.dp))
                        .clickable { onSelectSize(size) }
                        .testTag("builder_size_$size"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = size,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = if (isSelected) Color.White else DarkNavy
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Help me choose my size CTA
        OutlinedButton(
            onClick = onOpenAssistant,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_size_assistant_button")
        ) {
            Icon(Icons.Default.Straighten, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("مساعد المقاس الذكي (Help me choose my size)", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step5_next")
        ) {
            Text("متابعة لإضافة النص والتصميم", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// Size Assistant Dialog
@Composable
private fun SizeAssistantDialog(onDismiss: () -> Unit, onCalculate: (heightCm: Int, weightKg: Int) -> Unit) {
    var heightText by remember { mutableStateOf("175") }
    var weightText by remember { mutableStateOf("75") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("مساعد اختيار المقاس 📏", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("أدخل طولك ووزنك لحساب المقاس الأمثل لقصتك المختارة:", fontSize = 13.sp, color = TextGray)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = heightText,
                    onValueChange = { heightText = it },
                    label = { Text("الطول بالسنتيمتر (Height in cm)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("size_height_input")
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it },
                    label = { Text("الوزن بالكيلوجرام (Weight in kg)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("size_weight_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val h = heightText.toIntOrNull() ?: 175
                    val w = weightText.toIntOrNull() ?: 75
                    onCalculate(h, w)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
                modifier = Modifier.testTag("size_assistant_calculate_button")
            ) {
                Text("احسب المقاس المناسب")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

// STEP 6: Design & Text Customization
@Composable
private fun Step6DesignAndText(
    currentText: String,
    currentPosition: String,
    currentDesignUrl: String,
    designs: List<DesignEntity>,
    onUpdateDesign: (text: String, textColorHex: String, position: String, design: DesignEntity?) -> Unit,
    onNext: () -> Unit
) {
    var textInput by remember { mutableStateOf(currentText) }
    var positionInput by remember { mutableStateOf(currentPosition) }
    var selectedTextColorHex by remember { mutableStateOf("#00E5FF") }
    var selectedDesign by remember { mutableStateOf<DesignEntity?>(null) }

    val positions = listOf("Front", "Back", "Left Chest", "Right Chest", "Sleeve")
    val textColors = listOf("#FFFFFF", "#00E5FF", "#121212", "#FFD700", "#FF1744", "#76FF03")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("أضف النص والرسومات (Text & Artwork)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("اكتب العبارة المفضلة واختر مكان الطباعة أو الرسومات الجاهزة", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = textInput,
            onValueChange = {
                textInput = it
                onUpdateDesign(it, selectedTextColorHex, positionInput, selectedDesign)
            },
            label = { Text("النص المطبوع (Custom Text)") },
            placeholder = { Text("مثال: CustomWear 2026") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("builder_text_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("لون النص:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            textColors.forEach { hex ->
                val parsed = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { Color.Cyan }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(parsed)
                        .border(2.dp, if (selectedTextColorHex == hex) DeepPurple700 else BorderGray, CircleShape)
                        .clickable {
                            selectedTextColorHex = hex
                            onUpdateDesign(textInput, hex, positionInput, selectedDesign)
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("مكان الطباعة (Design Position):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        LazyRow(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(positions) { pos ->
                FilterChip(
                    selected = positionInput == pos,
                    onClick = {
                        positionInput = pos
                        onUpdateDesign(textInput, selectedTextColorHex, pos, selectedDesign)
                    },
                    label = { Text(pos) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("اختر رسومات وفنون جاهزة من المصممين (+رسوم):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        LazyRow(
            contentPadding = PaddingValues(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(designs) { design ->
                val isSelected = selectedDesign?.id == design.id
                Card(
                    onClick = {
                        selectedDesign = if (isSelected) null else design
                        onUpdateDesign(textInput, selectedTextColorHex, positionInput, selectedDesign)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = if (isSelected) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(DeepPurple700)) else null,
                    modifier = Modifier.width(100.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(6.dp)) {
                        AsyncImage(
                            model = design.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Text(design.name, fontSize = 10.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                        Text("+${design.price.toInt()} EGP", fontSize = 10.sp, color = DeepPurple700)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step6_next")
        ) {
            Text("معاينة الشكـل الحي (Live Preview)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 7: Live Interactive Visual Mockup Preview Canvas
@Composable
private fun Step7InteractivePreview(builderState: BuilderState, onNext: () -> Unit) {
    val shirtColor = remember(builderState.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(builderState.colorHex))
        } catch (e: Exception) {
            Color.Black
        }
    }

    val textColor = remember(builderState.textColorHex) {
        try {
            Color(android.graphics.Color.parseColor(builderState.textColorHex))
        } catch (e: Exception) {
            Color.White
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("المعاينة التفاعلية المباشرة (Live Mockup)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("شاهد شكل تصميمك قبل تأكيد الطلب", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive 2D Canvas Mockup Card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Dynamic T-Shirt Shape Canvas Drawing
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val shirtPath = Path().apply {
                        // Neck collar
                        moveTo(w * 0.35f, h * 0.15f)
                        quadraticTo(w * 0.5f, h * 0.22f, w * 0.65f, h * 0.15f)
                        // Right shoulder & sleeve
                        lineTo(w * 0.88f, h * 0.25f)
                        lineTo(w * 0.78f, h * 0.45f)
                        lineTo(w * 0.72f, h * 0.40f)
                        // Right body side
                        lineTo(w * 0.72f, h * 0.88f)
                        // Bottom hem
                        lineTo(w * 0.28f, h * 0.88f)
                        // Left body side
                        lineTo(w * 0.28f, h * 0.40f)
                        lineTo(w * 0.22f, h * 0.45f)
                        lineTo(w * 0.12f, h * 0.25f)
                        close()
                    }

                    // Fill shirt with selected color
                    drawPath(path = shirtPath, color = shirtColor)
                    // Draw seam outlines
                    drawPath(path = shirtPath, color = Color.Gray.copy(alpha = 0.4f), style = Stroke(width = 3f))
                }

                // Overlay Artwork/Design or Text
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.offset(
                        y = when (builderState.designPosition) {
                            "Left Chest" -> (-30).dp
                            "Right Chest" -> (-30).dp
                            "Back" -> 20.dp
                            "Sleeve" -> (-10).dp
                            else -> 0.dp
                        },
                        x = when (builderState.designPosition) {
                            "Left Chest" -> (-30).dp
                            "Right Chest" -> 30.dp
                            "Sleeve" -> 70.dp
                            else -> 0.dp
                        }
                    )
                ) {
                    if (builderState.designImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = builderState.designImageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    if (builderState.customText.isNotEmpty()) {
                        Text(
                            text = builderState.customText,
                            color = textColor,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary details chip
        Surface(
            color = LightPurple100,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("تفاصيل التصميم الحالية:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DeepPurple700)
                Text("${builderState.clothingType} • ${builderState.fabricName} • ${builderState.fit} • ${builderState.colorName} • Size: ${builderState.size}", fontSize = 12.sp, color = DarkNavy)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step7_next")
        ) {
            Text("الانتقال لحساب التكلفة والسعر", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

// STEP 8: Price Calculator Breakdown
@Composable
private fun Step8PriceBreakdown(builderState: BuilderState, onNext: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("حساب وتفاصيل السعر (Price Breakdown)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("شفافية كاملة لجميع التكاليف والرسوم", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                PriceRow("سعر القطعة الأساسية (${builderState.clothingType}):", "${builderState.basePrice.toInt()} EGP")
                PriceRow("رسوم الخامة المحددة (${builderState.fabricName}):", "+${builderState.fabricPrice.toInt()} EGP")
                PriceRow("رسوم الطباعة والتطريز:", "+${builderState.printingPrice.toInt()} EGP")
                PriceRow("رسوم التصميم والتفصيل المخصص:", "+${builderState.customizationFee.toInt()} EGP")
                PriceRow("تكلفة الشحن والتوصيل:", "+${builderState.deliveryFee.toInt()} EGP")

                Divider(modifier = Modifier.padding(vertical = 12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("الإجمالي الكلي (Total Price):", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkNavy)
                    Text("${(builderState.totalPrice + builderState.deliveryFee).toInt()} EGP", fontWeight = FontWeight.Black, fontSize = 18.sp, color = DeepPurple700)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("builder_step8_next")
        ) {
            Text("تأكيد وإضافة للسلة", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}

@Composable
private fun PriceRow(label: String, price: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextGray)
        Text(price, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
    }
}

// STEP 9: Save & Add to Cart
@Composable
private fun Step9SummaryAndAddToCart(builderState: BuilderState, onUpdateQuantity: (Int) -> Unit, onAddToCart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("تأكيد طلب التفصيل (Final Confirmation)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkNavy)
        Text("اختر الكمية المطلوبة وأضف للسلة", fontSize = 13.sp, color = TextGray)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(builderState.productName, fontWeight = FontWeight.Black, fontSize = 16.sp, color = DarkNavy)
                Spacer(modifier = Modifier.height(8.dp))
                Text("• النوع: ${builderState.clothingType}", fontSize = 13.sp)
                Text("• الخامة: ${builderState.fabricName}", fontSize = 13.sp)
                Text("• القصة: ${builderState.fit}", fontSize = 13.sp)
                Text("• اللون: ${builderState.colorName}", fontSize = 13.sp)
                Text("• المقاس: ${builderState.size}", fontSize = 13.sp)
                if (builderState.customText.isNotEmpty()) Text("• النص المطبوع: '${builderState.customText}'", fontSize = 13.sp, color = DeepPurple700, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("الكمية (Quantity):", fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onUpdateQuantity(builderState.quantity - 1) }) {
                            Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease")
                        }
                        Text(builderState.quantity.toString(), fontWeight = FontWeight.Black, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { onUpdateQuantity(builderState.quantity + 1) }) {
                            Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onAddToCart,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("builder_add_to_cart_final_button")
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("أضف القطعة إلى السلة (${builderState.totalPrice.toInt()} EGP)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
