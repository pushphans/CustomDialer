package com.example.contacts.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.databinding.FragmentSaveLogsBinding
import com.example.contacts.room.Contacts
import com.example.contacts.viewmodel.ContactViewModel

class SaveLogsFragment : Fragment(R.layout.fragment_save_logs) {
    private var _binding: FragmentSaveLogsBinding? = null
    private val binding get() = _binding!!
    private val contactViewModel: ContactViewModel by activityViewModels()
    private val args: SaveLogsFragmentArgs by navArgs()
    private lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSaveLogsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        number = args.logs.number
        binding.tvNumber.text = number


        binding.btnSave.setOnClickListener {
            val insertedName = binding.tvName.text.toString()

            val contact = Contacts(name = insertedName, number = number)
            contactViewModel.insertContact(contact)
            Toast.makeText(requireContext(), "Contact Saved", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toolbarTitle.text = "Save Contacts"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}