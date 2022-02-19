package be.huyck.mijnnutsverbruik.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.databinding.FragmentDayAdapterBinding
import be.huyck.mijnnutsverbruik.model.DagGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class DagGegevensAdapter : RecyclerView.Adapter<DagGegevensViewHolder>() {
    var lijst: List<DagGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.DagGegevensAdapter"

    private lateinit var binding: FragmentDayAdapterBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DagGegevensViewHolder {
        Log.d(TAG, "Start onCreateViewHolder")
        binding = FragmentDayAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DagGegevensViewHolder(binding)
        /*return DagGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_day_adapter,
                parent,
                false
            )
        )*/
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

//class DagGegevensViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
class DagGegevensViewHolder constructor(private val binding: FragmentDayAdapterBinding) : RecyclerView.ViewHolder(binding.root) {
        val TAG = "be.huyck.mijnnutsverbruik.DagGegevensViewHolder"

    fun bind(daggegeven: DagGegevens) {

        val grafiekdatum = LocalDateTime.parse(daggegeven.datum, DateTimeFormatter.ISO_DATE_TIME)
        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val formatteruur = DateTimeFormatter.ofPattern("HH:mm")
        binding.datum.text = grafiekdatum.format(formatterdag)
        /*val tekstlwv =
            daggegeven.literwatervandaag.toString() + " l. water (geüpdatet op: " + grafiekdatum.format(
                formatteruur
            ) + ")"
        itemView.literwatervandaag.text = tekstlwv
        var kubgasvandaag = daggegeven.litergasvandaag ?: 0.0
        kubgasvandaag = kubgasvandaag/1000
        val tekstlgv =
            kubgasvandaag.toString() + " m³ gas (geüpdatet op: " + grafiekdatum.format(
                formatteruur
            ) + ")"
        itemView.litergasvandaag.text = tekstlgv */


        var kubgasvandaag = daggegeven.litergasvandaag ?: 0.0
        kubgasvandaag = kubgasvandaag/1000
        var kwPVvandaag = daggegeven.whPVvandaag ?: 0.0
        kwPVvandaag = kwPVvandaag/1000
        val tekstlwv =
            "Om " + grafiekdatum.format(formatteruur) + ": " + daggegeven.literwatervandaag.toString() + " l. water - " + kubgasvandaag.toString() + " m³ gas - "+ kwPVvandaag.toString() + " kWh zonnenergie"

        binding.datavandaag.text = tekstlwv



        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val GeefWaterWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_water",true)
        val GeefGasWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_gas",true)
        val GeefPVWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_pv",true)

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
            val dataSetGas: LineDataSet = LineDataSet(entriesgas, "Gas (l)")

            dataSetGas.setAxisDependency(YAxis.AxisDependency.RIGHT)
            dataSetGas.fillColor = Color.RED
            dataSetGas.color = Color.RED
            dataSetGas.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            dataSetGas.setDrawCircles(false)
            dataSetGas.setDrawValues(true)
            dataSetGas.setDrawFilled(true)
            dataSetsGegevens.add(dataSetGas)
        }

        if (daggegeven.whPVperkwartier != null && GeefPVWeerInGrafiek) {
            val plotdatapv = daggegeven.whPVperkwartier
            val numbersIteratorpv = plotdatapv.iterator()
            var loper = 0

            val entriespv = ArrayList<Entry>()
            entriespv.add(Entry(0.0F, 0.0F))
            while (numbersIteratorpv.hasNext()) {
                entriespv.add(Entry(loper.toFloat(), numbersIteratorpv.next().toFloat()))
                loper++
            }
            entriespv.add(Entry(0.0F, 0.0F))
            val dataSetPV: LineDataSet = LineDataSet(entriespv, "PV (Wh)")

            dataSetPV.setAxisDependency(YAxis.AxisDependency.RIGHT)
            dataSetPV.fillColor = Color.GREEN
            dataSetPV.color = Color.GREEN
            dataSetPV.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            dataSetPV.setDrawCircles(false)
            dataSetPV.setDrawValues(true)
            dataSetPV.setDrawFilled(true)
            dataSetsGegevens.add(dataSetPV)
        }



        val xAxis = binding.chart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        //xAxis.setTextColor(Color.RED)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.setLabelCount(8)
        xAxis.setGranularity(4f) // minimum axis-step (interval) is 1
        xAxis.valueFormatter = MyValueFormatter()


        val yAxis = binding.chart.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = binding.chart.axisRight
        yAxisr.axisMinimum = 0.0F
        yAxisr.setDrawGridLines(false)
        //.isEnabled = false

        val lineData: LineData = LineData(dataSetsGegevens)
        //val formatterstr = DateTimeFormatter.ofPattern("d/M/Y H:mm")

        //itemView.chart.setTitle(getString(R.string.grafiek_titel))
        if(daggegeven.mogelijksdataverlies == true){
            //itemView.litergasvandaag.setBackgroundColor(Color.rgb(255,200,200))
            binding.datavandaag.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_notifications_black_24dp,0)
            binding.datavandaag.setBackgroundColor(Color.rgb(255,140,0))
            //itemView.literwatervandaag.setBackgroundColor(Color.rgb(255,200,200))
            //itemView.literwatervandaag.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.ic_notifications_black_24dp,0)
        }
        //Log.d(TAG,"mogelijksdataverlies: $daggegeven.meetmogelijksdataverlies")
        //itemView.setBackgroundColor(Color.MAGENTA);
        binding.chart.setData(lineData)
        binding.chart.getDescription().setEnabled(false)
        binding.chart.fitScreen()
        binding.chart.setMaxVisibleValueCount(30)
        binding.chart.invalidate() // refresh

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

