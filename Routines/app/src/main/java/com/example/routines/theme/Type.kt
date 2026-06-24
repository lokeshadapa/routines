package com.example.routines.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.routines.R

@OptIn(ExperimentalTextApi::class)
val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_variable, weight = FontWeight.Normal,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.nunito_variable, weight = FontWeight.SemiBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.nunito_variable, weight = FontWeight.Bold,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    Font(R.font.nunito_variable, weight = FontWeight.ExtraBold,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))),
    Font(R.font.nunito_variable, weight = FontWeight.Black,
        variationSettings = FontVariation.Settings(FontVariation.weight(900))),
    Font(R.font.nunito_italic_variable, weight = FontWeight.Normal, style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(400))),
    Font(R.font.nunito_italic_variable, weight = FontWeight.SemiBold, style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(600))),
    Font(R.font.nunito_italic_variable, weight = FontWeight.Bold, style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(700))),
    Font(R.font.nunito_italic_variable, weight = FontWeight.ExtraBold, style = FontStyle.Italic,
        variationSettings = FontVariation.Settings(FontVariation.weight(800))),
)

val Typography = Typography(
    displayLarge   = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Black,     fontSize = 48.sp, letterSpacing = (-2).sp),
    headlineLarge  = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, letterSpacing = (-0.5).sp),
    titleLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 17.sp, letterSpacing = (-0.3).sp),
    titleMedium    = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 16.sp),
    bodyLarge      = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 24.sp),
    bodyMedium     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.SemiBold,  fontSize = 13.sp, lineHeight = 20.sp),
    labelLarge     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 10.sp, letterSpacing = 2.sp),
    labelSmall     = TextStyle(fontFamily = NunitoFontFamily, fontWeight = FontWeight.Bold,      fontSize = 10.sp, letterSpacing = 1.5.sp),
)
