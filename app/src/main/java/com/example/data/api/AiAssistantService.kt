package com.example.data.api

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AiCustomizationRecommendation(
    val clothingType: String,
    val fabric: String,
    val fit: String,
    val colorName: String,
    val colorHex: String,
    val customText: String,
    val description: String
)

object AiAssistantService {

    suspend fun analyzeStylePrompt(userPrompt: String): AiCustomizationRecommendation = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val response = callGeminiApi(userPrompt, apiKey)
                if (response != null) return@withContext response
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // Fallback local smart NLP parser for Egyptian Arabic & English fashion prompts
        return@withContext parsePromptLocally(userPrompt)
    }

    private fun callGeminiApi(prompt: String, apiKey: String): AiCustomizationRecommendation? {
        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json")
        conn.doOutput = true

        val systemInstruction = "You are CustomWear AI Fashion Assistant. Analyze fashion prompts in Arabic or English and output raw JSON ONLY without markdown: " +
                "{\"clothingType\":\"T-Shirt|Polo|Shirt|Hoodie|Sweatshirt\",\"fabric\":\"Cotton (قطن 100%)|Pique (بيكية)|Linen (كتان نقي)|Cotton Blend (مخلوط فخم)\",\"fit\":\"Oversized|Regular|Slim|Relaxed\",\"colorName\":\"أسود (Black)|أبيض (White)|كحلي (Navy)|رمادي (Gray)|أحمر (Red)|أخضر (Green)|أزرق سماوي (Blue)|بيج (Beige)\",\"colorHex\":\"#121212|#FFFFFF|#1A237E|#757575|#D32F2F|#2E7D32|#1976D2|#F5F5DC\",\"customText\":\"extracted or minimalist text\",\"description\":\"Short friendly explanation in Arabic\"}"

        val jsonBody = JSONObject().apply {
            put("contents", org.json.JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", org.json.JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", "$systemInstruction\nUser request: $prompt")
                        })
                    })
                })
            })
        }

        conn.outputStream.use { os ->
            os.write(jsonBody.toString().toByteArray())
        }

        if (conn.responseCode == 200) {
            val responseText = conn.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(responseText)
            val candidates = jsonResponse.getJSONArray("candidates")
            if (candidates.length() > 0) {
                val candidate = candidates.getJSONObject(0)
                val text = candidate.getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                val cleanedText = text.replace("```json", "").replace("```", "").trim()
                val parsed = JSONObject(cleanedText)
                return AiCustomizationRecommendation(
                    clothingType = parsed.optString("clothingType", "T-Shirt"),
                    fabric = parsed.optString("fabric", "Cotton (قطن 100%)"),
                    fit = parsed.optString("fit", "Oversized"),
                    colorName = parsed.optString("colorName", "أسود (Black)"),
                    colorHex = parsed.optString("colorHex", "#121212"),
                    customText = parsed.optString("customText", "CUSTOMWEAR"),
                    description = parsed.optString("description", "تم تحليل مواصفاتك بنجاح واختيار أفضل الخامات والألوان المناسبة لأسلوبك!")
                )
            }
        }
        return null
    }

    private fun parsePromptLocally(prompt: String): AiCustomizationRecommendation {
        val lower = prompt.lowercase()

        val clothingType = when {
            lower.contains("هودي") || lower.contains("hoodie") -> "Hoodie"
            lower.contains("بولو") || lower.contains("polo") -> "Polo"
            lower.contains("قميص") || lower.contains("shirt") -> "Shirt"
            lower.contains("سويت") || lower.contains("sweatshirt") -> "Sweatshirt"
            else -> "T-Shirt"
        }

        val fabric = when {
            lower.contains("بوليستر") || lower.contains("polyester") -> "Polyester Blend (بوليستر رياضي)"
            lower.contains("بيكية") || lower.contains("pique") -> "Pique (بيكية)"
            lower.contains("كتان") || lower.contains("linen") -> "Linen (كتان نقي)"
            lower.contains("مخلوط") || lower.contains("blend") -> "Cotton Blend (مخلوط فخم)"
            else -> "Cotton (قطن 100%)"
        }

        val fit = when {
            lower.contains("سليم") || lower.contains("slim") -> "Slim"
            lower.contains("واسع") || lower.contains("relaxed") -> "Relaxed"
            lower.contains("عادي") || lower.contains("regular") -> "Regular"
            else -> "Oversized"
        }

        val (colorName, colorHex) = when {
            lower.contains("أبيض") || lower.contains("ابيض") || lower.contains("white") -> "أبيض (White)" to "#FFFFFF"
            lower.contains("كحلي") || lower.contains("navy") -> "كحلي (Navy)" to "#1A237E"
            lower.contains("رمادي") || lower.contains("gray") -> "رمادي (Gray)" to "#757575"
            lower.contains("أحمر") || lower.contains("احمر") || lower.contains("red") -> "أحمر (Red)" to "#D32F2F"
            lower.contains("أخضر") || lower.contains("اخضر") || lower.contains("green") -> "أخضر (Green)" to "#2E7D32"
            lower.contains("أزرق") || lower.contains("ازرق") || lower.contains("blue") -> "أزرق سماوي (Blue)" to "#1976D2"
            lower.contains("بيج") || lower.contains("beige") -> "بيج (Beige)" to "#F5F5DC"
            else -> "أسود (Black)" to "#121212"
        }

        val customText = when {
            lower.contains("بسيط") -> "Minimalist"
            lower.contains("أبيض") && lower.contains("أسود") -> "Urban 2026"
            else -> "CUSTOMWEAR"
        }

        val description = "بناءً على طلبك \"$prompt\"، اخترنا لك قطعة $clothingType بتفصيل $fit بخامة $fabric بلون $colorName."

        return AiCustomizationRecommendation(
            clothingType = clothingType,
            fabric = fabric,
            fit = fit,
            colorName = colorName,
            colorHex = colorHex,
            customText = customText,
            description = description
        )
    }
}
