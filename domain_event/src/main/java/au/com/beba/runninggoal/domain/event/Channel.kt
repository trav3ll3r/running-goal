package au.com.beba.runninggoal.domain.event

import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

private val INDENT = "  "

interface SubscriberEventCentre {
    fun registerSubscriber(subscriber: Subscriber, event: KClass<out Event>)
    fun unregisterSubscriber(subscriber: Subscriber, event: KClass<out Event>)
}

interface PublisherEventCentre {
    fun publish(event: Event)
}

object EventCentre :
        SubscriberEventCentre,
        PublisherEventCentre {

    private val logger = LoggerFactory.getLogger(EventCentre::class.java.simpleName)

    private val channels = Channels()

    init {
        logger.info("init")
    }

    override fun registerSubscriber(subscriber: Subscriber, event: KClass<out Event>) {
        logger.info("registerSubscriber")
        channels.registerSubscriber(subscriber, event)
    }

    override fun unregisterSubscriber(subscriber: Subscriber, event: KClass<out Event>) {
        logger.info("unregisterSubscriber")
        channels.unregisterSubscriber(subscriber, event)
    }

    override fun publish(event: Event) {
        logger.info("publish")
        logger.debug("publish | ${event.type}")
        channels.publishEvent(event)
    }
}

class Channels {
    companion object {
        private val TAG = Channels::class.java.simpleName
    }

    private val logger = LoggerFactory.getLogger(TAG)

    private val channels = mutableListOf<Channel>()

    fun registerSubscriber(subscriber: Subscriber, event: KClass<out Event>) {
        logger.info("registerSubscriber")
        var channel = getChannelForEvent(event)
        if (channel == null) {
            channel = registerChannel(event)
        }
        channel.leasePostbox(subscriber)
    }

    fun unregisterSubscriber(subscriber: Subscriber, event: KClass<out Event>) {
        logger.info("unregisterSubscriber")
        synchronized(channels) {
            val channel = getChannelForEvent(event)
            if (channel != null) {
                channel.removeSubscriber(subscriber)
                if (!channel.hasSubscribers()) {
                    logger.info("unregisterSubscriber | remove channel")
                    logger.debug("unregisterSubscriber | remove channel | ${channel.dump()}")
                    channel.clear()
                    channels.remove(channel)
                }
            }
        }
    }

    fun publishEvent(event: Event) {
        logger.info("publishEvent")
        logger.debug("publishEvent | event=%s".format(event.type))
        logger.debug("publishEvent | channels=%s".format(channels.size))

        dump("before post and notify")

        channels
                .filter {
                    val filterResult = it.forEvent(event::class)
                    logger.debug("publishEvent | channel=${it.describe(false)} forEvent=$filterResult")
                    filterResult
                }
                .forEach {
                    logger.debug("publishEvent | channel=%s".format(it.describe()))
                    it.notify(event)
                }
        dump("after post and notify")
    }

    private fun getChannelForEvent(event: KClass<out Event>): Channel? {
        logger.info("getChannelForEvent")
        logger.debug("getChannelForEvent | type=%s".format(event.java.canonicalName))
        return channels.find { it.forEvent(event) }
    }

    private fun registerChannel(event: KClass<out Event>): Channel {
        logger.info("registerChannel | create new Channel")
        val channel = Channel(event)
        channels.add(channel)
        return channel
    }

    private fun dump(message: String = "") {
        val sb = StringBuilder("$message \n")

        sb.append("channels [").append("\n")
        channels.forEach {
            sb.append(it.dump())
        }
        sb.append("]").append("\n")

        logger.debug(sb.toString())
    }
}

class Channel(private val event: KClass<out Event>) {
    companion object {
        private val TAG = Channel::class.java.simpleName
    }

    private val logger = LoggerFactory.getLogger(TAG)

    private val postboxes = mutableListOf<Postbox>()

    fun forEvent(event: KClass<out Event>): Boolean {
        logger.info("forEvent")
        return (this.event == event)
    }

    fun leasePostbox(subscriber: Subscriber) {
        logger.info("leasePostbox")
        val existingPostbox = postboxes.find { it.forSubscriber(subscriber) }
        if (existingPostbox != null) {
            logger.info("leasePostbox | update Subscriber for existing Postbox")
            existingPostbox.updateSubscriber(subscriber)
        } else {
            logger.info("leasePostbox | lease new")
            val newPostbox = Postbox(subscriber)
            logger.debug("leasePostbox | ${newPostbox.describe(false)} event=${event.java.simpleName}")
            postboxes.add(newPostbox)
        }
        logger.debug("leasePostbox | after\n${dump()}")
    }

    fun removeSubscriber(subscriber: Subscriber) {
        logger.info("removeSubscriber")
        logger.debug("removeSubscriber | ${subscriber.id}")
        val postbox = postboxes.find { it.forSubscriber(subscriber) }
        if (postbox != null) {
            logger.debug("removeSubscriber | destroy ${postbox.dump()}")
            postbox.clear()
            postboxes.remove(postbox)
        } else {
            logger.info("removeSubscriber | Postbox not found")
        }

        logger.debug("removeSubscriber | after\n${dump()}")
    }

    fun notify(event: Event) {
        logger.info("notify")
        addEventToEveryPostbox(event)
    }

    private fun addEventToEveryPostbox(event: Event) {
        logger.info("addEventToEveryPostbox")
        if (forEvent(event::class)) {
            logger.debug("addEventToEveryPostbox | event valid for channel")
            logger.debug("addEventToEveryPostbox | postboxes count=${postboxes.size}")
            postboxes.forEach { it.dropIntoPostbox(event) }
            postboxes.forEach { it.notifySubscriber() }
        }
    }

    fun hasSubscribers(): Boolean {
        synchronized(postboxes) {
            return !postboxes.isEmpty()
        }
    }

    fun clear() {
        synchronized(postboxes) {
            postboxes.clear()
        }
    }

    fun describe(canonical: Boolean = true): String {
        return if (canonical) {
            event.java.canonicalName
        } else {
            event.java.simpleName
        }
    }

    internal fun dump(): String {
        val sb = StringBuilder("")
        val indent = INDENT
        sb.append(indent).append("channel ${describe(false)} (${postboxes.size}) [").append("\n")
        postboxes.forEach {
            sb.append(it.dump())
        }
        sb.append(indent).append("]").append("\n")
        return sb.toString()
    }
}

interface SubscriberPostbox {
    /**
     * Removes last (latest added) from the [Postbox] and returns it
     *
     * @return [Event] or null if postbox is empty
     */
    fun takeLast(): Event?

    fun clear()

    fun describe(canonical: Boolean = true): String
}

class Postbox(subscriber: Subscriber) : SubscriberPostbox {
    companion object {
        private val TAG = Postbox::class.java.simpleName
    }

    private val logger = LoggerFactory.getLogger(TAG)

    private val events = mutableListOf<Event>()
    private val subscriberId: KClass<out Subscriber> = subscriber.id
    private var subscriberRef: WeakReference<Subscriber>? = null

    init {
        subscriberRef = WeakReference(subscriber)
    }

    fun updateSubscriber(subscriber: Subscriber) {
        subscriberRef = WeakReference(subscriber)
    }

    fun forSubscriber(subscriber: Subscriber): Boolean {
        logger.info("forSubscriber")
        logger.debug("forSubscriber | look=%s".format(subscriber.id.java.simpleName))
        logger.debug("forSubscriber | this=%s".format(subscriberId.java.simpleName))
        val isMatch = subscriber.sameAs(subscriberId)
        logger.debug("forSubscriber | match=%s".format(isMatch))
        return isMatch
    }


    fun dropIntoPostbox(event: Event) {
        logger.info("dropIntoPostbox")
        events.add(event)
    }

    fun notifySubscriber() {
        logger.info("notifySubscriber")
        val sub = subscriberRef?.get()
        if (sub != null) {
            logger.debug("notifySubscriber | subscriber found, notifying")
            sub.newEvent(WeakReference(this))
        } else {
            logger.debug("notifySubscriber | subscriber ${subscriberId.java.simpleName} reference lost")
        }
    }

    override fun takeLast(): Event? {
        logger.info("takeLast")
        return when {
            events.isNotEmpty() -> {
                return events.removeAt(events.lastIndex)
            }
            else -> null
        }
    }

    override fun clear() {
        events.clear()
    }

    override fun describe(canonical: Boolean): String {
        return "%s {subscriber=%s, count=%s}".format(
                this::class.java.simpleName,
                if (canonical) subscriberId.java.canonicalName else subscriberId.java.simpleName,
                events.size)
    }

    internal fun dump(): String {
        val sb = StringBuilder("")
        val indent = INDENT + INDENT
        sb.append(indent).append(describe(false)).append("\n")
        return sb.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Postbox

        if (subscriberId != other.subscriberId) return false

        return true
    }

    override fun hashCode(): Int {
        return subscriberId.hashCode()
    }
}

interface Subscriber {
    val id: KClass<out Subscriber>
        get() {
            return this::class
        }

    fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        throw Exception("Event [%s] not handled! You are either missing a handler or subscribing to an unused Event".format(this::class.java.canonicalName))
    }

    fun sameAs(that: KClass<out Subscriber>): Boolean {
        return id.java.canonicalName == that.java.canonicalName
    }
}

interface Event {
    val type: String
        get() {
            return this::class.java.canonicalName
        }

    fun equals(other: Event): Boolean {
        return type == other.type
    }
}

//TODO: MOVE THIS SOMEWHERE ELSE
class GoalChangeEvent(val goalId: Long) : Event

class GoalDeleteEvent(val goalId: Long) : Event

class SyncSourceChange : Event
class SyncSourceDelete : Event
class NoDefaultSyncSource : Event
class WorkoutSyncEvent(val goalId: Long, val isUpdating: Boolean) : Event


class WidgetChangeEvent(val widgetId: Long, val goalId: Long?) : Event
class WidgetDeleteEvent(val widgetId: Long) : Event