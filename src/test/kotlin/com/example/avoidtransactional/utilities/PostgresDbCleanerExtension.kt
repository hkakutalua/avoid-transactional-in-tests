package com.example.avoidtransactional.utilities

import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.postgresql.ds.PGSimpleDataSource
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.boot.context.properties.bind.Binder
import org.springframework.boot.env.YamlPropertySourceLoader
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.core.io.ClassPathResource
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class PostgresDbCleanerExtension : BeforeEachCallback {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(PostgresDbCleanerExtension::class.java)

        private val TABLES_TO_IGNORE = listOf(
            TableData("databasechangelog"),
            TableData("databasechangeloglock")
        )
    }

    @Throws(Exception::class)
    override fun beforeEach(context: ExtensionContext) {
        val dataSource = getDataSourceFromYamlProperties("application.yml")
        cleanDatabase(dataSource)
    }

    private fun getDataSourceFromYamlProperties(yamlFileClasspath: String): DataSource {
        val yamlPropertySourceLoader = YamlPropertySourceLoader()
        val propertySources = yamlPropertySourceLoader.load(yamlFileClasspath, ClassPathResource(yamlFileClasspath))
        val propertySource = propertySources[0]

        val applicationContext = AnnotationConfigApplicationContext()
        val configurableEnvironment = applicationContext.environment
        configurableEnvironment.propertySources.addFirst(propertySource)

        val binder = Binder.get(configurableEnvironment)
        val dataSourceProperties = binder
            .bind("spring.datasource", Bindable.of(DataSourceProperties::class.java))
            .get()

        val pgSimpleDataSource = PGSimpleDataSource()
        pgSimpleDataSource.setUrl(dataSourceProperties.url)
        pgSimpleDataSource.user = dataSourceProperties.username
        pgSimpleDataSource.password = dataSourceProperties.password
        return pgSimpleDataSource
    }

    private fun cleanDatabase(dataSource: DataSource) {
        try {
            dataSource.connection.use { connection ->
                connection.autoCommit = false
                val tablesToClean = loadTablesToClean(connection)
                cleanTablesData(tablesToClean, connection)
                connection.commit()
            }
        } catch (e: SQLException) {
            LOGGER.error(String.format("Failed to clean database due to error: \"%s\"", e.message))
            e.printStackTrace()
        }
    }

    @Throws(SQLException::class)
    private fun loadTablesToClean(connection: Connection): List<TableData> {
        val databaseMetaData = connection.metaData
        val resultSet = databaseMetaData.getTables(
            connection.catalog, null, null, arrayOf("TABLE"))

        val tablesToClean = mutableListOf<TableData>()
        while (resultSet.next()) {
            val table = TableData(
                schema = resultSet.getString("TABLE_SCHEM"),
                name = resultSet.getString("TABLE_NAME")
            )

            if (!TABLES_TO_IGNORE.contains(table)) {
                tablesToClean.add(table)
            }
        }

        return tablesToClean
    }

    @Throws(SQLException::class)
    private fun cleanTablesData(tablesNames: List<TableData>, connection: Connection) {
        if (tablesNames.isEmpty()) {
            return
        }
        val stringBuilder = StringBuilder("TRUNCATE ")
        for (i in tablesNames.indices) {
            if (i == 0) {
                stringBuilder.append(tablesNames[i].fullyQualifiedTableName)
            } else {
                stringBuilder
                    .append(", ")
                    .append(tablesNames[i].fullyQualifiedTableName)
            }
        }
        connection.prepareStatement(stringBuilder.toString())
            .execute()
    }

    data class TableData(val name: String, val schema: String? = "public") {
        val fullyQualifiedTableName =
            if (schema != null) "$schema.$name" else name
    }
}
