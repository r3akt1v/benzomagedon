package com.benzomageddon.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.benzomageddon.R

// --- Palette: quiet ledger dark ---
val Bg = Color(0xFF0B0D11)
val Surface = Color(0xFF12151B)
val Raised = Color(0xFF1A1E26)
val Hairline = Color(0xFF232833)
val TextPrimary = Color(0xFFECEFF4)
val TextSecondary = Color(0xFF8A93A3)
val TextFaint = Color(0xFF5A6272)

val Accent = Color(0xFFF5B841)       // warm amber — single accent
val Income = Color(0xFF5BD6A0)       // muted green
val Expense = Color(0xFFF07167)      // muted red

private val DarkColors = darkColorScheme(
    primary = Accent,
    onPrimary = Color(0xFF1A1405),
    primaryContainer = Color(0xFF2A2113),
    onPrimaryContainer = Accent,
    secondary = TextSecondary,
    onSecondary = Bg,
    secondaryContainer = Raised,
    onSecondaryContainer = TextPrimary,
    tertiary = Income,
    background = Bg,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Raised,
    onSurfaceVariant = TextSecondary,
    outline = Hairline,
    outlineVariant = Hairline,
    error = Expense,
    onError = Bg,
    errorContainer = Color(0xFF2A1512),
    onErrorContainer = Expense,
    surfaceContainerLowest = Bg,
    surfaceContainerLow = Surface,
    surfaceContainer = Surface,
    surfaceContainerHigh = Raised,
    surfaceContainerHighest = Raised
)

val PlexSans = FontFamily(
    Font(R.font.ibm_plex_sans_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_sans_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_sans_semibold, FontWeight.SemiBold),
    Font(R.font.ibm_plex_sans_bold, FontWeight.Bold)
)

val PlexMono = FontFamily(
    Font(R.font.ibm_plex_mono_regular, FontWeight.Normal),
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium)
)

private val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Medium, fontSize = 40.sp, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.3).sp),
    headlineMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    headlineSmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 18.sp),
    titleLarge = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Medium, fontSize = 17.sp),
    titleMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    titleSmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Medium, fontSize = 13.sp),
    bodyLarge = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = PlexSans, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.08.em),
    labelMedium = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Normal, fontSize = 11.sp, letterSpacing = 0.08.em),
    labelSmall = TextStyle(fontFamily = PlexMono, fontWeight = FontWeight.Normal, fontSize = 10.sp, letterSpacing = 0.08.em)
)

@Composable
fun BenzomagedonTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = AppTypography,
        content = content
    )
}
