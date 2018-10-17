package au.com.beba.runninggoal.domain.event

import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference
import kotlin.reflect.KClass


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
        logger.debug("publish | %s", event.type)
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
        channel.addSubscriber(subscriber)
    }

    fun unregisterSubscriber(subscriber: Subscriber, event: KClass<out Event>) {
        logger.info("unregisterSubscriber")
        synchronized(channels) {
            val channel = getChannelForEvent(event)
            channel?.removeSubscriber(subscriber)
            if (channel?.hasSubscribers() != true) {
                channel?.clear()
                channels.remove(channel)
            }
        }
    }

    fun publishEvent(event: Event) {
        logger.info("publishEvent")
        logger.debug("publishEvent | event=%s".format(event.type))
        logger.debug("publishEvent | channels=%s".format(channels.size))
        channels
                .filter {
                    val filterResult = it.forEvent(event::class)
                    logger.info("publishEvent | channel.forEvent=$filterResult")
                    filterResult
                }
                .forEach {
                    logger.info("publishEvent | channel")
                    it.notify(event)
                }
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

    fun addSubscriber(subscriber: Subscriber) {
        logger.info("addSubscriber")
        val postbox = postboxes.find { it.forSubscriber(subscriber) }
        if (postbox != null) {
            logger.info("addSubscriber | update Subscriber ref for existing Postbox")
            postbox.updateSubscriber(subscriber)
        } else {
            logger.info("addSubscriber | create new Postbox")
            postboxes.add(Postbox(subscriber))
        }
    }

    fun removeSubscriber(subscriber: Subscriber) {
        logger.info("removeSubscriber")
        val postbox = postboxes.find { it.forSubscriber(subscriber) }
        if (postbox != null) {
            logger.info("removeSubscriber | destroy Postbox")
            postbox.clear()
            postboxes.remove(postbox)
        } else {
            logger.info("removeSubscriber | Postbox not found")
        }
    }

    fun notify(event: Event) {
        logger.info("notify")
        addEventToEveryPostbox(event)
    }

    private fun addEventToEveryPostbox(event: Event) {
        logger.info("addEventToEveryPostbox")
        if (forEvent(event::class)) {
            logger.info("addEventToEveryPostbox | event valid for channel")
            logger.info("addEventToEveryPostbox | postboxes count = ${postboxes.size}")
            postboxes.forEach { it.post(event) }
        }
    }

    fun hasSubscribers(): Boolean {
        synchronized(postboxes) {
            return postboxes.isEmpty()
        }
    }

    fun clear() {
        synchronized(postboxes) {
            postboxes.clear()
        }
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
}

class Postbox(subscriber: Subscriber) : SubscriberPostbox {
    companion object {
        private val TAG = Postbox::class.java.simpleName
    }

    private val logger = LoggerFactory.getLogger(TAG)

    private val events = mutableListOf<Event>()
    private val subscriberId: String = subscriber.id
    private var subscriberRef: WeakReference<Subscriber>? = null

    init {
        subscriberRef = WeakReference(subscriber)
    }

    fun updateSubscriber(subscriber: Subscriber) {
        subscriberRef = WeakReference(subscriber)
    }

    fun forSubscriber(subscriber: Subscriber): Boolean {
        logger.info("forSubscriber")
        logger.debug("forSubscriber | %s".format(subscriber.id))
        return (subscriberId == subscriber::class.java.canonicalName)
    }

    fun post(event: Event) {
        logger.info("post")
        events.add(event)
        subscriberRef?.get()?.newEvent(WeakReference(this))
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

    override fun toString(): String {
        return "%s {subscriber=%s, events=%s}".format(this::class.java.canonicalName, subscriberId, events.size)
    }
}

interface Subscriber {
    val id: String
        get() {
            return this::class.java.canonicalName
        }

    fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        throw Exception("Event [%s] not handled! You are either missing a handler or subscribing to an unused Event".format(this::class.java.canonicalName))
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
class WorkoutSyncEvent(val goalId: Long, val isUpdating: Boolean) : Event