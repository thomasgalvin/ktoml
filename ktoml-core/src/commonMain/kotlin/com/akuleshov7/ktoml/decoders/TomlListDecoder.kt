package com.akuleshov7.ktoml.decoders

import com.akuleshov7.ktoml.parsers.node.TomlArray
import com.akuleshov7.ktoml.parsers.node.TomlKeyValueList
import com.akuleshov7.ktoml.parsers.node.TomlNode
import com.akuleshov7.ktoml.parsers.node.TomlValue
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
public class TomlListDecoder(
    val rootNode: TomlKeyValueList,
    val config: DecoderConf,
) : AbstractDecoder() {
    private var nextElementIndex = 0
    private val list = rootNode.value.content as List<TomlValue>
    private lateinit var currentElementDecoder: TomlScalarDecoder

    override val serializersModule: SerializersModule = EmptySerializersModule

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = list.size

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (nextElementIndex == list.size) {
            return CompositeDecoder.DECODE_DONE
        }
        currentElementDecoder = TomlScalarDecoder(
            list[nextElementIndex]
        )
        return nextElementIndex++
    }

    private val haveStartedReadingElements: Boolean
        get() = nextElementIndex > 0

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        if (haveStartedReadingElements) {
            return currentElementDecoder
        }
        return super.beginStructure(descriptor)
    }

    override fun decodeString(): String = currentElementDecoder.decodeString()
    override fun decodeInt(): Int = currentElementDecoder.decodeInt()
    override fun decodeLong(): Long = currentElementDecoder.decodeLong()
    override fun decodeShort(): Short = currentElementDecoder.decodeShort()
    override fun decodeByte(): Byte = currentElementDecoder.decodeByte()
    override fun decodeDouble(): Double = currentElementDecoder.decodeDouble()
    override fun decodeFloat(): Float = currentElementDecoder.decodeFloat()
    override fun decodeBoolean(): Boolean = currentElementDecoder.decodeBoolean()
    override fun decodeChar(): Char = currentElementDecoder.decodeChar()
    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = currentElementDecoder.decodeEnum(enumDescriptor)
}
