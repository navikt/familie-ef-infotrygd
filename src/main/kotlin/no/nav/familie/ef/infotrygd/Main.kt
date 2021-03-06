package no.nav.familie.ef.infotrygd

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Main

fun main(args: Array<String>) {
    System.setProperty("oracle.jdbc.fanEnabled", "false")
    runApplication<Main>(*args)
}
