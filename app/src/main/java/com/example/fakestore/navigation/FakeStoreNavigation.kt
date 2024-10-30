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
import com.example.fakestore.util.ConstTransition

@Composable
fun FakeStoreNavigation(navHostController: NavHostController = rememberNavController()) {
    NavHost(navController = navHostController, startDestination = FakeStoreRoute.Login.route) {
        composable(route = FakeStoreRoute.Login.route) {
            LoginScreen(navigator = navHostController)
        }
        composable(route = FakeStoreRoute.Home.route) {
            HomeScreen(navHostController = navHostController)
        }
        composable(route = FakeStoreRoute.DetailProduct.route, enterTransition = {
            ConstTransition.ENTER_SLIDE_IN_HORIZONTAL
        }, exitTransition = {
            ConstTransition.EXIT_SLIDE_OUT_HORIZONTAL
        }, popEnterTransition = {
            ConstTransition.POP_ENTER_SLIDE_IN_HORIZONTAL
        }, popExitTransition = {
            ConstTransition.POP_EXIT_SLIDE_IN_HORIZONTAL
        }) {
            val id = it.arguments?.getString("id")?.toInt() ?: 0
            DetailProductScreen(navHostController = navHostController, idProduct = id)
        }
        composable(route = FakeStoreRoute.Cart.route, enterTransition = {
            ConstTransition.ENTER_SCALE_IN_CART
        }, exitTransition = {
            ConstTransition.EXIT_SCALE_OUT_CART
        }, popEnterTransition = {
            ConstTransition.POP_ENTER_SCALE_IN_CART
        }, popExitTransition = {
            ConstTransition.POP_EXIT_SCALE_OUT_CART
        }) {
            CartScreen(navHostController = navHostController)
        }
        composable(route = FakeStoreRoute.SummaryCheckout.route, enterTransition = {
            ConstTransition.ENTER_SLIDE_IN_HORIZONTAL
        }, exitTransition = {
            ConstTransition.EXIT_SLIDE_OUT_HORIZONTAL
        }, popEnterTransition = {
            ConstTransition.POP_ENTER_SLIDE_IN_HORIZONTAL
        }, popExitTransition = {
            ConstTransition.POP_EXIT_SLIDE_IN_HORIZONTAL
        }) {
            SummaryScreen(navHostController = navHostController)
        }
    }
}