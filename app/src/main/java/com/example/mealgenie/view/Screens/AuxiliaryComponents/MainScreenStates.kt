package com.example.mealgenie.view.Screens.AuxiliaryComponents

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/*Ocultar/Mostrar la BottomNavBar*/
class MainScreenStates {
    var isBottomBarVisible by mutableStateOf(true)
        private set

    fun updateVisibility(visible: Boolean){
        isBottomBarVisible = visible
    }

}

@Composable
fun rememberMainScreenState(): MainScreenStates {
    return remember { MainScreenStates() }
}