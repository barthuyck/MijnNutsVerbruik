package be.huyck.mijnnutsverbruik.model

import java.time.LocalDateTime

data class DagGegevens(
    var datum: String? = null,
    var literwatervandaag : Double? = null,
    val literwaterperkwartier: List<Double>? = null,
    var litergasvandaag : Double? = null,
    val litergasperkwartier: List<Double>? = null,
    val mogelijksdataverlies: Boolean? = null,
    val whPVperkwartier : List<Double>? = null,
    var whPVvandaag : Double? = null
)

data class DagDatumGegevens(
    var datum: LocalDateTime = LocalDateTime.now(),
    var literwatervandaag : Double = 0.0,
    var litergasvandaag : Double = 0.0,
    var lagekwaliteitdata : Boolean = false
)

data class WeekGegevens(
    var datadagen : List<DagDatumGegevens>? = null,
    var weekdata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var weekdatagas : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var weekdatapv : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

data class MaandGegevens(
    var datadagen : List<DagDatumGegevens>? = null,
    var maanddata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var maanddatagas : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var maanddatapv : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

data class JaarGegevens(
    var eersteDagJaar : DagDatumGegevens? = null,
    var laatsteDagJaar : DagDatumGegevens? = null,
    var jaardata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var jaardatagas : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0),
    var jaardatapv : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

