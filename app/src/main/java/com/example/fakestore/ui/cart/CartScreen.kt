package com.example.fakestore.ui.cart

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.component.EmptyStateView
import com.example.fakestore.component.ErrorView
import com.example.fakestore.component.ShimmeringEffect
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.navigation.FakeStoreRoute
import com.example.fakestore.ui.detail.TopAppBarDetail
import com.example.fakestore.util.navigateAndClean
import com.example.fakestore.util.toFormat
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin

@Composable
fun CartScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartUiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchCarts()
    }

    CartContent(
        cartUiState = cartUiState,
        onCheckout = { navHostController.navigateAndClean(FakeStoreRoute.SummaryCheckout.route) },
        onQuantityChange = { id, increment -> viewModel.updateQuantity(id, increment) },
        onBack = { navHostController.popBackStack() }
    )
}

@Composable
fun CartContent(
    modifier: Modifier = Modifier,
    cartUiState: CartUiState = CartUiState.Loading,
    onCheckout: () -> Unit = {},
    onQuantityChange: (Int, Boolean) -> Unit = { _, _ -> },
    onBack: () -> Unit = {}
) {
    val scrollState = rememberLazyListState()

    val bottomBarElevation by remember {
        derivedStateOf {
            if (scrollState.firstVisibleItemIndex > 0 || scrollState.firstVisibleItemScrollOffset > 0) {
                16.dp
            } else {
                8.dp
            }
        }
    }

    val total = if (cartUiState is CartUiState.Success) {
        cartUiState.total
    } else {
        0.0
    }

    Log.e("philo", "philo total: $total")

    Scaffold(
        topBar = { TopAppBarDetail(title = "Carts", onBack = onBack) },
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                state = scrollState
            ) {
                when (cartUiState) {
                    is CartUiState.Loading -> {
                        items(count = 3) {
                            LoadingItemCart(modifier = Modifier.testTag("loading_item_cart"))
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    is CartUiState.Empty -> {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                EmptyStateView("Cart is empty")
                            }
                        }
                    }

                    is CartUiState.Error -> {
                        item {
                            ErrorView(message = cartUiState.message)
                        }
                    }

                    is CartUiState.Success -> {
                        items(items = cartUiState.items, key = { cart -> cart.id }) { cart ->
                            ItemCart(cart = cart) { isIncrement ->
                                onQuantityChange(
                                    cart.id,
                                    isIncrement
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            BoxBottom(
                modifier = Modifier
                    .align(Alignment.BottomCenter),
                elevation = bottomBarElevation,
                total = total,
                onCheckout = onCheckout
            )
        }
    }
}

@Composable
private fun ItemCart(
    modifier: Modifier = Modifier,
    cart: CartEntity,
    onQuantityChange: (Boolean) -> Unit = {}
) {
    Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CoilImage(
                modifier = Modifier
                    .size(100.dp, 100.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .aspectRatio(3f / 3f),
                imageModel = { cart.imageUrl },
                imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
                component = rememberImageComponent {
                    +CrossfadePlugin()
                    +ShimmerPlugin(
                        Shimmer.Resonate(
                            baseColor = Color.Transparent,
                            highlightColor = Color.LightGray,
                        ),
                    )
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    cart.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Normal
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    cart.price.toFormat(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                BoxQuantity(
                    modifier = Modifier.align(Alignment.End),
                    quantity = cart.quantity,
                    onQuantityChange = onQuantityChange
                )
            }
        }
    }
}

@Composable
private fun BoxQuantity(
    modifier: Modifier = Modifier,
    quantity: Int = 1,
    onQuantityChange: (Boolean) -> Unit = {}
) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary
            )
            .border(1.dp, MaterialTheme.colorScheme.onPrimary, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = { onQuantityChange(false) }, modifier = Modifier.size(24.dp)) {
            if (quantity == 1) {
                Icon(Icons.Default.Delete, "Decrease", tint = MaterialTheme.colorScheme.onPrimary)
            } else {
                Icon(Icons.Default.Remove, "Decrease", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Text(
            text = quantity.toString(),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        IconButton(onClick = { onQuantityChange(true) }, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.Add, "Increase", tint = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun BoxBottom(
    modifier: Modifier = Modifier,
    elevation: Dp = 8.dp,
    total: Double = 0.0,
    onCheckout: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        color = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        tonalElevation = elevation,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    total.toFormat(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .width(120.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                Text(
                    "Checkout",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LoadingItemCart(
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            ShimmeringEffect(modifier = Modifier.size(100.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                ShimmeringEffect(
                    modifier = Modifier
                        .height(16.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShimmeringEffect(
                    modifier = Modifier
                        .height(16.dp)
                        .width(50.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                ShimmeringEffect(
                    modifier = Modifier
                        .align(Alignment.End)
                        .size(width = 100.dp, height = 20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewLoadingItemCart() {
    LoadingItemCart()
}

@Preview
@Composable
private fun PreviewBoxBottom() {
    BoxBottom()
}

@Preview
@Composable
private fun PreviewItemCart() {
    ItemCart(
        cart = CartEntity(
            id = 9517,
            productId = 5655,
            title = "posteaposteaposteaposteaposteaposteapostea",
            price = 2.3,
            quantity = 3508,
            imageUrl = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
            username = "Lou Peters"
        )
    )
}

@Preview
@Composable
private fun PreviewBoxQuantity() {
    BoxQuantity(quantity = 2)
}

@Preview(name = "CartScreen")
@Composable
private fun PreviewCartScreen() {
    CartContent(cartUiState = CartUiState.Empty)
}