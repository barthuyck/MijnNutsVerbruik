package be.huyck.mijnnutsverbruik.ui.week

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import be.huyck.mijnnutsverbruik.R
import be.huyck.mijnnutsverbruik.adapter.WeekGegevensAdapter
import be.huyck.mijnnutsverbruik.model.WeekGegevens
import be.huyck.mijnnutsverbruik.viewmodel.VerbruiksViewModel
import kotlinx.android.synthetic.main.fragment_week.*

class WeekFragment : Fragment(){

    val TAGJE = "be.huyck.mijnnutsverbruik.WeekFragment"

    private lateinit var verbruiksViewModel: VerbruiksViewModel
    private lateinit var gegevensadapter: WeekGegevensAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_week, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d(TAGJE,"WeekFragment onViewCreated")
        verbruiksViewModel = ViewModelProvider(this).get(VerbruiksViewModel::class.java)

        //viewpagerdag.orientation = ViewPager2.ORIENTATION_VERTICAL
        gegevensadapter = WeekGegevensAdapter()
        viewpagerweek.adapter = gegevensadapter

        //viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        this.verbruiksViewModel = activity?.run {
            ViewModelProvider(this)[VerbruiksViewModel::class.java]
        } ?: throw Exception("WeekFragment: Invalid Activity")
        // Use the ViewModel

        // Create the observer which updates the UI.
        verbruiksViewModel.getMLWeekGegevens().observe(this, Observer<List<WeekGegevens>> { geg ->
            // update UI
            //Log.d(TAGJE,"WeekFragment : nieuwe gegevens geladen via observer")
            if (geg !=null){
                gegevensadapter.geefWeekGegevensDoor(geg)
                gegevensadapter.notifyDataSetChanged()
            }
            else {
                //Log.d(TAGJE, "WeekFragment: geg is null")
            }

        })

    }
}