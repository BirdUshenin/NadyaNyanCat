package org.birdushenin.nadyanyancat

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import platform.SystemConfiguration.SCNetworkReachabilityCreateWithName
import platform.SystemConfiguration.SCNetworkReachabilityFlagsVar
import platform.SystemConfiguration.SCNetworkReachabilityGetFlags
import platform.SystemConfiguration.kSCNetworkFlagsReachable
import kotlinx.cinterop.*

@Composable
@OptIn(ExperimentalForeignApi::class)
actual fun isInternetAvailable(): Boolean {
    val reachability = SCNetworkReachabilityCreateWithName(null, "www.google.com")
    val flagsVar = nativeHeap.alloc<SCNetworkReachabilityFlagsVar>()
    val result = SCNetworkReachabilityGetFlags(reachability, flagsVar.ptr)

    return result && (flagsVar.value and kSCNetworkFlagsReachable != 0u)
}