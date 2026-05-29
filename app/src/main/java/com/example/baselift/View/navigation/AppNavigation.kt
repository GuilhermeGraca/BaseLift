package com.example.baselift.View.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.example.baselift.View.theme.NeonGreen
import com.example.baselift.View.theme.PureBlack
import com.example.baselift.ViewModel.onboarding.OnboardingViewModel
import com.example.baselift.ViewModel.onboarding.OnboardingViewModelFactory
import com.example.baselift.ViewModel.progress.ProgressViewModel
import com.example.baselift.ViewModel.progress.ProgressViewModelFactory

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
        factory = OnboardingViewModelFactory(appContainer.userRepository, appContainer.progressRepository)
    )

    val progressViewModel: ProgressViewModel = viewModel(
        factory = ProgressViewModelFactory(appContainer.progressRepository, appContainer.userRepository)
    )

    val isLoaded by onboardingViewModel.isLoaded.collectAsStateWithLifecycle()
    val isRecalibrating by onboardingViewModel.isRecalibrating.collectAsStateWithLifecycle()

    if (!isLoaded) {
        PremiumSplashScreen()
    } else {
        val startDestination = if (isRecalibrating) Routes.INSIGHTS else Routes.ONBOARDING
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
                startDestination = startDestination,
                modifier = modifier.padding(innerPadding)
            ) {
                composable(Routes.ONBOARDING) {
                    OnboardingScreen(
                        viewModel = onboardingViewModel,
                        onNavigateToDashboard = {
                            navController.navigate(Routes.INSIGHTS) {
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
                        onboardingViewModel = onboardingViewModel,
                        progressViewModel = progressViewModel,
                        onRecalibrate = {
                            onboardingViewModel.startRecalibration()
                            navController.navigate(Routes.ONBOARDING) {
                                popUpTo(Routes.DASHBOARD) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BaseLift",
                color = NeonGreen,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            CircularProgressIndicator(
                color = NeonGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
