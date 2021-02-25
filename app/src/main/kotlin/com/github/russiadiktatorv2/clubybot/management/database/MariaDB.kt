package com.github.russiadiktatorv2.clubybot.management.database

import java.io.IOException
import java.sql.*

object MariaDB {

    private const val username = "root"
    private const val database: String = "clubydatabase"
    private const val host: String = "localhost"
    private const val port: String = "3306"

    var connection: Connection? = null
    private var statement: Statement? = null

    fun connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database", username, null)
            println("Verbindung zur Datenbank hergestellt.")
            statement = connection?.createStatement()
            TableManager.onCreate()

        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    fun disconnect() {
        try {
            if (connection != null) {
                connection!!.close()
                println("Verbindung zur Datenbank getrennt.")
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun onUpdate(sql: String?) {
        try {
            statement?.execute(sql)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun onQuery(sql: String?): ResultSet? {
        try {
            return statement?.executeQuery(sql)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
        return null
    }
}