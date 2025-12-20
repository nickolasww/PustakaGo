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
import androidx.compose.material.icons.filled.Star
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
import com.example.pustakago.ui.screen.bookdetail.components.ReviewDialog
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme

// Colors
val HeaderBlue = Color(0xFF0096DB)
val DarkText = Color(0xFF212121)
val GrayText = Color(0xFF9E9E9E)
val LightGray = Color(0xFFF5F5F5)
val StarGold = Color(0xFFFFB800)

@OptIn(ExperimentalMaterial3Api::class)
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

    // Show Review Dialog
    ReviewDialog(
        isVisible = state.showReviewDialog,
        initialRating = state.userRating,
        initialText = state.reviewText,
        isSubmitting = state.isSubmittingReview,
        onRatingChanged = viewModel::updateUserRating,
        onTextChanged = viewModel::updateReviewText,
        onSubmit = viewModel::submitReview,
        onDismiss = viewModel::hideReviewDialog
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top App Bar
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            actions = {
                IconButton(onClick = { /* TODO: Implement share */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Share",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { viewModel.toggleBookmark() }) {
                    Icon(
                        if (state.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderBlue)
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
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun BookDetailContent(
    book: BookDto,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        // Section 1: Book Information
        BookInfoSection(book = book)
        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Statistics Row
        StatisticsRow(
            rating = book.rating,
            reviewCount = book.reviewCount,
            pageCount = book.pages
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Read Button
        ReadNowButton()
        Spacer(modifier = Modifier.height(24.dp))

        // Section 4: About Book
        AboutBookSection(description = book.description)
        Spacer(modifier = Modifier.height(24.dp))

        // Section 5: Categories
        CategoriesSection(categories = book.category)
        Spacer(modifier = Modifier.height(24.dp))

        // Section 6: Rating and Reviews
        RatingAndReviewsSection(
            rating = book.rating,
            reviewCount = book.reviewCount,
            ratingDistribution = book.ratingDistribution
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Section 7: User Reviews
        UserReviewsSection(
            reviews = state.reviews,
            isLoading = state.isReviewLoading
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Section 8: About Author
        AboutAuthorSection(
            authorName = book.author,
            authorBio = book. authorDetailBio,
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Section 9: Rating Input
        RatingInputSection(
            isLoggedIn = state.isLoggedIn,
            userRating = state.userRating,
            userReview = state.userReview,
            onShowReviewDialog = viewModel::showReviewDialog,
            onEditReview = viewModel::editExistingReview,
            onRatingChanged = viewModel::updateUserRating
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Section 10: Book Details
        BookDetailsSection(book = book)

        Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
    }
}

@Composable
fun BookInfoSection(book: BookDto) {
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
        }
    }
}

@Composable
fun StatisticsRow(
    rating: Double,
    reviewCount: Int,
    pageCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rating
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$rating ($reviewCount)",
                fontSize = 14.sp,
                color = DarkText,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )
        }

        // Page Count
        Text(
            text = "$pageCount halaman",
            fontSize = 14.sp,
            color = DarkText,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium
        )

        // Additional Page Info
        Text(
            text = "Hard Cover",
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins
        )
    }
}

@Composable
fun ReadNowButton() {
    Button(
        onClick = { /* TODO: Navigate to reader */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Baca sekarang",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = Poppins
        )
    }
}

@Composable
fun AboutBookSection(description: String) {
    Column {
        Text(
            text = "Tentang buku ini",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Text(
            text = description,
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun CategoriesSection(categories: List<String>) {
    Column {
        Text(
            text = "Kategori",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.take(3).forEach { category ->
                Surface(
                    color = LightGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text(
                        text = category,
                        color = DarkText,
                        fontSize = 12.sp,
                        fontFamily = Poppins,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun RatingAndReviewsSection(
    rating: Double,
    reviewCount: Int,
    ratingDistribution: Map<String, Int>
) {
    Column {
        Text(
            text = "Rating dan ulasan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Rating Number
            Text(
                text = String.format("%.1f", rating),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                fontFamily = Poppins
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Star Ratings and Reviews
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Stars
                Row {
                    repeat(5) { index ->
                        val starFilled = index < rating.toInt()
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (starFilled) StarGold else GrayText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$reviewCount ulasan",
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Modern Rating Distribution - Firebase data dengan proporsi yang benar
                val maxCount = ratingDistribution.values.maxOrNull() ?: 1

                (5 downTo 1).forEach { star ->
                    val count = ratingDistribution[star.toString()] ?: 0
                    val percentage = if (maxCount > 0) (count * 100f / maxCount) else 0f

                    ModernRatingBar(
                        starCount = star,
                        count = count,
                        percentage = percentage
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ModernRatingBar(
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
        // Rating number and star icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.width(60.dp)
        ) {
            Text(
                text = "$starCount",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint = StarGold,
                modifier = Modifier.size(14.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Modern Progress Bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(LightGray, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage / 100f)
                    .background(HeaderBlue, RoundedCornerShape(4.dp))
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Review count
        Text(
            text = count.toString(),
            fontSize = 14.sp,
            color = DarkText,
            fontFamily = Poppins,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
fun UserReviewsSection(
    reviews: List<com.example.pustakago.data.model.ReviewDto>,
    isLoading: Boolean
) {
    Column {
        Text(
            text = "Ulasan Pengguna",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HeaderBlue)
                }
            }
            reviews.isEmpty() -> {
                Text(
                    text = "Belum ada ulasan",
                    fontSize = 14.sp,
                    color = GrayText,
                    fontFamily = Poppins,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
            else -> {
                // Display reviews
                reviews.take(3).forEach { review ->
                    UserReviewItem(review = review)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (reviews.size > 3) {
                    Text(
                        text = "Lihat semua ${reviews.size} ulasan",
                        color = HeaderBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Poppins,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { /* TODO: Show all reviews */ }
                    )
                }
            }
        }
    }
}

@Composable
fun UserReviewItem(review: com.example.pustakago.data.model.ReviewDto) {
    Row {
        AsyncImage(
            model = review.userAvatar.ifEmpty {
                "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face"
            },
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.userName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontFamily = Poppins
                )
                Spacer(modifier = Modifier.width(8.dp))
                repeat(5) { index ->
                    val starFilled = index < review.rating
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = if (starFilled) StarGold else GrayText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = review.reviewText,
                fontSize = 12.sp,
                color = GrayText,
                fontFamily = Poppins,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun AboutAuthorSection(
    authorName: String,
    authorBio: String,
) {
    Column {
        Text(
            text = "Tentang $authorName",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Gunakan bio lengkap jika tersedia, fallback ke default jika kosong
        val fullBio = authorBio.ifEmpty {
            "Penulis terkenal dengan karya-karya yang influential dalam bidangnya."
        }

        Text(
            text = fullBio,
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Baca selengkapnya",
            color = HeaderBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Poppins,
            modifier = Modifier.clickable { /* TODO: Expand bio */ }
        )
    }
}

@Composable
fun RatingInputSection(
    isLoggedIn: Boolean,
    userRating: Int,
    userReview: com.example.pustakago.data.model.ReviewDto?,
    onShowReviewDialog: () -> Unit,
    onEditReview: () -> Unit,
    onRatingChanged: (Int) -> Unit
) {
    Column {
        Text(
            text = "Beri Nilai Buku Ini",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoggedIn) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) { index ->
                    val starFilled = index < userRating
                    IconButton(
                        onClick = { onRatingChanged(index + 1) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rate ${index + 1} star",
                            tint = if (starFilled) StarGold else Color.LightGray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (userReview != null) {
                        onEditReview()
                    } else {
                        onShowReviewDialog()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (userReview != null) "Edit ulasan" else "Tulis ulasan",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )
            }
        } else {
            Text(
                text = "Login untuk memberikan rating dan ulasan",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun BookDetailsSection(book: BookDto) {
    Column {
        Text(
            text = "Detail buku",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        DetailRow("Pengarang", book.author)
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
            .padding(vertical = 8.dp),
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
