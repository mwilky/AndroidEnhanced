package com.mwilky.androidenhanced.xposed
import android.content.res.XModuleResources
import com.mwilky.androidenhanced.R
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources

import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    companion object {

        //Package to hook
        const val SYSTEMUI_PACKAGE = "com.android.systemui"
        const val FRAMEWORK_PACKAGE = "android"
        const val LAUNCHER_PACKAGE = "com.google.android.apps.nexuslauncher"

        private var MODULE_PATH: String? = null

        //Modified resources
        var QSQuickTileSize: Int = 0
        var QSLabelContainerMargin = 0
        var QSTilePadding = 0
        var QSTileTextSize = 0
        var QSTileHeight = 0
        var QSCellMarginHorizontal = 0
        var QSCellMarginVertical = 0
        var QSTileTextLineHeight = 0

        var iconContainerBackground = 0
        var iconContainerBackgroundShape = 0
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        when(lpparam?.packageName) {
            FRAMEWORK_PACKAGE -> {
                Buttons.init(lpparam.classLoader)
                Misc.init(lpparam.classLoader)
                Lockscreen.initFramework(lpparam.classLoader)
                Notifications.initFramework(lpparam.classLoader)
            }
            SYSTEMUI_PACKAGE -> {
                SystemUIApplication.init(lpparam.classLoader)
                Statusbar.init(lpparam.classLoader)
                StatusbarPremium.init(lpparam.classLoader)
                Lockscreen.initSystemUI(lpparam.classLoader)
                Quicksettings.init(lpparam.classLoader)
                QuicksettingsPremium.init(lpparam.classLoader)
                Notifications.initSystemUI(lpparam.classLoader)
            }
            LAUNCHER_PACKAGE -> {
                Buttons.initLauncher(lpparam.classLoader)
            }
        }
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {

        val modRes = XModuleResources.createInstance(MODULE_PATH, resparam!!.res)

        if (resparam.packageName != SYSTEMUI_PACKAGE) return

        QSQuickTileSize =
            resparam.res.addResource(modRes, R.dimen.qs_quick_tile_size)
        QSLabelContainerMargin =
            resparam.res.addResource(modRes, R.dimen.qs_label_container_margin)
        QSTilePadding =
            resparam.res.addResource(modRes, R.dimen.qs_tile_padding)
        QSTileTextSize =
            resparam.res.addResource(modRes, R.dimen.qs_tile_text_size)
        QSTileHeight =
            resparam.res.addResource(modRes, R.dimen.qs_tile_height)
        QSCellMarginHorizontal =
            resparam.res.addResource(modRes, R.dimen.qs_tile_margin_horizontal)
        QSCellMarginVertical =
            resparam.res.addResource(modRes, R.dimen.qs_tile_margin_vertical)
        QSTileTextLineHeight =
            resparam.res.addResource(modRes, R.dimen.qs_tile_text_line_height)

        iconContainerBackground =
            resparam.res.addResource(modRes, com.mwilky.androidenhanced.R.drawable.icon_container_background)

        iconContainerBackgroundShape =
            resparam.res.addResource(modRes, com.mwilky.androidenhanced.R.drawable.icon_container_background_shape)

        //Send modified resources to premium module
        QuicksettingsPremium.QSQuickTileSize = QSQuickTileSize
        QuicksettingsPremium.QSLabelContainerMargin = QSLabelContainerMargin
        QuicksettingsPremium.QSTilePadding = QSTilePadding
        QuicksettingsPremium.QSTileTextSize = QSTileTextSize
        QuicksettingsPremium.QSTileHeight = QSTileHeight
        QuicksettingsPremium.QSCellMarginHorizontal = QSCellMarginHorizontal
        QuicksettingsPremium.QSCellMarginVertical = QSCellMarginVertical
        QuicksettingsPremium.QSTileTextLineHeight = QSTileTextLineHeight

        QuicksettingsPremium.iconContainerBackground = iconContainerBackground
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        MODULE_PATH = startupParam!!.modulePath
    }

}