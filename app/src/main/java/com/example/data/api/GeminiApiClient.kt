package com.example.data.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import com.example.data.model.MessageSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {

    private const val TAG = "GeminiApiClient"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNullOrBlank() || key == "MY_GEMINI_API_KEY") "" else key
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        userMessage: String,
        modelName: String = "gemini-3.8-flash",
        systemInstruction: String = "Kamu adalah Sang Resi Pusaka, pemandu spiritual dan ahli taktik perang kuno di dunia Petualangan Legenda Nusantara. Berikan nasihat petualangan, strategi pertarungan, lore pusaka, dan bimbingan dengan nada bijak, karismatik, dan penuh semangat petualangan. Gunakan bahasa Indonesia yang kaya dan menginspirasi."
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            // Provide context-aware smart in-game guide answer when API key is not configured in Secrets
            return@withContext getLocalGuideResponse(userMessage)
        }

        val cleanModel = modelName.removePrefix("models/")
        val url = "$BASE_URL/$cleanModel:generateContent?key=$apiKey"

        val requestJson = JSONObject()

        // System Instruction
        val sysObj = JSONObject()
        val sysParts = JSONArray().put(JSONObject().put("text", systemInstruction))
        sysObj.put("parts", sysParts)
        requestJson.put("systemInstruction", sysObj)

        // Contents (Multi-turn conversation history)
        val contentsArr = JSONArray()
        // Take up to last 10 messages for context
        val recentHistory = history.takeLast(10)
        for (msg in recentHistory) {
            val role = if (msg.sender == MessageSender.USER) "user" else "model"
            val msgObj = JSONObject()
            msgObj.put("role", role)
            val parts = JSONArray().put(JSONObject().put("text", msg.text))
            msgObj.put("parts", parts)
            contentsArr.put(msgObj)
        }

        // Current message
        val currentObj = JSONObject()
        currentObj.put("role", "user")
        currentObj.put("parts", JSONArray().put(JSONObject().put("text", userMessage)))
        contentsArr.put(currentObj)
        requestJson.put("contents", contentsArr)

        // Generation config
        val genConfig = JSONObject()
        genConfig.put("temperature", 0.7)
        requestJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                Log.e(TAG, "Gemini API error ($modelName): ${response.code} - $responseBody")
                // Check if quota or fallback
                return@withContext "Resi Pusaka bergumam: 'Energi langit sedang bergejolak (${response.code}). Namun dengarkan nasihatku: Selalu perhatikan sisa Mana dan simpan jurus terkuatmu untuk membalikkan keadaan!'"
            }

            val json = JSONObject(responseBody)
            val candidates = json.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) return@withContext text
                }
            }

            return@withContext "Resi Pusaka tersenyum: 'Tetap teguh hatimu, Pahlawan. Setiap langkah di medan tempur adalah guru terbaikmu.'"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call Gemini API", e)
            return@withContext "Resi Pusaka: 'Koneksi gaib terputus sejenak: ${e.localizedMessage}. Tetap waspada terhadap serangan mendadak musuh!'"
        }
    }

    suspend fun generateImage(
        prompt: String,
        imageSize: String = "1K", // 1K, 2K, 4K
        modelName: String = "gemini-3-pro-image-preview"
    ): Bitmap = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            throw IllegalStateException(
                "GEMINI_API_KEY belum terpasang di Secrets panel AI Studio. Silakan masukkan API Key Anda untuk menghasilkan gambar resolusi tinggi."
            )
        }

        val cleanModel = modelName.removePrefix("models/")
        val url = "$BASE_URL/$cleanModel:generateContent?key=$apiKey"

        val requestJson = JSONObject()
        val contentsArr = JSONArray()
        val promptObj = JSONObject()
        promptObj.put("parts", JSONArray().put(JSONObject().put("text", prompt)))
        contentsArr.put(promptObj)
        requestJson.put("contents", contentsArr)

        val genConfig = JSONObject()
        genConfig.put("responseModalities", JSONArray().put("IMAGE"))

        val imageConfig = JSONObject()
        imageConfig.put("imageSize", imageSize)
        genConfig.put("imageConfig", imageConfig)

        requestJson.put("generationConfig", genConfig)

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = httpClient.newCall(request).execute()
        val responseBody = response.body?.string().orEmpty()

        if (!response.isSuccessful) {
            Log.e(TAG, "Image generation error: ${response.code} - $responseBody")
            throw IllegalStateException("Gagal membuat gambar ($imageSize): Kode ${response.code} - $responseBody")
        }

        val json = JSONObject(responseBody)
        val candidates = json.optJSONArray("candidates")
        if (candidates != null && candidates.length() > 0) {
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts != null) {
                for (i in 0 until parts.length()) {
                    val part = parts.getJSONObject(i)
                    val inlineData = part.optJSONObject("inlineData")
                    if (inlineData != null) {
                        val base64Data = inlineData.optString("data", "")
                        if (base64Data.isNotBlank()) {
                            val imageBytes = Base64.decode(base64Data, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                            if (bitmap != null) {
                                return@withContext bitmap
                            }
                        }
                    }
                }
            }
        }

        throw IllegalStateException("Model tidak mengembalikan data gambar yang valid.")
    }

    private fun getLocalGuideResponse(query: String): String {
        val lower = query.lowercase()
        return when {
            "level" in lower || "boss" in lower || "kalahkan" in lower -> {
                "Resi Pusaka bersabda: 'Setiap musuh memiliki pola! Pada awal giliran, amati serangannya. Gunakan tombol [Bertahan] untuk memulihkan Mana dan melipatgandakan Pertahananmu saat musuh bersiap melancarkan serangan dahsyat!'"
            }
            "senjata" in lower || "item" in lower || "keris" in lower -> {
                "Resi Pusaka mengangguk: 'Kekuatan sejati pahlawan ada pada penyelarasan pusaka! Kalahkan penjaga tiap level untuk memperebutkan pusaka legendaris seperti Keris Surya Brahmana dan Mahkota Dewata di Tas Inventaris.'"
            }
            "mana" in lower || "skill" in lower -> {
                "Resi Pusaka menasihati: 'Keluarkan jurus [Tebasan Kilat] di awal pertempuran, dan simpan [Aura Suci] ketika HP-mu berada di bawah 40%. Jika kehabisan Mana, gunakan posisi Bertahan atau teguklah Embun Tirta Suci!'"
            }
            else -> {
                "Resi Pusaka memandangmu dengan bijak: 'Perjalanan seribu mil dimulai dengan satu ayunan pedang. Jelajahi kelima level rimba nusantara, raih bintang tertinggi, dan jadilah legenda sejati! Ada yang ingin kau tanyakan lagi, Pahlawan?'"
            }
        }
    }
}
