package com.github.kr328.clash.design.component

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import com.github.kr328.clash.core.model.Proxy
import com.github.kr328.clash.design.model.ProxyState
import kotlin.math.absoluteValue
import kotlin.math.max

class ProxyViewState(
    val config: ProxyViewConfig,
    val proxy: Proxy,
    private val parent: ProxyState,
    private val link: ProxyState?
) {
    val paint = Paint()
    val rect = Rect()
    val path = Path()

    var title: String = ""
    var subtitle: String = ""
    var delayText: String = ""
    var displayTitle: String = ""
    var countryFlag: String = "🌐"
    var background: Int = config.unselectedBackground
    var controls: Int = config.unselectedControl

    private var delay: Int = 0
    var selected: Boolean = false
        private set
    private var parentNow: String = ""
    private var linkNow: String? = null

    private var lastFrameTime = System.currentTimeMillis()

    fun update(snap: Boolean): Boolean {
        val frameTime = System.currentTimeMillis()
        var invalidate = false

        if (proxy.isGroup) {
            title = proxy.name

            if (link == null) {
                subtitle = proxy.type
            } else {
                if (linkNow !== link.now) {
                    linkNow = link.now

                    subtitle = "%s(%s)".format(
                        proxy.type,
                        link.now.ifEmpty { "*" }
                    )
                }
            }
        } else {
            title = proxy.title
            subtitle = proxy.subtitle
        }

        displayTitle = title.replace(LEADING_SYMBOLS, "").ifBlank { title }
        countryFlag = flagFor(title)

        if (delay != proxy.delay) {
            delay = proxy.delay
            delayText = if (proxy.delay in 0..Short.MAX_VALUE) proxy.delay.toString() else ""
        }

        if (parentNow !== parent.now) {
            parentNow = parent.now
            selected = proxy.name == parent.now
        }

        controls = if (selected) config.selectedControl else config.unselectedControl

        if (snap) {
            background = if (selected) config.selectedBackground else config.unselectedBackground
        } else {
            val target = if (selected) config.selectedBackground else config.unselectedBackground

            if (background != target) {
                val sa = Color.alpha(background)
                val sr = Color.red(background)
                val sg = Color.green(background)
                val sb = Color.blue(background)

                val ta = Color.alpha(target)
                val tr = Color.red(target)
                val tg = Color.green(target)
                val tb = Color.blue(target)

                val da = ta - sa
                val dr = tr - sr
                val dg = tg - sg
                val db = tb - sb

                val max = max(
                    da.absoluteValue,
                    max(
                        dr.absoluteValue,
                        max(
                            dg.absoluteValue,
                            db.absoluteValue
                        )
                    )
                )

                val frameOffset = frameTime - lastFrameTime

                val colorOffset = (frameOffset / max.toFloat().coerceAtLeast(0.001f))
                    .coerceIn(0.0f, 1.0f)

                background = if (colorOffset > 0.999f) {
                    target
                } else {
                    Color.argb(
                        (sa + da * colorOffset).toInt(),
                        (sr + dr * colorOffset).toInt(),
                        (sg + dg * colorOffset).toInt(),
                        (sb + db * colorOffset).toInt()
                    )
                }

                invalidate = true
            }
        }

        lastFrameTime = frameTime

        return invalidate
    }

    fun qualityBars(): Int = when {
        delay !in 1..Short.MAX_VALUE -> 0
        delay <= 160 -> 3
        delay <= 360 -> 2
        else -> 1
    }

    companion object {
        private val LEADING_SYMBOLS = Regex("^[\\p{So}\\s]+")

        private fun flagFor(name: String): String {
            val key = name.lowercase()
            return when {
                "美国" in name || "us" in key || "united states" in key -> "🇺🇸"
                "香港" in name || "hong kong" in key -> "🇭🇰"
                "日本" in name || "japan" in key || "jp" in key -> "🇯🇵"
                "新加坡" in name || "singapore" in key || "sg" in key -> "🇸🇬"
                "加拿大" in name || "canada" in key || "ca" in key -> "🇨🇦"
                "英国" in name || "uk" in key || "united kingdom" in key -> "🇬🇧"
                "德国" in name || "germany" in key || "de" in key -> "🇩🇪"
                "韩国" in name || "korea" in key || "kr" in key -> "🇰🇷"
                "台湾" in name || "taiwan" in key || "tw" in key -> "🇹🇼"
                "澳大利亚" in name || "australia" in key || "au" in key -> "🇦🇺"
                else -> "🌐"
            }
        }
    }
}
