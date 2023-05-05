package my.edu.tarc.contact

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import my.edu.tarc.contact.databinding.FragmentSecondBinding
import my.tarc.mycontact.Contact
import my.tarc.mycontact.ContactViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment(), MenuProvider {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Refer to the view model created by Main Activity.kt
    //if which fragment u like to use the view model insert this line
    private val contactViewModel: ContactViewModel by activityViewModels()

    //ZHI Yi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)

        //Let ProfileFragment to manage the Menu
        val menuHost: MenuHost = this.requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner,
            Lifecycle.State.RESUMED)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Determine the mode of the fragment; Add or Edit
        if(contactViewModel.selectedIndex != -1){ //Edit Mode
            if(contactViewModel.contactList.isInitialized){
                val contact: Contact = contactViewModel.contactList.value!!.get(contactViewModel.selectedIndex)

                with(binding){
                    editTextName.setText(contact.name)
                    editTextPhone.setText(contact.phone)
                    editTextName.requestFocus()
                    editTextPhone.isEnabled = false
                }
            }
        }

        //Extra: below all did no ocur in sir slide
        //ZHIYI
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        // Hide the floating action button
        val fab: View = requireActivity().findViewById(R.id.fab)
        fab!!.isVisible = false
        Log.d("Add Fragment", "onViewCreated")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        contactViewModel.selectedIndex = -1
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.second_menu, menu)
        menu.findItem(R.id.action_settings).isVisible = false
        if(contactViewModel.selectedIndex == -1){//Add
            menu.findItem(R.id.action_delete).isVisible = false
        }

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_save){
            binding.apply{
                val name = editTextName.text.toString()
                val phone = editTextPhone.text.toString()
                val newContact = Contact(name, phone)
                if(contactViewModel.selectedIndex == -1){//Add mode
                    contactViewModel.insertContact(newContact)
                } else{//Edit mode
                    contactViewModel.updateContact(newContact)
                    ///TODO UPDATE Record
                }

//                findNavController().navigateUp()
            }
            //with
            Toast.makeText(context, getString(R.string.contact_saved), Toast.LENGTH_SHORT).show()
        }
        else if(menuItem.itemId == R.id.action_delete){
            //You can also use the with to do?
            val deleteAlertDialog = AlertDialog.Builder(requireActivity())
            deleteAlertDialog.setMessage(R.string.delete_record)
            deleteAlertDialog.setPositiveButton(
                getString(R.string.delete),
                {_,_ -> //dialog,id
                    //Method had should have 2 parameter, we are not using , so replace _
                    val contact = Contact(binding.editTextName.text.toString(),
                    binding.editTextPhone.text.toString())
                    contactViewModel.deleteContact(contact)
                    findNavController().navigateUp()
                }
            )
            deleteAlertDialog.setNegativeButton(
                getString(android.R.string.cancel),
                {
                    _,_ ->
                    //Do nothing here
                }
            )
            deleteAlertDialog.create().show()
        }
        else if(menuItem.itemId == android.R.id.home){
            findNavController().navigateUp()
        }
        return true
    }

}