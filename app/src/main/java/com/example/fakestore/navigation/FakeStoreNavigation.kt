package com.example.fakestore.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.ui.cart.CartScreen
import com.example.fakestore.ui.detail.DetailProductScreen
import com.example.fakestore.ui.home.HomeScreen
import com.example.fakestore.ui.login.LoginScreen
import com.example.fakestore.ui.summary.SummaryScreen

@Composable
fun FakeStoreNavigation(navHostController: NavHostController = rememberNavController()) {
    NavHost(navController = navHostController, startDestination = FakeStoreRoute.Login.route) {
        composable(route = FakeStoreRoute.Login.route) {
            LoginScreen(navigator = navHostController)
        }
        composable(route = FakeStoreRoute.Home.route) {
            HomeScreen(navHostController = navHostController)
        }
        composable(route = FakeStoreRoute.DetailProduct.route) {
            val id = it.arguments?.getString("id")?.toInt() ?: 0
            DetailProductScreen(navHostController = navHostController, idProduct = id)
        }
        composable(route = FakeStoreRoute.Cart.route) {
            CartScreen(navHostController = navHostController)
        }
        composable(route = FakeStoreRoute.SummaryCheckout.route) {
            SummaryScreen(navHostController = navHostController)
        }
    }
}