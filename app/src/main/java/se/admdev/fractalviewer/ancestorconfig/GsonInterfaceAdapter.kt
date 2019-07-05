package se.admdev.fractalviewer.ancestorconfig

import com.google.gson.*
import java.lang.reflect.Type

class GsonInterfaceAdapter : JsonSerializer<Any>, JsonDeserializer<Any> {

    override fun deserialize(
        jsonElement: JsonElement,
        typeOfT: Type,
        jsonDeserializationContext: JsonDeserializationContext
    ): Any {
        val jsonObject = jsonElement.asJsonObject
        val prim = jsonObject.get(CLASSNAME) as JsonPrimitive
        val className = prim.asString
        return jsonDeserializationContext.deserialize(jsonObject.get(DATA), getObjectClass(className))
    }

    override fun serialize(src: Any, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty(CLASSNAME, src.javaClass.name)
        jsonObject.add(DATA, context.serialize(src))
        return jsonObject
    }

    private fun getObjectClass(className: String): Class<*> {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e.message)
        }
    }

    companion object {
        private const val CLASSNAME = "gson_CLASSNAME"
        private const val DATA = "gson_DATA"
    }
}