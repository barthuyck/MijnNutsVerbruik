package be.huyck.mijnnutsverbruik.ui.year

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
import be.huyck.mijnnutsverbruik.adapter.JaarGegevensAdapter
import be.huyck.mijnnutsverbruik.model.JaarGegevens
import be.huyck.mijnnutsverbruik.viewmodel.VerbruiksViewModel
import kotlinx.android.synthetic.main.fragment_year.*

class YearFragment : Fragment(){

    private lateinit var verbruiksViewModel: VerbruiksViewModel

    val TAGJE = "be.huyck.mijnnutsverbruik.YearFragment"

    private lateinit var gegevensadapter: JaarGegevensAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_year, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d(TAGJE,"onViewCreated")
        verbruiksViewModel = ViewModelProvider(this).get(VerbruiksViewModel::class.java)

        gegevensadapter = JaarGegevensAdapter()
        viewpageryear.adapter = gegevensadapter

        //viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        this.verbruiksViewModel = activity?.run {
            ViewModelProvider(this)[VerbruiksViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        // Use the ViewModel


        // Create the observer which updates the UI.
        verbruiksViewModel.getMLJaarGegevens().observe(this, Observer<List<JaarGegevens>> { geg ->
            // update UI
            //Log.d(TAGJE,"YearFragment: nieuwe gegevens geladen via observer")
            if (geg !=null){
                gegevensadapter.geefJaarGegevensDoor(geg)
                gegevensadapter.notifyDataSetChanged()
            }
            else {
              //  Log.d(TAGJE, "geg is null")
            }

        })

    }
}