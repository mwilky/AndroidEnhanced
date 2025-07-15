package com.mwilky.androidenhanced

import android.content.res.XModuleResources
import com.mwilky.androidenhanced.Utils.Companion.QSCellMarginHorizontal
import com.mwilky.androidenhanced.Utils.Companion.QSCellMarginVertical
import com.mwilky.androidenhanced.Utils.Companion.QSLabelContainerMargin
import com.mwilky.androidenhanced.Utils.Companion.QSQuickTileSize
import com.mwilky.androidenhanced.Utils.Companion.QSTileHeight
import com.mwilky.androidenhanced.Utils.Companion.QSTilePadding
import com.mwilky.androidenhanced.Utils.Companion.QSTileTextLineHeight
import com.mwilky.androidenhanced.Utils.Companion.QSTileTextSize
import com.mwilky.androidenhanced.Utils.Companion.iconContainerBackground
import com.mwilky.androidenhanced.Utils.Companion.iconContainerBackgroundShape
import com.mwilky.androidenhanced.xposed.Launcher
import com.mwilky.androidenhanced.xposed.Framework
import com.mwilky.androidenhanced.xposed.SystemUI
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

    companion object {
        //Package to hook
        const val SYSTEMUI_PACKAGE = "com.android.systemui"
        const val FRAMEWORK_PACKAGE = "android"
        const val LAUNCHER_PACKAGE = "com.google.android.apps.nexuslauncher"

        private var MODULE_PATH: String? = null

    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        when (lpparam.packageName) {

            FRAMEWORK_PACKAGE -> {
                Framework.init(lpparam)
            }

            SYSTEMUI_PACKAGE -> {
                SystemUI.init(lpparam.classLoader)
            }

            LAUNCHER_PACKAGE -> {
                Launcher.init(lpparam.classLoader)
            }
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        MODULE_PATH = startupParam!!.modulePath
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
            resparam.res.addResource(modRes, R.drawable.icon_container_background)
        iconContainerBackgroundShape =
            resparam.res.addResource(modRes, R.drawable.icon_container_background_shape)
    }
}