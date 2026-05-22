package com.spotshare.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.spotshare.presentation.screens.auth.AuthViewModel
import com.spotshare.presentation.screens.auth.AuthState
import com.spotshare.presentation.screens.auth.LoginScreen
import com.spotshare.presentation.screens.auth.SignUpScreen
import com.spotshare.presentation.screens.auth.OnboardingScreen
import com.spotshare.presentation.screens.splash.SplashScreen
import com.spotshare.presentation.screens.feed.FeedScreen
import com.spotshare.presentation.screens.explore.ExploreScreen
import com.spotshare.presentation.screens.reels.ReelsScreen
import com.spotshare.presentation.screens.profile.ProfileScreen
import com.spotshare.presentation.screens.settings.SettingsScreen
import com.spotshare.presentation.screens.chat.ChatListScreen
import com.spotshare.presentation.screens.chat.ChatScreen
import com.spotshare.presentation.screens.story.StoryViewScreen
import com.spotshare.presentation.screens.story.CreateStoryScreen
import com.spotshare.presentation.screens.create.CreatePostScreen
import com.spotshare.presentation.screens.create.MediaPickerScreen
import com.spotshare.presentation.screens.reels.CreateReelScreen
import com.spotshare.presentation.screens.profile.EditProfileScreen
import com.spotshare.presentation.screens.notifications.NotificationScreen
import com.spotshare.presentation.screens.profile.SocialListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(
                onLoginSuccess = { state ->
                    if (state is AuthState.RequiresOnboarding) {
                        navController.navigate(Screen.Onboarding.route)
                    } else {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                viewModel = authViewModel
            )
        }
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.SignUp.route) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Feed.route) {
            FeedScreen(
                onNotificationsClick = { navController.navigate(Screen.Notifications.route) },
                onMessagesClick = { navController.navigate(Screen.ChatList.route) },
                onAddStoryClick = { navController.navigate(Screen.CreateStory.route) },
                onStoryClick = { userId -> navController.navigate(Screen.StoryView.createRoute(userId)) },
                onPostClick = { _ -> },
                onProfileClick = { userId -> navController.navigate(Screen.Profile.createRoute(userId)) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.CreateStory.route) {
            CreateStoryScreen(
                onBackClick = { navController.popBackStack() },
                onStoryCreated = { navController.popBackStack() }
            )
        }
        composable(route = Screen.Explore.route) {
            ExploreScreen(
                onPostClick = { _ -> }
            )
        }
        composable(route = Screen.Reels.route) {
            ReelsScreen(
                onBackClick = { navController.popBackStack() },
                onCameraClick = { navController.navigate(Screen.CreateReel.route) },
                onProfileClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                },
                onLocationClick = { _ -> },
                onMessageClick = { userId ->
                    navController.navigate(Screen.Profile.createRoute(userId))
                }
            )
        }
        composable(route = Screen.CreateReel.route) {
            CreateReelScreen(
                onBackClick = { navController.popBackStack() },
                onReelCreated = { navController.popBackStack() }
            )
        }
        composable(route = Screen.CreatePost.route) {
            CreatePostScreen(
                onBackClick = { navController.popBackStack() },
                onPostCreated = {
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(Screen.Feed.route) { inclusive = true }
                    }
                },
                onCameraClick = {
                    navController.navigate(Screen.CreateStory.route)
                },
                onOpenGallery = {
                    navController.navigate(Screen.MediaPicker.route)
                }
            )
        }
        composable(route = Screen.MediaPicker.route) {
            MediaPickerScreen(
                onBackClick = { navController.popBackStack() },
                onMediaSelected = { uris ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_media", uris.map { it.toString() })
                    navController.popBackStack()
                }
            )
        }
        
        // Define both routes to handle the optional userId safely
        composable(route = "profile") {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onShareProfileClick = { _ -> },
                onFollowersClick = { userId -> navController.navigate(Screen.SocialList.createRoute(userId, 0)) },
                onFollowingClick = { userId -> navController.navigate(Screen.SocialList.createRoute(userId, 1)) },
                onNavigateToChat = { chatId -> navController.navigate(Screen.Chat.createRoute(chatId)) }
            )
        }

        composable(
            route = Screen.Profile.ROUTE_WITH_ARGS,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                onShareProfileClick = { _ -> },
                onFollowersClick = { userId -> navController.navigate(Screen.SocialList.createRoute(userId, 0)) },
                onFollowingClick = { userId -> navController.navigate(Screen.SocialList.createRoute(userId, 1)) },
                onNavigateToChat = { chatId -> navController.navigate(Screen.Chat.createRoute(chatId)) }
            )
        }

        composable(route = Screen.EditProfile.route) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }
        composable(route = Screen.ChatList.route) {
            ChatListScreen(
                onBackClick = { navController.popBackStack() },
                onChatClick = { chatId -> navController.navigate(Screen.Chat.createRoute(chatId)) }
            )
        }
        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument("chatId") { type = NavType.StringType })
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            ChatScreen(
                chatId = chatId,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(route = Screen.Notifications.route) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = Screen.SocialList.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("tab") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val tab = backStackEntry.arguments?.getInt("tab") ?: 0
            SocialListScreen(
                username = "Explorer", 
                initialTab = tab,
                onBackClick = { navController.popBackStack() },
                onUserClick = { targetId ->
                    navController.navigate(Screen.Profile.createRoute(targetId))
                }
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
