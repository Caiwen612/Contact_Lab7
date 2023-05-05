package my.edu.tarc.contact

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import my.edu.tarc.contact.databinding.FragmentFirstBinding
import my.tarc.mycontact.ContactAdapter
import my.tarc.mycontact.ContactViewModel
import my.tarc.mycontact.RecordClickListener

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), MenuProvider, RecordClickListener {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Refer to the view model created by Main Activity.kt
    //if which fragment u like to use the view model insert this line
    private val contactViewModel: ContactViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        //Let First Fragment to manage the Menu
        val menuHost: MenuHost = this.requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner,
            Lifecycle.State.RESUMED)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //Add an observer

        //make relationship between adapter and recycle view
        val adapter = ContactAdapter(this)
        //insert data to the adapter
        contactViewModel.contactList.observe(
            viewLifecycleOwner,
            Observer {list -> //the list -> default is it, now we change it to the list
                //if the data contactlist had any change, this observer function wil be run
                if(list.isEmpty()){
                    binding.textViewCount.isVisible = true
                    binding.textViewCount.text = getString(R.string.no_record)
                } else{
                    binding.textViewCount.isVisible = false
                }
                adapter.setContact(list)
            }
        )

        binding.recyclerView.adapter = adapter

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        //Do nothing because we did not have menu to inflate
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.action_upload) {
            //TODO - Upload records to the Cloud Database
            //Trust local or remote? or based on version
            val sharedPreferences: SharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
            val id = sharedPreferences.getString(getString(R.string.phone),"")

            if(id.isNullOrEmpty()){
                Toast.makeText(context,getString(R.string.profie_error), Toast.LENGTH_SHORT).show()
            } else{
                contactViewModel.uploadContact(id)
                Toast.makeText(context,getString(R.string.contact_uploaded), Toast.LENGTH_SHORT).show()
            }

        }
        return true
    }

    override fun onRecordClickListener(index: Int) {
        contactViewModel.selectedIndex = index
        findNavController().navigate(R.id.nav_second)

    }
}