package no.nav.familie.ef.infotrygd.config

import no.nav.familie.ef.infotrygd.Profiles
import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@EnableJwtTokenValidation(ignore = ["org.springframework", "springfox"])
@Profile("!${Profiles.NOAUTH}")
@Configuration
class SecurityConfiguration
