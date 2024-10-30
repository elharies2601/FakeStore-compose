package com.example.fakestore.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.fakestore.component.EmptyStateView
import com.example.fakestore.component.ErrorView
import com.example.fakestore.component.RoundedAliasIcon
import com.example.fakestore.component.ShimmeringEffect
import com.example.fakestore.data.model.UiState
import com.example.fakestore.data.model.product.ProductRes
import com.example.fakestore.data.model.user.UserRes
import com.example.fakestore.navigation.FakeStoreRoute
import com.example.fakestore.util.capitalize
import com.example.fakestore.util.nameAlias
import com.example.fakestore.util.navigateAndClean
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

@Composable
fun HomeScreen(
    navHostController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.categorySelected.collectAsStateWithLifecycle()
    val countCart by viewModel.countCart.collectAsStateWithLifecycle()
    val user by viewModel.user.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        doCallFirst(viewModel)
    }

    LaunchedEffect(key1 = selectedCategory) {
        viewModel.fetchProducts(selectedCategory)
    }

    HomeContent(
        uiStateCategories = categories,
        uiStateProducts = products,
        uiStateUser = user,
        selectedCategory = selectedCategory,
        countCart = countCart,
        onSelectFilter = { c -> viewModel.toggleCategory(c) },
        onClickCart = { navHostController.navigate(FakeStoreRoute.Cart.route) },
        onClickDetail = { id ->
            navHostController.navigate(
                FakeStoreRoute.DetailProduct.createRoute(
                    id
                )
            )
        },
        onLogout = {
            viewModel.doLogout()
            navHostController.navigateAndClean(FakeStoreRoute.Login.route)
        },
        onRetryError = {
            doCallFirst(viewModel)
            viewModel.fetchProducts(selectedCategory)
        }
    )
}

private fun doCallFirst(viewModel: HomeViewModel) {
    viewModel.fetchCategories()
    viewModel.checkCart()
    viewModel.fetchUser()
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    uiStateCategories: UiState<MutableList<String>> = UiState.Idle,
    uiStateProducts: UiState<MutableList<ProductRes>> = UiState.Idle,
    uiStateUser: UiState<UserRes> = UiState.Idle,
    selectedCategory: String = "",
    countCart: Int = 0,
    onSelectFilter: (String) -> Unit = {},
    onClickCart: () -> Unit = {},
    onClickDetail: (Int) -> Unit = {},
    onLogout: () -> Unit = {},
    onRetryError: () -> Unit = {}
) {
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(topBar = {
        TopAppBarHome(modifier = Modifier, countCart = countCart, onClickCart) {
            showBottomSheet = true
        }
    }) {
        ConstraintLayout(
            modifier = modifier
                .padding(it)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val (listProducts, categoriesFilter) = createRefs()

            CategoriesFilter(
                modifier = Modifier.constrainAs(categoriesFilter) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                uiState = uiStateCategories,
                selectedCategory = selectedCategory,
                onSelectFilter = onSelectFilter
            )

            ListProducts(
                modifier = Modifier.constrainAs(listProducts) {
                    top.linkTo(categoriesFilter.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }.padding(top = 16.dp),
                uiState = uiStateProducts,
                onClickProduct = onClickDetail,
                onRetryError = onRetryError
            )
        }
    }

    if (showBottomSheet && uiStateUser is UiState.Success) {
        ProfileBottomSheet(isShow = showBottomSheet, userRes = uiStateUser.result, onLogout = {
            showBottomSheet = false
            onLogout()
        }) {
            showBottomSheet = false
        }
    }
}

@Composable
private fun ListProducts(
    modifier: Modifier = Modifier,
    uiState: UiState<MutableList<ProductRes>> = UiState.Idle,
    onClickProduct: (Int) -> Unit = {},
    onRetryError: () -> Unit = {}
) {
    when (uiState) {
        is UiState.Loading -> LoadingProduct(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .testTag("loading_products")
        )

        is UiState.Failed -> {
            ErrorView(message = uiState.message) {
                onRetryError()
            }
        }

        is UiState.Success -> {
            if (uiState.result.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = modifier.padding(top = 8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.result, key = { it.id }) { product ->
                        ItemProduct(product = product, onClickProduct = onClickProduct)
                    }
                }
            } else {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyStateView(message = "No Product Found")
                }
            }
        }
    }
}

@Composable
private fun ItemProduct(
    modifier: Modifier = Modifier,
    product: ProductRes = ProductRes(),
    onClickProduct: (Int) -> Unit = {}
) {
    var palette by remember { mutableStateOf<Palette?>(null) }
    val backgroundColor by palette.paletteBackgroundColor()
    val textColor by palette.paletteTextColor()
    val urlImage = product.image

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable { onClickProduct(product.id) },
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
        ) {
            CoilImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 6f),
                imageModel = { urlImage },
                imageOptions = ImageOptions(contentScale = ContentScale.FillBounds),
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
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(start = 8.dp, end = 8.dp, bottom = 16.dp)
            ) {
                Text(
                    product.title,
                    style = MaterialTheme.typography.titleMedium.copy(color = textColor),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    product.price.toFormat(),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Start",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        product.rating.rate.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(color = textColor, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "${product.rating.count} sold",
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoriesFilter(
    modifier: Modifier = Modifier,
    uiState: UiState<MutableList<String>>,
    selectedCategory: String = "",
    onSelectFilter: (String) -> Unit = {}
) {
    when (uiState) {
        is UiState.Loading -> LoadingCategory(modifier = modifier.fillMaxWidth().testTag("loading_category"))
        is UiState.Failed -> {
            Text(
                text = "Error: ${uiState.message}",
                color = MaterialTheme.colorScheme.error,
                modifier = modifier.padding(16.dp)
            )
        }

        is UiState.Success -> {
            LazyRow(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.result) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectFilter(category) },
                        label = { Text(category.capitalize()) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBarHome(
    modifier: Modifier = Modifier,
    countCart: Int = 0,
    onClickCart: () -> Unit = {},
    onClickProfile: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = "Home",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        actions = {
            IconCartBadged(count = countCart) {
                onClickCart()
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                Icons.Default.Person,
                contentDescription = "icon person",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .testTag("icon_person")
                    .padding(end = 8.dp)
                    .clickable { onClickProfile() })
        },
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors().copy(MaterialTheme.colorScheme.primary)
    )
}

@Composable
private fun LoadingCategory(modifier: Modifier = Modifier) {
    LazyRow(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(count = 6) {
            ShimmeringEffect(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .width(100.dp)
                    .height(20.dp)
            )
        }
    }
}

@Composable
private fun LoadingProduct(modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxWidth(),
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(count = 10) {
            ShimmeringEffect(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }
    }
}

@Composable
private fun IconCartBadged(
    modifier: Modifier = Modifier,
    count: Int = 0,
    onClickCart: () -> Unit = {}
) {
    val isEmptyCart = count == 0
    BadgedBox(
        modifier = modifier,
        badge = {
            if (!isEmptyCart) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Text(
                        text = if (count > 99) "+99" else count.toString()
                    )
                }
            }
        }
    ) {
        Icon(
            Icons.Default.ShoppingCart,
            contentDescription = "icon cart",
            tint = if (isEmptyCart) Color.Gray else MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clickable { if (!isEmptyCart) onClickCart() })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileBottomSheet(
    isShow: Boolean = false,
    userRes: UserRes = UserRes(),
    onLogout: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    if (isShow) {
        val modalBottomSheet = rememberModalBottomSheetState(true)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = modalBottomSheet
        ) {
            ProfileContentBottomSheet(
                modifier = Modifier.fillMaxWidth(),
                userRes = userRes,
                onLogout = onLogout
            )
        }

    }
}

@Composable
private fun ProfileContentBottomSheet(
    modifier: Modifier = Modifier,
    userRes: UserRes = UserRes(),
    onLogout: () -> Unit = {}
) {
    val name = "${userRes.name.firstname} ${userRes.name.lastname}"
    val address = "${userRes.address.street}, ${userRes.address.city}, ${userRes.address.zipcode}"
    Column(
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            RoundedAliasIcon(size = 100.dp, fontSize = 25.sp, alias = name.nameAlias())
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = name.capitalize(),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = userRes.phone,
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = address.capitalize(),
                    style = MaterialTheme.typography.labelMedium.copy(color = MaterialTheme.colorScheme.onBackground),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Justify
                )
            }
        }
        Spacer(modifier = Modifier.height(64.dp))
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp)
        ) {
            Text(text = "Logout", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun PreviewProfileBottomSheet() {
    val temp = UserRes(
        address = UserRes.Address(
            geolocation = UserRes.Address.Geolocation(
                lat = "pharetra",
                long = "similique"
            ), city = "Greystone", street = "conubia", number = 7453, zipcode = "85062"
        ),
        id = 4888,
        email = "caroline.olsen@example.com",
        username = "Courtney Larson",
        password = "fringilla",
        name = UserRes.Name(
            firstname = "Alexandria Hudson",
            lastname = "Claudine McCarty"
        ),
        phone = "(160) 880-8338",
        v = 2637
    )
    ProfileContentBottomSheet(userRes = temp)
}

@Preview
@Composable
private fun PreviewItemProduct() {
    val temp = ProductRes(
        title = "Title",
        price = 127.0,
        image = "https://i.pravatar.cc",
        rating = ProductRes.Rating(3.7, 12)
    )
    ItemProduct(product = temp)
}

@Preview
@Composable
private fun PreviewIconCartBadged() {
    IconCartBadged(count = 100)
}

@Preview
@Composable
private fun PreviewTopAppBarHome() {
    TopAppBarHome()
}

@Preview(name = "HomeScreen")
@Composable
private fun PreviewHomeScreen() {
    HomeContent()
}