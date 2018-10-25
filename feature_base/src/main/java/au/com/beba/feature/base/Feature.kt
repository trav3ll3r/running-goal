package au.com.beba.feature.base

interface Feature {

    /**
     * [Feature]'s name mainly used for logging
     *
     * Default value: Class::simpleName, override for other values
     */
    val name: String get() = this::class.java.simpleName

    /**
     * Whether or not the [Feature] is marked as suspended.
     *
     * [Feature] can be suspended via internal/remote config.
     *
     * @return True if the [Feature] is suspended and should not be exposed in the host app.
     */
    var isSuspended: Boolean

    /**
     * Checks if the [Feature] is fully set up and ready to be used
     */
    var isReady: Boolean

}