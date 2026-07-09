package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(private val database: AppDatabase) {

    val listingDao = database.listingDao()
    val bookingDao = database.bookingDao()
    val messageDao = database.messageDao()
    val favoriteDao = database.favoriteDao()

    val allListings: Flow<List<Listing>> = listingDao.getAllListings()
    val featuredListings: Flow<List<Listing>> = listingDao.getFeaturedListings()
    val activeListings: Flow<List<Listing>> = listingDao.getActiveListings()
    val allBookings: Flow<List<Booking>> = bookingDao.getAllBookings()
    val chatSessions: Flow<List<String>> = messageDao.getChatSessions()
    val favorites: Flow<List<Favorite>> = favoriteDao.getFavorites()

    suspend fun getListingById(id: Long): Listing? = withContext(Dispatchers.IO) {
        listingDao.getListingById(id)
    }

    suspend fun insertListing(listing: Listing): Long = withContext(Dispatchers.IO) {
        listingDao.insertListing(listing)
    }

    suspend fun updateListing(listing: Listing) = withContext(Dispatchers.IO) {
        listingDao.updateListing(listing)
    }

    suspend fun deleteListing(listing: Listing) = withContext(Dispatchers.IO) {
        listingDao.deleteListing(listing)
    }

    suspend fun markAsSold(listingId: Long) = withContext(Dispatchers.IO) {
        listingDao.markAsSold(listingId)
    }

    // Bookings
    suspend fun insertBooking(booking: Booking): Long = withContext(Dispatchers.IO) {
        bookingDao.insertBooking(booking)
    }

    suspend fun updateBooking(booking: Booking) = withContext(Dispatchers.IO) {
        bookingDao.updateBooking(booking)
    }

    suspend fun rateBooking(bookingId: Long, rating: Int) = withContext(Dispatchers.IO) {
        bookingDao.rateBooking(bookingId, rating)
    }

    // Chats
    fun getMessagesForSession(sessionId: String): Flow<List<Message>> {
        return messageDao.getMessagesForSession(sessionId)
    }

    suspend fun sendMessage(sessionId: String, text: String, senderRole: String, transEn: String = "", transHi: String = "", transPa: String = ""): Long = withContext(Dispatchers.IO) {
        val msg = Message(
            sessionId = sessionId,
            text = text,
            senderRole = senderRole,
            translationEn = transEn,
            translationHi = transHi,
            translationPa = transPa
        )
        messageDao.insertMessage(msg)
    }

    // Favorites
    suspend fun toggleFavorite(listingId: Long) = withContext(Dispatchers.IO) {
        val isFav = favoriteDao.isFavorite(listingId)
        if (isFav) {
            favoriteDao.removeFavorite(Favorite(listingId))
        } else {
            favoriteDao.addFavorite(Favorite(listingId))
        }
    }

    suspend fun isFavorite(listingId: Long): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.isFavorite(listingId)
    }

    // Prepopulate Sample Data if Empty
    suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        val existing = activeListings.first()
        if (existing.isEmpty()) {
            val samples = listOf(
                Listing(
                    category = "Cows",
                    breed = "Sahiwal",
                    ageYears = 3.5,
                    weightKg = 420,
                    dailyMilkYieldLiters = 16.5,
                    lactationNumber = 2,
                    pregnancyStatus = "Pregnant (3 Months)",
                    vaccinationHistory = "FMD, Brucellosis, Hemorrhagic Septicemia (HS) - Fully Vaccinated",
                    healthCertificateUrl = "CERT-SAH-992",
                    location = "Ludhiana, Punjab",
                    price = 72000.0,
                    isNegotiable = true,
                    ownerName = "Sardar Harpreet Singh",
                    ownerPhone = "+91 98765 43210",
                    distanceKm = 12.4,
                    isFeatured = true,
                    photosCsv = "sahiwal_cow,cow_detail_1,cow_detail_2"
                ),
                Listing(
                    category = "Buffaloes",
                    breed = "Murrah",
                    ageYears = 4.0,
                    weightKg = 580,
                    dailyMilkYieldLiters = 22.0,
                    lactationNumber = 3,
                    pregnancyStatus = "Not Pregnant",
                    vaccinationHistory = "FMD, Black Quarter (BQ) - Certified Vaccinated",
                    healthCertificateUrl = "CERT-MUR-501",
                    location = "Amritsar, Punjab",
                    price = 115000.0,
                    isNegotiable = false,
                    ownerName = "Gurdev Singh Dhillon",
                    ownerPhone = "+91 87654 32109",
                    distanceKm = 35.8,
                    isFeatured = true,
                    photosCsv = "murrah_buffalo,buffalo_detail_1,buffalo_detail_2"
                ),
                Listing(
                    category = "Cows",
                    breed = "Gir",
                    ageYears = 2.8,
                    weightKg = 390,
                    dailyMilkYieldLiters = 14.0,
                    lactationNumber = 1,
                    pregnancyStatus = "Pregnant (5 Months)",
                    vaccinationHistory = "Lumpy Skin Disease, FMD - Checked",
                    healthCertificateUrl = "CERT-GIR-423",
                    location = "Patiala, Punjab",
                    price = 85000.0,
                    isNegotiable = true,
                    ownerName = "Rajinder Prasad",
                    ownerPhone = "+91 76543 21098",
                    distanceKm = 8.1,
                    isFeatured = false,
                    photosCsv = "gir_cow,cow_detail_3"
                ),
                Listing(
                    category = "Buffaloes",
                    breed = "Nili Ravi",
                    ageYears = 5.0,
                    weightKg = 610,
                    dailyMilkYieldLiters = 19.5,
                    lactationNumber = 4,
                    pregnancyStatus = "Not Pregnant",
                    vaccinationHistory = "Fully Vaccinated (FMD, HS, Anthrax)",
                    healthCertificateUrl = "CERT-NILI-881",
                    location = "Jalandhar, Punjab",
                    price = 98000.0,
                    isNegotiable = true,
                    ownerName = "Sukhbir Singh",
                    ownerPhone = "+91 91234 56789",
                    distanceKm = 24.5,
                    isFeatured = false,
                    photosCsv = "nili_ravi_buffalo"
                ),
                Listing(
                    category = "Goats",
                    breed = "Beetal",
                    ageYears = 1.5,
                    weightKg = 45,
                    dailyMilkYieldLiters = 3.2,
                    lactationNumber = 1,
                    pregnancyStatus = "Not Pregnant",
                    vaccinationHistory = "PPR, Goat Pox - Vaccinated",
                    healthCertificateUrl = "CERT-GOAT-112",
                    location = "Sangrur, Punjab",
                    price = 16500.0,
                    isNegotiable = true,
                    ownerName = "Amrik Singh",
                    ownerPhone = "+91 99887 76655",
                    distanceKm = 42.0,
                    isFeatured = false,
                    photosCsv = "beetal_goat"
                ),
                Listing(
                    category = "Horses",
                    breed = "Marwari",
                    ageYears = 4.5,
                    weightKg = 450,
                    dailyMilkYieldLiters = 0.0,
                    lactationNumber = 0,
                    pregnancyStatus = "Not Pregnant",
                    vaccinationHistory = "Equine Influenza, Tetanus - Fully Vaccinated",
                    healthCertificateUrl = "CERT-EQU-003",
                    location = "Bathinda, Punjab",
                    price = 280000.0,
                    isNegotiable = true,
                    ownerName = "Sardar Baldev Singh",
                    ownerPhone = "+91 98111 22233",
                    distanceKm = 56.2,
                    isFeatured = true,
                    photosCsv = "marwari_horse"
                ),
                Listing(
                    category = "Bulls",
                    breed = "Sahiwal Bull",
                    ageYears = 3.0,
                    weightKg = 620,
                    dailyMilkYieldLiters = 0.0,
                    lactationNumber = 0,
                    pregnancyStatus = "N/A",
                    vaccinationHistory = "FMD, Brucellosis, HS",
                    healthCertificateUrl = "CERT-BULL-342",
                    location = "Hoshiarpur, Punjab",
                    price = 95000.0,
                    isNegotiable = true,
                    ownerName = "Manpreet Johal",
                    ownerPhone = "+91 88822 33445",
                    distanceKm = 18.9,
                    isFeatured = false,
                    photosCsv = "sahiwal_bull"
                )
            )

            for (sample in samples) {
                listingDao.insertListing(sample)
            }

            // Let's add some default bookings to look real
            bookingDao.insertBooking(
                Booking(
                    type = "Veterinary",
                    providerName = "Dr. Amanpreet Singh (B.V.Sc & A.H)",
                    date = "2026-07-10",
                    timeSlot = "10:00 AM - 11:30 AM",
                    details = "General Health Checkup & Pregnancy Pregnancy Scan for Sahiwal Cow",
                    price = 500.0,
                    status = "Confirmed"
                )
            )
            bookingDao.insertBooking(
                Booking(
                    type = "Transport",
                    providerName = "Sher-E-Punjab Livestock Carriers",
                    date = "2026-07-12",
                    timeSlot = "08:00 AM",
                    details = "Ludhiana Farm to Jalandhar Dairy Hub - Large Live Animal Carrier Truck",
                    price = 4500.0,
                    status = "Pending"
                )
            )

            // Let's also add some initial chat messages
            val session = "gurdev_harpreet"
            messageDao.insertMessage(
                Message(
                    sessionId = session,
                    text = "Hello Harpreet, is the Sahiwal Cow still available?",
                    senderRole = "Buyer",
                    translationEn = "Hello Harpreet, is the Sahiwal Cow still available?",
                    translationHi = "नमस्कार हरप्रीत, क्या साहिवाल गाय अभी भी उपलब्ध है?",
                    translationPa = "ਸਤਿ ਸ੍ਰੀ ਅਕਾਲ ਹਰਪ੍ਰੀਤ, ਕੀ ਸਾਹੀਵਾਲ ਗਾਂ ਅਜੇ ਵੀ ਮਿਲ ਸਕਦੀ ਹੈ?"
                )
            )
            messageDao.insertMessage(
                Message(
                    sessionId = session,
                    text = "Yes Gurdev ji, she is available. Yield is around 16.5 liters daily.",
                    senderRole = "Seller",
                    translationEn = "Yes Gurdev ji, she is available. Yield is around 16.5 liters daily.",
                    translationHi = "हां गुरदेव जी, वह उपलब्ध है। दैनिक दूध उत्पादन लगभग 16.5 लीटर है।",
                    translationPa = "ਹਾਂ ਗੁਰਦੇਵ ਜੀ, ਉਹ ਮਿਲ ਸਕਦੀ ਹੈ। ਦੁੱਧ ਦਾ ਝਾੜ ਰੋਜ਼ਾਨਾ ਲਗਭਗ 16.5 ਲੀਟਰ ਹੈ।"
                )
            )
        }
    }
}
