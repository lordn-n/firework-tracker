package com.koome.fireworkstracker

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.benasher44.uuid.uuid4

object FireworkEventsRepository {

    private val _events = MutableStateFlow<List<FireworkEvent>>(emptyList())
    val events: StateFlow<List<FireworkEvent>> = _events.asStateFlow()

    fun addEvent(event: FireworkEvent) {
        _events.update {
            it + event.copy(id = uuid4().toString())
        }
    }

    fun deleteEvent(eventId: String) {
        _events.update { currentEvents ->
            currentEvents.filterNot { it.id == eventId }
        }
    }
}
