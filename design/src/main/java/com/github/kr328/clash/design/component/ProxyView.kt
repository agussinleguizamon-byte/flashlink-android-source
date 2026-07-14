package com.github.kr328.clash.design.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.view.View

class ProxyView(
    context: Context,
    private val config: ProxyViewConfig,
) : View(context) {
    var state: ProxyViewState? = null

    constructor(context: Context) : this(context, ProxyViewConfig(context, 1))

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val preferred = (config.contentPadding * 2 + config.routeFlagSize + config.routeGap).toInt()
        val height = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.UNSPECIFIED -> preferred
            else -> preferred.coerceAtMost(MeasureSpec.getSize(heightMeasureSpec))
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val state = state ?: return
        if (state.update(false)) postInvalidate()

        val config = state.config
        val paint = state.paint.apply { reset(); isAntiAlias = true }
        val left = config.layoutPadding
        val top = config.layoutPadding
        val right = width.toFloat() - config.layoutPadding
        val bottom = height.toFloat() - config.layoutPadding
        val card = RectF(left, top, right, bottom)
        val centerY = card.centerY()

        // Card shadow and selected outline mirror the app's orange/white visual system.
        paint.color = if (state.selected) config.selectedBackground else config.routeWhite
        paint.style = Paint.Style.FILL
        paint.setShadowLayer(config.cardRadius * .45f, 0f, config.cardRadius * .18f, config.shadow)
        setLayerType(LAYER_TYPE_SOFTWARE, paint)
        canvas.drawRoundRect(card, config.cardRadius, config.cardRadius, paint)
        paint.clearShadowLayer()
        if (state.selected) {
            paint.color = config.routeBorder
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.5f * resources.displayMetrics.density
            canvas.drawRoundRect(card, config.cardRadius, config.cardRadius, paint)
        }

        val flagX = left + config.contentPadding + config.routeFlagSize / 2f
        paint.style = Paint.Style.FILL
        paint.color = config.routeWhite
        canvas.drawCircle(flagX, centerY, config.routeFlagSize / 2f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = resources.displayMetrics.density
        paint.color = config.routeCircle
        canvas.drawCircle(flagX, centerY, config.routeFlagSize / 2f, paint)
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = config.routeFlagSize * .58f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText(state.countryFlag, flagX, centerY - (paint.ascent() + paint.descent()) / 2f, paint)

        val indicatorX = right - config.contentPadding - config.routeIndicatorSize / 2f
        val textX = flagX + config.routeFlagSize / 2f + config.routeGap
        val textRight = indicatorX - config.routeIndicatorSize / 2f - config.routeGap

        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = config.routeTitleSize
        paint.color = config.routeTitle
        val title = ellipsize(state.displayTitle, textRight - textX, paint)
        canvas.drawText(title, textX, centerY - config.routeGap * .35f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = config.routeSubtitleSize
        paint.color = config.routeMuted
        val subtitleY = centerY + config.routeGap * 1.45f
        canvas.drawText("线路质量", textX, subtitleY, paint)
        val subtitleWidth = paint.measureText("线路质量")
        drawQualityBars(canvas, paint, state, textX + subtitleWidth + config.routeGap, subtitleY)

        if (state.selected) {
            paint.color = config.routeBorder
            paint.style = Paint.Style.FILL
            canvas.drawCircle(indicatorX, centerY, config.routeIndicatorSize / 2f, paint)
            paint.color = config.routeWhite
            paint.style = Paint.Style.STROKE
            paint.strokeCap = Paint.Cap.ROUND
            paint.strokeWidth = 3.1f * resources.displayMetrics.density
            val tick = config.routeIndicatorSize * .24f
            canvas.drawLine(indicatorX - tick, centerY, indicatorX - tick * .18f, centerY + tick * .74f, paint)
            canvas.drawLine(indicatorX - tick * .18f, centerY + tick * .74f, indicatorX + tick * 1.12f, centerY - tick * .74f, paint)
            paint.strokeCap = Paint.Cap.BUTT
        } else {
            paint.color = config.routeUnselectedCircle
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 1.7f * resources.displayMetrics.density
            canvas.drawCircle(indicatorX, centerY, config.routeIndicatorSize / 2f, paint)
        }
    }

    private fun drawQualityBars(canvas: Canvas, paint: Paint, state: ProxyViewState, x: Float, baseline: Float) {
        val density = resources.displayMetrics.density
        val barWidth = 13f * density
        val barHeight = 8f * density
        val gap = 4f * density
        val top = baseline - barHeight + density
        val level = state.qualityBars()
        repeat(3) { index ->
            paint.style = Paint.Style.FILL
            paint.color = if (index < level) state.config.routeBorder else state.config.routeUnselectedCircle
            canvas.drawRoundRect(
                x + index * (barWidth + gap), top,
                x + index * (barWidth + gap) + barWidth, top + barHeight,
                2.5f * density, 2.5f * density, paint
            )
        }
    }

    private fun ellipsize(value: String, width: Float, paint: Paint): String {
        if (paint.measureText(value) <= width) return value
        val dots = "…"
        val count = paint.breakText(value, true, (width - paint.measureText(dots)).coerceAtLeast(0f), null)
        return value.take(count) + dots
    }
}
