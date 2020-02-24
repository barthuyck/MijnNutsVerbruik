package be.huyck.mijnnutsverbruik.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.MaandGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_month_adapter.view.*
import kotlinx.android.synthetic.main.fragment_week_adapter.view.*
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class MaandGegevensAdapter : RecyclerView.Adapter<MaandGegevensViewHolder>() {
    var lijst: List<MaandGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.MaandGegevensAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaandGegevensViewHolder {
        Log.d(TAG, "Start onCreateViewHolder")
        return MaandGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_month_adapter,
                parent,
                false
            )
        )
    }

    fun geefMaandGegevensDoor(glijst: List<MaandGegevens>) {
        lijst = glijst
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lijst.size
    }

    override fun onBindViewHolder(holder: MaandGegevensViewHolder, position: Int) {
        holder.bind(lijst.get(position))
        //Log.d(TAG,"Position $position")
        //Log.d(TAG, "$lijst")
    }
}

class MaandGegevensViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(maandgegeven: MaandGegevens) {

        //itemView.barChartWeekDatum.text = " hh "
        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val grafiekdatumeerst = maandgegeven.datadagen?.last()?.datum?.format(formatterdag)
        val grafiekdatumlaatst = maandgegeven.datadagen?.first()?.datum?.format(formatterdag)
        val grafiekdatum = grafiekdatumeerst + " - " + grafiekdatumlaatst
        itemView.barChartMonthDatum.text = grafiekdatum
        val verbruikinkub =maandgegeven.maanddata.sum()/1000
        itemView.barChartMonthTotaal.text = "Verbruik deze maand: " + verbruikinkub.toString() + " m³."

        //val jaar = maandgegeven.datadagen?.first()?.datum?.year

        val jaar :Int = maandgegeven.datadagen?.first()?.datum?.year ?: 2020
        val maand : Int = maandgegeven.datadagen?.first()?.datum?.monthValue ?: 2


        val yearMonthObject: YearMonth = YearMonth.of(
            jaar,
            maand
        )
        val daysInMonth: Int = yearMonthObject.lengthOfMonth() //28


        val plotdata = maandgegeven.maanddata.subList(0,daysInMonth)
        val numbersIterator = plotdata.iterator()
        var loper = 1
        val entries = ArrayList<BarEntry>()
        while (numbersIterator.hasNext()) {
            entries.add(BarEntry(loper.toFloat(), numbersIterator.next().toFloat()))
            loper++
        }

        val set = BarDataSet(entries, "Water (l)")

        val data = BarData(set)
        data.barWidth = 0.7f // set custom bar width

        itemView.barChartMonth.setData(data)
        //itemView.barChartWeek.setFitBars(true) // make the x-axis fit exactly all bars

        val xAxis = itemView.barChartMonth.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        //xAxis.valueFormatter = MyValueFormatterWeek()

        val yAxis = itemView.barChartMonth.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = itemView.barChartMonth.axisRight
        yAxisr.isEnabled = false

        itemView.barChartMonth.getDescription().setEnabled(false)
        itemView.barChartMonth.invalidate() // refresh

    }

    // the labels that should be drawn on the XAxis

    class MyValueFormatterWeek : ValueFormatter() {
        val dagen = arrayOf("ma", "di", "woe", "do", "vr", "za", "zo")

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return dagen[value.toInt()]
        }
    }

}