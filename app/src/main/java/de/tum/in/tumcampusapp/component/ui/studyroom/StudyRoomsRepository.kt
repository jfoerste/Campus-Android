package de.tum.`in`.tumcampusapp.component.ui.studyroom

import androidx.lifecycle.MutableLiveData
import de.tum.`in`.tumcampusapp.component.ui.studyroom.StudyRoomDao
import de.tum.`in`.tumcampusapp.component.ui.studyroom.StudyRoomGroupDao
import de.tum.`in`.tumcampusapp.component.ui.studyroom.model.StudyRoom
import de.tum.`in`.tumcampusapp.component.ui.studyroom.model.StudyRoomGroup
import de.tum.`in`.tumcampusapp.database.TcaDb
import org.jetbrains.anko.doAsync

class StudyRoomsRepository (db: TcaDb) {
    private val roomsDao: StudyRoomDao = db.studyRoomDao()
    private val groupsDao: StudyRoomGroupDao = db.studyRoomGroupDao()

    val groups = MutableLiveData<List<StudyRoomGroup>>()
    val currentRoomGroup = MutableLiveData<StudyRoomGroup>()

    init {
        val allRoomGroups = groupsDao.all.sorted()
        groups.value = allRoomGroups
        if (allRoomGroups.isNotEmpty()) {
            val group = allRoomGroups[0]
            group.rooms = roomsDao.getAll(group.id).sorted()
            currentRoomGroup.value = group
        }
    }

    fun selectCurrent(id: Int) {
        val group = groupsDao.get(id)
        group.rooms = roomsDao.getAll(group.id).sorted()
        currentRoomGroup.value = group
    }

    fun updateDatabase(groups: List<StudyRoomGroup>, callback: () -> Unit) {
        doAsync {
            groupsDao.removeCache()
            roomsDao.removeCache()

            groupsDao.insert(*groups.toTypedArray())

            groups.forEach { group ->
                group.rooms.forEach { room ->
                    // only insert rooms that have data
                    if (room.code != "" &&
                            room.name != "" &&
                            room.buildingName != "" &&
                            room.id != -1) {
                        roomsDao.insert(room)
                    }
                }
            }

            callback()
        }
    }

    fun getAllStudyRoomsForGroup(groupId: Int): List<StudyRoom> {
        return roomsDao.getAll(groupId).sorted()
    }

    fun getAllStudyGroups(): List<StudyRoomGroup> {
        return groupsDao.all.sorted() // TODO
    }

    fun refresh() {
        // TODO
        Thread.sleep(1000)
    }
}