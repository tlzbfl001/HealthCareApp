package com.makebodywell.bodywell.view.report

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.makebodywell.bodywell.R

class ReportMarkerView1(context: Context?, layoutResource: Int) : MarkerView(context, layoutResource) {
    private var tvContent: TextView? = null

    init {
        tvContent = findViewById<View>(R.id.tvContentHead) as TextView
    }

    override fun refreshContent(e: Entry, highlight: Highlight?) {
        if (e is CandleEntry) {
            tvContent!!.text = "" + Utils.formatNumber(e.high.toInt().toFloat(), 0, true) + "시간"
        } else {
            tvContent!!.text = "" + Utils.formatNumber(e.y.toInt().toFloat(), 0, true) + "시간"
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }
}