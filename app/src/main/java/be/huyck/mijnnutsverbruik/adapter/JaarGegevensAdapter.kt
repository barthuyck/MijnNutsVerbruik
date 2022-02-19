package be.huyck.mijnnutsverbruik.adapter

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.databinding.FragmentYearAdapterBinding
import be.huyck.mijnnutsverbruik.model.JaarGegevens
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import java.time.format.DateTimeFormatter


class JaarGegevensAdapter : RecyclerView.Adapter<JaarGegevensViewHolder>() {
    var lijst: List<JaarGegevens> = ArrayList()
    val TAG = "be.huyck.mijnnutsverbruik.JaarGegevensAdapter"

    private lateinit var binding: FragmentYearAdapterBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JaarGegevensViewHolder {
        Log.d(TAG, "Start onCreateViewHolder")
        binding = FragmentYearAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JaarGegevensViewHolder(binding)
        /*return JaarGegevensViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.fragment_year_adapter,
                parent,
                false
            )
        )*/
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
    }
}

class JaarGegevensViewHolder constructor(private val binding:FragmentYearAdapterBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(jaargegeven: JaarGegevens) {

        val formatterdag = DateTimeFormatter.ofPattern("E dd/MM/yyyy")
        val grafiekdatumeerst = jaargegeven.eersteDagJaar?.datum?.format(formatterdag)
        val grafiekdatumlaatst = jaargegeven.laatsteDagJaar?.datum?.format(formatterdag)
        val grafiekdatum = grafiekdatumeerst + " - " + grafiekdatumlaatst
        binding.barChartYearDatum.text = grafiekdatum
        val verbruikwaterinkub =jaargegeven.jaardata.sum()/1000
        binding.barChartYearWater.text = "Verbruik dit jaar: " + verbruikwaterinkub.toString() + " m³."
        val verbruikgasinkub =jaargegeven.jaardatagas.sum()
        binding.barChartYearGas.text = "Verbruik dit jaar: " + String.format("%.3f", verbruikgasinkub) + " m³."


        //val eulaKey = "mykey"
        //val mContext: Context = getApplicationContext()
        //mPrefs = mContext.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
        //val editor: SharedPreferences.Editor = mPrefs.edit()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.context)
        val GeefWaterWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_water", true)
        val GeefGasWeerInGrafiek = sharedPreferences.getBoolean("switch_preference_gas", true)


        val plotdatawater = jaargegeven.jaardata
        val numbersIteratorwater = plotdatawater.iterator()
        var loper = 1
        val entrieswater = ArrayList<BarEntry>()
        if(GeefWaterWeerInGrafiek) {
            while (numbersIteratorwater.hasNext()) {
                entrieswater.add(BarEntry(loper.toFloat(), numbersIteratorwater.next().toFloat()))
                loper++
            }
        }
        else{
            entrieswater.add(BarEntry(0.0F, 0.0F))
        }

        val setwater = BarDataSet(entrieswater, "Water (m³)")

        val plotdatagas = jaargegeven.jaardatagas
        val numbersIteratorgas = plotdatagas.iterator()
        loper = 1
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

        val data = BarData(setwater,setgas)

        val groupSpace = 0.1f
        val barSpace = 0.05f // x2 dataset
        val barWidth = 0.4f // x2 dataset

        data.barWidth = barWidth // set custom bar width

        binding.barChartYear.setData(data)
        binding.barChartYear.groupBars(0.5f, groupSpace, barSpace) // perform the "explicit" grouping

        //binding.barChartWeek.setFitBars(true) // make the x-axis fit exactly all bars

        val xAxis = binding.barChartYear.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        //xAxis.valueFormatter = MyValueFormatterWeek()

        val yAxis = binding.barChartYear.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = binding.barChartYear.axisRight
        yAxisr.axisMinimum = 0.0F
        yAxisr.setDrawGridLines(false)

        binding.barChartYear.getDescription().setEnabled(false)
        binding.barChartYear.invalidate() // refresh

    }

    // the labels that should be drawn on the XAxis

    /*class MyValueFormatterWeek : ValueFormatter() {
        val dagen = arrayOf("ma", "di", "woe", "do", "vr", "za", "zo")

        override fun getAxisLabel(value: Float, axis: AxisBase): String {
            return dagen[value.toInt()]
        }
    }*/

}