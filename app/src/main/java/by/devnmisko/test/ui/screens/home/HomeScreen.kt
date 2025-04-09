package by.devnmisko.test.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import by.devnmisko.test.ui.screens.cart.Cart
import by.devnmisko.test.ui.screens.products.Products
import by.devnmisko.test.ui.screens.profile.Profile

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val bottomNavController = rememberNavController()
    
    Scaffold(
        modifier = modifier,
        bottomBar = { BottomNavBar(bottomNavController) }
    ) {  padding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.Products.route,
        ) {
            composable(Screen.Products.route) { Products(modifier = Modifier.padding(padding)) }
            composable(Screen.Cart.route) { Cart(modifier = Modifier.padding(padding)) }
            composable(Screen.Profile.route) { Profile(modifier = Modifier.padding(padding)) }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screen.Products,
        Screen.Cart,
        Screen.Profile
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                }
            )
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Products : Screen("products", "Товары", Icons.Default.Home)
    object Cart : Screen("cart", "Корзина", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Профиль", Icons.Default.Person)
}