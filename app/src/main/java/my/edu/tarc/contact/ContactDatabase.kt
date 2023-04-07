package my.tarc.mycontact

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Pass in a list of entity, we only have one table, if u have more than 1 table, just put , Table2::class
//if u have make changes , u need to increase the version,
// database version compiler will assume u did not have change in database if your version is same
// Connect entity to database
@Database (entities = arrayOf(Contact::class), version = 1, exportSchema = false)

abstract class ContactDatabase: RoomDatabase() {
    //connect database with dao,now, we have the entity and dao connect to database
    abstract fun contactDao(): ContactDao

    companion object{
        //Singleton prevents multiple instances of database opening at the same time
        //Singleton (Design pattern) = Create a class and this class only can one instance
        //exp in here: we only can create ContactDatabase class one times because we using singleton

        //If u create more than 1 database, if u have more database created, that u mean  u did not have data accuracy because u have 2 database
        //Memory also high

        @Volatile //Volatile memory (Ram memory) // can be destroyed, uninstall app, data destroyed
        private var INSTANCE: ContactDatabase? = null

        //Context = context level parameter -> Application (Apps its self)
        fun getDatabase(context: Context) : ContactDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){ //if database exist
                return tempInstance
            }

            //THis is the else condition
            //Run at the same times with the application  ..... I will wait for you
            //Your apps will not continue, until this things finish.
            // Create an instance of the database
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactDatabase::class.java,
                    "contact_db"
                ).build() //Build a database

                INSTANCE = instance
                return instance
            }
        }
    }
}