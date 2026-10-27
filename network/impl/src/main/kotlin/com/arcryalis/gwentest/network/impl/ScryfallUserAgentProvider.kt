package com.arcryalis.gwentest.network.impl

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScryfallUserAgentProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val scryfallUserAgent: String by lazy {
        val version = runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull()

        if (version != null) "${context.packageName}/$version" else context.packageName
    }
}
