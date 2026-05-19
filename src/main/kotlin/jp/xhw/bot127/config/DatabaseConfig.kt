package jp.xhw.bot127.config

data class DatabaseConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
) {
    companion object {
        fun fromEnvironment(environment: Map<String, String> = System.getenv()): DatabaseConfig {
            val jdbcUrl =
                environment.optional("DATABASE_URL")
                    ?: run {
                        val host = environment.required("DB_HOST")
                        val port = environment.optional("DB_PORT") ?: "3306"
                        val name = environment.required("DB_NAME")
                        "jdbc:mariadb://$host:$port/$name"
                    }

            return DatabaseConfig(
                jdbcUrl = jdbcUrl,
                username = environment.required("DB_USER"),
                password = environment.required("DB_PASSWORD"),
            )
        }
    }
}
