package be.huyck.mijnnutsverbruik.ui.month

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
import be.huyck.mijnnutsverbruik.adapter.DagGegevensAdapter
import be.huyck.mijnnutsverbruik.adapter.MaandGegevensAdapter
import be.huyck.mijnnutsverbruik.model.DagGegevens
import be.huyck.mijnnutsverbruik.model.MaandGegevens
import be.huyck.mijnnutsverbruik.viewmodel.VerbruiksViewModel
import kotlinx.android.synthetic.main.fragment_day.*
import kotlinx.android.synthetic.main.fragment_month.*

class MonthFragment : Fragment(){

    private lateinit var verbruiksViewModel: VerbruiksViewModel
    private lateinit var gegevensadapter: MaandGegevensAdapter

    val TAGJE = "be.huyck.mijnnutsverbruik.MonthFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return  inflater.inflate(R.layout.fragment_month, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d(TAGJE,"onViewCreated")
        verbruiksViewModel = ViewModelProvider(this).get(VerbruiksViewModel::class.java)

        gegevensadapter = MaandGegevensAdapter()
        viewpagermonth.adapter = gegevensadapter

        //viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        this.verbruiksViewModel = activity?.run {
            ViewModelProvider(this)[VerbruiksViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        // Use the ViewModel


        // Create the observer which updates the UI.
        verbruiksViewModel.getMLMaandGegevens().observe(this, Observer<List<MaandGegevens>> { geg ->
            // update UI
            //Log.d(TAGJE,"nieuwe gegevens geladen via observer")
            if (geg !=null){
                gegevensadapter.geefMaandGegevensDoor(geg)
                gegevensadapter.notifyDataSetChanged()
            }
            else {
                //Log.d(TAGJE, "geg is null")
            }

        })

    }
}