package com.example.baselift.View.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

/**
 * Rotas da aplicação (Screens)
 */
object Routes {
    const val ONBOARDING = "onboarding"
    const val DASHBOARD = "dashboard"
}

/**
 * AppNavigation é o componente central que gere toda a navegação da aplicação
 * define os ecrãs e as transições entre eles
 */
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.ONBOARDING,
        modifier = modifier
    ) {
        composable(Routes.ONBOARDING) {
            // Ecrã provisório
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Onboarding Screen (Phase 2)")
            }
        }
        
        composable(Routes.DASHBOARD) {
            // Ecrã provisório
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Dashboard Screen (Phase 2)")
            }
        }
    }
}
