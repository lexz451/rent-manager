package cu.lexz451.rentmanager.utils

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import cu.lexz451.rentmanager.data.ManagerDatabase


class ViewModelFactory constructor(
    private val db: ManagerDatabase,
    owner: SavedStateRegistryOwner,
    args: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, args) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructor = modelClass.getConstructor(db::class.java)
        return try {
            constructor.newInstance(db)
        } catch (e: Exception) {
            error(e.message ?: "Invalid ViewModel constructor")
        }
    }
}

class ViewModelDetailFactory constructor(
    private val id: Long,
    private val db: ManagerDatabase,
    owner: SavedStateRegistryOwner,
    args: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, args) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        val constructor = modelClass.getConstructor(id::class.java,db::class.java)
        return try {
            constructor.newInstance(id, db)
        } catch (e: Exception) {
            error(e.message ?: "Invalid ViewModel constructor")
        }
    }
}

