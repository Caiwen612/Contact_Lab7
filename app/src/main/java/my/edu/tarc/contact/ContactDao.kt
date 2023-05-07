package my.tarc.mycontact

import androidx.lifecycle.LiveData
import androidx.room.*

//DAO = Data access object
//-Provide common operation CRUD (Create/Read|Retrieve/Update/Delete )
//Dao must be interface
@Dao
interface ContactDao {

    //Query refer to sql code
    @Query("SELECT * FROM contact ORDER BY name ASC")
    fun getAllContact(): LiveData<List<Contact>>
    //Live data mean u can observe the change of data

    //Suspend
    //Mean: execute the function in a separate thread , separate from the ui thread, asynchronous task

    @Insert(onConflict = OnConflictStrategy.IGNORE)//Insert data, if have conflict just ignore it.
    suspend fun insert(contact: Contact)

    @Update
    suspend fun update(contact: Contact)

    @Delete
    suspend fun delete(contact: Contact)

    //Delete all
    @Query("DELETE FROM contact")
    suspend fun deleteAll()

    //Find by name

    //FInd by phone


}