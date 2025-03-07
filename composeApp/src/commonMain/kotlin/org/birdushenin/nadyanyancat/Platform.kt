package org.birdushenin.nadyanyancat

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform