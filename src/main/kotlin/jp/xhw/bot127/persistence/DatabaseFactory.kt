package jp.xhw.bot127.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jp.xhw.bot127.config.DatabaseConfig
import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource

    fun init(config: DatabaseConfig) {
        dataSource =
            HikariDataSource(
                HikariConfig().apply {
                    jdbcUrl = config.jdbcUrl
                    username = config.username
                    password = config.password
                    maximumPoolSize = 5
                },
            )
        Database.connect(dataSource)
        SchemaInitializer.init(dataSource)
    }

    fun close() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }
}
