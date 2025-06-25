package xyz.raincards.ui.main

import javax.inject.Inject
import xyz.raincards.api.Api
import xyz.raincards.api.BaseDataSource

class MainRepository @Inject constructor(
    private val api: Api
//    private val database: AppDatabase
) : BaseDataSource()
