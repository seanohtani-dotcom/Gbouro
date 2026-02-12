package com.animegallery.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.animegallery.app.presentation.auth.LoginScreen
import com.animegallery.app.presentation.auth.RegisterScreen
import com.animegallery.app.presentation.detail.DetailScreen
import com.animegallery.app.presentation.favorites.FavoritesScreen
import com.animegallery.app.presentation.fullscreen.FullscreenViewer
import com.animegallery.app.presentation.home.HomeScreen
import com.animegallery.app.presentation.profile.ProfileScreen

/**
 * Navigation routes
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detail : Screen("detail/{imageId}") {
        fun createRoute(imageId: String) = "detail/$imageId"
    }
    object Favorites : Screen("favorites")
    object Fullscreen : Screen("fullscreen/{imageUrl}") {
        fun createRoute(imageUrl: String) = "fullscreen/${java.net.URLEncoder.encode(imageUrl, "UTF-8")}"
    }
    object Login : Screen("login")
    object Register : Screen("register")
    object Profile : Screen("profile")
}

/**
 * Main navigation graph
 */
@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onImageClick = { imageId ->
                    navController.navigate(Screen.Detail.createRoute(imageId))
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onSkipLogin = {
                    navController.popBackStack()
                },
                onLoginSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignOut = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(
                navArgument("imageId") { type = NavType.StringType }
            )
        ) {
            DetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onImageClick = { imageUrl ->
                    navController.navigate(Screen.Fullscreen.createRoute(imageUrl))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onNavigateBack = { navController.popBackStack() },
                onImageClick = { imageId ->
                    navController.navigate(Screen.Detail.createRoute(imageId))
                }
            )
        }

        dialog(
            route = Screen.Fullscreen.route,
            arguments = listOf(
                navArgument("imageUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageUrl = backStackEntry.arguments?.getString("imageUrl")
                ?.let { java.net.URLDecoder.decode(it, "UTF-8") } ?: ""
            
            FullscreenViewer(
                imageUrl = imageUrl,
                onClose = { navController.popBackStack() }
            )
        }
    }
}
