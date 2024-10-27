package com.example.fakestore.ui.detail

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.data.db.entity.CartEntity
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.model.product.ProductRes.Companion.mapperToCart
import com.example.fakestore.util.capitalize
import com.example.fakestore.util.paletteBackgroundColor
import com.example.fakestore.util.paletteTextColor
import com.example.fakestore.util.toFormat
import com.kmpalette.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.crossfade.CrossfadePlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import com.skydoves.landscapist.placeholder.shimmer.Shimmer
import com.skydoves.landscapist.placeholder.shimmer.ShimmerPlugin
import java.util.Locale

@Composable
fun DetailProductScreen(
    navHostController: NavController = rememberNavController(),
    viewModel: DetailViewModel = hiltViewModel(),
    idProduct: Int = 0
) {
    val detail by viewModel.detail.collectAsStateWithLifecycle()
    val inserted by viewModel.insertedCart.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle("")

    LaunchedEffect(key1 = Unit) {
        viewModel.fetchDetailProduct(idProduct)
    }

    DetailProductContent(uiStateDetail = detail, uiStateInserted = inserted, username = username ?: "", onAddCart = { cart ->
        viewModel.addToCart(cart)
    }) {
        navHostController.popBackStack()
    }
}

@Composable
fun DetailProductContent(
    modifier: Modifier = Modifier,
    uiStateDetail: UiState<ProductRes> = UiState.Idle,
    uiStateInserted: UiState<Int> = UiState.Idle,
    username: String = "",
    onAddCart: (CartEntity) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = uiStateInserted) {
        when(uiStateInserted) {
            is UiState.Success -> {
                snackBarHostState.showSnackbar("Success Add Cart")
            }
            is UiState.Failed -> {
                snackBarHostState.showSnackbar("Failed Add Cart: ${uiStateInserted.message}")
            }
            else -> {}
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = { TopAppBarDetail(onBack = onBack) },
        floatingActionButton = {
            if (uiStateDetail is UiState.Success) {
                FabAddCart(uiState = uiStateInserted) {
                    onAddCart(uiStateDetail.result.mapperToCart(username))
                }
            }
        },
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (uiStateDetail) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    BannerProduct(product = uiStateDetail.result)
                    HeaderDetail(product = uiStateDetail.result)
                    Spacer(modifier = Modifier.height(8.dp))
                    DescProduct(description = uiStateDetail.result.description)
                }
            }
        }
    }
}

@Composable
private fun DescProduct(
    modifier: Modifier = Modifier,
    description: String = ""
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            "Description",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            description,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSecondary),
            textAlign = TextAlign.Justify
        )
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun HeaderDetail(
    modifier: Modifier = Modifier,
    product: ProductRes = ProductRes()
) {
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            product.title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 3,
            overflow = TextOverflow.Clip,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
        )
        Text(
            product.category.capitalize(),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSecondary
            ),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            Text(
                "${product.rating.count} Sold",
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSecondary)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        product.rating.rate.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerProduct(
    modifier: Modifier = Modifier,
    product: ProductRes = ProductRes()
) {
    var palette by remember { mutableStateOf<Palette?>(null) }
    val backgroundColor by palette.paletteBackgroundColor()
    val textColor by palette.paletteTextColor()
    val urlImage = product.image
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
    ) {
        CoilImage(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 3f),
            imageModel = { urlImage },
            imageOptions = ImageOptions(contentScale = ContentScale.Fit),
            component = rememberImageComponent {
                +CrossfadePlugin()
                +ShimmerPlugin(
                    Shimmer.Resonate(
                        baseColor = Color.Transparent,
                        highlightColor = Color.LightGray,
                    ),
                )

                if (!LocalInspectionMode.current) {
                    +PalettePlugin(
                        imageModel = urlImage,
                        useCache = true,
                        paletteLoadedListener = { palette = it },
                    )
                }
            }
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart), color = backgroundColor
        ) {
            Text(
                product.price.toFormat(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarDetail(modifier: Modifier = Modifier, title: String = "Detail Product", onBack: () -> Unit = {}) {
    TopAppBar(
        title = {
            Text(
                title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back")
            }
        },
        modifier = modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors().copy(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun FabAddCart(
    modifier: Modifier = Modifier,
    uiState: UiState<Int> = UiState.Idle,
    onAddCart: () -> Unit = {}
) {
    ExtendedFloatingActionButton(onClick = onAddCart, modifier = modifier) {
        if (uiState is UiState.Loading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Icon(Icons.Default.ShoppingCart, contentDescription = "Add Cart")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Cart")
        }
    }
}

@Preview
@Composable
private fun PreviewFabAddCart() {
    FabAddCart()
}

@Preview
@Composable
private fun PreviewHeaderDetail() {
    val temp =
        ProductRes(
            title = "Title Panjang",
            category = "Baju",
            rating = ProductRes.Rating(rate = 4.5, count = 100)
        )
    HeaderDetail(product = temp)
}

@Preview
@Composable
private fun PreviewBannerProduct() {
    val temp = ProductRes(
        image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
        price = 100000.0,
        title = "Title Panjang"
    )
    BannerProduct(product = temp)
}

@Preview
@Composable
private fun PreviewDescProduct() {
    DescProduct(description = "Ini Deskripsi panjang Ini Deskripsi panjang Ini Deskripsi panjang Ini Deskripsi panjang Ini Deskripsi panjang")
}

@Preview(name = "DetailProductScreen")
@Composable
private fun PreviewDetailProductScreen() {
    val temp = ProductRes(
        image = "https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg",
        price = 100000.0,
        title = "Title Panjang",
        rating = ProductRes.Rating(rate = 4.5, count = 100)
    )
    val success = UiState.Success(temp)
    DetailProductContent(uiStateDetail = success)
}