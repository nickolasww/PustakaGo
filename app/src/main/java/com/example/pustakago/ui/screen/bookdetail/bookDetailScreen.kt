package com.example.pustakago.ui.screen.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.pustakago.data.model.BookDto
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme

// Colors
val HeaderBlue = Color(0xFF0096DB)
val DarkText = Color(0xFF212121)
val GrayText = Color(0xFF9E9E9E)
val LightGray = Color(0xFFF5F5F5)
val StarGold = Color(0xFFFFB800)

@Composable
fun BookDetailScreen(
    bookId: String,
    navController: NavHostController,
    viewModel: BookDetailViewModel = viewModel(
        key = "bookDetail_$bookId",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BookDetailViewModel(bookId) as T
            }
        }
    )
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        HeaderSection(
            onBackClick = { navController.popBackStack() },
            onShareClick = { /* TODO: Implement share */ },
            isBookmarked = state.isBookmarked,
            onBookmarkClick = { viewModel.toggleBookmark() }
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
                    onRetry = { viewModel.refreshBookDetail() }
                )
            }
            state.book != null -> {
                BookDetailContent(
                    book = state.book!!,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Share and Bookmark
            Row {
                IconButton(
                    onClick = onShareClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BookDetailContent(
    book: BookDto,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Book Header Section
        BookHeaderSection(book = book)
        Spacer(modifier = Modifier.height(24.dp))

        // Book Details Section
        BookDetailsSection(book = book)
        Spacer(modifier = Modifier.height(24.dp))

        // Description Section
        DescriptionSection(description = book.description)
        Spacer(modifier = Modifier.height(24.dp))

        // Author Section
        AuthorSection(
            authorName = book.author,
            authorBio = book.authorBio,
            authorImageUrl = book.authorImageUrl
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Rating Distribution Section
        RatingDistributionSection(
            rating = book.rating,
            reviewCount = book.reviewCount,
            ratingDistribution = book.ratingDistribution
        )

        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}

@Composable
fun BookHeaderSection(book: BookDto) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Book Cover
        AsyncImage(
            model = book.imageUrl,
            contentDescription = book.title,
            modifier = Modifier
                .width(120.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Book Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Title
            Text(
                text = book.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontFamily = Poppins,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Author
            Text(
                text = book.author,
                fontSize = 16.sp,
                color = GrayText,
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Year
            Text(
                text = "${book.year}",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Rating and Review Count
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Star Rating
                Row {
                    repeat(5) { index ->
                        val starFilled = index < book.rating.toInt()
                        Text(
                            text = if (starFilled) "★" else "☆",
                            color = if (starFilled) StarGold else GrayText,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${book.rating} (${book.reviewCount} review)",
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Categories
            book.category.take(3).forEach { category ->
                Surface(
                    color = LightGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = category,
                        color = DarkText,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BookDetailsSection(book: BookDto) {
    Column {
        Text(
            text = "Detail Buku",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        DetailRow("Penerbit", book.publisher)
        DetailRow("Tahun Terbit", book.year.toString())
        DetailRow("ISBN", book.isbn)
        DetailRow("Bahasa", book.language)
        DetailRow("Format", book.format)
        DetailRow("Halaman", "${book.pages} halaman")
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = DarkText,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun DescriptionSection(description: String) {
    Column {
        Text(
            text = "Deskripsi",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = description,
            fontSize = 14.sp,
            color = DarkText,
            fontFamily = Poppins,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun AuthorSection(
    authorName: String,
    authorBio: String,
    authorImageUrl: String
) {
    Column {
        Text(
            text = "Tentang Penulis",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row {
            // Author Image
            AsyncImage(
                model = authorImageUrl,
                contentDescription = authorName,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Author Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = authorName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = authorBio,
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins,
                    lineHeight = 18.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun RatingDistributionSection(
    rating: Double,
    reviewCount: Int,
    ratingDistribution: Map<String, Int>
) {
    Column {
        Text(
            text = "Distribusi Rating",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Overall Rating
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(
                text = "$rating",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row {
                    repeat(5) { index ->
                        val starFilled = index < rating.toInt()
                        Text(
                            text = if (starFilled) "★" else "☆",
                            color = if (starFilled) StarGold else GrayText,
                            fontSize = 20.sp
                        )
                    }
                }
                Text(
                    text = "$reviewCount review",
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )
            }
        }

        // Rating Bars
        (5 downTo 1).forEach { star ->
            val count = ratingDistribution[star.toString()] ?: 0
            val percentage = if (reviewCount > 0) (count * 100f / reviewCount) else 0f

            RatingBarRow(
                starCount = star,
                count = count,
                percentage = percentage
            )
        }
    }
}

@Composable
fun RatingBarRow(
    starCount: Int,
    count: Int,
    percentage: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$starCount",
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            modifier = Modifier.width(20.dp)
        )

        Text(
            text = "★",
            fontSize = 14.sp,
            color = StarGold,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Progress bar background
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(Color.LightGray, RoundedCornerShape(4.dp))
        ) {
            // Progress bar fill
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .background(StarGold, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "$count",
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            modifier = Modifier.width(30.dp)
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
            text = "Terjadi Kesalahan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = error,
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue)
        ) {
            Text(
                text = "Coba Lagi",
                color = Color.White,
                fontFamily = Poppins
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun BookDetailScreenPreview() {
    PustakaGoTheme {
        BookDetailScreen(
            bookId = "sample-book-id",
            navController = rememberNavController()
        )
    }
}
