package api.models

import kotlinx.serialization.json.JsonObject

data class NestedObject(val className: String, val details: JsonObject)
