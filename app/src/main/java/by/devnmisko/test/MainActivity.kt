package by.devnmisko.test

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import by.devnmisko.test.ui.common.ChangeStatusBarColor
import by.devnmisko.test.ui.common.SnackbarController
import by.devnmisko.test.ui.screens.auth.Auth
import by.devnmisko.test.ui.screens.auth.AuthViewModel
import by.devnmisko.test.ui.screens.home.HomeScreen
import by.devnmisko.test.ui.theme.TestBKSTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var snackbarController: SnackbarController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val context = LocalContext.current

            TestBKSTheme {
                ChangeStatusBarColor(MaterialTheme.colorScheme.surface)
                val appViewModel: AuthViewModel = hiltViewModel()
                val isUserLoggedIn by appViewModel.isUserLoggedIn.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    contentWindowInsets = WindowInsets.systemBars,
                ) { paddingValues ->
                    if (!isUserLoggedIn) {
                        Auth()
                    } else {
                        HomeScreen(navController = navController)
                    }
                }

                val snackbar by snackbarController.snackbars.collectAsState()
                LaunchedEffect(snackbar) {
                    snackbar.firstOrNull()?.run {
                        snackbarHostState.showSnackbar(
                            message = messageRes,
                            actionLabel = actionLabelRes?.let { context.getString(it) },
                            withDismissAction = withDismissAction,
                            duration = duration
                        )
                        snackbarController.dismissSnackbar(this@run)
                    }
                }
            }
        }
    }

}