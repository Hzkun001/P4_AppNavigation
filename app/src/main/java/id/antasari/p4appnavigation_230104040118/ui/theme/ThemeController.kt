package id.antasari.p4appnavigation_230104040118.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeController {
    // default ikut sistem; nanti kita set manual dari Switch
    var isDark by mutableStateOf(false)
}
