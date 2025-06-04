package com.example.loginapp.presentation.login

import com.example.loginapp.data.CountryCodeItem
import com.example.loginapp.data.sampleCountryCodes

data class LoginState(
    val selectedCountry: CountryCodeItem = sampleCountryCodes.first { it.countryIsoCode == "TW" },
    val phoneNumber: String = "",
    val isPhoneNumberError: Boolean = false,
    val rememberMeChecked: Boolean = false,
    val isLoading: Boolean = false,
    val loginError: String? = null,
    val generalMessage: String? = null,
    // val showCountryCodeSelection: Boolean = false, // No longer needed
    val navigateToCountryCodePicker: Boolean = false, // Trigger for navigation
    val navigateToHome: Boolean = false
)

sealed class LoginIntent {
    data class CountrySelected(val country: CountryCodeItem) : LoginIntent() // Retained for direct setting if needed
    data class PhoneNumberChanged(val number: String) : LoginIntent()
    data class RememberMeToggled(val checked: Boolean) : LoginIntent()
    object LoginClicked : LoginIntent()
    object CountryCodePickerOpened : LoginIntent() // User action to open picker
    // object CountryCodePickerDismissed : LoginIntent() // Handled by NavController
    object NavigatedToCountryCodePicker : LoginIntent() // To reset navigation trigger in ViewModel
    data class CountryCodeReturned(val countryIsoCode: String) : LoginIntent() // Intent for result from picker
    object ErrorMessageShown : LoginIntent()
    object GeneralMessageShown : LoginIntent()
    object NavigatedToHome : LoginIntent()
    // LanguageChangeRequested can be added if VM handles it
}
