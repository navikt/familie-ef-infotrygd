package no.nav.familie.ef.infotrygd.model

import no.nav.commons.foedselsnummer.FoedselsNr
import no.nav.familie.ef.infotrygd.model.converters.Char2Converter
import no.nav.familie.ef.infotrygd.model.converters.Char3Converter
import no.nav.familie.ef.infotrygd.model.converters.NavReversedLocalDateConverter
import no.nav.familie.ef.infotrygd.model.converters.ReversedFoedselNrConverter
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "SA_SAK_10")
data class Sak(
        @Id
        @Column(name = "ID_SAK", columnDefinition = "DECIMAL", nullable = false)
        val id: Long,

        @Column(name = "REGION", columnDefinition = "CHAR")
        val region: String,

        //@Column(name = "S01_PERSONKEY", columnDefinition = "DECIMAL") // tknr + fnr
        //val personKey: Long,

        @Column(name = "S05_SAKSBLOKK", columnDefinition = "CHAR")
        val saksblokk: String,

        @Column(name = "S10_SAKSNR", columnDefinition = "CHAR")
        val saksnummer: String,

        @Column(name = "S10_KAPITTELNR", columnDefinition = "CHAR")
        val kapittelNr: String,

        @Column(name = "S10_VALG", columnDefinition = "CHAR")
        @Convert(converter = Char2Converter::class)
        val valg: String,

        @Column(name = "S10_TYPE", columnDefinition = "CHAR")
        @Convert(converter = Char2Converter::class)
        val type: String,

        @Column(name = "S10_UNDERVALG", columnDefinition = "CHAR")
        @Convert(converter = Char2Converter::class)
        val undervalg: String?,

        @Column(name = "S10_RESULTAT", columnDefinition = "CHAR")
        @Convert(converter = Char2Converter::class)
        val resultat: String?,

        @Column(name = "S10_VEDTAKSDATO", columnDefinition = "DECIMAL")
        @Convert(converter = NavReversedLocalDateConverter::class)
        val vedtaksdato: LocalDate?,

        @Column(name = "S10_IVERKSATTDATO", columnDefinition = "DECIMAL")
        @Convert(converter = NavReversedLocalDateConverter::class)
        val iverksattdato: LocalDate?,

        @Column(name = "S10_REG_DATO", columnDefinition = "DECIMAL")
        @Convert(converter = NavReversedLocalDateConverter::class)
        val regDato: LocalDate?,

        @Column(name = "S10_MOTTATTDATO", columnDefinition = "DECIMAL")
        @Convert(converter = NavReversedLocalDateConverter::class)
        val mottattdato: LocalDate?,

        @Column(name = "S10_NIVAA", columnDefinition = "CHAR")
        @Convert(converter = Char3Converter::class)
        val nivaa: String? = null,

        @Column(name = "S10_AARSAKSKODE", columnDefinition = "CHAR")
        @Convert(converter = Char2Converter::class)
        val aarsakskode: String? = null,

        @Column(name = "S10_BEHEN_ENHET", columnDefinition = "CHAR(4)")
        val behenEnhet: String? = null,

        @Column(name = "S10_REG_AV_ENHET", columnDefinition = "CHAR(4)")
        val regAvEnhet: String? = null,

        //@Column(name = "S10_VEDTATT_AV_ENHET", columnDefinition = "CHAR(4)")
        //val vedtattAvEnhet: String? = null,

        @Column(name = "TK_NR", columnDefinition = "CHAR(4)")
        val tkNr: String? = null,

        @Column(name = "F_NR", columnDefinition = "CHAR(11)")
        @Convert(converter = ReversedFoedselNrConverter::class)
        val fnr: FoedselsNr,
)