package no.nav.familie.ef.infotrygd.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.nio.file.Files
import java.nio.file.Paths
import javax.sql.DataSource

@Configuration
class DatasourceConfig {
    @Bean
    fun vaultDatasourceUsername(
        @Value("\${vault.username}") filePath: String,
    ): String {
        val path = Paths.get(filePath)
        return Files.readString(path)
    }

    @Bean
    fun vaultDatasourcePassword(
        @Value("\${vault.password}") filePath: String,
    ): String {
        val path = Paths.get(filePath)
        return Files.readString(path)
    }

    @Bean
    @Profile("!test")
    fun datasource(
        vaultDatasourceUsername: String,
        vaultDatasourcePassword: String,
        @Value("\${spring.datasource.url}") url: String,
        @Value("\${spring.datasource.driver-class-name}") driverName: String,
    ): DataSource {
        val dataSourceBuilder = DataSourceBuilder.create()
        dataSourceBuilder.driverClassName(driverName)
        dataSourceBuilder.url(url)
        dataSourceBuilder.username(vaultDatasourceUsername)
        dataSourceBuilder.password(vaultDatasourcePassword)
        return dataSourceBuilder.build()
    }
}
