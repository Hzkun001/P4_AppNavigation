package id.antasari.p4appnavigation_230104040118.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val LightColorScheme = lightColorScheme(
    // KUNCI: primary dipakai untuk aksen kecil (icon/btn), appbar pakai primaryContainer
    primary = Indigo500,
    onPrimary = Color.White,

    primaryContainer = Indigo10,
    onPrimaryContainer = IndigoOnContainer,

    secondary = BlueGrey500,
    onSecondary = Color.White,

    secondaryContainer = BlueGrey10,
    onSecondaryContainer = BlueGreyOnContainer,

    background = BgLight,
    onBackground = Color(0xFF1C1B1F),

    surface = SurfaceLight,
    onSurface = Color(0xFF1C1B1F),

    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Indigo500Dark,
    onPrimary = Color(0xFF101223),

    primaryContainer = IndigoContainerDark,
    onPrimaryContainer = IndigoOnContainerDark,

    secondary = BlueGrey500Dark,
    onSecondary = Color(0xFF0D1114),

    secondaryContainer = BlueGreyContainerDark,
    onSecondaryContainer = BlueGreyOnContainerDark,

    background = BgDark,
    onBackground = Color(0xFFE6E1E5),

    surface = SurfaceDark,
    onSurface = Color(0xFFE6E1E5),

    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark,
)

@Composable
fun P4appnavigation_230104040118Theme(
    darkTheme: Boolean = ThemeController.isDark,
    dynamicColor: Boolean = false, // matikan supaya palet custom dipakai, biar konsisten
    content: @Composable () -> Unit
) {
    val scheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = scheme, typography = Typography, content = content)
}
