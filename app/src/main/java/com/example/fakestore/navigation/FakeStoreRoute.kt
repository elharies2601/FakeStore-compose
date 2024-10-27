package com.example.fakestore.navigation

sealed class FakeStoreRoute(val route: String) {
    data object Login: FakeStoreRoute("login")
    data object Home: FakeStoreRoute("home")
    data object DetailProduct: FakeStoreRoute("detail_product/{id}") {
        fun createRoute(id: Int) = "detail_product/$id"
    }
    data object Cart: FakeStoreRoute("cart")
    data object SummaryCheckout: FakeStoreRoute("summary")
}