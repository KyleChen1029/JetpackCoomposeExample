package com.example.loginapp.data // Or com.example.loginapp.model

data class CountryCodeItem(
    val nameResId: Int, // Resource ID for translated country name
    val code: String,        // e.g., "+886"
    val countryIsoCode: String // e.g., "TW" for regex matching later
)

// Sample list of country codes. A real app would have a more comprehensive list,
// possibly loaded from a JSON file or a library.
// Using stringResource IDs from previous steps for display names.
val sampleCountryCodes = listOf(
    CountryCodeItem(nameResId = com.example.loginapp.R.string.taiwan_country_code_display_name, code = "+886", countryIsoCode = "TW"),
    CountryCodeItem(nameResId = com.example.loginapp.R.string.japan_country_code_display_name, code = "+81", countryIsoCode = "JP"),
    CountryCodeItem(nameResId = com.example.loginapp.R.string.korea_country_code_display_name, code = "+82", countryIsoCode = "KR"),
    CountryCodeItem(nameResId = com.example.loginapp.R.string.usa_country_code_display_name, code = "+1", countryIsoCode = "US")
    // Add more countries as needed for testing
)
