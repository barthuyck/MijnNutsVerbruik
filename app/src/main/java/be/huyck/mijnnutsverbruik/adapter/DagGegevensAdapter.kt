package be.huyck.mijnnutsverbruik.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.DagGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import kotlinx.android.synthetic.main.fragment_day_adapter.view.*
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class DagGegevensAdapter : RecyclerView.Adapter<DagGegevensViewHolder>() {
    var lijst: List<DagGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.DagGegevensAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DagGegevensViewHolder {
        Log.d(TAG, "Start onCreateViewHolder")
        return DagGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_day_adapter,
                parent,
                false
            )
        )
    }

    fun geefGegevensDoor(dglijst: List<DagGegevens>) {
        lijst = dglijst
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return lijst.size
    }

    override fun onBindViewHolder(holder: DagGegevensViewHolder, position: Int) {
        holder.bind(lijst.get(position))
    }
}

class DagGegevensViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(daggegeven: DagGegevens) {

        val grafiekdatum = LocalDateTime.parse(daggegeven.datum, DateTimeFormatter.ISO_DATE_TIME)
        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val formatteruur = DateTimeFormatter.ofPattern("HH:mm")
        itemView.datum.text = grafiekdatum.format(formatterdag)
        val tekstlwv =
            daggegeven.literwatervandaag.toString() + " l. water (geupdatetet op: " + grafiekdatum.format(
                formatteruur
            ) + ")"
        itemView.literwatervandaag.text = tekstlwv
        var kubgasvandaag = daggegeven.litergasvandaag ?: 0.0
        kubgasvandaag = kubgasvandaag/1000
        val tekstlgv =
            kubgasvandaag.toString() + " m³ gas (geupdatetet op: " + grafiekdatum.format(
                formatteruur
            ) + ")"
        itemView.litergasvandaag.text = tekstlgv

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val GeefWaterWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_water",true)
        val GeefGasWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_gas",true)


        val dataSetsGegevens: MutableList<ILineDataSet> = ArrayList()
        if (daggegeven.literwaterperkwartier != null && GeefWaterWeerInGrafiek) {
            val plotdatawater = daggegeven.literwaterperkwartier
            val numbersIteratorwater = plotdatawater.iterator()
            var loper = 0

            val entrieswater = ArrayList<Entry>()
            entrieswater.add(Entry(0.0F, 0.0F))
            while (numbersIteratorwater.hasNext()) {
                entrieswater.add(Entry(loper.toFloat(), numbersIteratorwater.next().toFloat()))
                loper++
            }
            entrieswater.add(Entry(0.0F, 0.0F))

            val dataSetWater: LineDataSet =
                LineDataSet(entrieswater, "Water (l)") // add entries to dataset
            dataSetWater.setAxisDependency(YAxis.AxisDependency.LEFT)
            dataSetWater.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            dataSetWater.setDrawCircles(false)
            dataSetWater.setDrawValues(true)
            dataSetWater.setDrawFilled(true)
            dataSetsGegevens.add(dataSetWater)
        }

        if (daggegeven.litergasperkwartier != null && GeefGasWeerInGrafiek) {
            val plotdatagas = daggegeven.litergasperkwartier
            val numbersIteratorgas = plotdatagas.iterator()
            var loper = 0

            val entriesgas = ArrayList<Entry>()
            entriesgas.add(Entry(0.0F, 0.0F))
            while (numbersIteratorgas.hasNext()) {
                entriesgas.add(Entry(loper.toFloat(), numbersIteratorgas.next().toFloat()))
                loper++
            }
            entriesgas.add(Entry(0.0F, 0.0F))
            val dataSetGas: LineDataSet = LineDataSet(entriesgas, "Gas (m³)")

            dataSetGas.setAxisDependency(YAxis.AxisDependency.RIGHT)
            dataSetGas.fillColor = Color.RED
            dataSetGas.color = Color.RED
            dataSetGas.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            dataSetGas.setDrawCircles(false)
            dataSetGas.setDrawValues(true)
            dataSetGas.setDrawFilled(true)
            dataSetsGegevens.add(dataSetGas)
        }

        val xAxis = itemView.chart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        //xAxis.setTextColor(Color.RED)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.setLabelCount(8)
        xAxis.setGranularity(4f) // minimum axis-step (interval) is 1
        xAxis.valueFormatter = MyValueFormatter()


        val yAxis = itemView.chart.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = itemView.chart.axisRight
        yAxisr.axisMinimum = 0.0F
        yAxisr.setDrawGridLines(false)
        //.isEnabled = false




        val lineData: LineData = LineData(dataSetsGegevens)
        //val formatterstr = DateTimeFormatter.ofPattern("d/M/Y H:mm")

        //itemView.chart.setTitle(getString(R.string.grafiek_titel))


        itemView.chart.setData(lineData)
        itemView.chart.getDescription().setEnabled(false)
        itemView.chart.fitScreen()
        itemView.chart.setMaxVisibleValueCount(30)

        itemView.chart.invalidate() // refresh


    }

    class MyValueFormatter : ValueFormatter() {
        private val format = DecimalFormat("###,##0.0")

        // override this for BarChart
        /*override fun getBarLabel(barEntry: BarEntry?): String {
            return format.format(barEntry?.y)
        }*/

        // override this for custom formatting of XAxis or YAxis labels
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            var tijd = LocalTime.of(0, 0, 0)
            val loper = value.toInt()
            tijd = tijd.plusMinutes(15 * loper.toLong())
            return tijd.format(DateTimeFormatter.ofPattern("H:mm"))
            // return //format.format(value)
        }
        // ... override other methods for the other chart types
    }
}

