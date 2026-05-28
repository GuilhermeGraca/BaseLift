package com.example.baselift.View.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.baselift.AppContainer
import com.example.baselift.View.dashboard.DashboardScreen
import com.example.baselift.View.insights.InsightsScreen
import com.example.baselift.View.onboarding.CustomTargetsScreen
import com.example.baselift.View.onboarding.OnboardingScreen
import com.example.baselift.View.theme.CrystalWhite
import com.example.baselift.ViewModel.onboarding.OnboardingViewModel
import com.example.baselift.ViewModel.onboarding.OnboardingViewModelFactory

/**
 * Rotas da aplicação (Screens)
 */
object Routes {
    const val ONBOARDING = "onboarding"
    const val CUSTOM_TARGETS = "custom_targets"
    const val DASHBOARD = "dashboard"
    const val WORKOUT = "workout"
    const val NUTRITION = "nutrition"
    const val INSIGHTS = "insights"
}

/**
 * AppNavigation é o componente central que gere toda a navegação da aplicação
 * define os ecrãs e as transições entre eles
 */
@Composable
fun AppNavigation(
    appContainer: AppContainer,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Tabs principais na Bottom Navigation Bar
    val mainTabs = listOf(Routes.DASHBOARD, Routes.WORKOUT, Routes.NUTRITION, Routes.INSIGHTS)
    val showBottomBar = currentRoute in mainTabs

    // ViewModel partilhado para o fluxo de onboarding e insights
    val onboardingViewModel: OnboardingViewModel = viewModel(
        factory = OnboardingViewModelFactory(appContainer.userRepository)
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.ONBOARDING,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    viewModel = onboardingViewModel,
                    onNavigateToDashboard = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onNavigateToCustomTargets = {
                        navController.navigate(Routes.CUSTOM_TARGETS)
                    }
                )
            }
            
            composable(Routes.CUSTOM_TARGETS) {
                CustomTargetsScreen(
                    viewModel = onboardingViewModel,
                    onSaveAndSync = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onRevert = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen()
            }
            
            composable(Routes.WORKOUT) {
                // Ecrã provisório
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Workout Screen", color = CrystalWhite)
                }
            }
            
            composable(Routes.NUTRITION) {
                // Ecrã provisório
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Nutrition Screen", color = CrystalWhite)
                }
            }
            
            composable(Routes.INSIGHTS) {
                InsightsScreen(
                    viewModel = onboardingViewModel,
                    onRecalibrate = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.DASHBOARD) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
