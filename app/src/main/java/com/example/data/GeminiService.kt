package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}

object GeminiService {

    private fun getApiKey(): String {
        return BuildConfig.GEMINI_API_KEY
    }

    suspend fun generateResponse(prompt: String, systemInstruction: String? = null): String = withContext(Dispatchers.IO) {
        val key = getApiKey()
        if (key.isEmpty() || key == "MY_GEMINI_API_KEY") {
            return@withContext "API Key is currently missing. Please set your GEMINI_API_KEY in the Secrets panel in AI Studio UI to enable live AI features."
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.7f),
            systemInstruction = systemInstruction?.let { Content(parts = listOf(Part(text = it))) }
        )

        try {
            val response = GeminiClient.service.generateContent(key, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "No response received from Gemini."
        } catch (e: Exception) {
            "Failed to connect to Gemini API: ${e.localizedMessage}. Please ensure you have an active internet connection and that your API key is valid."
        }
    }

    // Smart AI Tool: Estimate Fair Market Value
    suspend fun estimateFairMarketValue(
        category: String,
        breed: String,
        age: Double,
        weight: Int,
        milkYield: Double,
        location: String
    ): String {
        val prompt = """
            Estimate the fair market price range in Indian Rupees (INR) for the following livestock:
            - Category: $category
            - Breed: $breed
            - Age: $age years
            - Weight: $weight kg
            - Daily Milk Yield: $milkYield liters/day
            - Location: $location
            
            Format your response clearly. Include:
            1. Estimated Price Range (e.g., ₹75,000 - ₹82,000)
            2. Justification based on age, breed, and milk production in Punjab/India markets.
            3. Tips to get a better price.
            Keep the response friendly and professional.
        """.trimIndent()
        
        return generateResponse(
            prompt = prompt,
            systemInstruction = "You are PashuValuer, an expert agricultural economist specializing in Indian livestock markets."
        )
    }

    // Smart AI Tool: Detect Possible Fraud Listing
    suspend fun detectFraud(
        breed: String,
        age: Double,
        milkYield: Double,
        price: Double,
        location: String
    ): String {
        val prompt = """
            Assess the fraud risk for this livestock listing:
            - Breed: $breed
            - Age: $age years
            - Daily Milk Yield: $milkYield liters/day
            - Listed Price: ₹$price
            - Location: $location
            
            Look for typical red flags:
            - Price is too good to be true (e.g., extremely low for high milk yield).
            - Unrealistic age vs lactation vs milk yield correlations.
            
            Return a risk score (LOW, MEDIUM, HIGH) and 3 short bullet points explaining why, plus standard safety tips for buyers.
        """.trimIndent()
        
        return generateResponse(
            prompt = prompt,
            systemInstruction = "You are PashuSafety, a listing verification and fraud prevention bot for an Indian livestock marketplace."
        )
    }

    // Smart AI Tool: Feed Recommendation
    suspend fun recommendFeed(
        breed: String,
        category: String,
        milkYield: Double
    ): String {
        val prompt = """
            Provide a daily feed recommendation plan for this animal:
            - Category: $category
            - Breed: $breed
            - Daily Milk Production: $milkYield liters/day
            
            Give standard ratios for:
            1. Green Fodder (e.g. Berseem, Maize silage)
            2. Dry Fodder (e.g. Wheat straw)
            3. Concentrate Mix (granular feed)
            4. Minerals & Salts (e.g. Calcium, bypass fat)
            
            Keep the recommendations practical for farmers in Punjab/India. Use bold headings and clean formatting.
        """.trimIndent()
        
        return generateResponse(
            prompt = prompt,
            systemInstruction = "You are PashuDiet, a professional veterinary nutritionist for dairy farms."
        )
    }
}
