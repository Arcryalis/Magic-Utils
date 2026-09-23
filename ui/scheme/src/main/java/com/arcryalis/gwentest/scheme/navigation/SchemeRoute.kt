package com.arcryalis.gwentest.scheme.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class SchemeRoute(
    val setId: String
) : NavKey