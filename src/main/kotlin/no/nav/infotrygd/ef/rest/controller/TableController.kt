package no.nav.infotrygd.ef.rest.controller

import no.nav.infotrygd.ef.integration.TableIntegrator
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.transaction.Transactional

@RestController
@Unprotected
@Transactional
class TableController(private val tableIntegrator: TableIntegrator,
                      private val jdbcTemplate: NamedParameterJdbcTemplate) {

    @GetMapping(path = ["/tables"])
    fun get(): Map<String, List<String>> {
        return tableIntegrator.tables
    }

    @GetMapping(path = ["/tables2"])
    fun get2(): Map<String, Any?> {
        return tableIntegrator.tables.keys.map {
            it to jdbcTemplate.queryForObject("select count(*) from $it", emptyMap<String, Any>(), Int::class.java)
        }.toMap()
    }
}