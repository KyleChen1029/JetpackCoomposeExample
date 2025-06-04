package com.example.loginapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Prefer this
// import androidx.compose.material.icons.filled.ArrowBack // Fallback
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.loginapp.R
import com.example.loginapp.data.CountryCodeItem
import com.example.loginapp.ui.navigation.AppRoutes // Corrected import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedCountryCodeDisplay(
    selectedCountryCode: CountryCodeItem?, // Allow null for initial state
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField( // Using OutlinedTextField to give it a defined boundary
        value = selectedCountryCode?.let { stringResource(id = it.nameResId) } ?: stringResource(R.string.select_country_code),
        onValueChange = {}, // Not directly editable
        readOnly = true,
        label = { Text(stringResource(R.string.country_code)) },
        trailingIcon = {
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "Select Country Code",
                modifier = Modifier.clickable(onClick = onClick) // Make icon clickable too
            )
        },
        modifier = modifier
            .clickable(onClick = onClick) // Make the whole field clickable
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeSelectionScreen(
    navController: NavController,
    countryCodes: List<CountryCodeItem>
) {
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    val filteredCountries = if (searchQuery.isEmpty()) {
        countryCodes
    } else {
        countryCodes.filter {
            context.getString(it.nameResId).contains(searchQuery, ignoreCase = true) ||
            it.code.contains(searchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_country_code)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.abc_action_bar_up_description)) // Or use R.string.back
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.search)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(filteredCountries) { country ->
                    Text(
                        text = "${stringResource(id = country.nameResId)} (${country.code})",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set(AppRoutes.COUNTRY_CODE_RESULT_KEY, country.countryIsoCode)
                                navController.popBackStack()
                            }
                            .padding(vertical = 12.dp)
                    )
                    Divider()
                }
            }
        }
    }
}
