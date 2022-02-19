package be.huyck.mijnnutsverbruik.adapter

import android.graphics.Color
import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView

import be.huyck.mijnnutsverbruik.databinding.FragmentWeekAdapterBinding

import be.huyck.mijnnutsverbruik.model.WeekGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
//import kotlinx.android.synthetic.main.fragment_week_adapter.view.*
import java.time.format.DateTimeFormatter


class WeekGegevensAdapter : RecyclerView.Adapter<WeekGegevensViewHolder>() {
    var lijst: List<WeekGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.WeekGegevensAdapter"

    private lateinit var binding: FragmentWeekAdapterBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekGegevensViewHolder {
        //Log.d(TAG, "Start onCreateViewHolder")
        binding = FragmentWeekAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekGegevensViewHolder(binding)
        /*return WeekGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_week_adapter,
                parent,
                false
            )
        )*/
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

class WeekGegevensViewHolder constructor(private val binding: FragmentWeekAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weekgegeven: WeekGegevens) {

        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val grafiekdatumeerst = weekgegeven.datadagen?.last()?.datum?.format(formatterdag)
        val grafiekdatumlaatst = weekgegeven.datadagen?.first()?.datum?.format(formatterdag)
        val grafiekdatum = grafiekdatumeerst + " - " + grafiekdatumlaatst
        binding.barChartWeekDatum.text = grafiekdatum
        val verbruikwaterinkub = weekgegeven.weekdata.sum() / 1000
        val verbruikgasinkub = weekgegeven.weekdatagas.sum()
        binding.barChartWeekWater.text = "Weekverbruik water: " + verbruikwaterinkub.toString() + " m³."
        binding.barChartWeekGas.text = "Weekverbruik gas: " + String.format("%.3f", verbruikgasinkub) + " m³."

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val GeefWaterWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_water", true)
        val GeefGasWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_gas", true)

        val plotdata = weekgegeven.weekdata
        val numbersIterator = plotdata.iterator()
        var loper = 0
        val entries = ArrayList<BarEntry>()
        if (GeefWaterWeerInGrafiek) {
            while (numbersIterator.hasNext()) {
                entries.add(BarEntry(loper.toFloat(), numbersIterator.next().toFloat()))
                loper++
            }
        } else {
            entries.add(BarEntry(0.0F, 0.0F))
        }

        val setwater = BarDataSet(entries, "Water (l)")
        setwater.setAxisDependency(YAxis.AxisDependency.LEFT)


        val plotdatagas = weekgegeven.weekdatagas
        val numbersIteratorgas = plotdatagas.iterator()
        loper = 0
        val entriesgas = ArrayList<BarEntry>()
        if(GeefGasWeerInGrafiek) {
            while (numbersIteratorgas.hasNext()) {
                entriesgas.add(BarEntry(loper.toFloat(), numbersIteratorgas.next().toFloat()))
                loper++
            }
        }
        else{
            entriesgas.add(BarEntry(0.0F, 0.0F))
        }
        val setgas = BarDataSet(entriesgas, "Gas (m³)")
        setgas.setAxisDependency(YAxis.AxisDependency.RIGHT)
        setgas.color = Color.RED

        var data = BarData(setwater, setgas)

        val groupSpace = 0.1f
        val barSpace = 0.05f // x2 dataset
        val barWidth = 0.4f // x2 dataset
        // (0.02 + 0.45) * 2 + 0.06 = 1.00 -> interval

        data.barWidth = barWidth // set custom bar width
        binding.barChartWeek.setData(data)
        binding.barChartWeek.groupBars(-0.5f, groupSpace, barSpace) // perform the "explicit" grouping
        //itemView.barChartWeek.setFitBars(true) // make the x-axis fit exactly all bars

        val xAxis = binding.barChartWeek.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = MyValueFormatterWeek()

        val yAxis = binding.barChartWeek.axisLeft
        yAxis.axisMinimum = 0.0F

        val yAxisr = binding.barChartWeek.axisRight
        yAxisr.axisMinimum = 0.0F
        yAxisr.setDrawGridLines(false)
        //yAxisr.isEnabled = false

        binding.barChartWeek.getDescription().setEnabled(false)
        binding.barChartWeek.invalidate() // refresh
    }

    // the labels that should be drawn on the XAxis

    class MyValueFormatterWeek : ValueFormatter() {
        val dagen = arrayOf("ma", "di", "woe", "do", "vr", "za", "zo")

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return dagen[value.toInt()]
        }
    }

}