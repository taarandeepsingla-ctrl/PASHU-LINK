package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Booking
import com.example.data.Listing
import com.example.data.Message
import com.example.data.Repository
import com.example.data.GeminiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PashuViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = Repository(database)

    // --- Active User Preferences ---
    val currentRole = MutableStateFlow("Buyer") // "Buyer", "Seller", "Veterinarian", "Admin"
    val currentLanguage = MutableStateFlow("English") // "English", "Hindi", "Punjabi"
    val currentUserPhone = MutableStateFlow("+91 94630-12345")
    val currentUserName = MutableStateFlow("Sardar Amarjit Singh")
    val isUserVerified = MutableStateFlow(true)

    // --- Search & Filter States ---
    val searchQuery = MutableStateFlow("")
    val filterCategory = MutableStateFlow("All") // All, Cows, Buffaloes, Bulls, Calves, Goats, Sheep, Horses
    val filterBreed = MutableStateFlow("All")
    val filterMaxPrice = MutableStateFlow(350000.0)
    val filterMinMilk = MutableStateFlow(0.0)
    val filterOnlyVerified = MutableStateFlow(false)
    val filterOnlyPregnant = MutableStateFlow(false)

    // --- Dynamic Streams ---
    val listingsList: StateFlow<List<Listing>> = repository.activeListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredListings: StateFlow<List<Listing>> = repository.featuredListings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookingsList: StateFlow<List<Booking>> = repository.allBookings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatSessions: StateFlow<List<String>> = repository.chatSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<Long>> = repository.favorites
        .map { favs -> favs.map { it.listingId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- AI Tool States ---
    val aiResponseText = MutableStateFlow("")
    val aiLoading = MutableStateFlow(false)

    // --- AI Guru Chat States ---
    private val _aiGuruMessages = MutableStateFlow<List<Message>>(listOf(
        Message(sessionId = "ai_guru", text = "Sat Sri Akal! I am your AI Dairy Guru. Ask me anything about animal feed, health, breeding, or government subsidies.", senderRole = "Seller")
    ))
    val aiGuruMessages: StateFlow<List<Message>> = _aiGuruMessages.asStateFlow()
    val aiGuruLoading = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            // Seed sample database listings for first launch
            repository.prepopulateIfEmpty()
        }
    }

    // --- Filtered Listings ---
    val filteredListings: StateFlow<List<Listing>> = combine(
        listingsList,
        searchQuery,
        filterCategory,
        filterBreed,
        filterMaxPrice,
        filterMinMilk,
        filterOnlyVerified,
        filterOnlyPregnant
    ) { array ->
        val listings = array[0] as List<Listing>
        val query = array[1] as String
        val cat = array[2] as String
        val breed = array[3] as String
        val maxPrice = array[4] as Double
        val minMilk = array[5] as Double
        val verified = array[6] as Boolean
        val pregnant = array[7] as Boolean

        listings.filter { item ->
            val matchesQuery = item.breed.contains(query, ignoreCase = true) ||
                    item.location.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            
            val matchesCat = cat == "All" || item.category.equals(cat, ignoreCase = true)
            val matchesBreed = breed == "All" || item.breed.equals(breed, ignoreCase = true)
            val matchesPrice = item.price <= maxPrice
            val matchesMilk = item.dailyMilkYieldLiters >= minMilk
            val matchesVerified = !verified || item.distanceKm < 15.0 // Representing nearby/verified criteria
            val matchesPregnant = !pregnant || item.pregnancyStatus.contains("Pregnant", ignoreCase = true)

            matchesQuery && matchesCat && matchesBreed && matchesPrice && matchesMilk && matchesVerified && matchesPregnant
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- Action Methods ---

    fun toggleFavorite(listingId: Long) {
        viewModelScope.launch {
            repository.toggleFavorite(listingId)
        }
    }

    fun addListing(
        category: String,
        breed: String,
        age: Double,
        weight: Int,
        milkYield: Double,
        lactation: Int,
        pregnancy: String,
        vaccination: String,
        location: String,
        price: Double,
        isNegotiable: Boolean,
        photosCsv: String = ""
    ) {
        viewModelScope.launch {
            val newListing = Listing(
                category = category,
                breed = breed,
                ageYears = age,
                weightKg = weight,
                dailyMilkYieldLiters = milkYield,
                lactationNumber = lactation,
                pregnancyStatus = pregnancy,
                vaccinationHistory = vaccination,
                healthCertificateUrl = "CERT-LOCAL-${(100..999).random()}",
                location = location,
                price = price,
                isNegotiable = isNegotiable,
                ownerName = currentUserName.value,
                ownerPhone = currentUserPhone.value,
                distanceKm = (2..20).random().toDouble(),
                isFeatured = false,
                photosCsv = photosCsv.ifEmpty { "default_animal" }
            )
            repository.insertListing(newListing)
        }
    }

    fun deleteListing(listing: Listing) {
        viewModelScope.launch {
            repository.deleteListing(listing)
        }
    }

    fun markAsSold(listingId: Long) {
        viewModelScope.launch {
            repository.markAsSold(listingId)
        }
    }

    // Bookings
    fun bookVetOrTransport(type: String, provider: String, date: String, slot: String, details: String, price: Double) {
        viewModelScope.launch {
            val booking = Booking(
                type = type,
                providerName = provider,
                date = date,
                timeSlot = slot,
                details = details,
                price = price,
                status = "Pending"
            )
            repository.insertBooking(booking)
        }
    }

    fun rateBooking(bookingId: Long, rating: Int) {
        viewModelScope.launch {
            repository.rateBooking(bookingId, rating)
        }
    }

    // Chat Actions
    fun getMessages(sessionId: String): Flow<List<Message>> {
        return repository.getMessagesForSession(sessionId)
    }

    fun sendChatMessage(sessionId: String, text: String, sender: String) {
        viewModelScope.launch {
            // First, insert local message
            repository.sendMessage(sessionId, text, sender)
            
            // Auto translate or trigger mock responses to make the chat feel alive
            if (sender == "Buyer") {
                val responseText = "Hello! I received your inquiry about the animal. Let me verify the details for you."
                // In addition, call Gemini to get Punjabi and Hindi translations of the text to support multi-language chat translations
                viewModelScope.launch {
                    val prompt = """
                        Translate this livestock chat message into Hindi and Punjabi. Return exactly as:
                        HINDI: <translation>
                        PUNJABI: <translation>
                        
                        Message: "$text"
                    """.trimIndent()
                    val translationResult = GeminiService.generateResponse(prompt)
                    
                    var hi = ""
                    var pa = ""
                    try {
                        if (translationResult.contains("HINDI:") && translationResult.contains("PUNJABI:")) {
                            hi = translationResult.substringAfter("HINDI:").substringBefore("PUNJABI:").trim()
                            pa = translationResult.substringAfter("PUNJABI:").trim()
                        }
                    } catch (e: Exception) {
                        hi = "अनुवाद उपलब्ध नहीं है"
                        pa = "ਅਨੁਵਾਦ ਉਪਲਬਧ ਨਹੀਂ ਹੈ"
                    }
                    
                    repository.sendMessage(
                        sessionId = sessionId,
                        text = responseText,
                        senderRole = "Seller",
                        transEn = responseText,
                        transHi = "नमस्ते! मुझे पशु के बारे में आपका संदेश मिला। मैं विवरण सत्यापित करता हूँ।",
                        transPa = "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ! ਮੈਨੂੰ ਪਸ਼ੂ ਬਾਰੇ ਤੁਹਾਡਾ ਸੁਨੇਹਾ ਮਿਲਿਆ। ਮੈਂ ਵੇਰਵਿਆਂ ਦੀ ਪੁਸ਼ਟੀ ਕਰਦਾ ਹਾਂ।"
                    )
                }
            }
        }
    }

    // --- AI Smart Tools Triggers ---

    fun runFairValueCalculator(category: String, breed: String, age: Double, weight: Int, milkYield: Double, location: String) {
        viewModelScope.launch {
            aiLoading.value = true
            aiResponseText.value = ""
            val res = GeminiService.estimateFairMarketValue(category, breed, age, weight, milkYield, location)
            aiResponseText.value = res
            aiLoading.value = false
        }
    }

    fun runFraudRiskDetector(breed: String, age: Double, milkYield: Double, price: Double, location: String) {
        viewModelScope.launch {
            aiLoading.value = true
            aiResponseText.value = ""
            val res = GeminiService.detectFraud(breed, age, milkYield, price, location)
            aiResponseText.value = res
            aiLoading.value = false
        }
    }

    fun runFeedRecommender(breed: String, category: String, milkYield: Double) {
        viewModelScope.launch {
            aiLoading.value = true
            aiResponseText.value = ""
            val res = GeminiService.recommendFeed(breed, category, milkYield)
            aiResponseText.value = res
            aiLoading.value = false
        }
    }

    fun askAiGuru(userMessage: String) {
        if (userMessage.isBlank()) return
        viewModelScope.launch {
            aiGuruLoading.value = true
            val userMsg = Message(sessionId = "ai_guru", text = userMessage, senderRole = "Buyer")
            _aiGuruMessages.value = _aiGuruMessages.value + userMsg

            val instruction = """
                You are PashuGuru, a knowledgeable and compassionate dairy farming advisor for Punjab and India. 
                Answer queries about cows, buffaloes, breeding, feed mixtures, silage, lactation, milk yield improvements, 
                veterinary tips, and central/Punjab dairy subsidies (e.g. NABARD schemes). 
                Respond in a helpful, warm tone, incorporating traditional Punjabi wisdom where helpful.
            """.trimIndent()

            val response = GeminiService.generateResponse(userMessage, instruction)
            val aiMsg = Message(sessionId = "ai_guru", text = response, senderRole = "Seller")
            _aiGuruMessages.value = _aiGuruMessages.value + aiMsg
            aiGuruLoading.value = false
        }
    }
}
