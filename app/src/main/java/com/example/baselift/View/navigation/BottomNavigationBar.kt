package com.example.baselift.View.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.baselift.View.theme.DarkSurface
import com.example.baselift.View.theme.MediumGrey
import com.example.baselift.View.theme.NeonGreen
import com.example.baselift.View.theme.PureBlack

enum class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    Dashboard("Dashboard", Icons.Default.Dashboard, "dashboard"),
    Workout("Workout", Icons.Default.FitnessCenter, "workout"),
    Nutrition("Nutrition", Icons.Default.Restaurant, "nutrition"),
    Insights("Insights", Icons.Default.Analytics, "insights") // ícone de análise para insights
}

@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier,
        containerColor = DarkSurface,
        contentColor = MediumGrey
    ) {
        BottomNavItem.values().forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NeonGreen,
                    selectedTextColor = NeonGreen,
                    unselectedIconColor = MediumGrey,
                    unselectedTextColor = MediumGrey,
                    indicatorColor = PureBlack
                )
            )
        }
    }
}
