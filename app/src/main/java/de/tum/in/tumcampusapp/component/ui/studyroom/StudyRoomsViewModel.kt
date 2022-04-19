package de.tum.`in`.tumcampusapp.component.ui.studyroom

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.tum.`in`.tumcampusapp.component.ui.studyroom.model.StudyRoomGroup
import de.tum.`in`.tumcampusapp.database.TcaDb
import org.jetbrains.anko.doAsync

class StudyRoomsViewModel(application: Application): AndroidViewModel(application) {
    private val db = TcaDb.getInstance(application.applicationContext)
    private val repository = StudyRoomsRepository(db)

    val current: LiveData<StudyRoomGroup> = repository.currentRoomGroup

    private val _alternatives = MediatorLiveData<List<StudyRoomGroup>>()
    val alternatives: LiveData<List<StudyRoomGroup>> = _alternatives;

    private val _error = MutableLiveData<Int>()
    val error: LiveData<Int> = _error

    private val _refreshing = MutableLiveData<Boolean>(false)
    val refreshing = _refreshing

    init {
        _alternatives.addSource(repository.groups) {
            repository.currentRoomGroup.value?.apply {
                _alternatives.value = it.filter { it.compareTo(this) != 0 }
            }
        }

        _alternatives.addSource(repository.currentRoomGroup) {
            repository.groups.value?.apply {
                _alternatives.value = this.filter { e -> e.compareTo(it) != 0 }
            }
        }
    }

    fun selectAlternative(position: Int) {
        val currentValue = alternatives.value

        val alternativesValue = alternatives.value
        if (alternativesValue == null || position >= alternativesValue.size) {
            // TODO error
            return
        }

        repository.selectCurrent(alternativesValue[position].id)
    }

    fun refresh() {
        refreshing.value = true
        doAsync {
            repository.refresh()
            refreshing.value = false
        }
    }
}