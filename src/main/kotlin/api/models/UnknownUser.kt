package api.models

import kotlinx.serialization.json.JsonObject

data class UnknownUser(val id: Int, val details: JsonObject)
