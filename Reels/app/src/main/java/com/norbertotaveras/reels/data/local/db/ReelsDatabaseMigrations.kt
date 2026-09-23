/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.db

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

object ReelsDatabaseMigrations {
    val migration1To2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `saved_people` (
                    `id` INTEGER NOT NULL,
                    `name` TEXT NOT NULL,
                    `biography` TEXT NOT NULL,
                    `birthday` TEXT,
                    `placeOfBirth` TEXT,
                    `profilePath` TEXT,
                    `knownForDepartment` TEXT,
                    `savedAt` INTEGER NOT NULL,
                    PRIMARY KEY(`id`)
                )
                """.trimIndent()
            )

            connection.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `saved_person_known_for` (
                    `personId` INTEGER NOT NULL,
                    `movieId` INTEGER NOT NULL,
                    `title` TEXT NOT NULL,
                    `posterPath` TEXT,
                    `releaseDate` TEXT,
                    `character` TEXT,
                    `voteAverage` REAL NOT NULL,
                    `popularity` REAL NOT NULL,
                    `position` INTEGER NOT NULL,
                    PRIMARY KEY(`personId`, `movieId`)
                )
                """.trimIndent()
            )
        }
    }

    val all: Array<Migration> = arrayOf(migration1To2)
}
