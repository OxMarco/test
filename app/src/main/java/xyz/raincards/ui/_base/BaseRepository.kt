package xyz.raincards.ui._base

import javax.inject.Inject
import xyz.raincards.api.BaseDataSource

class BaseRepository @Inject constructor(
//    private val database: AppDatabase
) : BaseDataSource() {

//    private val userDAO = database.userDAO()
    fun clearDatabase() {
//        database.clearAllTables()
    }
}
