package com.example.blinkitusersideclone

import com.example.blinkituserclone.modals.Category

object Constants {

    val MERCHANT_ID = "PGTESTPAYUAT86"
    val SALT_KEY = "96434309-7796-489d-8924-ab56988a6076"
    val MERCHANT_TRANSACTION_ID = "MT7850590068188104"
    val MERCHANT_USER_ID = "MU933037302229373"
    val SALT_KEY_INDEX = "1"
    val API_END_POINT = "/pg/v1/pay"


    val categoryList = arrayListOf(
        Category("Spices", R.drawable.masala),
        Category("Chocolate", R.drawable.sweet_tooth),
        Category("Vegetables", R.drawable.vegetable),
        Category("Personal Care", R.drawable.personal_care),
        Category("Home and Office", R.drawable.home_office),
        Category("Stationary", R.drawable.paan_corner),
        Category("Kitchens and Bathrooms", R.drawable.cleaning),
        Category("Dairy", R.drawable.dairy_breakfast),
        Category("Bakery", R.drawable.bakery_biscuits),
        Category("Cold Drinks", R.drawable.cold_and_juices),
        Category("Baby Care", R.drawable.baby_care),
        Category("Medicines", R.drawable.pharma_wellness),
        Category("Pets", R.drawable.pet_care),
        Category("Beverages", R.drawable.tea),
        Category("Kitchen", R.drawable.organic_premium)

    )

}