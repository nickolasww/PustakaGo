package com.example.pustakago.ui.screen.bookpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.pustakago.ui.theme.Poppins
import com.example.pustakago.ui.theme.PustakaGoTheme

// Colors
private val HeaderBlue = Color(0xFF0096DB)
private val DarkText = Color(0xFF212121)
private val GrayText = Color(0xFF757575)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPagesScreen(
    bookId: String,
    navController: NavHostController,
    viewModel: BookPagesViewModel = viewModel(
        key = "bookPages_$bookId",
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BookPagesViewModel(bookId) as T
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
        // Minimalist App Bar - only back icon, no title
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = DarkText
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

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
                ErrorContent(
                    error = state.error!!,
                    onRetry = { viewModel.retry() }
                )
            }
            state.currentPage != null -> {
                ReadingContent(
                    state = state,
                    onNextPage = { viewModel.nextPage() },
                    onPreviousPage = { viewModel.previousPage() }
                )
            }
            else -> {
                EmptyContent()
            }
        }
    }
}

@Composable
private fun ReadingContent(
    state: BookPagesState,
    onNextPage: () -> Unit,
    onPreviousPage: () -> Unit
) {
    val currentPage = state.currentPage ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Scrollable Reading Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Chapter label
            Text(
                text = currentPage.chapter,
                fontSize = 14.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = GrayText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Main title
            Text(
                text = currentPage.title,
                fontSize = 24.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                lineHeight = 32.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Content paragraphs
            ContentParagraphs(content = currentPage.content)

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Footer Navigation
        PageNavigation(
            currentPage = state.currentPageIndex + 1,
            totalPages = state.totalPages,
            hasNext = state.hasNextPage,
            hasPrevious = state.hasPreviousPage,
            onNext = onNextPage,
            onPrevious = onPreviousPage
        )
    }
}

@Composable
private fun ContentParagraphs(content: String) {
    // Split content by double line breaks or handle quotes
    val paragraphs = content.split("\n\n").filter { it.isNotBlank() }

    if (paragraphs.isEmpty()) {
        // If no double line breaks, display as single paragraph
        BookParagraph(text = content)
    } else {
        paragraphs.forEachIndexed { index, paragraph ->
            val trimmedParagraph = paragraph.trim()

            // Check if it's a quote (starts with quote character or contains citation pattern)
            if (trimmedParagraph.startsWith("\"") ||
                trimmedParagraph.startsWith("'") ||
                trimmedParagraph.contains(" - ") && trimmedParagraph.length < 500) {
                // Quote styling - simple with just spacing and dash
                QuoteParagraph(text = trimmedParagraph)
            } else {
                BookParagraph(text = trimmedParagraph)
            }

            if (index < paragraphs.lastIndex) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun BookParagraph(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        color = DarkText,
        lineHeight = 28.sp,
        textAlign = TextAlign.Start
    )
}

@Composable
private fun QuoteParagraph(text: String) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = "—",
            fontSize = 20.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Light,
            color = GrayText,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = text,
            fontSize = 15.sp,
            fontFamily = Poppins,
            fontWeight = FontWeight.Normal,
            color = GrayText,
            lineHeight = 26.sp,
            textAlign = TextAlign.Start,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

@Composable
private fun PageNavigation(
    currentPage: Int,
    totalPages: Int,
    hasNext: Boolean,
    hasPrevious: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page indicator
            Text(
                text = "Halaman $currentPage",
                fontSize = 14.sp,
                fontFamily = Poppins,
                fontWeight = FontWeight.Normal,
                color = GrayText
            )

            // Next button
            Button(
                onClick = onNext,
                enabled = hasNext,
                colors = ButtonDefaults.buttonColors(
                    containerColor = HeaderBlue,
                    disabledContainerColor = Color.LightGray
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Next",
                    fontSize = 14.sp,
                    fontFamily = Poppins,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
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
            colors = ButtonDefaults.buttonColors(containerColor = HeaderBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Coba Lagi",
                color = Color.White,
                fontFamily = Poppins
            )
        }
    }
}

@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Tidak ada konten untuk ditampilkan",
            fontSize = 16.sp,
            color = GrayText,
            fontFamily = Poppins
        )
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun BookPagesScreenPreview() {
    PustakaGoTheme {
        BookPagesScreen(
            bookId = "book_01",
            navController = rememberNavController()
        )
    }
}
