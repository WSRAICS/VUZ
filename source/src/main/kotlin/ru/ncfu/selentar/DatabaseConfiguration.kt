package ru.ncfu.selentar

import org.hibernate.SessionFactory
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import ru.ncfu.selentar.domain.LearningRecord
import ru.ncfu.selentar.domain.Record
import java.util.*

/**
 * Настройка подключения к базе данных H2
 */
object DatabaseConfiguration {

    val SESSION_FACTORY: SessionFactory by lazy {
        val configuration = Configuration()

        val prop = Properties()
        prop[Environment.URL] = "jdbc:h2:file:./database"
        prop[Environment.HBM2DDL_AUTO] = "update"
        configuration.properties = prop

        configuration.addAnnotatedClass(Record::class.java)
        configuration.addAnnotatedClass(LearningRecord::class.java)

        configuration.buildSessionFactory()
    }
}