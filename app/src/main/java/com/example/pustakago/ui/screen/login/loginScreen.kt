package com.example.pustakago.ui.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pustakago.R
import com.example.pustakago.ui.theme.PustakaGoTheme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.example.pustakago.R.font
import com.example.pustakago.ui.theme.Poppins
import androidx.navigation.NavHostController

val PrimaryBlue = Color(0xFF007BFF)
val GrayText = Color(0xFF9E9E9E)
val DarkText = Color(0xFF212121)
val ButtonColor = Color(0xFF0F8CD6)


@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Pustaka GO Logo",
            modifier = Modifier.size(120.dp)
        )


        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = buildAnnotatedString {
                append("Selamat datang kembali di Pustaka ")
                withStyle(style = SpanStyle(color = PrimaryBlue)) {
                    append("GO !")
                }
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = DarkText,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masuk melalui formulir berikut.",
            fontSize = 14.sp,
            color = GrayText,
            fontFamily = Poppins
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email Field
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Email",
                fontSize = 14.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Masukkan email anda", color = GrayText) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = "Email",
                        tint = GrayText
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = PrimaryBlue
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Kata Sandi",
                fontSize = 14.sp,
                color = DarkText,
                fontWeight = FontWeight.Medium,
                fontFamily = Poppins
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Masukkan sandi anda", color = GrayText) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = GrayText
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedBorderColor = PrimaryBlue
                ),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Remember Me Checkbox
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = { rememberMe = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = PrimaryBlue,
                    uncheckedColor = PrimaryBlue,
                    checkmarkColor = Color.White
                )
            )
            Text(
                text = "Ingat saya",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(ButtonColor, RoundedCornerShape(8.dp))
                .clickable { /* Handle login */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Masuk",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Divider with "Atau"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
            Text(
                text = "  Atau  ",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Google Login Button
        OutlinedButton(
            onClick = { /* Handle Google login */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(8.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Masuk dengan Google",
                fontSize = 14.sp,
                color = DarkText,
                fontFamily = Poppins
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Register Link
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Belum punya akun ? ",
                fontSize = 14.sp,
                color = GrayText,
                fontFamily = Poppins
            )
            Text(
                text = "Daftar",
                fontSize = 14.sp,
                color = PrimaryBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { navController.navigate("register") },
                fontFamily = Poppins
            )
        }
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun LoginScreenPreview() {
    PustakaGoTheme {
        LoginScreen(navController = rememberNavController())
    }
}
