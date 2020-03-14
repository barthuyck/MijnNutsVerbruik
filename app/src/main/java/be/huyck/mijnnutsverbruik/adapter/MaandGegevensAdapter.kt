package be.huyck.mijnnutsverbruik.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.MaandGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
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
        val verbruikwaterinkub = maandgegeven.maanddata.sum()/1000
        itemView.barChartMonthWater.text = "Maandverbruik water: " + verbruikwaterinkub.toString() + " m³."
        val verbruikgasinkub = maandgegeven.maanddatagas.sum()
        itemView.barChartMonthGas.text = "Maandverbruik gas: " + String.format("%.3f", verbruikgasinkub) + " m³." // verbruikgasinkub.toString()

        val jaar :Int = maandgegeven.datadagen?.first()?.datum?.year ?: 2020
        val maand : Int = maandgegeven.datadagen?.first()?.datum?.monthValue ?: 2


        val yearMonthObject: YearMonth = YearMonth.of(
            jaar,
            maand
        )
        val daysInMonth: Int = yearMonthObject.lengthOfMonth() //28

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val GeefWaterWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_water", true)
        val GeefGasWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_gas", true)


        val plotdatawater = maandgegeven.maanddata.subList(0,daysInMonth)
        val numbersIteratorwater = plotdatawater.iterator()
        var loper = 1
        val entrieswater = ArrayList<BarEntry>()
        if (GeefWaterWeerInGrafiek) {
            while (numbersIteratorwater.hasNext()) {
                entrieswater.add(BarEntry(loper.toFloat(), numbersIteratorwater.next().toFloat()))
                loper++
            }
        } else {
            entrieswater.add(BarEntry(0.0F, 0.0F))
        }

        val setwater = BarDataSet(entrieswater, "Water (l)")

        val plotdataGas = maandgegeven.maanddatagas.subList(0, daysInMonth)
        val numbersIteratorGas = plotdataGas.iterator()
        loper = 1
        val entriesGas = ArrayList<BarEntry>()
        if (GeefGasWeerInGrafiek) {
            while (numbersIteratorGas.hasNext()) {
                entriesGas.add(BarEntry(loper.toFloat(), numbersIteratorGas.next().toFloat()))
                loper++
            }
        } else {
            entriesGas.add(BarEntry(0.0F, 0.0F))
        }

        val setGas = BarDataSet(entriesGas, "Gas (m³)")
        setGas.setAxisDependency(YAxis.AxisDependency.RIGHT)
        setGas.color = Color.RED

        val data = BarData(setwater,setGas)

        val groupSpace = 0.1f
        val barSpace = 0.05f // x2 dataset
        val barWidth = 0.4f // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval

        data.barWidth = barWidth // set custom bar width

        itemView.barChartMonth.setData(data)
        itemView.barChartMonth.groupBars(0.5f, groupSpace, barSpace) // perform the "explicit" grouping
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
        yAxisr.axisMinimum = 0.0F
        yAxisr.setDrawGridLines(false)

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