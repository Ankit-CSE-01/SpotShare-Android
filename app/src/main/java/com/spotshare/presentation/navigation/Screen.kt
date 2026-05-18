package com.spotshare.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Auth : Screen("auth")
    object SignUp : Screen("sign_up")
    object Home : Screen("home")
    object Feed : Screen("feed")
    object Explore : Screen("explore")
    object Reels : Screen("reels")
    object CreateReel : Screen("create_reel")
    object AddSpot : Screen("add_spot")
    object Map : Screen("map")
    object Profile : Screen("profile?userId={userId}") {
        fun createRoute(userId: String? = null) = if (userId != null) "profile?userId=$userId" else "profile"
    }
    object EditProfile : Screen("edit_profile")
    object SpotDetail : Screen("spot_detail/{spotId}") {
        fun createRoute(spotId: String) = "spot_detail/$spotId"
    }
    object StoryView : Screen("story_view/{userId}") {
        fun createRoute(userId: String) = "story_view/$userId"
    }
    object CreatePost : Screen("create_post")
    object MediaPicker : Screen("media_picker")
    object CreateStory : Screen("create_story")
    object ChatList : Screen("chat_list")
    object Chat : Screen("chat/{chatId}") {
        fun createRoute(chatId: String) = "chat/$chatId"
    }
    object Notifications : Screen("notifications")
    object SocialList : Screen("social_list/{userId}/{tab}") {
        fun createRoute(userId: String, tab: Int) = "social_list/$userId/$tab"
    }
    object Settings : Screen("settings")
}
