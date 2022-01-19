package no.nav.familie.ef.infotrygd.rest.api

import java.time.LocalDate

data class InfotrygdSakResponse(
        val saker: List<InfotrygdSak>
)

data class InfotrygdSak(
        val id: Long? = null,
        val saksnr: String? = null,
        val saksblokk: String? = null,
        val regDato: LocalDate? = null,
        val mottattdato: LocalDate? = null,
        val kapittelnr: String? = null,
        val valg: String? = null, // TODO Det virker som att denne inneholder fler verdier enn den burde?
        val undervalg: String? = null,
        val type: InfotrygdSakType? = null,
        val nivå: String? = null,
        val resultat: InfotrygdSakResultat,
        val vedtaksdato: LocalDate? = null,
        val iverksattdato: LocalDate? = null,
        val årsakskode: String? = null,
        val behenEnhet: String? = null,
        val regAvEnhet: String? = null,
        val tkNr: String? = null,
        val region: String? = null,
)

enum class InfotrygdSakType(val infotrygdKode: String, val beskrivelse: String) {

    ANKE("A", "Anke"),// 	antall=1954
    DISPENSASJON_FORELDELSE("DF", "Dispensasjon foreldelse"), // antall=0
    DOKUMENTINNSYN("DI", "Dokumentinnsyn"),// antall=77
    ETTERLYSE_GIROKORT("EG", "Etterlyse girokort"), // antall=0
    FORESPØRSEL("FS", "Forespørsel"),// antall=1209
    GRUNNBELØP_OMREGNING_OVERGANGSSTØNAD("GO", "Grunnbeløp omregning (manuell G-omregning) Overgangsstønad"),// antall=1715
    INFORMASJONSSAK("I", "Informasjonssak"),// 	antall=62196
    JOURNALSAK("J", "Journalsak"),// 	antall=898
    JOURNALSAK_PRIVATPERSON("JP", "Journalsak fra privatperson"),// antall=1
    JOURNALSAK_TRYGDEKONTOR("JT", "Journalsak fra trygdekontor"),// antall=1
    JOURNALSAK_UTENL("JU", "Journalsak fra utenl trm"), // 0
    KLAGE("K", "Klage"),// 	antall=24618
    KLAGE_AVREGNING("KA", "Klage avregning"), // antall=0
    KLAGE_ETTERGIVELSE("KE", "Klage ettergivelse"),// antall=22
    KONVERTERING("KO", "Konvertering, de som ble lagt inn da rutinen ble lagt over til DB2"),// antall=42210
    KONTROLLSAK("KS", "Kontrollsak"),// antall=402
    KLAGE_TILBAKEBETALING("KT", "Klage tilbakebetaling"),// antall=4947
    MASKINELL_G_OMREGNING("MG", "Maskinell G-omregning (Årlig G-reg. overgangsstønad)"),// antall=256828
    MASKINELL_SATSOMREGNING("MS", "Maskinell Satsomregning (Årlig satsendring Barnetilsyn)"),// antall=1315
    REVURDERING("R", "Revurdering"),// 	antall=556924
    SØKNAD("S", "Søknad"),// 	antall=589777
    SØKNAD_ETTERGIVELSE("SE", "Søknad om ettergivelse"),// antall=127
    STRAFFERETTSLIG_VURDERING("SV", "Strafferettslig vurdering"),// antall=581
    SØKNAD_ØKNING_ENDRING("SØ", "Søknad om økning/endring"),// antall=4384
    TILBAKEBETALINGSSAK("T", "Tilbakebetalingssak"),// 	antall=60010
    TA("TA", ""),// antall=202  TODO
    TILBAKEBETALING_ENDRING("TE", "Tilbakebetaling endring"),// antall=2143
    TIPSUTREDNING("TU", "Tipsutredning"),// antall=50
    UA("UA", ""),// antall=1 TODO
    VURDERING_UTESTENGING("VU", "Vurdering utestenging");// antall=54

    companion object {

        private val kodeMap = values().associateBy(InfotrygdSakType::infotrygdKode)
        fun fraInfotrygdKode(kode: String): InfotrygdSakType {
            return kodeMap[kode] ?: error("Fant ikke sakType for $kode")
        }
    }
}

enum class InfotrygdSakResultat(val infotrygdKode: String, val beskrivelse: String) {
    ÅPEN_SAK("BLANK","Åpen sak"), // Denne er alltså blank, den har ikke verdiet blank
    AVSLAG("A","Avslag"),
    AVSLAG_GODKJENT("AG","Avslag godkjent"),
    AVVIST_KLAGE("AK","Avvist klage"),
    ANNULLERING("AN","Annullering"),
    ADVARSEL("AV","Advarsel"),
    DELVIS_GODKJENT("DG","Delvis godkjent"),
    DELVIS_INNVILGET("DI","Delvis innvilget"),
    DELVIS_TILBAKEBETALE("DT","Delvis tilbakebetale"),
    FERDIG_BEHANDLET("FB","Ferdig behandlet"),
    FORTSATT_INNVILGET("FI","Fortsatt innvilget"),
    GODKJENT("GK","Godkjent"),
    HENLAGT_TRUKKET_TILBAKE("H","Henlagt / trukket tilbake"),
    HENLAGT_BORTFALT("HB","Henlagt / bortfalt"),
    INNVILGET("I","Innvilget"),
    IB("IB",""), // TODO
    INNVILGET_NY_SITUASJON("IN","Innvilget ny situasjon"),
    IKKE_STRAFFBART("IS","Ikke straffbart"),
    IKKE_TILBAKEBETALE("IT","Ikke tilbakebetale"),
    IU("IU",""), // TODO
    KLAGE("K","Klage"),
    MIDLERTIDIG_OPPHØRT("MO","Midlertidig opphørt"),
    NB("NB",""), // TODO
    O("O","Opphørt	Hvis type-sak er 'R', dannes hendelse til BA-sak med 'OPPHOERT'"),
    POLITIANMELDELSE("PA","Politianmeldelse"),
    REDUSERT("R","Redusert"),
    TILBAKEBETALE("TB","Tilbakebetale"),
    TF("TF",""), // TODO
    TIPS_OPPFØLGING("TO","Tips oppfølging"),
    VU("VU",""), // TODO
    ØKNING("Ø","Økning")
    ;

    companion object {

        private val kodeMap = values().associateBy(InfotrygdSakResultat::infotrygdKode)
        fun fraInfotrygdKode(kode: String): InfotrygdSakResultat {
            if(kode.isEmpty()) return ÅPEN_SAK
            return kodeMap[kode] ?: error("Fant ikke sakResultat for $kode")
        }
    }
}