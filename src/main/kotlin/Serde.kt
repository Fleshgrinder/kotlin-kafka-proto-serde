@file:Suppress("RedundantVisibilityModifier")

package com.fleshgrinder.kotlin.kafka

import com.google.protobuf.Parser
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serializer
import org.apache.kafka.common.errors.SerializationException

/**
 * [BaseSerde] combines [Serde], [Serializer], and [Deserializer] into a single
 * unit and fixes type information for Kotlin. One should not program against
 * this directly, however, it is a convenient basis for writing Kafka [Serde]s
 * for other types that you might require in your code base.
 *
 * @see serde
 */
public abstract class BaseSerde<T> : Serde<T>, Serializer<T>, Deserializer<T> {
    /**
     * Configure this object, which will configure the underlying [Serializer]
     * and [Deserializer].
     *
     * @param config configs in key/value pairs
     * @param isKey whether [config] is for key or value
     */
    override fun configure(config: Map<String, *>, isKey: Boolean): Unit = Unit

    /**
     * Get the underlying [Deserializer]
     *
     * @return always itself
     */
    final override fun deserializer(): Deserializer<T> = this

    /**
     * Get the underlying [Serializer]
     *
     * @return always itself
     */
    final override fun serializer(): Serializer<T> = this

    /**
     * Convert [data] [T] to a [ByteArray]
     *
     * @param topic associated with [data]
     * @param data to serialize
     * @return [ByteArray] serialized from [data] or `null` if [data] is `null`
     * @see Serializer.serialize
     * @throws SerializationException
     */
    final override fun serialize(topic: String, data: T?): ByteArray? = data?.let { doSerialize(topic, it) }

    /**
     * Convert [data] to [T]
     *
     * @param topic associated with [data]
     * @param data to deserialize
     * @return [T] deserialized from [data] or `null` if [data] is `null`
     * @see Deserializer.deserialize
     * @throws SerializationException
     */
    final override fun deserialize(topic: String, data: ByteArray?): T? = data?.let { doDeserialize(topic, it) }

    /**
     * Close this [Serde]/[Serializer]/[Deserializer] and frees any currently
     * held resources. This method must be idempotent because it might be called
     * multiple times.
     */
    override fun close(): Unit = Unit

    /**
     * Extending classes must implement this method to convert [data] of type
     * [T] to a [ByteArray].
     *
     * @param topic associated with [data]
     * @param data to serialize
     * @return [ByteArray] serialized from [data]
     * @see serialize
     * @throws SerializationException
     */
    protected abstract fun doSerialize(topic: String, data: T): ByteArray

    /**
     * Extending classes must implement this function to convert the [ByteArray]
     * [data] back to its original type [T].
     *
     * @param topic associated with [data]
     * @param data to deserialize
     * @return [T] deserialized from [data]
     * @see deserialize
     * @throws SerializationException
     */
    protected abstract fun doDeserialize(topic: String, data: ByteArray): T
}

/**
 * [NativeSerde] decorates Kafka [Serde]s and allows direct access to its
 * [Serializer] and [Deserializer] functions.
 *
 * @param T is the concrete type the decorated [Serde] is able to serialize and
 *   deserialize.
 * @param inner [Serde] to decorate with the [BaseSerde] interface.
 * @see serde
 */
public class NativeSerde<T>(private val inner: Serde<T>) : BaseSerde<T>() {
    override fun doSerialize(topic: String, data: T): ByteArray = inner.serializer().serialize(topic, data)
    override fun doDeserialize(topic: String, data: ByteArray): T = inner.deserializer().deserialize(topic, data)
}

/**
 * The [ProtoSerde] class is a Kafka [Serde]/[Serializer]/[Deserializer] for
 * [Proto], version 2 and 3, objects.
 *
 * @param T is the concrete [Proto] type to construct the [ProtoSerde] for.
 * @param parser for serialization and deserialization of [T].
 * @see serde foo
 */
public class ProtoSerde<T : Proto>(private val parser: Parser<T>) : BaseSerde<T>() {
    override fun doSerialize(topic: String, data: T): ByteArray = data.toByteArray()
    override fun doDeserialize(topic: String, data: ByteArray): T = parser.parseFrom(data) as T
}

/**
 * Construct new [Serde]/[Serializer]/[Deserializer] for [T].
 *
 * @param T to get a [Serde]/[Serializer]/[Deserializer] for.
 * @throws IllegalArgumentException if no [Serde] exists for [T].
 */
@Suppress("UNCHECKED_CAST")
public inline fun <reified T> serde(): BaseSerde<T> =
    if (Proto::class.java.isAssignableFrom(T::class.java)) {
        ProtoSerde(
            T::class.java.getDeclaredField("PARSER")
                .apply { isAccessible = true }
                .get(null) as Parser<Proto>
        ) as BaseSerde<T>
    } else try {
        NativeSerde(Serdes.serdeFrom(T::class.java))
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("${e.message}, and ${Proto::class.java.typeName} instances")
    }
