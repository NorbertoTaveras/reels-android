/*
 * Copyright (c) 2026 Norberto Taveras. All rights reserved.
 *
 * This source code is proprietary and confidential. Unauthorized copying,
 * modification, distribution, or use is prohibited.
 */

package com.norbertotaveras.reels.data.remote.error

import java.io.IOException

class TmdbSessionExpiredException : IOException(
    "Your TMDB session expired. Connect again in Settings."
)
