package no.nav.familie.ef.infotrygd.config

import com.zaxxer.hikari.HikariDataSource
import jakarta.validation.constraints.NotEmpty
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.nio.file.Files
import java.nio.file.Paths
import javax.sql.DataSource

@Configuration
@EnableConfigurationProperties(DatasourceConfiguration::class)
class DatasourceConfig {
    @Bean
    fun datasourceConfiguration(): DatasourceConfiguration = DatasourceConfiguration()

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
    fun datasource(
        datasourceConfiguration: DatasourceConfiguration,
        vaultDatasourceUsername: String,
        vaultDatasourcePassword: String,
        @Value("\${spring.datasource.hikari.schema}") defaultschema: String,
    ): DataSource {
        requireNotNull(datasourceConfiguration.url) { "spring.datasource.url is null" }
        requireNotNull(datasourceConfiguration.driverClassName) { "spring.datasource.driverClassName is null" }

        return HikariDataSource().apply {
            jdbcUrl = datasourceConfiguration.url
            driverClassName = datasourceConfiguration.driverClassName
            username = vaultDatasourceUsername
            password = vaultDatasourcePassword
            schema = defaultschema
        }
    }
}

@ConfigurationProperties(prefix = "spring.datasource")
@Validated
data class DatasourceConfiguration(
    @NotEmpty
    var url: String? = null,
    @NotEmpty
    var driverClassName: String? = null,
)
