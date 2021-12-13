package no.nav.familie.ef.infotrygd.rest.api

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.StønadType
import java.time.LocalDate

data class PeriodeRequest(val personIdenter: Set<FoedselsNr>,
                          val stønadstyper: Set<StønadType>)

data class PeriodeArenaRequest(val personIdenter: Set<FoedselsNr>,
                               val fomDato: LocalDate?,
                               val tomDato: LocalDate?)

data class PeriodeArenaResponse(val perioder: List<ArenaPeriode>)

data class ArenaPeriode(val personIdent: FoedselsNr,
                        val fomDato: LocalDate,
                        val tomDato: LocalDate,
                        val opphørsdato: LocalDate?,
                        val beløp: Float)

data class PeriodeResponse(val overgangsstønad: List<Periode>,
                           val barnetilsyn: List<Periode>,
                           val skolepenger: List<Periode>)

data class Periode(val personIdent: String,
                   val kode: InfotrygdEndringKode,
                   val brukerId: String,
                   val stønadId: Long,
                   val vedtakId: Long,
                   val stønadBeløp: Int,
                   val inntektsreduksjon: Int,
                   val samordningsfradrag: Int,
                   val beløp: Int,
                   val startDato: LocalDate,
                   val stønadFom: LocalDate,
                   val stønadTom: LocalDate,
                   val opphørsdato: LocalDate?)

/**
 * AN      	Annullert
 * Disse finnes tvers de ulike søknadene i Infotrygd, men det er kun noen få som finnes blant våre stønader
 * AS      	Avgang småbarnstillegg
 * B       	Barnetillegg
 * BB      	Barn 18 år
 * E       	Endring i beregningsgrunnlag
 * F       	Førstegangsvedtak
 * G       	G-regulering
 * H       	Barn etterlatte.3 måneder
 * I       	Institusjonsopphold
 * IN      	Ny inntektsgr.
 * KB      	Barn 3 år
 * NB      	Nytt barn
 * NY      	Ny
 * O       	Opphørt
 * S       	Satsendring
 * TS      	Tilgang småbarnstillegg
 * U       	Uføregrad
 * UA      	Uaktuell
 * P       	Passivt
 * AV      	Avslag
 * OO      	Overf ny løsning
 */
enum class InfotrygdEndringKode(val kode: String, val beskrivelse: String) {

    ANNULERT("AN", "Annullert"),
    ENDRING_BEREGNINGSGRUNNLAG("E", "Endring i beregningsgrunnlag"),
    FØRSTEGANGSVEDTAK("F", "Førstegangsvedtak"),
    G_REGULERING("G", "G-regulering"),
    NY("NY", "Ny"),
    OPPHØRT("O", "Opphørt"),
    SATSENDRING("S", "Satsendring"),
    UAKTUELL("UA", "Uaktuell"),
    OVERTFØRT_NY_LØSNING("OO", "Overf ny løsning");

    companion object {

        private val kodeMap = values().associateBy(InfotrygdEndringKode::kode)
        fun mapKodeTilEndringKode(kode: String): InfotrygdEndringKode {
            return kodeMap[kode] ?: throw error("Fant ikke endringKode for $kode")
        }
    }
}