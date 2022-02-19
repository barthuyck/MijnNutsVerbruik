package be.huyck.mijnnutsverbruik.ui.day

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import be.huyck.mijnnutsverbruik.adapter.DagGegevensAdapter
import be.huyck.mijnnutsverbruik.databinding.FragmentDayBinding
import be.huyck.mijnnutsverbruik.model.DagGegevens
import be.huyck.mijnnutsverbruik.viewmodel.VerbruiksViewModel

class DayFragment : Fragment() {

    private lateinit var verbruiksViewModel: VerbruiksViewModel
    private lateinit var gegevensadapter: DagGegevensAdapter

    private var _binding: FragmentDayBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    val TAGJE = "be.huyck.mijnnutsverbruik.DayFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAGJE,"onCreateView")
        _binding = FragmentDayBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
        //return inflater.inflate(R.layout.fragment_day, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Log.d(TAGJE,"onViewCreated")
        verbruiksViewModel = ViewModelProvider(this).get(VerbruiksViewModel::class.java)

        //viewpagerdag.orientation = ViewPager2.ORIENTATION_VERTICAL
        gegevensadapter = DagGegevensAdapter()
        binding.viewpagerdag.adapter = gegevensadapter

        //viewModel = ViewModelProviders.of(this).get(DataViewModel::class.java)
        this.verbruiksViewModel = activity?.run {
            ViewModelProvider(this)[VerbruiksViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        // Use the ViewModel


        // Create the observer which updates the UI.
        verbruiksViewModel.getMLDagGegevens().observe(this, Observer<List<DagGegevens>> { geg ->
            // update UI
            Log.d(TAGJE,"nieuwe gegevens geladen via observer")
            if (geg !=null){
                gegevensadapter.geefGegevensDoor(geg)
                gegevensadapter.notifyDataSetChanged()
            }
            else {
                Log.d(TAGJE, "geg is null")
            }

        })

    }
}



