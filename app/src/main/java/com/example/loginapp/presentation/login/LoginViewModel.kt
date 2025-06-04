package com.example.loginapp.presentation.login

import android.app.Application // Required for Application context
import androidx.lifecycle.AndroidViewModel // Change from ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginapp.data.local.UserPreferencesRepository
import com.example.loginapp.data.sampleCountryCodes
import com.example.loginapp.ui.components.getPhoneNumberRegex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first // To get initial value
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// Change to AndroidViewModel to get Application context for UserPreferencesRepository
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    init {
        loadInitialPreferences()
    }

    private fun loadInitialPreferences() {
        viewModelScope.launch {
            val prefs = userPreferencesRepository.userPreferencesFlow.first()
            prefs.rememberedCountryIsoCode?.let { isoCode ->
                sampleCountryCodes.firstOrNull { it.countryIsoCode == isoCode }?.let { country ->
                    val phoneNumber = prefs.rememberedPhoneNumber ?: ""
                    val regex = getPhoneNumberRegex(country.countryIsoCode)
                    val isError = phoneNumber.isNotEmpty() && !regex.matches(phoneNumber)
                    _uiState.update {
                        it.copy(
                            selectedCountry = country,
                            phoneNumber = phoneNumber,
                            rememberMeChecked = true, // If details are saved, "Remember Me" should be checked
                            isPhoneNumberError = isError
                        )
                    }
                }
            }
        }
    }

    fun onIntent(intent: LoginIntent) {
        viewModelScope.launch {
            when (intent) {
                is LoginIntent.CountrySelected -> handleCountrySelected(intent.country)
                is LoginIntent.PhoneNumberChanged -> handlePhoneNumberChanged(intent.number)
                is LoginIntent.RememberMeToggled -> handleRememberMeToggled(intent.checked)
                LoginIntent.LoginClicked -> handleLoginClicked()
                LoginIntent.CountryCodePickerOpened -> _uiState.update { it.copy(navigateToCountryCodePicker = true, loginError = null) }
                LoginIntent.NavigatedToCountryCodePicker -> _uiState.update { it.copy(navigateToCountryCodePicker = false) }
                is LoginIntent.CountryCodeReturned -> {
                    sampleCountryCodes.firstOrNull { it.countryIsoCode == intent.countryIsoCode }?.let { country ->
                        handleCountrySelected(country)
                    }
                }
                LoginIntent.ErrorMessageShown -> _uiState.update { it.copy(loginError = null) }
                LoginIntent.GeneralMessageShown -> _uiState.update { it.copy(generalMessage = null) }
                LoginIntent.NavigatedToHome -> _uiState.update { it.copy(navigateToHome = false) }
            }
        }
    }

    private fun handleRememberMeToggled(checked: Boolean) {
        _uiState.update { it.copy(rememberMeChecked = checked) }
        // If "Remember Me" is unchecked, clear saved preferences
        if (!checked) {
            viewModelScope.launch {
                userPreferencesRepository.clearLoginDetails()
                _uiState.update { it.copy(generalMessage = "Login details will not be remembered.") }
            }
        }
    }

    private fun handleCountrySelected(country: com.example.loginapp.data.CountryCodeItem) {
        val currentPhoneNumber = _uiState.value.phoneNumber
        val regex = getPhoneNumberRegex(country.countryIsoCode)
        val isError = currentPhoneNumber.isNotEmpty() && !regex.matches(currentPhoneNumber)
        _uiState.update {
            it.copy(selectedCountry = country, isPhoneNumberError = isError, loginError = null)
        }
    }

    private fun handlePhoneNumberChanged(number: String) {
        val regex = getPhoneNumberRegex(_uiState.value.selectedCountry.countryIsoCode)
        val isError = number.isNotEmpty() && !regex.matches(number)
        _uiState.update {
            it.copy(phoneNumber = number, isPhoneNumberError = isError, loginError = null)
        }
    }

    private fun handleLoginClicked() {
        val currentState = _uiState.value
        if (currentState.phoneNumber.isEmpty() || currentState.isPhoneNumberError) {
            _uiState.update { it.copy(loginError = "Invalid phone number.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, loginError = null) }

        viewModelScope.launch {
            delay(1500) // Simulate network request

            if (currentState.phoneNumber == "0912345678" && currentState.selectedCountry.countryIsoCode == "TW") { // Mock Success
                if (currentState.rememberMeChecked) {
                    userPreferencesRepository.saveLoginDetails(
                        currentState.selectedCountry.countryIsoCode,
                        currentState.phoneNumber
                    )
                    _uiState.update { it.copy(isLoading = false, navigateToHome = true, generalMessage = "Login details saved.") }
                } else {
                    // If remember me is not checked, ensure any previously saved details are cleared.
                    // This might already be handled by handleRememberMeToggled, but can be explicit here too.
                    userPreferencesRepository.clearLoginDetails()
                    _uiState.update { it.copy(isLoading = false, navigateToHome = true, generalMessage = "Logged in (not remembered).") }
                }
            } else { // Mock Fail
                // Optionally clear details on failed login if "Remember Me" was checked
                // if (currentState.rememberMeChecked) {
                //     userPreferencesRepository.clearLoginDetails()
                // }
                _uiState.update { it.copy(isLoading = false, loginError = "Login failed. Check credentials.") }
            }
        }
    }
}
