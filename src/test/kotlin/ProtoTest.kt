package com.fleshgrinder.kotlin.kafka

import com.google.protobuf.DescriptorProtos
import org.junit.jupiter.api.Test

class ProtoTest {
    private fun Char.isOther() =
        when (category) {
            CharCategory.CONTROL,
            CharCategory.FORMAT,
            CharCategory.SURROGATE,
            CharCategory.PRIVATE_USE,
            CharCategory.UNASSIGNED -> true
            else -> false
        }

    @Test
    fun `proto debug string does not include line feeds`() {
        val s = PROTO.toDebugString()

        assert(s.all { !it.isOther() }) {
            "debug string must not contain any Unicode character from the Other category, got: $s"
        }
    }

    @Test
    fun `proto debug string contains simple class name`() {
        val s = PROTO.toDebugString()

        assert(s.startsWith("${DescriptorProtos.FileOptions::class.java.simpleName}(")) {
            "debug string must start with the simple name of the proto class, got: $s"
        }
    }

    companion object {
        val PROTO: DescriptorProtos.FileOptions = DescriptorProtos.FileOptions.newBuilder().apply {
            javaOuterClassname = "TestProto"
            javaPackage = "com.fleshgrinder.kotlin.kafka"
        }.build()

        val PROTO_SERIALIZED: ByteArray = PROTO.toByteArray()
    }
}
