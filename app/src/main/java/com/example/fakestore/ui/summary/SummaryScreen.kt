package com.example.fakestore.ui.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.component.EmptyStateView
import com.example.fakestore.component.ErrorView
import com.example.fakestore.component.ShimmeringEffect
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.navigation.FakeStoreRoute
import com.example.fakestore.util.capitalize
import com.example.fakestore.util.navigateAndClean
import com.example.fakestore.util.toFormat
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import kotlinx.coroutines.delay

@Composable
fun SummaryScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: SummaryViewModel = hiltViewModel()
) {
    val summaryUiState by viewModel.summaryUiState.collectAsStateWithLifecycle()
    val addressState by viewModel.address.collectAsStateWithLifecycle()
    val deletedCarts by viewModel.deletedCart.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchSummary()
        viewModel.fetchAddress()
    }

    SummaryContent(
        summaryUiState = summaryUiState,
        addressState = addressState,
        deletedState = deletedCarts,
        onDeleted = { viewModel.clearCarts() },
        onBackHome = { navHostController.navigateAndClean(FakeStoreRoute.Home.route) }
    )
}

@Composable
fun SummaryContent(
    modifier: Modifier = Modifier,
    summaryUiState: SummaryUiState = SummaryUiState.Loading,
    addressState: UiState<UserRes.Address> = UiState.Loading,
    deletedState: UiState<Boolean> = UiState.Idle,
    onDeleted: () -> Unit = {},
    onBackHome: () -> Unit = {}
) {

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = deletedState) {
        if (deletedState is UiState.Success) {
            snackBarHostState.showSnackbar(
                message = "Checkout Success"
            )
            delay(500L)
            onBackHome()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = { TopAppBarSummary() }) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 70.dp)
            ) {
                HeaderAddress(uiState = addressState)
                Spacer(modifier = Modifier.height(8.dp))
                BoxSummary(uiState = summaryUiState)
            }
            Button(
                onClick = onDeleted,
                modifier = Modifier
                    .testTag("checkout_button")
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            ) {
                if (deletedState is UiState.Loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.testTag("checkout_loading")
                    )
                } else {
                    Text(
                        "Checkout",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BoxSummary(
    modifier: Modifier = Modifier,
    uiState: SummaryUiState = SummaryUiState.Loading
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        color = MaterialTheme.colorScheme.primary
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Text(
                    "Product Detail",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            when (uiState) {
                is SummaryUiState.Loading -> {
                    items(2) {
                        LoadingItemSummary(modifier = Modifier.testTag("loading_item_summary"))
                    }
                }

                is SummaryUiState.Error -> {
                    item {
                        ErrorView(uiState.message)
                    }
                }

                is SummaryUiState.Success -> {
                    if (uiState.items.isEmpty()) {
                        item {
                            EmptyStateView(message = "No Items Found")
                        }
                    } else {
                        items(items = uiState.items, key = { it.id }) { summary ->
                            ItemSummary(cartEntity = summary)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                        item {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                thickness = 2.dp
                            )
                        }
                        item {
                            FooterBoxSummary(uiState.total)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FooterBoxSummary(total: Double = 0.0) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            "Total Purchase",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            total.toFormat(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Preview
@Composable
private fun PreviewBoxSummary() {
    BoxSummary()
}

@Composable
private fun ItemSummary(
    modifier: Modifier = Modifier,
    cartEntity: CartEntity
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            HeaderItemSummary(cart = cartEntity)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            FooterItemSummary(cart = cartEntity)
        }
    }
}

@Composable
private fun FooterItemSummary(modifier: Modifier = Modifier, cart: CartEntity) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
    ) {
        Text(
            "Total Price",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Normal
            ),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            (cart.quantity * cart.price).toFormat(),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Composable
private fun HeaderItemSummary(modifier: Modifier = Modifier, cart: CartEntity) {
    Row(
        horizontalArrangement = Arrangement.Start,
        modifier = modifier.fillMaxWidth()
    ) {
        CoilImage(
            modifier = Modifier
                .size(100.dp, 100.dp)
                .clip(RoundedCornerShape(8.dp))
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
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Normal
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Text(
                "${cart.quantity} x ${cart.price.toFormat()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

@Composable
private fun LoadingItemSummary(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
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
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(8.dp))
            ShimmeringEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(50.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            ShimmeringEffect(
                modifier = Modifier
                    .height(16.dp)
                    .width(100.dp)
            )
        }
    }
}

@Composable
private fun HeaderAddress(
    modifier: Modifier = Modifier,
    uiState: UiState<UserRes.Address> = UiState.Loading
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        contentColor = MaterialTheme.colorScheme.onPrimary,
        color = MaterialTheme.colorScheme.primary
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            when (uiState) {
                is UiState.Loading -> {
                    LoadingAddress(modifier = Modifier.testTag("loading_address"))
                }

                is UiState.Failed -> {
                    ErrorView(uiState.message)
                }

                is UiState.Success -> {
                    val address = uiState.result
                    BoxAddress(modifier, address)
                }
            }
        }
    }
}

@Composable
private fun BoxAddress(
    modifier: Modifier = Modifier,
    address: UserRes.Address = UserRes.Address()
) {
    Column(modifier = modifier) {
        Text(
            "Delivery Location",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal)
        )
        Text(
            "${address.city.capitalize()}, ${address.street.capitalize()}, ${address.number}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            maxLines = 5,
            textAlign = TextAlign.Justify
        )
    }
}

@Composable
private fun LoadingAddress(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ShimmeringEffect(
            modifier = Modifier
                .height(8.dp)
                .width(100.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        ShimmeringEffect(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarSummary(modifier: Modifier = Modifier) {
    TopAppBar(
        title = {
            Text(
                "Summary Checkout",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        modifier = modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors().copy(MaterialTheme.colorScheme.primary)
    )
}

@Preview
@Composable
private fun PreviewItemSummary() {
    val temp = CartEntity(
        id = 9517,
        productId = 5655,
        title = "posteaposteaposteaposteaposteaposteapostea",
        price = 2.3,
        quantity = 3508,
        imageUrl = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
        username = "Lou Peters"
    )
    ItemSummary(cartEntity = temp)
}

@Preview
@Composable
private fun PreviewBoxAddress() {
    val temp = UserRes.Address(
        geolocation = UserRes.Address.Geolocation(
            lat = "egestas",
            long = "inciderint"
        ), city = "Duma Hills", street = "magna", number = 7650, zipcode = "67272"
    )
    BoxAddress(address = temp)
}

@Preview
@Composable
private fun PreviewHeaderAddress() {
    HeaderAddress()
}

@Preview(name = "SummaryScreen")
@Composable
private fun PreviewSummaryScreen() {
    SummaryContent(summaryUiState = SummaryUiState.Loading, addressState = UiState.Loading)
}