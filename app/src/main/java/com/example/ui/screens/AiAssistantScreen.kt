package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Send
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
import com.example.data.api.AiCustomizationRecommendation
import com.example.ui.theme.*

@Composable
fun AiAssistantScreen(
    promptInput: String,
    onPromptChange: (String) -> Unit,
    isLoading: Boolean,
    recommendation: AiCustomizationRecommendation?,
    onSubmitPrompt: () -> Unit,
    onApplyRecommendation: (AiCustomizationRecommendation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SoftGrayBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightCyan100),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanDark)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("مساعد الموضة الذكي (AI Fashion Assistant)", fontSize = 18.sp, fontWeight = FontWeight.Black, color = DarkNavy)
                Text("اكتب ما يدور في خاطرك وسيحلله الذكاء الاصطناعي لتصميم ملابسك!", fontSize = 11.sp, color = TextGray)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Example chip shortcuts
        Text("أمثلة سريعة للتحربة:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkNavy)

        val quickExamples = listOf(
            "عايز تيشيرت oversized أسود، خامة قطن، وتصميم بسيط أبيض.",
            "عايز بولو كحلي شيك للعمل خامة بيكية.",
            "هودي واسع رمادي خامة قطن ثقيل."
        )

        quickExamples.forEach { ex ->
            SuggestionChip(
                onClick = {
                    onPromptChange(ex)
                },
                label = { Text(ex, fontSize = 11.sp) },
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = promptInput,
            onValueChange = onPromptChange,
            placeholder = { Text("صف زيك المطلوب بالكامل هنا...") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_prompt_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onSubmitPrompt,
            enabled = promptInput.isNotBlank() && !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DeepPurple700),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("ai_submit_button")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("جاري التحليل الذكي...")
            } else {
                Icon(Icons.Default.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تحليل وتحديد المواصفات (Analyze)")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recommendation Result Breakdown Card
        recommendation?.let { rec ->
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_recommendation_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("التوصية المقترحة لك ✨", fontWeight = FontWeight.Black, fontSize = 16.sp, color = DeepPurple700)
                    Text(rec.description, fontSize = 12.sp, color = TextGray, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))

                    Divider()

                    Spacer(modifier = Modifier.height(12.dp))

                    RecommendationRow("نوع الملابس (Clothing):", rec.clothingType)
                    RecommendationRow("الخامة (Fabric):", rec.fabric)
                    RecommendationRow("قصة التفصيل (Fit):", rec.fit)
                    RecommendationRow("اللون المقترح (Color):", rec.colorName)
                    RecommendationRow("النص المطبوع (Design Text):", rec.customText)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onApplyRecommendation(rec) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyanDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("ai_apply_choices_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تطبيق هذه الخيارات بالكامل في أداة التصميم", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = TextGray)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkNavy)
    }
}
