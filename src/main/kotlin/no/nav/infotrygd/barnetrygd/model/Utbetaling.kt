package no.nav.infotrygd.barnetrygd.model

import no.nav.infotrygd.barnetrygd.model.converters.NavLocalDateConverter
import no.nav.infotrygd.barnetrygd.model.converters.UtbetalingsgradConverter
import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "IS_UTBETALING_15")
data class Utbetaling(
    @Id
    @Column(name = "ID_UTBT", columnDefinition = "DECIMAL")
    val id: Long,

    @Column(name = "IS01_PERSONKEY", columnDefinition = "DECIMAL")
    val personKey: Long,

    @Column(name = "IS10_ARBUFOER_SEQ", columnDefinition = "DECIMAL")
    val arbufoerSeq: Long,

    @Column(name = "IS15_UTBETFOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltFom: LocalDate,

    @Column(name = "IS15_UTBETTOM", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetaltTom: LocalDate,

    @Column(name = "IS15_UTBETDATO", columnDefinition = "DECIMAL")
    @Convert(converter = NavLocalDateConverter::class)
    val utbetalingsdato: LocalDate?,

    @Column(name = "IS15_GRAD", columnDefinition = "CHAR")
    @Convert(converter = UtbetalingsgradConverter::class)
    val grad: Int?,

    @Column(name = "IS15_TYPE", columnDefinition = "CHAR")
    val type: String?,

    @Column(name = "IS15_KORR", columnDefinition = "CHAR")
    val korr: String?
)