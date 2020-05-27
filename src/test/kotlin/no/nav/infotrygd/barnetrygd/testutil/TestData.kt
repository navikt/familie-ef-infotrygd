package no.nav.infotrygd.barnetrygd.testutil

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.commons.foedselsnummer.Kjoenn
import no.nav.commons.foedselsnummer.testutils.FoedselsnummerGenerator
import no.nav.infotrygd.barnetrygd.model.Inntekt
import no.nav.infotrygd.barnetrygd.model.Periode
import no.nav.infotrygd.barnetrygd.model.Utbetaling
import no.nav.infotrygd.barnetrygd.model.db2.*
import no.nav.infotrygd.barnetrygd.model.ip.Person
import no.nav.infotrygd.barnetrygd.model.ip.Personkort
import no.nav.infotrygd.barnetrygd.model.kodeverk.*
import no.nav.infotrygd.barnetrygd.model.sak.Sak
import no.nav.infotrygd.barnetrygd.model.sak.Status
import no.nav.infotrygd.barnetrygd.nextId
import java.time.LocalDate
import java.time.LocalDateTime

object TestData {
    fun foedselsNr(
        foedselsdato: LocalDate? = null,
        kjoenn: Kjoenn = Kjoenn.MANN): FoedselsNr {

        return fnrGenerator.foedselsnummer(
            foedselsdato = foedselsdato,
            kjoenn = kjoenn
        )
    }

    fun periode(): Periode {
        return Periode(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            fnr = foedselsNr(),
            stoenadstype = Stoenadstype.SVANGERSKAP,
            frisk = Frisk.LOPENDE,
            arbufoer = LocalDate.now(),
            stoppdato = null,
            utbetalingshistorikk = listOf(),
            inntekter = listOf(),
            utbetaltFom = null,
            utbetaltTom = null,
            foedselsdatoBarn = null,
            arbeidskategori = null,
            registrert = LocalDate.now(),
            brukerId = "br.id",
            morFnr = foedselsNr(),
            arbufoerTom = null,
            friskmeldtDato = null,
            maksdato = null
        )
    }

    fun utbetaling(): Utbetaling =
        Utbetaling(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            utbetaltTom = LocalDate.now(),
            utbetaltFom = LocalDate.now(),
            utbetalingsdato = LocalDate.now(),
            grad = null,
            type = null,
            korr = null
        )

    fun inntekt(): Inntekt =
        Inntekt(
            id = nextId(),
            personKey = 1,
            arbufoerSeq = 1,
            arbgiverNr = "12345678901",
            loenn = 1.toBigDecimal(),
            periode = Inntektsperiode.MAANEDLIG,
            refusjon = false
        )

    fun inntektStonad(): no.nav.infotrygd.barnetrygd.model.db2.Inntekt =
        no.nav.infotrygd.barnetrygd.model.db2.Inntekt(
            stonadId = -1,
            orgNr = 12345678900,
            inntekt = 100.toBigDecimal(),
            inntektFom = LocalDate.now(),
            lopeNr = 1,
            status = "L",
            periode = Inntektsperiode.MAANEDLIG,
            refusjon = false
        )

    fun sak(fnr: FoedselsNr = foedselsNr()): Sak {
        val saksblokk = "X"
        val saksnummer = "11"
        val personKey = nextId()
        return Sak(
            id = nextId(),
            fnr = fnr,
            personKey = personKey,
            saksblokk = saksblokk,
            saksnummer = saksnummer,
            kapittelNr = "BS",
            valg = SakValg.PN,
            type = SakType.A,
            resultat = SakResultat.A,
            vedtaksdato = LocalDate.now(),
            iverksattdato = LocalDate.now(),
            registrert = LocalDate.now(),
            statushistorikk = listOf(
                Status(
                    id = nextId(),
                    personKey = personKey,
                    saksblokk = saksblokk,
                    saksnummer = saksnummer,
                    lopeNr = nextId() % 99,
                    status = SakStatus.IKKE_BEHANDLET
                )
            )
        )
    }

    fun personkort(
        fnr: FoedselsNr = foedselsNr(),
        dato: LocalDate = LocalDate.now(),
        fom: LocalDate = LocalDate.now(),
        tom: LocalDate? = LocalDate.now(),
        kontonummer: Long = nextId(),
        tekst: String = "hello world"
    ): Personkort {
        return Personkort(
            id = nextId(),
            datoSeq = nextId(),
            kontonummer = kontonummer,
            dato = dato,
            fom = fom,
            tom = tom,
            tekst = tekst,
            person = Person(
                id = nextId(),
                merkeligPersonKey = nextId().toString(),
                fnr = fnr
            )
        )
    }

    data class PeriodeFactory(
        val personKey: Long = nextId(),
        val arbufoerSeq: Long = nextId(),
        val fnr: FoedselsNr = foedselsNr(),
        val barnFnr: FoedselsNr = foedselsNr(),
        val stebarnsadopsjon: String? = null) {

        fun periode(): Periode = TestData.periode().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq,
            fnr = fnr
        )

        fun utbetaling(): Utbetaling = TestData.utbetaling().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )

        fun inntekt(): Inntekt = TestData.inntekt().copy(
            personKey = personKey,
            arbufoerSeq = arbufoerSeq
        )
    }

    fun stonadBs(fnrBarn: FoedselsNr = foedselsNr()): StonadBs {
        return StonadBs(
            id = nextId(),
            brukerId = "bruker",
            tidspunktRegistrert = LocalDateTime.now(),
            barn = LopenrFnr(
                id = nextId(),
                fnr = fnrBarn
            )
        )
    }

    fun stonad(stonadBs: StonadBs = stonadBs()): Stonad {
        val id = nextId()
        return Stonad(
            id = id,
            kodeRutine = "BS",
            datoStart = LocalDate.now(),
            datoOpphoer = LocalDate.now(),
            stonadBs = stonadBs.copy(id = id),
            inntektshistorikk = listOf(),
            tidspunktRegistrert = LocalDateTime.now()
        )
    }

    fun delytelse(): Delytelse {
        return Delytelse(
            vedtakId = -1,
            type = "PN",
            tidspunktRegistrert = LocalDateTime.now(),
            fom = LocalDate.now(),
            tom = LocalDate.now(),
            delytelseSpFaBs = delytelserSpFaBs()
        )
    }

    fun delytelserSpFaBs(): DelytelseSpFaBs {
        return DelytelseSpFaBs(
            vedtakId = -1,
            type = "PN",
            tidspunktRegistrert = LocalDateTime.now(),
            grad = -1
        )
    }

    fun vedtak(
        datoStart: LocalDate = LocalDate.now(),
        fnr: FoedselsNr = foedselsNr(),
        kodeRutine: String = "BS",
        delytelserEksermpler: List<Delytelse> = listOf(delytelse()),
        arbeidskategori: Arbeidskategori = Arbeidskategori.AMBASSADEPERSONELL,
        tidspunktRegistrert: LocalDateTime = LocalDateTime.now(),
        datoOpphoer: LocalDate? = LocalDate.now(),
        vedtakSpFaBsOpphoer: LocalDate? = null,
        stonad: Stonad = stonad(stonadBs())
    ): Vedtak {
        val vedtakId = nextId()
        val delytelser = delytelserEksermpler.map { it.copy(
            vedtakId = vedtakId,
            delytelseSpFaBs = it.delytelseSpFaBs?.copy(
                vedtakId = vedtakId,
                type = it.type,
                tidspunktRegistrert = it.tidspunktRegistrert
            )
        ) }

        return Vedtak(
            id = vedtakId,
            stonad = stonad.copy(
                kodeRutine = kodeRutine,
                tidspunktRegistrert = tidspunktRegistrert,
                datoOpphoer = datoOpphoer,
                datoStart = datoStart
            ),
            person = LopenrFnr(id = nextId(), fnr = fnr),
            kodeRutine = kodeRutine,
            datoStart = datoStart,
            vedtakSpFaBs = VedtakSpFaBs(
                vedtakId = vedtakId,
                arbeidskategori = arbeidskategori,
                opphoerFom = vedtakSpFaBsOpphoer
            ),
            alleDelytelser = delytelser
        )
    }

    private val fnrGenerator = FoedselsnummerGenerator()
}