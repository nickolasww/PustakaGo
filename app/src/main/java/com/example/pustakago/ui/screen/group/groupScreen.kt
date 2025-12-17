package com.example.pustakago.ui.screen.group

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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

// Data class for Group
data class ReadingGroup(
    val id: String,
    val name: String,
    val memberCount: Int,
)

// Data class for Recommended Community
data class RecommendedCommunity(
    val id: String,
    val name: String,
    val memberCount: Int,
    val imageRes: Int? = null
)

@Composable
fun GroupScreen(
    navController: NavHostController,
    viewModel: GroupViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        GroupHeaderSection(
            search = state.searchQuery,
            onSearchChange = { viewModel.onSearchQueryChange(it) },
            isLoggedIn = state.isLoggedIn,
            userName = state.userName,
            onProfileClick = {
                if (state.isLoggedIn) {
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
            // Recommended Communities Section
            RecommendedCommunitiesSection()

            Spacer(modifier = Modifier.height(24.dp))

            // Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Komunitas Lainnya",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    fontFamily = Poppins
                )

                // Add Group Button
                if (state.isLoggedIn) {
                    IconButton(
                        onClick = { /* Handle create group */ },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(HeaderBlue)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Grup",
                            tint = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group List
            val groups = state.groups.ifEmpty { getSampleGroups() }

            groups.forEach { group ->
                GroupCard(
                    group = group,
                    onClick = { /* Navigate to group detail */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(80.dp)) // Space for bottom nav
        }
    }
}

@Composable
fun RecommendedCommunitiesSection() {
    Column {
        Text(
            text = "Komunitas yang cocok buat kamu!",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            fontFamily = Poppins,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(getRecommendedCommunities()) { community ->
                RecommendedCommunityCard(
                    community = community,
                    onClick = { /* Navigate to community detail */ }
                )
            }
        }
    }
}

@Composable
fun RecommendedCommunityCard(
    community: RecommendedCommunity,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background with gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0096DB).copy(alpha = 0.8f),
                                Color(0xFF0077B6).copy(alpha = 0.9f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            // Content Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomStart
            ) {
                Column {
                    Text(
                        text = community.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = Poppins,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${community.memberCount} member",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontFamily = Poppins
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupHeaderSection(
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
                        "Cari kelompk mu...",
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
fun GroupCard(
    group: ReadingGroup,
    onJoinClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Group Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HeaderBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Groups,
                    contentDescription = null,
                    tint = HeaderBlue,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Group Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = group.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText,
                    fontFamily = Poppins,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = GrayText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${group.memberCount}k Member",
                        fontSize = 12.sp,
                        color = GrayText,
                        fontFamily = Poppins
                    )
                }
            }

            // Join Button
            Button(
                onClick = { onJoinClick() },
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HeaderBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Gabung",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Poppins
                )
            }
        }
    }
}

// Categories data
data class Category(
    val id: String,
    val name: String,
    val isSelected: Boolean = false
)

@Composable
fun CategoriesSlider() {
    var selectedCategory by remember { mutableStateOf("semua") }

    val categories = listOf(
        Category("semua", "Semua", selectedCategory == "semua"),
        Category("fiksi", "Fiksi", selectedCategory == "fiksi"),
        Category("non-fiksi", "Non-Fiksi", selectedCategory == "non-fiksi"),
        Category("sains", "Sains", selectedCategory == "sains"),
        Category("sejarah", "Sejarah", selectedCategory == "sejarah"),
        Category("filosofi", "Filosofi", selectedCategory == "filosofi"),
        Category("horror", "Horror", selectedCategory == "horror")
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 4.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                onClick = { selectedCategory = category.id },
                label = {
                    Text(
                        text = category.name,
                        fontSize = 14.sp,
                        fontWeight = if (selectedCategory == category.id) FontWeight.SemiBold else FontWeight.Normal,
                        fontFamily = Poppins
                    )
                },
                selected = selectedCategory == category.id,
                modifier = Modifier.height(36.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = HeaderBlue,
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5),
                    labelColor = GrayText
                ),
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}

// Sample data
fun getSampleGroups(): List<ReadingGroup> {
    return listOf(
        ReadingGroup(
            id = "1",
            name = "Pecinta Sains",
            memberCount = 25,
        ),
        ReadingGroup(
            id = "2",
            name = "Filosofi Indonesia",
            memberCount = 18,
        ),
        ReadingGroup(
            id = "3",
            name = "Horror Club",
            memberCount = 32,
        ),
        ReadingGroup(
            id = "4",
            name = "Buku Fiksi",
            memberCount = 45,
        )
    )
}

fun getRecommendedCommunities(): List<RecommendedCommunity> {
    return listOf(
        RecommendedCommunity(
            id = "rec1",
            name = "Pecinta Novel Fiksi",
            memberCount = 1250
        ),
        RecommendedCommunity(
            id = "rec2",
            name = "Book Club Jakarta",
            memberCount = 890
        )
    )
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun GroupScreenPreview() {
    PustakaGoTheme {
        GroupScreen(navController = rememberNavController())
    }
}
