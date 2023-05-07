package my.edu.tarc.contact

import android.content.Context
import android.content.SharedPreferences
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import my.edu.tarc.contact.databinding.FragmentFirstBinding
import my.edu.tarc.mycontact.WebDB
import my.tarc.mycontact.Contact
import my.tarc.mycontact.ContactAdapter
import my.tarc.mycontact.ContactViewModel
import my.tarc.mycontact.RecordClickListener
import org.json.JSONArray
import org.json.JSONObject
import java.net.UnknownHostException

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

        //Add observer of this data, if the data had change, it will update the ui
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
        //Upload
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
        //Download
        if(menuItem.itemId == R.id.action_download){
            //TODO - Download records from web server
            downloadContact(requireActivity(),getString(R.string.url_server) + getString(R.string.url_read))
        }
        return true
    }

    fun downloadContact(context: Context, url: String){
        binding.progressBar.isVisible = true
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Process the JSON
                try {
                    if (response != null) {
                        val strResponse = response.toString()
                        val jsonResponse = JSONObject(strResponse)
                        val jsonArray: JSONArray = jsonResponse.getJSONArray("records")
                        val size: Int = jsonArray.length()

                        if(contactViewModel.contactList.value?.isNotEmpty()!!){
                            //Trust the data from server
                            contactViewModel.deleteAl()
                        }

                        for (i in 0..size - 1) {
                            var jsonContact: JSONObject = jsonArray.getJSONObject(i)
                            var contact = Contact(
                                jsonContact.getString("name"),
                                jsonContact.getString("contact")
                            )
                            contactViewModel.insertContact(Contact(contact?.name!!, contact?.phone!!))
                        }
                        Toast.makeText(context, "$size record(s) downloaded", Toast.LENGTH_SHORT).show()
                        binding.progressBar.isVisible = false
                    }
                }catch (e: UnknownHostException){
                    Log.d("ContactRepository", "Unknown Host: %s".format(e.message.toString()))
                    binding.progressBar.isVisible = false
                }
                catch (e: Exception) {
                    Log.d("ContactRepository", "Response: %s".format(e.message.toString()))
                    binding.progressBar.isVisible = false
                }
            },
            { error ->
                Log.d("ContactRepository", "Error Response: %s".format(error.message.toString()))
            },
        )

        //Volley request policy, only one time request
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
            0, //no retry
            1f
        )

        // Access the RequestQueue through your singleton class.
        WebDB.getInstance(context).addToRequestQueue(jsonObjectRequest)
    }


    override fun onRecordClickListener(index: Int) {
        contactViewModel.selectedIndex = index
        findNavController().navigate(R.id.nav_second)

    }
}