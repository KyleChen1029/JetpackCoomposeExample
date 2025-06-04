package com.example.loginapp

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
// import androidx.compose.runtime.Composable // Added for @Composable annotation if needed for helper functions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginapp.data.sampleCountryCodes // For providing to CountryCodeSelectionScreen
import com.example.loginapp.ui.navigation.AppRoutes // Corrected import
import com.example.loginapp.ui.components.CountryCodeSelectionScreen
import com.example.loginapp.ui.screens.LoginScreen
import com.example.loginapp.ui.theme.LoginAppTheme
import com.example.loginapp.utils.LocaleManager
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val initialLocale = Locale("zh", "TW")
        Locale.setDefault(initialLocale)
        val config = Configuration(newBase.resources.configuration)
        config.setLocale(initialLocale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = AppRoutes.LOGIN_SCREEN) {
                    composable(AppRoutes.LOGIN_SCREEN) {
                        LoginScreen(navController = navController)
                    }
                    composable(AppRoutes.COUNTRY_CODE_SELECTION_SCREEN) {
                        CountryCodeSelectionScreen(
                            navController = navController,
                            countryCodes = sampleCountryCodes // Pass the list here
                        )
                    }
                    // composable("home_screen_route") { // Placeholder for home screen
                    //    Text("Welcome Home!")
                    // }
                }
            }
        }
    }

    fun setLanguage(languageCode: String) {
        LocaleManager.setLocale(this, languageCode)
        recreate()
    }
}
