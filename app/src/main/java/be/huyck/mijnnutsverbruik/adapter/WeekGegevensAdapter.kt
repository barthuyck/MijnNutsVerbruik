package be.huyck.mijnnutsverbruik.adapter

import android.provider.Settings.System.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.WeekGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.android.synthetic.main.fragment_week_adapter.view.*
import java.time.format.DateTimeFormatter







class WeekGegevensAdapter : RecyclerView.Adapter<WeekGegevensViewHolder>() {
    var lijst: List<WeekGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.WeekGegevensAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekGegevensViewHolder {
        //Log.d(TAG, "Start onCreateViewHolder")
        return WeekGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_week_adapter,
                parent,
                false
            )
        )
    }

    fun geefWeekGegevensDoor(glijst: List<WeekGegevens>) {
        lijst = glijst
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lijst.size
    }

    override fun onBindViewHolder(holder: WeekGegevensViewHolder, position: Int) {
        holder.bind(lijst.get(position))
        //Log.d(TAG,"Position $position")
        //Log.d(TAG, "$lijst")
    }
}

class WeekGegevensViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(weekgegeven: WeekGegevens) {

        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val grafiekdatumeerst = weekgegeven.datadagen?.last()?.datum?.format(formatterdag)
        val grafiekdatumlaatst = weekgegeven.datadagen?.first()?.datum?.format(formatterdag)
        val grafiekdatum = grafiekdatumeerst + " - " + grafiekdatumlaatst
        itemView.barChartWeekDatum.text = grafiekdatum
        val verbruikinkub = weekgegeven.weekdata.sum()/1000
        itemView.barChartWeekTotaal.text = "Verbruik deze week: " + verbruikinkub.toString() + " m³."

        val plotdata = weekgegeven.weekdata
        val numbersIterator = plotdata.iterator()
        var loper = 0
        val entries = ArrayList<BarEntry>()
        while (numbersIterator.hasNext()) {
            entries.add(BarEntry(loper.toFloat(), numbersIterator.next().toFloat()))
            loper++
        }

        val set = BarDataSet(entries, "Water (l)")

        val data = BarData(set)
        data.barWidth = 0.7f // set custom bar width

        itemView.barChartWeek.setData(data)
        //itemView.barChartWeek.setFitBars(true) // make the x-axis fit exactly all bars

        val xAxis = itemView.barChartWeek.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)


        xAxis.valueFormatter = MyValueFormatterWeek()


        val yAxis = itemView.barChartWeek.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = itemView.barChartWeek.axisRight
        yAxisr.isEnabled = false

        itemView.barChartWeek.getDescription().setEnabled(false)
        itemView.barChartWeek.invalidate() // refresh

    }

    // the labels that should be drawn on the XAxis

    class MyValueFormatterWeek : ValueFormatter() {
        val dagen = arrayOf("ma", "di", "woe", "do", "vr", "za", "zo")

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return dagen[value.toInt()]
        }
    }

}