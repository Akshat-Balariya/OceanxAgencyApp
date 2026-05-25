package com.akshat.oceanxagency

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshat.oceanxagency.ui.theme.OceanxAgencyTheme

private val AppYellow = Color(0xFFFFCC00)
private val AppYellowDark = Color(0xFFF0B900)
private val AppBackground = Color(0xFFF6F6F6)
private val AppText = Color(0xFF111111)
private val AppMuted = Color(0xFF6C6C6C)
private val AppCard = Color(0xFFFFFFFF)
private val AppSoftPurple = Color(0xFFF1ECFF)
private val AppSoftRed = Color(0xFFFDECEC)
private val AppBorder = Color(0xFFE6E6E6)

@Composable
private fun Modifier.visibleClick(onClick: () -> Unit): Modifier {
    return clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = ripple(
            bounded = true,
            color = Color.Black.copy(alpha = 0.22f)
        ),
        onClick = onClick
    )
}

@Composable
private fun PressableButtonSurface(
    containerColor: Color,
    pressedContainerColor: Color,
    horizontalPadding: Int,
    borderColor: Color? = null,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed = interactionSource.collectIsPressedAsState()
    val shape = RoundedCornerShape(16.dp)
    val scale = animateFloatAsState(
        targetValue = if (isPressed.value) 0.88f else 1f,
        label = "buttonPressScale"
    )
    val elevation = animateDpAsState(
        targetValue = if (isPressed.value) 1.dp else 8.dp,
        label = "buttonPressElevation"
    )
    val backgroundColor = animateColorAsState(
        targetValue = if (isPressed.value) pressedContainerColor else containerColor,
        label = "buttonPressColor"
    )

    Box(
        modifier = Modifier
            .scale(scale.value)
            .shadow(elevation.value, shape)
            .clip(shape)
            .background(backgroundColor.value)
            .then(
                if (borderColor != null) Modifier.border(1.dp, borderColor, shape)
                else Modifier
            )
        .clickable(
            interactionSource = interactionSource,
            indication = ripple(
                bounded = true,
                    radius = 88.dp,
                    color = Color.Black.copy(alpha = 0.5f)
            ),
            onClick = onClick
        )
            .padding(horizontal = horizontalPadding.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

data class OrderItem(
    val orderId: String,
    val amount: String,
    val status: String,
    val pickup: String,
    val drop: String
)

private fun searchOrders(orders: List<OrderItem>, query: String): List<OrderItem> {
    val searchText = query.trim()
    if (searchText.isEmpty()) return orders

    return orders.filter { order ->
        order.orderId.contains(searchText, ignoreCase = true) ||
            order.pickup.contains(searchText, ignoreCase = true) ||
            order.drop.contains(searchText, ignoreCase = true) ||
            order.status.contains(searchText, ignoreCase = true)
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OceanxAgencyTheme(dynamicColor = false) {
                OrdersScreen()
            }
        }
    }
}

@Composable
fun OrdersScreen() {
    val selectedTab = remember { mutableStateOf("All Orders") }
    val selectedBottomItem = remember { mutableStateOf("Orders") }
    val searchQuery = remember { mutableStateOf("") }
    val showInfoBanner = remember { mutableStateOf(true) }
    val orders = remember {
        listOf(
            OrderItem("#ORD12345", "₹ 229.0", "CANCELLED", "741, Gumanwara", "00, Main Rd, Shivaji Nagar, Jhansi,\nUttar Pradesh 284001, India"),
            OrderItem("#ORD12346", "₹ 229.0", "CANCELLED", "741, Gumanwara", "00, Main Rd, Shivaji Nagar, Jhansi,\nUttar Pradesh 284001, India"),
            OrderItem("#ORD12347", "₹ 1515.0", "CANCELLED", "332, Gumanwara", "GC72+GGV, Kamrari, Madhya Pradesh\n475661, India"),
            OrderItem("#ORD12347", "₹ 1515.0", "CANCELLED", "332, Gumanwara", "GC72+GGV, Kamrari, Madhya Pradesh\n475661, India")
        )
    }
    val visibleOrders = remember(orders, searchQuery.value) {
        searchOrders(orders = orders, query = searchQuery.value)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection()
            if (selectedBottomItem.value == "Orders") {
                ContentSection(
                    orders = visibleOrders,
                    selectedTab = selectedTab.value,
                    searchQuery = searchQuery.value,
                    showInfoBanner = showInfoBanner.value,
                    modifier = Modifier.weight(1f),
                    onTabSelected = { selectedTab.value = it },
                    onSearchChange = { searchQuery.value = it },
                    onFilterClick = {},
                    onSortClick = {},
                    onInvoiceClick = {},
                    onBookAgainClick = {},
                    onBannerDismiss = { showInfoBanner.value = false }
                )
            } else {
                NotAvailableScreen(
                    title = selectedBottomItem.value,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        FloatingHelpButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 120.dp),
            onClick = {}
        )

        BottomNavBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectedItem = selectedBottomItem.value,
            onItemClick = { selectedBottomItem.value = it }
        )
    }
}

@Composable
private fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppYellow)
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {


            Spacer(modifier = Modifier.height(2.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = "My Orders",
                        color = AppText,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "View your completed trips here. You can\n"
                            + "download invoices or quickly book the same order again.",
                        color = AppText.copy(alpha = 0.9f),
                        fontSize = 20.sp,
                        lineHeight = 22.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(AppYellowDark.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = R.mipmap.dev_app_foreground),
                            contentDescription = null,

                            modifier = Modifier.size(300.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                    }
                }
            }
        }
    }
}

@Composable
private fun ContentSection(
    orders: List<OrderItem>,
    selectedTab: String,
    searchQuery: String,
    showInfoBanner: Boolean,
    modifier: Modifier = Modifier,
    onTabSelected: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit,
    onInvoiceClick: (OrderItem) -> Unit,
    onBookAgainClick: (OrderItem) -> Unit,
    onBannerDismiss: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = AppCard,
            tonalElevation = 7.dp,
            shadowElevation = 0.dp,
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .offset(y = (-12).dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                SearchAndActionsRow(
                    searchQuery = searchQuery,
                    onSearchChange = onSearchChange,
                    onFilterClick = onFilterClick,
                    onSortClick = onSortClick
                )
                Spacer(modifier = Modifier.height(14.dp))
                StatusTabs(selectedTab = selectedTab, onTabSelected = onTabSelected)
                if (showInfoBanner) {
                    Spacer(modifier = Modifier.height(14.dp))
                    InfoBanner(onDismiss = onBannerDismiss)
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (orders.isEmpty()) {
                    EmptyOrdersMessage(modifier = Modifier.weight(1f))
                } else {
                    OrdersRecyclerView(
                        orders = orders,
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 96.dp),
                        onInvoiceClick = onInvoiceClick,
                        onBookAgainClick = onBookAgainClick
                    )
                }
            }
        }
    }
}

@Composable
private fun OrdersRecyclerView(
    orders: List<OrderItem>,
    modifier: Modifier = Modifier,
    onInvoiceClick: (OrderItem) -> Unit,
    onBookAgainClick: (OrderItem) -> Unit
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds(),
        factory = { context ->
            RecyclerView(context).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = OrdersAdapter(
                    orders = orders,
                    onInvoiceClick = onInvoiceClick,
                    onBookAgainClick = onBookAgainClick
                )
                clipChildren = true
                clipToPadding = true
                overScrollMode = RecyclerView.OVER_SCROLL_IF_CONTENT_SCROLLS
            }
        },
        update = { recyclerView ->
            (recyclerView.adapter as? OrdersAdapter)?.updateOrders(orders)
        }
    )
}

private class OrdersAdapter(
    private var orders: List<OrderItem>,
    private val onInvoiceClick: (OrderItem) -> Unit,
    private val onBookAgainClick: (OrderItem) -> Unit
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val composeView = ComposeView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }
        return OrderViewHolder(composeView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position], onInvoiceClick, onBookAgainClick)
    }

    override fun getItemCount(): Int = orders.size

    fun updateOrders(updatedOrders: List<OrderItem>) {
        if (orders == updatedOrders) return
        orders = updatedOrders
        notifyDataSetChanged()
    }

    class OrderViewHolder(
        private val composeView: ComposeView
    ) : RecyclerView.ViewHolder(composeView) {

        fun bind(
            order: OrderItem,
            onInvoiceClick: (OrderItem) -> Unit,
            onBookAgainClick: (OrderItem) -> Unit
        ) {
            composeView.setContent {
                OceanxAgencyTheme(dynamicColor = false) {
                    Box(modifier = Modifier.padding(bottom = 12.dp)) {
                        OrderCard(
                            order = order,
                            onInvoiceClick = { onInvoiceClick(order) },
                            onBookAgainClick = { onBookAgainClick(order) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotAvailableScreen(title: String, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = AppCard,
        tonalElevation = 7.dp,
        shadowElevation = 0.dp,
        modifier = modifier
            .fillMaxWidth()
            .offset(y = (-12).dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 96.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = AppText,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Not available",
                    color = AppMuted,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SearchAndActionsRow(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onSortClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        SearchChip(
            modifier = Modifier.weight(1f),
            query = searchQuery,
            onQueryChange = onSearchChange
        )
        Spacer(modifier = Modifier.width(12.dp))

        ActionChip(text = "Filter", onClick = onFilterClick,icon=R.drawable.funnel_svgrepo_com)
        Spacer(modifier = Modifier.width(10.dp))
        ActionChip(text = "Sort", onClick = onSortClick,icon=R.drawable.sort_svgrepo_com)
    }
}

@Composable
private fun SearchChip(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    SearchSurface(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.search_svgrepo_com),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(
                    color = AppText,
                    fontSize = 20.sp
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isBlank()) {
                            Text(
                                text = "Search by Order ID / Location",
                                color = AppMuted,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun SearchSurface(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            content()
        }
    }
}

@Composable
private fun EmptyOrdersMessage(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No orders found",
            color = AppMuted,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ActionChip(text: String, onClick: () -> Unit, icon: Int) {


    ChipSurface(

        modifier = Modifier.width(110.dp),
        contentPadding = 14,
        centerContent = true,
        onClick = onClick
    ) {Row( verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center)
    {
        Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.size(26.dp)

    )

        Text(
            text = text,
            color = AppText,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        ) }

    }
}

@Composable
private fun ChipSurface(
    modifier: Modifier = Modifier,
    contentPadding: Int,
    centerContent: Boolean = false,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .visibleClick(onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, AppBorder)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = contentPadding.dp, vertical = 14.dp),
            contentAlignment = if (centerContent) Alignment.Center else Alignment.CenterStart
        ) {
            content()
        }
    }
}

@Composable
private fun StatusTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, AppBorder, RoundedCornerShape(18.dp))
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically
    ) {

        TabItem(
            "All Orders",
            selected = selectedTab == "All Orders",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("All Orders") }
        )

        VerticalDivider()

        TabItem(
            "Completed",
            selected = selectedTab == "Completed",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("Completed") }
        )

        VerticalDivider()

        TabItem(
            "Cancelled",
            selected = selectedTab == "Cancelled",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("Cancelled") }
        )

        VerticalDivider()

        TabItem(
            "Booked Again",
            selected = selectedTab == "Booked Again",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("Booked Again") }
        )
    }
}

@Composable
private fun VerticalDivider() {
    Box(
        modifier = Modifier
            .height(24.dp)
            .width(1.dp)
            .background(AppBorder)
    )
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                color = if (selected) AppYellow else Color.Transparent,
                shape = RoundedCornerShape(18.dp)
            )
            .visibleClick(onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = AppText,
            fontSize = 18.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
@Composable
private fun InfoBanner(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppSoftPurple)
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "i", color = Color(0xFF5A4BB4), fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "यहाँ आप अपने सभी पिछले ऑर्डर देख सकते हैं।",
                color = AppText,
                fontSize = 18.sp,
                lineHeight = 20.sp
            )
            Text(
                text = "आप इनवॉइस डाउनलोड कर सकते हैं या उसी पते पर दोबारा बुक कर सकते हैं।",
                color = AppText,
                fontSize = 18.sp,
                lineHeight = 20.sp
            )
        }
        Box(
            modifier = Modifier
                .padding(start = 8.dp)
                .size(32.dp)
                .clip(CircleShape)
                .visibleClick(onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.cross_svgrepo_com),
                contentDescription = "Dismiss",
                tint = Color.Unspecified,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderItem,
    onInvoiceClick: () -> Unit,
    onBookAgainClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AppYellow.copy(alpha = 0.24f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.truck_transport_svgrepo_com),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Four Wheeler",
                        color = AppText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "05 Feb, 4:46 PM  |  Order ID: ${order.orderId}",
                        color = AppMuted,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_pin_svgrepo_com__3_),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = order.pickup,
                            color = AppText,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            painter = painterResource(id = R.drawable.location_pin_svgrepo_com__4_),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = order.drop,
                            color = AppText,
                            fontSize = 18.sp
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = order.amount,
                        color = AppText,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.dots_vertical_svgrepo_com),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp),

                    )

                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = order.status)
                Row {
                    SmallActionButton(text = "Invoice", onClick = onInvoiceClick)
                    Spacer(modifier = Modifier.width(12.dp))
                    LargeActionButton(text = "Book Again", onClick = onBookAgainClick)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppSoftRed)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            color = Color(0xFFB54545),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallActionButton(text: String, onClick: () -> Unit) {
    PressableButtonSurface(
        containerColor = Color.White,
        pressedContainerColor = Color(0xFFDCDCDC),
        horizontalPadding = 16,
        borderColor = AppBorder,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_invoice),
                contentDescription = null,
                tint = AppText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                color = AppText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun LargeActionButton(text: String, onClick: () -> Unit) {
    PressableButtonSurface(
        containerColor = AppYellow,
        pressedContainerColor = AppYellowDark,
        horizontalPadding = 18,
        onClick = onClick
    ) {
        Text(
            text = text,
            color = AppText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun FloatingHelpButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(AppYellow)
                .border(4.dp, Color.White, CircleShape)
                .visibleClick(onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(id = R.drawable.headset_solid_svgrepo_com),
                    contentDescription = "Help",
                    tint = AppText,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Help", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    modifier: Modifier = Modifier,
    selectedItem: String,
    onItemClick: (String) -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem("Home", R.drawable.ic_home, selectedItem == "Home", onClick = { onItemClick("Home") })
            BottomNavItem("Orders", R.drawable.clock_0500_svgrepo_com, selectedItem == "Orders", onClick = { onItemClick("Orders") })
            BottomNavItem("Payments", R.drawable.wallet_svgrepo_com, selectedItem == "Payments", onClick = { onItemClick("Payments") })
            BottomNavItem("Account", R.drawable.ic_account, selectedItem == "Account", onClick = { onItemClick("Account") })
        }
    }
}

@Composable
private fun BottomNavItem(label: String, iconRes: Int, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .visibleClick(onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            tint = if (selected) AppYellowDark else AppText,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (selected) AppYellowDark else Color.Unspecified,
            fontSize = 16.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true, device = "spec:height=1280dp,width=800dp")
@Composable
private fun OrdersScreenPreview() {
    OceanxAgencyTheme(dynamicColor = false) {
        OrdersScreen()
    }
}
