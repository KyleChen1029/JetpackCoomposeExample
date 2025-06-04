package com.example.loginapp.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import java.util.Locale

object LocaleManager {

    const val LANGUAGE_ENGLISH = "en"
    const val LANGUAGE_KOREAN = "ko"
    const val LANGUAGE_JAPANESE = "ja"
    const val LANGUAGE_CHINESE_TRADITIONAL = "zh-TW" // Locale for Traditional Chinese (Taiwan)

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            LANGUAGE_CHINESE_TRADITIONAL -> Locale("zh", "TW")
            else -> Locale(languageCode)
        }
        Locale.setDefault(locale)

        val resources: Resources = context.resources
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale) // Handle RTL layouts if necessary

        return context.createConfigurationContext(configuration)
    }

    fun updateResources(context: Context, language: String): ContextWrapper {
        val locale = when (language) {
            LANGUAGE_CHINESE_TRADITIONAL -> Locale("zh", "TW")
            else -> Locale(language)
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        val newContext = context.createConfigurationContext(config)
        return ContextWrapper(newContext)
    }
}

// Helper class for wrapping context, usually placed in the same file or a separate utils file
class ContextWrapper(base: Context) : android.content.ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, newLocale: Locale): ContextWrapper {
            var context = context
            val config = Configuration(context.resources.configuration)
            config.setLocale(newLocale)
            context = context.createConfigurationContext(config)
            return ContextWrapper(context)
        }
    }
}
