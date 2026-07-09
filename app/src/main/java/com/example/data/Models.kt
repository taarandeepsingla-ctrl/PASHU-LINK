package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // Cows, Buffaloes, Bulls, Calves, Goats, Sheep, Horses
    val breed: String,
    val ageYears: Double,
    val weightKg: Int,
    val dailyMilkYieldLiters: Double,
    val lactationNumber: Int,
    val pregnancyStatus: String, // e.g. "Pregnant (3 months)", "Not Pregnant"
    val vaccinationHistory: String, // e.g. "FMD, Brucellosis"
    val healthCertificateUrl: String,
    val location: String, // e.g. "Ludhiana, Punjab"
    val price: Double,
    val isNegotiable: Boolean,
    val ownerName: String,
    val ownerPhone: String,
    val distanceKm: Double,
    val dateListed: Long = System.currentTimeMillis(),
    val isFeatured: Boolean = false,
    val isSold: Boolean = false,
    val photosCsv: String = "", // Comma-separated list of photo image names or links
    val videoUrl: String = ""
)

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "Veterinary", "Vaccination", "Transport"
    val providerName: String,
    val date: String,
    val timeSlot: String,
    val details: String, // e.g. "Truck booking Ludhiana to Amritsar"
    val price: Double,
    val status: String, // "Pending", "Confirmed", "Completed"
    val rating: Int = 0 // After completion, user can rate 1-5
)

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String, // Conversation ID
    val text: String,
    val senderRole: String, // "Buyer", "Seller"
    val timestamp: Long = System.currentTimeMillis(),
    val translationEn: String = "",
    val translationHi: String = "",
    val translationPa: String = ""
)

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey val listingId: Long
)
