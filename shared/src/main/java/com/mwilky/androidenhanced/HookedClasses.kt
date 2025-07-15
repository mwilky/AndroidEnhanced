package com.mwilky.androidenhanced

class HookedClasses {
    companion object {

        // SystemUI
        const val QUICK_QS_PANEL_QQS_SIDE_LABEL_TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.QuickQSPanel\$QQSSideLabelTileLayout"
        const val QS_TILE_VIEW_IMPL_CLASS =
            "com.android.systemui.qs.tileimpl.QSTileViewImpl"
        const val QS_ICON_VIEW_IMPL_CLASS =
            "com.android.systemui.qs.tileimpl.QSIconViewImpl"
        const val QS_TILE_VIEW_IMPL_LONG_PRESS_ANIMATOR_CLASS =
            "$QS_TILE_VIEW_IMPL_CLASS\$initLongPressEffectCallback$1\$onStartAnimator$1$2"
        const val QS_ANIMATOR_CLASS =
            "com.android.systemui.qs.QSAnimator"
        const val USER_TILE_SPEC_REPOSITORY_CLASS =
            "com.android.systemui.qs.pipeline.data.repository.UserTileSpecRepository"
        const val CURRENT_TILES_INTERACT0R_IMPL_CLASS =
            "com.android.systemui.qs.pipeline.domain.interactor.CurrentTilesInteractorImpl"
        const val TINTED_ICON_MANAGER_CLASS =
            "com.android.systemui.statusbar.phone.ui.TintedIconManager"
        const val SHADE_HEADER_CONTROLLER_CLASS =
            "com.android.systemui.shade.ShadeHeaderController"
        const val MODERN_SHADE_CARRIER_GROUP_MOBILE_VEW_CLASS =
            "com.android.systemui.statusbar.pipeline.mobile.ui.view.ModernShadeCarrierGroupMobileView"
        const val QS_CUSTOMIZER_CLASS =
            "com.android.systemui.qs.customize.QSCustomizer"
        const val QS_IMPL_CLASS = "com.android.systemui.qs.QSImpl"
        const val FOOTER_ACTIONS_VIEW_MODEL_CLASS =
            "com.android.systemui.qs.footer.ui.viewmodel.FooterActionsViewModel"
        const val FOOTER_ACTIONS_BUTTON_VIEW_MODEL_CLASS =
            "com.android.systemui.qs.footer.ui.viewmodel.FooterActionsButtonViewModel"
        const val FOOTER_ACTIONS_CLASS =
            "com.android.systemui.qs.footer.ui.compose.FooterActionsKt"
        const val TILE_ADAPTER_CLASS =
            "com.android.systemui.qs.customize.TileAdapter"
        const val COLORKT_CLASS =
            "com.android.compose.theme.ColorKt"
        const val TILE_ADAPTER_TILE_DECORATION_CLASS =
            "com.android.systemui.qs.customize.TileAdapter\$TileItemDecoration"
        const val SCRIM_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.ScrimController"
        const val BRIGHTNESS_SLIDER_VIEW_CLASS =
            "com.android.systemui.settings.brightness.BrightnessSliderView"
        const val DARK_ICON_DISPATCHER_IMPL_CLASS =
            "com.android.systemui.statusbar.phone.DarkIconDispatcherImpl"
        const val SYSUI_DARK_ICON_DISPATCHER_DARK_CHANGE_CLASS =
            "com.android.systemui.statusbar.phone.SysuiDarkIconDispatcher\$DarkChange"
        const val BATTERY_METER_VIEW_CLASS =
            "com.android.systemui.battery.BatteryMeterView"
        const val BATTERY_METER_VIEW_CONTROLLER_TUNABLE_CLASS =
            "com.android.systemui.battery.BatteryMeterViewController$2"
        const val DUAL_TONE_HANDLER_CLASS =
            "com.android.systemui.DualToneHandler"
        const val STATUSBAR_ICON_VIEW_CLASS =
            "com.android.systemui.statusbar.StatusBarIconView"
        const val KEYGUARD_STATUSBAR_VIEW_CLASS =
            "com.android.systemui.statusbar.phone.KeyguardStatusBarView"
        const val KEYGUARD_ABS_KEY_INPUT_VIEW_CLASS =
            "com.android.keyguard.KeyguardAbsKeyInputView"
        const val NUM_PAD_KEY_CLASS =
            "com.android.keyguard.NumPadKey"
        const val CENTRAL_SURFACES_IMPL_CLASS =
            "com.android.systemui.statusbar.phone.CentralSurfacesImpl"
        const val ROW_APPEARANCE_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator"
        const val ROW_APPEARANCE_COORDINATOR_CLASS_ATTACH_2_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.RowAppearanceCoordinator\$attach$2"
        const val NOTIFICATION_ENTRY_CLASS =
            "com.android.systemui.statusbar.notification.collection.NotificationEntry"
        const val NOTIF_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.notification.collection.render.NotifViewController"
        const val KEYGUARD_COORDINATOR_ATTACH_1_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator\$attach$1"
        const val KEYGUARD_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator"
        const val SIDE_LABEL_TILE_LAYOUT_CLASS =
            "com.android.systemui.qs.SideLabelTileLayout"
        const val QS_TILE_IMPL_CLASS =
            "com.android.systemui.qs.tileimpl.QSTileImpl"
        const val QS_TILE_IMPL_H_CLASS =
            "com.android.systemui.qs.tileimpl.QSTileImpl\$H"
        const val QS_FOOTER_VIEW_CLASS =
            "com.android.systemui.qs.QSFooterView"
        const val QUICK_SETTINGS_CONTROLLER_IMPL_CLASS =
            "com.android.systemui.shade.QuickSettingsControllerImpl"
        const val QS_PANEL_CONTROLLER_CLASS =
            "com.android.systemui.qs.QSPanelController"
        const val QS_PANEL_CLASS =
            "com.android.systemui.qs.QSPanel"
        const val QS_PANEL_CONTROLLER_BASE_CLASS =
            "com.android.systemui.qs.QSPanelControllerBase"
        const val QUICK_QS_PANEL_CONTROLLER_CLASS =
            "com.android.systemui.qs.QuickQSPanelController"
        const val QS_TILE_STATE_STATE_CLASS =
            "com.android.systemui.plugins.qs.QSTile.State"
        const val QS_CUSTOMIZER_CONTROLLER_3_CLASS =
            "com.android.systemui.qs.customize.QSCustomizerController$3"
        const val QS_CUSTOMIZER_CONTROLLER_CLASS =
            "com.android.systemui.qs.customize.QSCustomizerController"
        const val BRIGHTNESS_MIRROR_HANDLER_CLASS =
            "com.android.systemui.settings.brightness.BrightnessMirrorHandler"
        const val QUICK_QS_PANEL_CLASS =
            "com.android.systemui.qs.QuickQSPanel"
        const val SYSUI_COLOR_EXTRACTOR_CLASS =
            "com.android.systemui.colorextraction.SysuiColorExtractor"
        const val NOTIFICATION_PANEL_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.shade.NotificationPanelViewController"
        const val NOTIFICATION_PANEL_VIEW_CONTROLLER_TOUCH_HANDLER_CLASS =
            "com.android.systemui.shade.NotificationPanelViewController\$TouchHandler"
        const val PHONE_STATUS_BAR_VIEW_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.PhoneStatusBarViewController"
        const val COLLAPSED_STATUSBAR_FRAGMENT_CLASS =
            "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment"
        const val HEADS_UP_APPEARANCE_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.HeadsUpAppearanceController"
        const val CLOCK_CLASS =
            "com.android.systemui.statusbar.policy.Clock"
        const val STATUSBAR_ICON_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.phone.ui.StatusBarIconController"
        const val SYSTEM_UI_APPLICATION_CLASS = "com.android.systemui.SystemUIApplication"
        const val PINNED_STATUS_CLASS =
            "com.android.systemui.statusbar.notification.headsup.PinnedStatus"
        const val UPDATE_PARENT_CLIPPING_CLASS =
            "com.android.systemui.statusbar.phone.HeadsUpAppearanceController$\$ExternalSyntheticLambda4"
        const val HEADS_UP_APPEARANCE_CONTROLLER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS =
            "com.android.systemui.statusbar.phone.HeadsUpAppearanceController$\$ExternalSyntheticLambda0"
        const val NOTIFICATION_STACK_SCROLL_LAYOUT_CONTROLLER_CLASS =
            "com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayoutController"
        const val NOTIFICATION_SHADE_WINDOW_CONTROLLER_IMPL_CLASS =
            "com.android.systemui.shade.NotificationShadeWindowControllerImpl"
        const val NOTIFICATION_SHADE_WINDOW_STATE_CLASS =
            "com.android.systemui.shade.NotificationShadeWindowState"
        const val GLOBAL_ACTIONS_DIALOG_LITE_CLASS =
            "com.android.systemui.globalactions.GlobalActionsDialogLite"
        const val EXPANDABLE_CLASS =
            "com.android.systemui.animation.Expandable"
        const val QS_FOOTER_EXTERNAL_SYNTHETIC_LAMBDA_0_CLASS =
            "com.android.systemui.qs.QSFooterView$\$ExternalSyntheticLambda0"
        const val STACK_COORDINATOR_CLASS =
            "com.android.systemui.statusbar.notification.collection.coordinator.StackCoordinator"
        const val HEADS_UP_STATUSBAR_VIEW_CLASS =
            "com.android.systemui.statusbar.HeadsUpStatusBarView"
        const val TOUCH_ANIMATOR_BUILDER_CLASS =
            "com.android.systemui.qs.TouchAnimator\$Builder"
        const val INTERPOLATORS_CLASS =
            "com.android.app.animation.Interpolators"
        const val PATH_INTERPOLATOR_BUILDER_CLASS =
            "com.android.systemui.qs.PathInterpolatorBuilder\$PathInterpolator"
        const val HEIGHT_EXPANSION_ANIMATOR_CLASS =
            "com.android.systemui.qs.QSAnimator\$HeightExpansionAnimator"
        const val CONTINUATION_IMPL_CLASS =
            "kotlin.coroutines.jvm.internal.ContinuationImpl"
        const val RECYCLER_VIEW_VIEW_HOLDER_CLASS =
            "androidx.recyclerview.widget.RecyclerView\$ViewHolder"
        const val RECYCLER_VIEW_VIEW_CLASS =
            "androidx.recyclerview.widget.RecyclerView"
        const val COMPOSER_CLASS =
            "androidx.compose.runtime.Composer"
        const val MODIFIER_CLASS =
            "androidx.compose.ui.Modifier"

        // Static classes
        const val VIEW_CLIPPING_UTIL_CLASS =
            "com.android.internal.widget.ViewClippingUtil"
        const val BRIGHTNESS_UTILS_CLASS =
            "com.android.settingslib.display.BrightnessUtils"
        const val FONT_SIZE_UTILS =
            "com.android.systemui.FontSizeUtils"


        // Android
        const val PHONE_WINDOW_MANAGER_CLASS =
            "com.android.server.policy.PhoneWindowManager"
        const val PHONE_WINDOW_MANAGER_POWER_KEY_RULE_CLASS =
            "com.android.server.policy.PhoneWindowManager\$PowerKeyRule"
        const val PHONE_WINDOW_MANAGER_POLICY_HANDLER_CLASS =
            "com.android.server.policy.PhoneWindowManager\$PolicyHandler"
        const val POWER_MANAGER_SERVICE_CLASS =
            "com.android.server.power.PowerManagerService"
        const val POWER_MANAGER_SERVICE_POWER_MANAGER_HANDLER_CALLBACK_CLASS =
            "com.android.server.power.PowerManagerService\$PowerManagerHandlerCallback"
        const val MEDIA_SESSIONS_LEGACY_HELPER_CLASS =
            "android.media.session.MediaSessionLegacyHelper"
        const val GESTURE_LAUNCHER_SERVICE_CLASS =
            "com.android.server.GestureLauncherService"
        const val PHONE_WINDOW_MANAGER_9_CLASS =
            "com.android.server.policy.PhoneWindowManager$9"
        const val DISPLAY_ROTATION_CLASS =
            "com.android.server.wm.DisplayRotation"
        const val WINDOW_STATE_CLASS =
            "com.android.server.wm.WindowState"
        const val NOTIFICATION_ATTENTION_HELPER_CLASS =
            "com.android.server.notification.NotificationAttentionHelper"
        const val NOTIFICATION_RECORD_CLASS =
            "com.android.server.notification.NotificationRecord"

        // Launcher
        const val WORKSPACE_TOUCH_LISTENER_CLASS =
            "com.android.launcher3.touch.WorkspaceTouchListener"
        const val LAUNCHER_APPLICATION_CLASS =
            "com.android.launcher3.LauncherApplication"
    }
}