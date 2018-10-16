package au.com.beba.runninggoal.domain.event

import org.slf4j.LoggerFactory
import java.lang.ref.WeakReference


interface SubscriberEventCentre {
    fun registerSubscriber(subscriber: Subscriber, event: String)/*: WeakReference<SubscriberPostbox>*/
}

interface PublisherEventCentre {
    fun publish(event: Event)
}

object EventCentre :
        SubscriberEventCentre,
        PublisherEventCentre {

    private val logger = LoggerFactory.getLogger(EventCentre::class.java.simpleName)

    private val channels = Channels()

    override fun registerSubscriber(subscriber: Subscriber, event: String)/*: WeakReference<SubscriberPostbox>*/ {
        logger.info("registerSubscriber")
        channels.registerSubscriber(subscriber, event)
//        val postbox: Postbox = channels.registerSubscriber(subscriber, event)
//        return WeakReference(postbox)
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

    fun registerSubscriber(subscriber: Subscriber, event: String): Postbox {
        logger.info("registerSubscriber")
        val channel = getChannelForEvent(event)
        return channel.addSubscriber(subscriber)
    }

    fun publishEvent(event: Event) {
        logger.info("publishEvent")
        logger.debug("publishEvent | event=%s".format(event.type))
        logger.debug("publishEvent | channels=%s".format(channels.size))
        channels
                .filter {
                    logger.info("publishEvent | channel.forEvent=%s".format(it.forEvent(event.type)))
                    it.forEvent(event.type)
                }
                .forEach {
                    logger.info("publishEvent | channel")
                    it.notify(event)
                }
    }

    private fun getChannelForEvent(event: String): Channel {
        logger.info("getChannelForEvent")
        var channel = channels.find { it.forEvent(event) }
        if (channel == null) {
            logger.info("getChannelForEvent | create new Channel")
            channel = Channel(event)
            channels.add(channel)
        }

        return channel
    }
}

class Channel(private val eventType: String) {
    companion object {
        private val TAG = Channel::class.java.simpleName
    }

    private val logger = LoggerFactory.getLogger(TAG)

    private val postboxes = mutableListOf<Postbox>()

    fun forEvent(eventType: String): Boolean {
        logger.info("forEvent")
        return (this.eventType == eventType)
    }

    fun addSubscriber(subscriber: Subscriber): Postbox {
        logger.info("addSubscriber")
        var postbox = postboxes.find { it.forSubscriber(subscriber) }
        if (postbox == null) {
            logger.info("addSubscriber | create new Postbox")
            postbox = Postbox(subscriber)
            postboxes.add(postbox)
        } else {
            logger.info("addSubscriber | update Subscriber ref for existing Postbox")
            postbox.updateSubscriber(subscriber)
        }

        return postbox
    }

    fun notify(event: Event) {
        logger.info("notify")
        addEventToEveryPostbox(event)
    }

    private fun addEventToEveryPostbox(event: Event) {
        logger.info("addEventToEveryPostbox")
        if (forEvent(event.type)) {
            logger.info("addEventToEveryPostbox | event valid for channel")
            logger.info("addEventToEveryPostbox | postboxes count = ${postboxes.size}")
            postboxes.forEach { it.post(event) }
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
}

interface Subscriber {
    val id: String
        get() {
            return this::class.java.canonicalName
        }

    fun newEvent(postbox: WeakReference<SubscriberPostbox>) {
        throw Exception("Event not handled. You are either missing a handler or subscribing to an unused Event")
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
class GoalDeleteEvent() : Event
class WorkoutSyncEvent(val goalId: Long, val isUpdating: Boolean) : Event