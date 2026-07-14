package com.github.kr328.clash.design.component

import android.content.Context
import android.graphics.Color
import com.github.kr328.clash.design.R
import com.github.kr328.clash.design.util.getPixels
import com.github.kr328.clash.design.util.resolveThemedColor
import com.github.kr328.clash.design.util.resolveThemedResourceId

class ProxyViewConfig(val context: Context, var proxyLine: Int) {
    private val colorSurface = context.resolveThemedColor(com.google.android.material.R.attr.colorSurface)

    val clickableBackground =
        context.resolveThemedResourceId(android.R.attr.selectableItemBackground)

    val selectedControl = context.getColor(R.color.flashlink_orange)
    val selectedBackground = context.getColor(R.color.flashlink_orange_soft)
    val routeTitle = context.getColor(R.color.flashlink_ink)
    val routeMuted = context.getColor(R.color.flashlink_muted)
    val routeBorder = context.getColor(R.color.flashlink_orange)
    val routeCircle = context.getColor(R.color.flashlink_orange_soft)
    val routeUnselectedCircle = Color.rgb(220, 220, 220)
    val routeWhite = Color.WHITE

    val unselectedControl = context.resolveThemedColor(com.google.android.material.R.attr.colorOnSurface)
    val unselectedBackground: Int
        get() = colorSurface

    val layoutPadding = context.getPixels(R.dimen.proxy_layout_padding).toFloat()
    val contentPadding
        get() = context.getPixels(R.dimen.proxy_content_padding).toFloat()
    val textMargin
        get() = if (proxyLine==2) context.getPixels(R.dimen.proxy_text_margin).toFloat() else context.getPixels(R.dimen.proxy_text_margin_grid3).toFloat()
    val textSize
        get() = if (proxyLine==2) context.getPixels(R.dimen.proxy_text_size).toFloat() else context.getPixels(R.dimen.proxy_text_size_grid3).toFloat()

    val shadow = Color.argb(
        0x15,
        Color.red(Color.DKGRAY),
        Color.green(Color.DKGRAY),
        Color.blue(Color.DKGRAY),
    )

    val cardRadius = context.getPixels(R.dimen.proxy_card_radius).toFloat()
    var cardOffset = context.getPixels(R.dimen.proxy_card_offset).toFloat()

    val routeFlagSize = context.getPixels(R.dimen.proxy_route_flag_size).toFloat()
    val routeIndicatorSize = context.getPixels(R.dimen.proxy_route_indicator_size).toFloat()
    val routeGap = context.getPixels(R.dimen.proxy_route_gap).toFloat()
    val routeTitleSize = context.getPixels(R.dimen.proxy_route_title_size).toFloat()
    val routeSubtitleSize = context.getPixels(R.dimen.proxy_route_subtitle_size).toFloat()
}
