/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.presentation.ui.extensions

import java.util.Locale

fun String.toDisplayLanguageName(): String {
    return Locale.forLanguageTag(this)
        .getDisplayLanguage(Locale.US)
        .replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) {
                firstChar.titlecase(Locale.US)
            } else {
                firstChar.toString()
            }
        }
        .takeIf { language -> language.isNotBlank() }
        ?: this
}
