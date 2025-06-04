package com.example.loginapp.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.loginapp.R
import com.example.loginapp.data.CountryCodeItem // Needed for validation context

// Basic regex patterns - these are very simplified and would need to be improved
// for real-world use, ideally sourced from a library or more comprehensive data.
fun getPhoneNumberRegex(countryIsoCode: String?): Regex {
    return when (countryIsoCode?.uppercase()) {
        "TW" -> Regex("^09\d{8}$") // Taiwan: 09xxxxxxxx (10 digits)
        "JP" -> Regex("^(0[7-9]0\d{8}|0[1-9]\d{7})$") // Japan: 070/080/090xxxxxxxx (11 digits) or 0x xxxxxxx (9 digits for landlines, less common for login)
        "KR" -> Regex("^01[016789]\d{7,8}$") // South Korea: 01x-xxx(x)-xxxx (10-11 digits)
        "US" -> Regex("^\d{10}$") // USA: NPA-NXX-XXXX (10 digits)
        else -> Regex("^\d{7,15}$") // Generic fallback: 7 to 15 digits
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneNumberField(
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    selectedCountry: CountryCodeItem?, // To get the ISO code for validation
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { newValue ->
            // Allow only digits for phone number input
            if (newValue.all { it.isDigit() }) {
                onPhoneNumberChange(newValue)
            }
        },
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(R.string.phone_number)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError && phoneNumber.isNotEmpty()) { // Show error only if there's input and it's invalid
                Text(stringResource(R.string.error_invalid_phone_number))
            }
        }
    )
}
