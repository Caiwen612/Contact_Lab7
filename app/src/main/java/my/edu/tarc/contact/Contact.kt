package my.tarc.mycontact

import androidx.room.Entity
import androidx.room.PrimaryKey

//Define a table structure
@Entity(tableName = "contact")
//Define phone as a primary key
data class Contact (val name: String,
                    @PrimaryKey val phone: String) {

    override fun toString(): String {
        return "$name : $phone"
    }
}
