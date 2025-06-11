package com.example.contacts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.databinding.FragmentEditBinding
import com.example.contacts.room.Contacts
import com.example.contacts.viewmodel.ContactViewModel


class EditFragment : Fragment(R.layout.fragment_edit) {

    private var _binding : FragmentEditBinding? = null
    private val binding get() = _binding!!
    private val args : EditFragmentArgs by navArgs()
    private val contactViewModel : ContactViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val id = args.contacts.id
        val name = args.contacts.name
        val number = args.contacts.number

        binding.etName.setText(name)
        binding.etNumber.setText(number)

        binding.btnUpdate.setOnClickListener {
            val updatedName = binding.etName.text.toString()
            val updatedNumber = binding.etNumber.text.toString()
            if(updatedNumber.isNotEmpty() && updatedName.isNotEmpty()){
                val contact = Contacts(id, updatedName, updatedNumber)

                Log.d("UPDATE_DEBUG", "Updating contact: $contact")
                contactViewModel.updateContact(contact)
                Toast.makeText(requireContext(), "Contact updated successfully", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }else{
                Toast.makeText(requireContext(), "Can't update empty fields", Toast.LENGTH_SHORT).show()
            }

        }

    }


    override fun onResume() {
        super.onResume()

        (activity as MainActivity).toolbarTitle.text = "Edit contact"
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}