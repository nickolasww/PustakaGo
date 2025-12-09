package com.example.pustakago.ui.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    color = Color(0xFF0096DB),
                    shape = RoundedCornerShape(
                        bottomStart = 20.dp,
                        bottomEnd = 20.dp
                    )
                )
        ) {

            // Jika kamu punya asset background bulat → tinggal masukkan:
            // Image(
            //     painter = painterResource(R.drawable.header_background),
            //     contentDescription = null,
            //     modifier = Modifier.fillMaxSize(),
            //     contentScale = ContentScale.Crop
            // )

           Box(modifier = Modifier
               .fillMaxSize()
               .padding(vertical = 20.dp, horizontal = 20.dp)

           ){
               SearchBar(
                   text = search,
                   onTextChange = { search = it },
                   modifier = Modifier
                       .padding(horizontal = 20.dp)
                       .align(Alignment.Center)
                       .offset(y = 28.dp)
               )
           }
        }

        Spacer(Modifier.height(24.dp))


    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ){

        // SECTION 1 -----------------------
        SectionTitle(title = "Eksplorasi ilmu sains!")
        BookRow(
            books = listOf(
//                Triple(R.drawable.book_kosmos, "Kosmos", "1980"),
//                Triple(R.drawable.book_tyson, "Astrophysics for…", "2017"),
//                Triple(R.drawable.book_hawking, "A Brief History…", "1980")
            )
        )


        Spacer(Modifier.height(24.dp))

        // SECTION 2 -----------------------
        SectionTitle(title = "Memperdalam pemahaman")
        BookRow(
            books = listOf(
//                Triple(R.drawable.book_sophie, "Sophie’s World", "1991"),
//                Triple(R.drawable.book_terras, "Filosofi Teras", "2018"),
//                Triple(R.drawable.book_filsafat, "Sejarah Filsafat…", "1945")
            )
        )

        Spacer(Modifier.height(24.dp))

        // SECTION 3 -----------------------
        SectionTitle(title = "Mencekam dan merinding!")
        BookRow(
            books = listOf(
//                Triple(R.drawable.book_it, "It", "1986"),
//                Triple(R.drawable.book_dracula, "Dracula", "1897"),
//                Triple(R.drawable.book_danur, "Danur", "2011")
            )
        )

        }
    }
}


@Composable
fun SearchBar(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        placeholder = { Text("Cari buku sekarang!") },
        shape = RoundedCornerShape(15.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}


@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun BookRow(books: List<Triple<Int, String, String>>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 12.dp)
    ) {
        items(books.size) { index ->
            val (image, title, year) = books[index]
            Column(
                modifier = Modifier.width(120.dp)
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                        .background(Color.LightGray, RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    maxLines = 1,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = year,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
