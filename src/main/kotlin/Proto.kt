@file:Suppress("RedundantVisibilityModifier")

package com.fleshgrinder.kotlin.kafka

import com.google.protobuf.Message
import com.google.protobuf.TextFormat

/**
 * [Proto] is a more descriptive type alias for the very generic [Message]
 * interface from Protobuf. It is also how most people address Protobuf in their
 * conversational language, hence, it aligns the code with that.
 */
public typealias Proto = Message

/**
 * Transform this [Proto] to a human readable string without any line feeds.
 */
public fun Proto.toDebugString(): String =
    "${this::class.java.simpleName}(${TextFormat.shortDebugString(this)})"
