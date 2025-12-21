package com.example.pustakago.ui.screen.news

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pustakago.data.model.NewsDto
import com.example.pustakago.ui.navigation.Routes
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme
import java.util.Date

// Colors
val HeaderBlue = Color(0xFF0096DB)
val DarkText = Color(0xFF212121)
val GrayText = Color(0xFF9E9E9E)
val PrimaryBlue = Color(0xFF007BFF)
val BreakingRed = Color(0xFFFF4444)
val TrendingOrange = Color(0xFFFF9800)

@Composable
fun NewsScreen(
    navController: NavHostController,
    viewModel: NewsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with Search Bar
        HeaderSection(
            search = state.searchQuery,
            onSearchChange = { viewModel.onSearchQueryChange(it) },
            isLoggedIn = state.isLoggedIn,
            userName = state.userName,
            onProfileClick = {
                if (state.isLoggedIn) {
                    viewModel.logout()
                } else {
                    navController.navigate(Routes.REGISTER)
                }
            }
        )

        // Loading indicator
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HeaderBlue)
            }
        }

        // Error message
        state.error?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Content Section with Scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Section 1: Breaking News
            if (state.breakingNews.isNotEmpty()) {
                NewsSection(
                    title = "Breaking News",
                    news = state.breakingNews,
                    navController = navController,
                    showBadge = true,
                    badgeColor = BreakingRed,
                    icon = Icons.Default.Newspaper
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 2: Latest News
            if (state.latestNews.isNotEmpty()) {
                NewsSection(
                    title = "Berita Terbaru",
                    news = state.latestNews,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 3: Trending News
            if (state.trendingNews.isNotEmpty()) {
                NewsSection(
                    title = "Trending",
                    news = state.trendingNews,
                    navController = navController,
                    showBadge = true,
                    badgeColor = TrendingOrange,
                    icon = Icons.Default.TrendingUp
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 4: Category News
            state.categoryNews.forEach { (category, newsList) ->
                if (newsList.isNotEmpty()) {
                    NewsSection(
                        title = category,
                        news = newsList,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Show all news if no category news loaded
            if (state.breakingNews.isEmpty() && state.latestNews.isEmpty() &&
                state.trendingNews.isEmpty() && state.allNews.isNotEmpty()) {
                NewsSection(
                    title = "Semua Berita",
                    news = state.allNews,
                    navController = navController
                )
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
        }
    }
}

@Composable
fun HeaderSection(
    search: String,
    onSearchChange: (String) -> Unit,
    isLoggedIn: Boolean,
    userName: String?,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                color = HeaderBlue,
                shape = RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            TextField(
                value = search,
                onValueChange = onSearchChange,
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = GrayText
                    )
                },
                placeholder = {
                    Text(
                        "Cari berita sekarang!",
                        color = GrayText,
                        fontFamily = Poppins
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Profile/Register Button
            if (isLoggedIn) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (userName != null && userName.isNotEmpty()) {
                        Text(
                            text = userName.first().uppercase(),
                            color = HeaderBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = HeaderBlue,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Button(
                    onClick = { onProfileClick() },
                    modifier = Modifier.height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Text(
                        text = "Daftar",
                        color = HeaderBlue,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Poppins,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NewsSection(
    title: String,
    news: List<NewsDto>,
    navController: NavHostController,
    showBadge: Boolean = false,
    badgeColor: Color = Color.Transparent,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column {
        // Section Title
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = DarkText,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // News Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(news) { newsItem ->
                NewsCard(
                    news = newsItem,
                    navController = navController,
                    showBadge = showBadge,
                    badgeColor = badgeColor
                )
            }
        }
    }
}

@Composable
fun NewsCard(
    news: NewsDto,
    navController: NavHostController,
    showBadge: Boolean = false,
    badgeColor: Color = Color.Transparent
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .clickable {
                // Navigate to NewsDetail screen
                navController.navigate("${Routes.NEWS_DETAIL}/${news.id}")
            }
    ) {
        // News Image with Badge
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            if (news.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = news.imageUrl,
                    contentDescription = news.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for news image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFBDBDBD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📰",
                        fontSize = 40.sp
                    )
                }
            }

            // Badge for breaking/trending news
            if (showBadge && (news.isBreaking || news.viewCount > 1000)) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(CircleShape)
                        .background(badgeColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (news.isBreaking) "BREAKING" else "TRENDING",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Poppins
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // News Title
        Text(
            text = news.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkText,
            fontFamily = Poppins,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        // News Summary
        if (news.summary.isNotEmpty()) {
            Text(
                text = news.summary,
                fontSize = 12.sp,
                color = GrayText,
                fontFamily = Poppins,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // News Meta Information
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Author and Category
            Column {
                Text(
                    text = news.author,
                    fontSize = 11.sp,
                    color = PrimaryBlue,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = news.category,
                    fontSize = 10.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
            }

            // Time and View Count
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = news.relativeTime,
                    fontSize = 10.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👁",
                        fontSize = 10.sp
                    )
                    Text(
                        text = " ${news.viewCount}",
                        fontSize = 10.sp,
                        color = GrayText,
                        fontFamily = Poppins
                    )
                }
            }
        }

        // Tags
        if (news.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                news.tags.take(3).forEach { tag ->
                    Text(
                        text = "#$tag",
                        fontSize = 9.sp,
                        color = PrimaryBlue,
                        fontFamily = Poppins,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun NewsScreenPreview() {
    PustakaGoTheme {
        NewsScreen(navController = rememberNavController())
    }
}
