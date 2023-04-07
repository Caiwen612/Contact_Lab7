package my.tarc.mycontact

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

//viewModel is develop to hold ui data
//How to Connect view model with ui level?
//Ans: U pass in the application,so thet connected


//IMPORTANCE : Can do all business logic and data logic in here
class ContactViewModel (application: Application): AndroidViewModel(application) {
    //Define private data (Zhi Yi）
    private val _contactList = MutableLiveData<List<Contact>>()
    //LiveData gives us updated contacts when they change
    var contactList : LiveData<List<Contact>> = _contactList

    private val repository: ContactRepository

    //connect repository to viewmodel

    //The moment u create an instance of contactView model, i want u to run the init function

    init {
        //Connect all the things together, Combine all things
        // Initialize DAO
        val contactDao = ContactDatabase.getDatabase(application).contactDao()
        // Associate DAO to Repository
        repository = ContactRepository(contactDao)
        // Get a copy of contact list from the repository
        contactList = repository.allContacts
    }

    //Any suspend function must using the launch syntax to call

    //Global scope
    //Live cycle scope

    //For view model just view model scope

    //Call a asyn task

    // global scope can run from activity, view model scope is for coroutine
    fun insertContact(contact: Contact) = viewModelScope.launch{
         repository.insert(contact)
    }

    fun updateContact(contact: Contact) = viewModelScope.launch {
        repository.update(contact)
    }

    fun deleteContact(contact: Contact) = viewModelScope.launch {
        repository.delete(contact)
    }
}
