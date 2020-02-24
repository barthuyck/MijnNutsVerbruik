package be.huyck.mijnnutsverbruik.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.JaarGegevens
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.android.synthetic.main.fragment_year_adapter.view.*
import java.time.format.DateTimeFormatter

class JaarGegevensAdapter : RecyclerView.Adapter<JaarGegevensViewHolder>() {
    var lijst: List<JaarGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.JaarGegevensAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JaarGegevensViewHolder {
        Log.d(TAG, "Start onCreateViewHolder")
        return JaarGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_year_adapter,
                parent,
                false
            )
        )
    }

    fun geefJaarGegevensDoor(glijst: List<JaarGegevens>) {
        lijst = glijst
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lijst.size
    }

    override fun onBindViewHolder(holder: JaarGegevensViewHolder, position: Int) {
        holder.bind(lijst.get(position))
        //Log.d(TAG,"Position $position")
        //Log.d(TAG, "$lijst")
    }
}

class JaarGegevensViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(jaargegeven: JaarGegevens) {

        //itemView.barChartWeekDatum.text = " hh "
        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val grafiekdatumeerst = jaargegeven.eersteDagJaar?.datum?.format(formatterdag)
        val grafiekdatumlaatst = jaargegeven.laatsteDagJaar?.datum?.format(formatterdag)
        val grafiekdatum = grafiekdatumeerst + " - " + grafiekdatumlaatst
        itemView.barChartYearDatum.text = grafiekdatum
        val verbruikinkub =jaargegeven.jaardata.sum()/1000
        itemView.barChartYearTotaal.text = "Verbruik dit jaar: " + verbruikinkub.toString() + " m³."

        //val jaar = maandgegeven.datadagen?.first()?.datum?.year


        val plotdata = jaargegeven.jaardata
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

        itemView.barChartYear.setData(data)
        //itemView.barChartWeek.setFitBars(true) // make the x-axis fit exactly all bars

        val xAxis = itemView.barChartYear.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        //xAxis.valueFormatter = MyValueFormatterWeek()

        val yAxis = itemView.barChartYear.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = itemView.barChartYear.axisRight
        yAxisr.isEnabled = false

        itemView.barChartYear.getDescription().setEnabled(false)
        itemView.barChartYear.invalidate() // refresh

    }

    // the labels that should be drawn on the XAxis

    /*class MyValueFormatterWeek : ValueFormatter() {
        val dagen = arrayOf("ma", "di", "woe", "do", "vr", "za", "zo")

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return dagen[value.toInt()]
        }
    }*/

}