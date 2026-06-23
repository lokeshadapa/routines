package com.example.routines.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.routines.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val NunitoFont = GoogleFont("Nunito")

val NunitoFontFamily = FontFamily(
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.ExtraBold),
    Font(googleFont = NunitoFont, fontProvider = provider, weight = FontWeight.Black),
)

val Typography = Typography(
    displayLarge  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Black,     fontSize = 48.sp, letterSpacing = (-2).sp),
    headlineLarge = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.5).sp),
    titleLarge    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 17.sp, letterSpacing = (-0.3).sp),
    titleMedium   = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 16.sp),
    bodyLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 13.sp, lineHeight = 20.sp),
    labelLarge    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 2.sp),
    labelSmall    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 10.sp, letterSpacing = 1.5.sp),
)
