/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.local.library.entity

import androidx.room.Embedded
import androidx.room.Relation

data class SavedPersonWithKnownFor(
    @Embedded
    val person: SavedPersonEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "personId"
    )
    val knownFor: List<SavedPersonKnownForEntity>
)
