package com.example.contacts.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.adapter.ContactAdapter
import com.example.contacts.databinding.FragmentContactsBinding
import com.example.contacts.utils.ResultState
import com.example.contacts.viewmodel.ContactViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private var _binding : FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel : ContactViewModel by activityViewModels()
    private val phone_call = android.Manifest.permission.CALL_PHONE
    private val permissions = mutableListOf(phone_call)
    private var tempNumber : String = ""

    private lateinit var adapter : ContactAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = ContactAdapter(
            onClick = { onClickedItem ->
               checkPermissions(onClickedItem.number)

        },
            longClick = {longClickedItem ->
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("Edit/Delete contact")
                dialog.setMessage("Do you want to edit or delete the contact?")
                dialog.setPositiveButton("Delete"){DialogInterface, which ->
                    contactViewModel.deleteContact(longClickedItem)
                    Toast.makeText(requireContext(), "Contact deleted successfully", Toast.LENGTH_SHORT).show()
                }
                dialog.setNeutralButton("Edit"){DialogInterface, which ->
                    val action = ContactsFragmentDirections.actionContactsFragmentToEditFragment(longClickedItem)
                    findNavController().navigate(action)
                }
                dialog.setNegativeButton("Cancel"){DialogInterface, which ->
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
                val alertDialog = dialog.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            })

        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        contactViewModel.getContacts()

        viewLifecycleOwner.lifecycleScope.launch {
            contactViewModel.contactState.collectLatest {
                when(it){
                    is ResultState.Loading -> {

                    }

                    is ResultState.Error -> {
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Success ->{
                        adapter.submitList(it.data)
                    }
                    else -> Unit
                }
            }
        }



        binding.btnAddContacts.setOnClickListener {
            findNavController().navigate(R.id.addContactFragment)
        }



        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null){
                    filterList(newText)
                }
                else{
                    viewLifecycleOwner.lifecycleScope.launch {
                        contactViewModel.contactState.collectLatest {
                            when(it){
                                is ResultState.Success -> {
                                    adapter.submitList(it.data)
                                }

                                is ResultState.Loading -> {

                                }

                                is ResultState.Error -> {
                                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                                }

                                else -> null
                            }
                        }
                    }

                }
                return true
            }

        })



    }

    private fun checkPermissions(number : String) {
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if(missingPermissions.isNotEmpty()){
            tempNumber = number
            permissionLauncher.launch(missingPermissions.toTypedArray())

        } else{
            makeCall(number)
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
        val allGranted = permissions.all {
            it.value == true
        }

        if(allGranted == true){
            makeCall(tempNumber)
        }
        else{
            Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeCall(number : String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(intent)
    }

    private fun filterList(newText: String) {

        viewLifecycleOwner.lifecycleScope.launch {
            val query = newText.replace(Regex("[^a-zA-Z0-9]"), "")

            contactViewModel.contactState.collectLatest {
                when(it){
                    is ResultState.Success -> {
                        val list = it.data.filter {
                            val cleanNumber = it.number.replace(Regex("[^a-zA-Z0-9]"), "").lowercase()
                            val cleanName = it.name.replace(Regex("[^a-zA-Z0-9]"),"").lowercase()

                            cleanName.contains(query) || cleanNumber.contains(query)
                        }
                        adapter.submitList(list)
                    }

                    else -> Unit
                }
            }


        }

    }


    override fun onResume() {
        super.onResume()

        (activity as MainActivity).toolbarTitle.text = "Contacts"
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}