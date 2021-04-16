package com.github.russiadiktatorv2.clubybot.management.database

import java.sql.*

object PostgreSQL {

    var connection: Connection? = null

    fun databaseConnect() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:postgresql://${System.getenv("postgrehost")}", System.getenv("postgreuser"), System.getenv("clubysqlpassword"))
                createTables()
            } catch (exception: SQLException) {
                exception.printStackTrace()
            }
        }
    }

    fun disconnectDatabase() {
        if (connection != null) {
            connection?.close()
        }
    }


    private fun createTables() {
        connection?.apply {
            //System Statements
            prepareStatement("CREATE TABLE IF NOT EXISTS prefix_system(server_id bigint NOT NULL PRIMARY KEY, server_prefix VARCHAR (7) NOT NULL);").execute()
            prepareStatement("CREATE TABLE IF NOT EXISTS welcome_system(server_id bigint NOT NULL PRIMARY KEY, channel_id bigint NOT NULL, welcome_message VARCHAR (170), username_allowed BOOLEAN, member_count_allowed BOOLEAN);").execute()
            prepareStatement("CREATE TABLE IF NOT EXISTS ticket_system(ticket_channel_id bigint NOT NULL PRIMARY KEY, ticket_channel_name VARCHAR (32) NOT NULL, ticket_channel_message VARCHAR (150), ticket_roles bigint[] );").execute()

            //Module Statements
            prepareStatement("CREATE TABLE IF NOT EXISTS moderation_module(server_id bigint NOT NULL PRIMARY KEY);").execute()
            prepareStatement("CREATE TABLE IF NOT EXISTS welcome_module(server_id bigint NOT NULL PRIMARY KEY);").execute()
            prepareStatement("CREATE TABLE IF NOT EXISTS ticket_module(server_id bigint NOT NULL PRIMARY KEY);").execute()
        }
    }
}