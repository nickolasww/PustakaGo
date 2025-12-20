package com.example.pustakago.ui.screen.mark

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
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
val LightGray = Color(0xFFF5F5F5)

@Composable
fun MarkScreen(
    navController: NavHostController,
    viewModel: MarkViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Filter books based on search query
    val filteredBooks = remember(state.bookmarkedBooks, state.searchQuery) {
        if (state.searchQuery.isEmpty()) {
            state.bookmarkedBooks
        } else {
            state.bookmarkedBooks.filter { book ->
                book.title.contains(state.searchQuery, ignoreCase = true) ||
                        book.author.contains(state.searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        HeaderSection(
            onBackClick = { navController.popBackStack() }
        )

        // Search Bar
        SearchSection(
            searchQuery = state.searchQuery,
            onSearchChange = viewModel::onSearchQueryChange
        )

        // Content
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HeaderBlue)
                }
            }
            state.error != null -> {
                ErrorSection(
                    error = state.error!!,
                    onRetry = { viewModel.refreshBookmarks() }
                )
            }
            !state.isLoggedIn -> {
                LoginPromptSection(
                    onLoginClick = { navController.navigate(Routes.REGISTER) }
                )
            }
            filteredBooks.isEmpty() -> {
                EmptyBookmarksSection(
                    searchQuery = state.searchQuery
                )
            }
            else -> {
                BookmarksListSection(
                    books = filteredBooks,
                    onBookClick = { bookId ->
                        navController.navigate("${Routes.BOOK_DETAIL}/$bookId")
                    },
                    onRemoveBookmark = { bookId ->
                        viewModel.removeBookmark(bookId)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Bookmark",
                color = Color.White,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderBlue)
    )
}

@Composable
fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = searchQuery,
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
                    "Cari buku bookmark...",
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
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true
        )
    }
}

@Composable
fun LoginPromptSection(
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔖",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Login Diperlukan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Masuk untuk melihat buku-buku yang Anda bookmark",
            fontSize = 16.sp,
            color = GrayText,
            fontFamily = Poppins,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onLoginClick,
            colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Masuk",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun EmptyBookmarksSection(
    searchQuery: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📚",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = if (searchQuery.isEmpty())
                "Belum Ada Bookmark"
            else
                "Buku Tidak Ditemukan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (searchQuery.isEmpty())
                " Mulai bookmark buku favorit Anda untuk melihatnya di sini"
            else
                " Coba kata kunci lain untuk pencarian Anda",
            fontSize = 16.sp,
            color = GrayText,
            fontFamily = Poppins,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
fun ErrorSection(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Terjadi Kesalahan",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = error,
            fontSize = 16.sp,
            color = GrayText,
            fontFamily = Poppins,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Coba Lagi",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
fun BookmarksListSection(
    books: List<BookDto>,
    onBookClick: (String) -> Unit,
    onRemoveBookmark: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(books) { book ->
            BookCard(
                book = book,
                onBookClick = { onBookClick(book.id) },
                onRemoveBookmark = { onRemoveBookmark(book.id) }
            )
        }
    }
}

@Composable
fun BookCard(
    book: BookDto,
    onBookClick: () -> Unit,
    onRemoveBookmark: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book Cover
            AsyncImage(
                model = book.imageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .width(60.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Book Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = book.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontFamily = Poppins,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = book.author,
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${book.year} • ${book.pages} halaman",
                    fontSize = 12.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
            }

            // Remove Bookmark Button
            IconButton(
                onClick = onRemoveBookmark,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Bookmark,
                    contentDescription = "Remove Bookmark",
                    tint = HeaderBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun MarkScreenPreview() {
    PustakaGoTheme {
        MarkScreen(navController = rememberNavController())
    }
}
