package com.example.ui

object LanguageHelper {

    private val translations = mapOf(
        "app_tagline" to mapOf(
            "English" to "Punjab's Premium Livestock Marketplace",
            "Hindi" to "पंजाब का प्रीमियम पशुधन बाजार",
            "Punjabi" to "ਪੰਜਾਬ ਦਾ ਪ੍ਰੀਮੀਅਮ ਪਸ਼ੂਧਨ ਬਾਜ਼ਾਰ"
        ),
        "search_hint" to mapOf(
            "English" to "Search Sahiwal, Murrah, Ludhiana...",
            "Hindi" to "साहिवाल, मुर्राह, लुधियाना खोजें...",
            "Punjabi" to "ਸਾਹੀਵਾਲ, ਮੁਰੱਰਾ, ਲੁਧਿਆਣਾ ਲੱਭੋ..."
        ),
        "categories" to mapOf(
            "English" to "Categories",
            "Hindi" to "श्रेणियाँ",
            "Punjabi" to "ਸ਼੍ਰੇਣੀਆਂ"
        ),
        "featured_animals" to mapOf(
            "English" to "Featured Livestock",
            "Hindi" to "विशेष रूप से प्रदर्शित पशु",
            "Punjabi" to "ਖਾਸ ਪਸ਼ੂਧਨ"
        ),
        "newly_added" to mapOf(
            "English" to "Newly Added",
            "Hindi" to "हाल ही में जोड़ा गया",
            "Punjabi" to "ਨਵੇਂ ਸ਼ਾਮਲ ਕੀਤੇ ਗਏ"
        ),
        "gov_schemes" to mapOf(
            "English" to "Government Schemes",
            "Hindi" to "सरकारी योजनाएं",
            "Punjabi" to "ਸਰਕਾਰੀ ਸਕੀਮਾਂ"
        ),
        "dairy_news" to mapOf(
            "English" to "Dairy News & Subsidies",
            "Hindi" to "डेयरी समाचार और सब्सिडी",
            "Punjabi" to "ਡੇਅਰੀ ਖਬਰਾਂ ਅਤੇ ਸਬਸਿਡੀਆਂ"
        ),
        "breed" to mapOf(
            "English" to "Breed",
            "Hindi" to "नस्ल",
            "Punjabi" to "ਨਸਲ"
        ),
        "age" to mapOf(
            "English" to "Age",
            "Hindi" to "उम्र",
            "Punjabi" to "ਉਮਰ"
        ),
        "weight" to mapOf(
            "English" to "Weight",
            "Hindi" to "वजन",
            "Punjabi" to "ਭਾਰ"
        ),
        "milk_yield" to mapOf(
            "English" to "Daily Yield",
            "Hindi" to "दैनिक दूध उत्पादन",
            "Punjabi" to "ਰੋਜ਼ਾਨਾ ਦੁੱਧ ਝਾੜ"
        ),
        "lactation" to mapOf(
            "English" to "Lactation No.",
            "Hindi" to "ब्यात संख्या",
            "Punjabi" to "ਸੂਆ ਨੰਬਰ"
        ),
        "pregnancy" to mapOf(
            "English" to "Pregnancy Status",
            "Hindi" to "गर्भावस्था की स्थिति",
            "Punjabi" to "ਗਰਭ ਅਵਸਥਾ"
        ),
        "vaccinated" to mapOf(
            "English" to "Vaccinated",
            "Hindi" to "टीकाकृत",
            "Punjabi" to "ਟੀਕਾਕਰਨ ਕੀਤਾ"
        ),
        "vaccination_history" to mapOf(
            "English" to "Vaccination History",
            "Hindi" to "टीकाकरण का इतिहास",
            "Punjabi" to "ਟੀਕਾਕਰਨ ਦਾ ਇਤਿਹਾਸ"
        ),
        "location" to mapOf(
            "English" to "Location",
            "Hindi" to "स्थान",
            "Punjabi" to "ਸਥਾਨ"
        ),
        "price" to mapOf(
            "English" to "Price",
            "Hindi" to "कीमत",
            "Punjabi" to "ਕੀਮਤ"
        ),
        "negotiable" to mapOf(
            "English" to "Negotiable",
            "Hindi" to "मोल-भाव संभव",
            "Punjabi" to "ਮੁੱਲ ਘਟਣਯੋਗ"
        ),
        "verified_seller" to mapOf(
            "English" to "Verified Seller",
            "Hindi" to "सत्यापित विक्रेता",
            "Punjabi" to "ਵੈਰੀਫਾਈਡ ਵਿਕਰੇਤਾ"
        ),
        "save_favorite" to mapOf(
            "English" to "Save Favorite",
            "Hindi" to "पसंदीदा सहेजें",
            "Punjabi" to "ਪਸੰਦ ਸੇਵ ਕਰੋ"
        ),
        "chat_seller" to mapOf(
            "English" to "Chat with Owner",
            "Hindi" to "मालिक से चैट करें",
            "Punjabi" to "ਮਾਲਕ ਨਾਲ ਗੱਲ ਕਰੋ"
        ),
        "book_transport" to mapOf(
            "English" to "Book Transport",
            "Hindi" to "परिवहन बुक करें",
            "Punjabi" to "ਟਰਾਂਸਪੋਰਟ ਬੁੱਕ ਕਰੋ"
        ),
        "book_vet" to mapOf(
            "English" to "Book Vet Checkup",
            "Hindi" to "पशु चिकित्सक बुक करें",
            "Punjabi" to "ਡਾਕਟਰ ਬੁੱਕ ਕਰੋ"
        ),
        "add_livestock" to mapOf(
            "English" to "Add Livestock",
            "Hindi" to "पशुधन जोड़ें",
            "Punjabi" to "ਪਸ਼ੂ ਜੋੜੋ"
        ),
        "my_listings" to mapOf(
            "English" to "My Listings",
            "Hindi" to "मेरी लिस्टिंग",
            "Punjabi" to "ਮੇਰੀਆਂ ਲਿਸਟਿੰਗਾਂ"
        ),
        "inquiries" to mapOf(
            "English" to "Inquiries",
            "Hindi" to "पूछताछ",
            "Punjabi" to "ਪੁੱਛਗਿੱਛ"
        ),
        "sales_analytics" to mapOf(
            "English" to "Sales Analytics",
            "Hindi" to "बिक्री विश्लेषण",
            "Punjabi" to "ਵਿਕਰੀ ਵਿਸ਼ਲੇਸ਼ਣ"
        ),
        "earnings" to mapOf(
            "English" to "Total Earnings",
            "Hindi" to "कुल कमाई",
            "Punjabi" to "ਕੁੱਲ ਕਮਾਈ"
        ),
        "aadhaar_verify" to mapOf(
            "English" to "Aadhaar / Gov ID Verification",
            "Hindi" to "आधार / सरकारी पहचान पत्र सत्यापन",
            "Punjabi" to "ਆਧਾਰ / ਸਰਕਾਰੀ ਆਈਡੀ ਵੈਰੀਫਿਕੇਸ਼ਨ"
        )
    )

    fun get(key: String, language: String): String {
        return translations[key]?.get(language) ?: translations[key]?.get("English") ?: key
    }
}
