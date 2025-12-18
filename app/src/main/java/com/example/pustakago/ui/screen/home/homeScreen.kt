package com.example.pustakago.ui.screen.home

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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.example.pustakago.data.model.BookDto
import com.example.pustakago.ui.navigation.Routes
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme

// Colors
val HeaderBlue = Color(0xFF0096DB)
val DarkText = Color(0xFF212121)
val GrayText = Color(0xFF9E9E9E)
val PrimaryBlue = Color(0xFF007BFF)

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
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
            // Section 1: Eksplorasi ilmu sains!
            if (state.scienceBooks.isNotEmpty()) {
                BookSection(
                    title = "Eksplorasi ilmu sains!",
                    books = state.scienceBooks,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 2: Memperdalam pemahaman
            if (state.philosophyBooks.isNotEmpty()) {
                BookSection(
                    title = "Memperdalam pemahaman",
                    books = state.philosophyBooks,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Section 3: Mencekam dan merinding!
            if (state.horrorBooks.isNotEmpty()) {
                BookSection(
                    title = "Mencekam dan merinding!",
                    books = state.horrorBooks,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Show all books if no category books loaded
            if (state.scienceBooks.isEmpty() && state.philosophyBooks.isEmpty() &&
                state.horrorBooks.isEmpty() && state.allBooks.isNotEmpty()) {
                BookSection(
                    title = "Semua Buku",
                    books = state.allBooks,
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
                        "Cari buku sekarang!",
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
fun BookSection(
    title: String,
    books: List<BookDto>,
    navController: NavHostController
) {
    Column {
        // Section Title
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Book Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookDto,
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .clickable {
                // Navigate to BookDetail screen
                navController.navigate("${Routes.BOOK_DETAIL}/${book.id}")
            }
    ) {
        // Book Cover
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            if (book.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = book.imageUrl,
                    contentDescription = book.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Placeholder for book image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFBDBDBD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📚",
                        fontSize = 40.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Book Title
        Text(
            text = book.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = DarkText,
            fontFamily = Poppins,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Book Author and Year
        Text(
            text = "${book.author} • ${book.year}",
            fontSize = 12.sp,
            color = GrayText,
            fontFamily = Poppins,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Rating
        if (book.rating > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⭐",
                    fontSize = 12.sp
                )
                Text(
                    text = " ${book.rating}",
                    fontSize = 12.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    PustakaGoTheme {
        HomeScreen(navController = rememberNavController())
    }
}
