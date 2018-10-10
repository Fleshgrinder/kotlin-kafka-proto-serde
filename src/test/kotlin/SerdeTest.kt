package com.fleshgrinder.kotlin.kafka

import com.google.protobuf.DescriptorProtos.FileOptions
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serializer
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class SerdeTest {
    @ParameterizedTest
    @MethodSource("provideSerdes")
    fun <T> `serde construction plus serialization and deserialization`(
        serde: T,
        serialized: ByteArray,
        deserialized: Any
    ) where T : Serde<Any>, T : Serializer<Any>, T : Deserializer<Any> {
        assertArrayEquals(serialized, serde.serialize("topic", deserialized)) {
            "serialization failed"
        }

        assertEquals(deserialized, serde.deserialize("topic", serialized)) {
            "deserialization failed"
        }
    }

    @Suppress("unused")
    fun provideSerdes(): Stream<Arguments> = Stream.of(
        arguments(serde<FileOptions>(), ProtoTest.PROTO_SERIALIZED, ProtoTest.PROTO),
        arguments(serde<String>(), "test".toByteArray(), "test")
    )

    @Test
    fun `exception message includes hint about available proto serde`() {
        val e = assertThrows<IllegalArgumentException> {
            serde<System>()
        }

        assert(e.message?.contains(Proto::class.java.typeName) == true) {
            "message should contain proto serde hint, got: ${e.message}"
        }
    }
}
