package com.example.loginapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language // Example icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.loginapp.MainActivity // Required to call setLanguage
import com.example.loginapp.R
import com.example.loginapp.utils.LocaleManager

data class LanguageOption(val code: String, val displayNameResId: Int)

val supportedLanguages = listOf(
    LanguageOption(LocaleManager.LANGUAGE_ENGLISH, R.string.lang_en),
    LanguageOption(LocaleManager.LANGUAGE_KOREAN, R.string.lang_ko),
    LanguageOption(LocaleManager.LANGUAGE_JAPANESE, R.string.lang_ja),
    LanguageOption(LocaleManager.LANGUAGE_CHINESE_TRADITIONAL, R.string.lang_zh_tw)
)

@Composable
fun LanguageSwitcher() {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var currentLanguageCode by remember {
        // Determine current language from context configuration
        val currentLocale = context.resources.configuration.locales[0]
        val lang = if (currentLocale.language == "zh" && currentLocale.country == "TW") {
            LocaleManager.LANGUAGE_CHINESE_TRADITIONAL
        } else {
            currentLocale.language
        }
        mutableStateOf(supportedLanguages.firstOrNull { it.code.startsWith(lang) }?.code ?: LocaleManager.LANGUAGE_CHINESE_TRADITIONAL)
    }

    IconButton(onClick = { showDialog = true }) {
        Icon(
            imageVector = Icons.Filled.Language,
            contentDescription = stringResource(id = R.string.language)
        )
    }

    if (showDialog) {
        LanguageSelectionDialog(
            currentLanguageCode = currentLanguageCode,
            onLanguageSelected = { languageCode ->
                currentLanguageCode = languageCode
                val activity = context as? MainActivity
                activity?.setLanguage(languageCode) // Call MainActivity's method to change language
                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguageCode: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.language)) },
        text = {
            Column {
                supportedLanguages.forEach { lang ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(lang.code) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = lang.code == currentLanguageCode,
                            onClick = { onLanguageSelected(lang.code) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = lang.displayNameResId))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK") // Or use a string resource
            }
        }
    )
}
