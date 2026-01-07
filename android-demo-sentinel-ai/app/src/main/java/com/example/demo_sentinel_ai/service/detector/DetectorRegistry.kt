package com.example.demo_sentinel_ai.service.detector

/**
 * Registry that maps package names to their appropriate detectors.
 */
object DetectorRegistry {
    private val detectors: List<AppDetector> = listOf(
        LineDetector(),
        WhatsAppDetector(),
        MessengerDetector()
    )

    private val packageToDetector: Map<String, AppDetector> = detectors
        .flatMap { detector -> detector.supportedPackages.map { pkg -> pkg to detector } }
        .toMap()

    /** All package names we monitor */
    val supportedPackages: Set<String> = packageToDetector.keys

    /** Get detector for a package, or null if not supported */
    fun getDetector(packageName: String): AppDetector? = packageToDetector[packageName]

    /** Get app display name for a package */
    fun getAppDisplayName(packageName: String): String =
        packageToDetector[packageName]?.appDisplayName ?: "Unknown"
}
