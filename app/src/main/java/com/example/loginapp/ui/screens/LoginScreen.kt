package com.example.loginapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState // For observing LiveData from SavedStateHandle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
// import com.example.loginapp.data.sampleCountryCodes // For preview or providing list if needed
import com.example.loginapp.presentation.login.LoginIntent
import com.example.loginapp.presentation.login.LoginViewModel
import com.example.loginapp.ui.components.*
import com.example.loginapp.ui.navigation.AppRoutes // Corrected import
// import com.example.loginapp.ui.theme.LoginAppTheme // For Preview

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = uiState.loginError) {
        uiState.loginError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.onIntent(LoginIntent.ErrorMessageShown)
        }
    }
    LaunchedEffect(key1 = uiState.generalMessage) {
        uiState.generalMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onIntent(LoginIntent.GeneralMessageShown)
        }
    }
    LaunchedEffect(key1 = uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            Toast.makeText(context, "Login Success! (Navigation to Home placeholder)", Toast.LENGTH_LONG).show()
            // Actual navigation: navController.navigate("home_screen_route") { popUpTo(AppRoutes.LOGIN_SCREEN) { inclusive = true } }
            viewModel.onIntent(LoginIntent.NavigatedToHome)
        }
    }

    LaunchedEffect(key1 = uiState.navigateToCountryCodePicker) {
        if (uiState.navigateToCountryCodePicker) {
            navController.navigate(AppRoutes.COUNTRY_CODE_SELECTION_SCREEN)
            viewModel.onIntent(LoginIntent.NavigatedToCountryCodePicker)
        }
    }

    val selectedCountryCodeResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>(AppRoutes.COUNTRY_CODE_RESULT_KEY) // Ensure non-nullable String if always set
        ?.observeAsState()

    LaunchedEffect(selectedCountryCodeResult?.value) { // Observe the value itself
        selectedCountryCodeResult?.value?.let { countryIsoCode ->
            if (countryIsoCode.isNotEmpty()){ // Process only if not empty
                viewModel.onIntent(LoginIntent.CountryCodeReturned(countryIsoCode))
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>(AppRoutes.COUNTRY_CODE_RESULT_KEY)
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) { LanguageSwitcher() }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            SelectedCountryCodeDisplay(
                selectedCountryCode = uiState.selectedCountry,
                onClick = { viewModel.onIntent(LoginIntent.CountryCodePickerOpened) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            PhoneNumberField(
                phoneNumber = uiState.phoneNumber,
                onPhoneNumberChange = { viewModel.onIntent(LoginIntent.PhoneNumberChanged(it)) },
                selectedCountry = uiState.selectedCountry,
                isError = uiState.isPhoneNumberError,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                RememberMeCheckbox(
                    checked = uiState.rememberMeChecked,
                    onCheckedChange = { viewModel.onIntent(LoginIntent.RememberMeToggled(it)) }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            LoginButton(
                onClick = { viewModel.onIntent(LoginIntent.LoginClicked) },
                enabled = uiState.phoneNumber.isNotEmpty() && !uiState.isPhoneNumberError && !uiState.isLoading
            )
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.weight(0.8f))
        }
    }
}

// Comment out or update previews as they might not work correctly without NavController
// @Preview(showBackground = true)
// @Composable
// fun LoginScreenPreview() { ... }
