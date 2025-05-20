package com.example.bitbloomadmin.utils

enum class TimeFilter {
    TODAY,      // from 00:00 local → now
    WEEKLY,     // last 7 full days
    MONTHLY,    // last 30 full days
    ANNUALLY,   // last 365 full days
    ALL_TIME    // no date filter
}