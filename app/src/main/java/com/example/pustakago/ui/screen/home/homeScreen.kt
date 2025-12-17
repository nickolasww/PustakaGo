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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme

// Colors
val HeaderBlue = Color(0xFF0096DB)
val DarkText = Color(0xFF212121)
val GrayText = Color(0xFF9E9E9E)
val PrimaryBlue = Color(0xFF007BFF)

// Data class for Book
data class Book(
    val imageRes: Int,
    val title: String,
    val year: String
)

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
                    // Show profile or logout option
                    viewModel.logout()
                } else {
                    navController.navigate("register")
                }
            }
        )

        // Content Section with Scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Section 1: Eksplorasi ilmu sains!
            BookSection(
                title = "Eksplorasi ilmu sains!",
                books = state.scienceBooks.ifEmpty { getScienceBooks() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: Memperdalam pemahaman
            BookSection(
                title = "Memperdalam pemahaman",
                books = state.philosophyBooks.ifEmpty { getPhilosophyBooks() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section 3: Mencekam dan merinding!
            BookSection(
                title = "Mencekam dan merinding!",
                books = state.horrorBooks.ifEmpty { getHorrorBooks() }
            )

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
                // Show Profile Icon when logged in
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
                // Show Register Button when not logged in
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
    books: List<Book>
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
                BookCard(book = book)
            }
        }
    }
}

@Composable
fun BookCard(book: Book) {
    Column(
        modifier = Modifier.width(120.dp)
    ) {
        // Book Cover
        Box(
            modifier = Modifier
                .height(160.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE0E0E0))
        ) {
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

        // Book Year
        Text(
            text = book.year,
            fontSize = 12.sp,
            color = GrayText,
            fontFamily = Poppins
        )
    }
}

// Sample data functions
fun getScienceBooks(): List<Book> {
    return listOf(
        Book(0, "Kosmos", "1980"),
        Book(0, "Astrophysics for…", "2017"),
        Book(0, "A Brief History…", "1980")
    )
}

fun getPhilosophyBooks(): List<Book> {
    return listOf(
        Book(0, "Sophie's World", "1991"),
        Book(0, "Filosofi Teras", "2018"),
        Book(0, "Sejarah Filsafat…", "1945")
    )
}

fun getHorrorBooks(): List<Book> {
    return listOf(
        Book(0, "It", "1986"),
        Book(0, "Dracula", "1897"),
        Book(0, "Danur", "2011")
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun HomeScreenPreview() {
    PustakaGoTheme {
        HomeScreen(navController = rememberNavController())
    }
}
