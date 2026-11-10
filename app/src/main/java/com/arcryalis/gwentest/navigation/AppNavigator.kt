package com.arcryalis.gwentest.navigation

import androidx.navigation3.runtime.NavKey

class AppNavigator(private val backStack: MutableList<NavKey>) {

    fun navigate(key: NavKey) {
        if (backStack.lastOrNull() != key) {
            backStack.add(key)
        }
    }

    fun navigateBack() {
        if (backStack.size > 1) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
}
