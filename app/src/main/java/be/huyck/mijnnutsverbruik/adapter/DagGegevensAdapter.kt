package be.huyck.mijnnutsverbruik.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.model.DagGegevens
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
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
            daggegeven.literwatervandaag.toString() + " l. (laatste update op: " + grafiekdatum.format(
                formatteruur
            ) + ")"
        itemView.literwatervandaag.text = tekstlwv


        val plotdata = daggegeven.literwaterperkwartier
        val numbersIterator = plotdata!!.iterator()
        var loper = 0

        val entries = ArrayList<Entry>()
        entries.add(Entry(0.0F, 0.0F))
        while (numbersIterator.hasNext()) {
            entries.add(Entry(loper.toFloat(), numbersIterator.next().toFloat()))
            loper++
        }
        entries.add(Entry(0.0F, 0.0F))

        val dataSet: LineDataSet = LineDataSet(entries, "Water (l)"); // add entries to dataset

        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(true)
        dataSet.setDrawFilled(true);
        //dataSet.fillColor = Color.BLUE

        val xAxis = itemView.chart.getXAxis()
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM)
        xAxis.setTextSize(10f)
        //xAxis.setTextColor(Color.RED)
        xAxis.setDrawAxisLine(true)
        xAxis.setDrawGridLines(true)
        xAxis.setLabelCount(8)
        xAxis.setGranularity(4f); // minimum axis-step (interval) is 1
        xAxis.valueFormatter = MyValueFormatter()


        val yAxis = itemView.chart.axisLeft
        yAxis.axisMinimum = 0.0F
        val yAxisr = itemView.chart.axisRight
        yAxisr.isEnabled = false

        val lineData: LineData = LineData(dataSet);
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
            return tijd.format(DateTimeFormatter.ofPattern("H:mm"));
            // return //format.format(value)
        }
        // ... override other methods for the other chart types
    }
}

