package com.mwilky.androidenhanced

import android.annotation.SuppressLint
import android.content.Context

class References {

    companion object {

        @SuppressLint("StaticFieldLeak")
        lateinit var SystemUIContext: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var ServicesContext: Context

        @SuppressLint("StaticFieldLeak")
        lateinit var LauncherContext: Context
        lateinit var CollapsedStatusBarFragment: Any
        lateinit var Clock: Any
        lateinit var ShadeHeaderController: Any
        lateinit var NotificationPanelViewController: Any
        lateinit var NotificationPanelView: Any
        lateinit var CentralSurfacesImpl: Any
        lateinit var ShadeController: Any
        lateinit var PhoneStatusBarViewController: Any
        lateinit var PhoneStatusBarView: Any
        lateinit var RowAppearanceCoordinator: Any
        lateinit var NotifCollection: Any
        lateinit var NotifStats: Any
        lateinit var KeyguardCoordinator: Any
        lateinit var DisplayRotation: Any
        lateinit var KeyguardStatusBarView: Any
        lateinit var QSFooterView: Any
        lateinit var QSAnimator: Any
        lateinit var BrightnessSliderControllerFactory: Any
        lateinit var BrightnessControllerFactory: Any
        lateinit var QSPanelController: Any
        lateinit var QuickQSPanelController: Any
        lateinit var QuickQSPanelQQSSideLabelTileLayout: Any
        lateinit var QSCustomizerController3: Any
        lateinit var CurrentTilesInteractorImpl: Any
        lateinit var QSTileViewImpl: Any
        lateinit var DarkIconDispatcherImpl: Any
        lateinit var TintedIconManager: Any
        lateinit var BatteryMeterView: Any
        var ModernShadeCarrierGroupMobileView: Any? = null
        val isDisplayRotationReady: Boolean
            get() = this::DisplayRotation.isInitialized
        lateinit var PhoneWindowManager: Any
        lateinit var MediaSessionLegacyHelper: Class<*>
        lateinit var TouchAnimatorBuilder: Class<*>
        lateinit var Interpolators: Class<*>
        lateinit var PathInterpolatorBuilderPathInterpolator: Class<*>
        lateinit var HeightExpansionAnimator: Class<*>

        //Clock position
        lateinit var mDefaultClockContainer: Any

        // Quicksettings brightness Slider
        lateinit var mBrightnessMirrorHandler: Any
        lateinit var mQQsBrightnessMirrorHandler: Any
        lateinit var mQQsBrightnessController: Any
        lateinit var mQQsBrightnessSliderController: Any

        // List of the current QS tiles
        var TileList = mutableListOf<Any>()

    }
}