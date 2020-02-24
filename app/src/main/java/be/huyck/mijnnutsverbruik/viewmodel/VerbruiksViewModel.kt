package be.huyck.mijnnutsverbruik.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import be.huyck.mijnnutsverbruik.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.lang.Double.sum
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class VerbruiksViewModel : ViewModel() {
    val TAG = "be.huyck.mijnnutsverbruik.viewmodel"


    private val _text = MutableLiveData<String>().apply {
        value = "This is test Fragment"
    }
    val text: LiveData<String> = _text

    private var auth: FirebaseAuth


    init {
        Log.d(TAG, "init gestart")
        auth = FirebaseAuth.getInstance()
        //Log.d(TAG,"Grootte: ${lijstDG.size.toString()}")
    }

    // daggevens
    private var lijstDG = mutableListOf<DagGegevens>()

    val MLDagGegevens: MutableLiveData<List<DagGegevens>> by lazy {
        MutableLiveData<List<DagGegevens>>()
    }

    fun getMLDagGegevens(): LiveData<List<DagGegevens>> {
        Log.d(TAG, "getlijstDagGegevens")
        return MLDagGegevens
    }

    // weekgegevens
    private var lijstWG = mutableListOf<WeekGegevens>()

    val MLWeekGegevens: MutableLiveData<List<WeekGegevens>> by lazy {
        MutableLiveData<List<WeekGegevens>>()
    }

    fun getMLWeekGegevens(): LiveData<List<WeekGegevens>> {
        Log.d(TAG, "getlijstWeekGegevens")
        return MLWeekGegevens
    }

    // maandgegevens
    private var lijstMG = mutableListOf<MaandGegevens>()

    val MLMaandGegevens: MutableLiveData<List<MaandGegevens>> by lazy {
        MutableLiveData<List<MaandGegevens>>()
    }

    fun getMLMaandGegevens(): LiveData<List<MaandGegevens>> {
        Log.d(TAG, "getlijstMaandGegevens")
        return MLMaandGegevens
    }

    // jaargegevens
    private var lijstJG = mutableListOf<JaarGegevens>()

    val MLJaarGegevens: MutableLiveData<List<JaarGegevens>> by lazy {
        MutableLiveData<List<JaarGegevens>>()
    }

    fun getMLJaarGegevens(): LiveData<List<JaarGegevens>> {
        Log.d(TAG, "getlijstJaarGegevens")
        return MLJaarGegevens
    }

    // functies

    fun cleardata(){
        val nu = LocalDateTime.now()
        lijstDG.clear()
        lijstDG.add(DagGegevens(nu.format(DateTimeFormatter.ISO_DATE_TIME),0.0, listOf(1.0,2.0,1.0)))
        MLDagGegevens.postValue(lijstDG)
        lijstWG.clear()
        MLWeekGegevens.postValue(lijstWG)
        lijstMG.clear()
        MLMaandGegevens.postValue(lijstMG)
        lijstJG.clear()
        MLJaarGegevens.postValue(lijstJG)
        calcWeek()
        calcMonth()
    }


    fun loadAllData() {
        val user = auth.currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            val db_useruid = "hg9ndEsTcuf0S0i7lPahRjUzCH83" //user.uid.toString()
            //Log.d(TAG, "Database user ID ${db_useruid}")

            val docRef = db.collection("users").document(db_useruid).collection("meetgegevens")
            docRef.orderBy("datum", Query.Direction.DESCENDING).get() //
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val docData = document.toObject(DagGegevens::class.java)
                        //Log.d(TAG, "docdata: ${docData.literwatervandaag}")
                        lijstDG.add(docData)
                    }
                    Log.d(TAG,"data succesfull recieved")
                    MLDagGegevens.postValue(lijstDG)
                    Log.d(TAG,"data succesfully posted")
                    calcWeek()
                    calcMonth()
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        }
    }

    fun calcWeek(){
        var week = WeekGegevens()
        var dagenmetdata = mutableListOf<DagDatumGegevens>()
        var dagindeweek = LocalDateTime.now().dayOfWeek
        var weekdata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)

        for (dag in lijstDG) {
            val tmp = dag
            val vandaag = LocalDateTime.parse(tmp.datum, DateTimeFormatter.ISO_DATE_TIME)
            val verbruiktwater = dag.literwatervandaag ?: 0.0
            //https://kotlinlang.org/docs/reference/null-safety.html#nullable-types-and-non-null-types

            dagindeweek = vandaag.dayOfWeek
            when(dagindeweek){
                DayOfWeek.MONDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[0] = verbruiktwater
                    week.weekdata = weekdata
                    week.datadagen = dagenmetdata
                    lijstWG.add(week)
                    //Log.d(TAG,"Week: $week")
                    week = WeekGegevens()
                    weekdata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
                    dagenmetdata = mutableListOf<DagDatumGegevens>()

                }
                DayOfWeek.TUESDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[1] = verbruiktwater
                }
                DayOfWeek.WEDNESDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[2] = verbruiktwater
                }
                DayOfWeek.THURSDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[3] = verbruiktwater
                }
                DayOfWeek.FRIDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    //week.vrijdag =  DagDatumGegevens(vandaag,verbruiktwater)
                    weekdata[4] = verbruiktwater
                }
                DayOfWeek.SATURDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[5] = verbruiktwater
                }
                DayOfWeek.SUNDAY -> {
                    dagenmetdata.add(DagDatumGegevens(vandaag,verbruiktwater))
                    weekdata[6] = verbruiktwater
                }
                null -> {}
            }
        }
        if(dagindeweek != DayOfWeek.MONDAY){
            //Log.d(TAG,"Week: $week")
            week.datadagen = dagenmetdata
            week.weekdata = weekdata
            lijstWG.add(week)
        }
        //Log.d(TAG,"$lijstWG")
        MLWeekGegevens.postValue(lijstWG)
    }

    fun calcMonth(){
        var maand = MaandGegevens()
        var dagenmetdata = mutableListOf<DagDatumGegevens>()
        var tellermaandinjaar = LocalDateTime.now().monthValue
        var maanddata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)

        var jaar = JaarGegevens()
        var jaardata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)

        for (dag in lijstDG) {
            val vandaag = LocalDateTime.parse(dag.datum, DateTimeFormatter.ISO_DATE_TIME)
            val verbruiktwater = dag.literwatervandaag ?: 0.0
            //https://kotlinlang.org/docs/reference/null-safety.html#nullable-types-and-non-null-types

            if (tellermaandinjaar == vandaag.monthValue) {
                dagenmetdata.add(DagDatumGegevens(vandaag, verbruiktwater))
                //Log.d(TAG,"vandaag.dayOfMonth: ${vandaag.dayOfMonth.toString()}")
                maanddata[vandaag.dayOfMonth-1] = verbruiktwater
                tellermaandinjaar = vandaag.monthValue
            } else {

                maand.maanddata = maanddata
                maand.datadagen = dagenmetdata
                lijstMG.add(maand)
                //Log.d(TAG,"Nieuwe maand toegevoegd")
                //Log.d(TAG,"Maand: $lijstMG")
                jaar.eersteDagJaar = dagenmetdata.last()
                if (jaar.laatsteDagJaar == null){
                    jaar.laatsteDagJaar = dagenmetdata.first()
                }
                jaardata[tellermaandinjaar-1] = maanddata.sum()
                jaar.jaardata = jaardata
                if (vandaag.monthValue == 12){
                    lijstJG.add(jaar)
                    jaar = JaarGegevens()
                    jaardata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
                }

                maanddata = mutableListOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
                maanddata[vandaag.dayOfMonth-1] = verbruiktwater
                dagenmetdata = mutableListOf<DagDatumGegevens>()
                dagenmetdata.add(DagDatumGegevens(vandaag, verbruiktwater))
                //Log.d(TAG,"vandaag.dayOfMonth: ${vandaag.dayOfMonth.toString()}")
                maand = MaandGegevens()

                tellermaandinjaar = vandaag.monthValue
            }
        }
        // maand toevoegen die niet afgewerkt is
        maand.maanddata = maanddata
        maand.datadagen = dagenmetdata
        lijstMG.add(maand)
        // jaar toevoegen dat nog niet afgewerkt is?
        jaar.eersteDagJaar = dagenmetdata.last()
        if (jaar.laatsteDagJaar == null){
            jaar.laatsteDagJaar = dagenmetdata.first()
        }
        jaardata[tellermaandinjaar-1] = maanddata.sum()
        jaar.jaardata = jaardata
        lijstJG.add(jaar)
        //Log.d(TAG,"Maand: $lijstJG")
        //Log.d(TAG,"Nieuwe maand toegevoegd")
        //Log.d(TAG,"lijstMG: ${lijstMG.size}")
        MLMaandGegevens.postValue(lijstMG)
        MLJaarGegevens.postValue(lijstJG)
    }


}