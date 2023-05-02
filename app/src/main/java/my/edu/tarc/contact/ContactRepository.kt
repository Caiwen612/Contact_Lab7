package my.tarc.mycontact

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

//A class to determine data source

//Where is the best location that we can get the data ? : Local database or online database(remove server)
//shopping cart - let repository decided
class ContactRepository(private val contactDao: ContactDao){

    //Room execute all queries on a separate thread
    // Create a cache copy of data in the DAO
    val allContacts: LiveData<List<Contact>> = contactDao.getAllContact() //get all contact from the database
    //list of live data

    //WorkerThread , if dao is suspend function, here also need suspend function
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(contact: Contact){// launch suspend function, only in coroutine
        contactDao.insert(contact)
    }

    @WorkerThread
    suspend fun update(contact: Contact){
        contactDao.update(contact)
    }

    @WorkerThread
    suspend fun delete(contact: Contact){
        contactDao.delete(contact)
    }

    fun uploadContact(id: String){
        if(allContacts.isInitialized){
            if(!allContacts.value.isNullOrEmpty()){
                val database = Firebase.database("https://contact-76b42-default-rtdb.asia-southeast1.firebasedatabase.app/").reference
                allContacts.value!!.forEach{
                    database.child("user").child(id).child(it.phone).setValue(it)
                }
            }
        }

    }
}