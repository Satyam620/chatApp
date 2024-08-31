package com.venom.chatapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.venom.chatapp.data.Screen
import com.venom.chatapp.screen.ChatRoomListScreen
import com.venom.chatapp.screen.SignUpScreen
import com.venom.chatapp.screen.LoginScreen
import com.venom.chatapp.screen.ChatScreen
import com.venom.chatapp.viewmodel.AuthViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.SignupScreen.route
    ) {
        composable(Screen.SignupScreen.route) {
            SignUpScreen(
                authViewModel= authViewModel,
                onNavigateToLogin = { navController.navigate(Screen.LoginScreen.route) }
            )
        }

        composable(Screen.LoginScreen.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignup = { navController.navigate(Screen.SignupScreen.route) }
            ){
                navController.navigate(Screen.ChatRoomsScreen.route)
            }
        }

        composable(Screen.ChatRoomsScreen.route) {
            ChatRoomListScreen{
                navController.navigate("${Screen.ChatScreen.route}/${it.id}")
            }
        }

        composable("${Screen.ChatScreen.route}/{roomId}") {
            val roomId: String = it
                .arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
    }
}

