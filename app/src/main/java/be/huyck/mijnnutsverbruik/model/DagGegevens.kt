package be.huyck.mijnnutsverbruik.model

import java.time.LocalDateTime

data class DagGegevens(
    var datum: String? = null,
    var literwatervandaag : Double? = null,
    val literwaterperkwartier: List<Double>? = null
)

data class DagDatumGegevens(
    var datum: LocalDateTime = LocalDateTime.now(),
    var literwatervandaag : Double = 0.0
)

data class WeekGegevens(
    var datadagen : List<DagDatumGegevens>? = null,
    /*var maandag: DagDatumGegevens? = null,
    var dinsdag: DagDatumGegevens? = null,
    var woensdag: DagDatumGegevens? = null,
    var donderdag: DagDatumGegevens? = null,
    var vrijdag: DagDatumGegevens? = null,
    var zaterdag: DagDatumGegevens? = null,
    var zondag: DagDatumGegevens? = null,*/
    var weekdata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

data class MaandGegevens(
    var datadagen : List<DagDatumGegevens>? = null,
    var maanddata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

data class JaarGegevens(
    var eersteDagJaar : DagDatumGegevens? = null,
    var laatsteDagJaar : DagDatumGegevens? = null,
    var jaardata : List<Double> = listOf(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
)

