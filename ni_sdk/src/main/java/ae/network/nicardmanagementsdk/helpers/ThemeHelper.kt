package ae.network.nicardmanagementsdk.helpers

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NITheme

class ThemeHelper {

    fun getThemeResId(niInput: NIInput): Int = when (niInput.displayAttributes?.theme) {
        NITheme.LIGHT -> R.style.ThemeOverlay_NICardManagementSDK_DialogTheme_Day
        NITheme.DARK_APP_COMPAT -> R.style.ThemeOverlay_NICardManagementSDK_DialogTheme_NightAppCompat
        NITheme.DARK_MATERIAL -> R.style.ThemeOverlay_NICardManagementSDK_DialogTheme_NightMaterial
        else -> R.style.ThemeOverlay_NICardManagementSDK_DialogTheme_Auto
    }
}