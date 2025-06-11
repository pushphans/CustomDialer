package com.example.contacts.bottomsheet

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.ActivityNavigatorExtras
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.databinding.FragmentDiallerBinding
import com.example.contacts.databinding.FragmentDiallerSheetBinding
import com.example.contacts.viewmodel.ContactViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class DiallerSheetFragment : BottomSheetDialogFragment(R.layout.fragment_dialler_sheet) {
    private var _binding : FragmentDiallerSheetBinding? = null
    private val binding get() = _binding!!

    private val contactViewModel: ContactViewModel by activityViewModels()

    private lateinit var displayInput: TextView
    private val number = StringBuilder()

    private val phone_call = android.Manifest.permission.CALL_PHONE
    private val permissionsToRequest = mutableListOf(phone_call)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiallerSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.cv1.setOnClickListener {
            number.append("1")
            updateNumber()
        }

        binding.cv2.setOnClickListener {
            number.append("2")
            updateNumber()
        }

        binding.cv3.setOnClickListener {
            number.append("3")
            updateNumber()
        }

        binding.cv4.setOnClickListener {
            number.append("4")
            updateNumber()
        }

        binding.cv5.setOnClickListener {
            number.append("5")
            updateNumber()
        }

        binding.cv6.setOnClickListener {
            number.append("6")
            updateNumber()
        }

        binding.cv7.setOnClickListener {
            number.append("7")
            updateNumber()
        }

        binding.cv8.setOnClickListener {
            number.append("8")
            updateNumber()
        }

        binding.cv9.setOnClickListener {
            number.append("9")
            updateNumber()
        }

        binding.cvAstrix.setOnClickListener {
            number.append("*")
            updateNumber()
        }

        binding.cvHash.setOnClickListener {
            number.append("#")
            updateNumber()
        }

        binding.cv0.setOnClickListener {
            number.append("0")
            updateNumber()
        }

        binding.cvBackspace.setOnClickListener {
            if (number.isNotEmpty()) {
                number.deleteCharAt(number.lastIndex)
                updateNumber()
            }
        }

//        binding.cvSearch.setOnClickListener {
//            val fullNumber = number.toString()
//            if (fullNumber.length == 10) {
//                contactViewModel.searchContact(fullNumber)
//
//            } else {
//                Toast.makeText(requireContext(), "Enter full number first", Toast.LENGTH_SHORT)
//                    .show()
//            }
//        }


        binding.cvCall.setOnClickListener {
            if (number.length == 10) {
                checkPermission()
            }
        }

//        viewLifecycleOwner.lifecycleScope.launch {
//            contactViewModel.searchContactState.collectLatest {
//                when (it) {
//                    is ResultState.Loading -> {}
//
//                    is ResultState.Error -> {
//                        binding.tvName.text = "Not in contacts"
//                    }
//
//                    is ResultState.Success -> {
//                        val contact = it.data
//                        if (contact != null) {
//                            binding.tvName.text = contact.name
//                        } else {
//                            binding.tvName.text = "Not in contacts"
//                        }
//                    }
//
//                    else -> Unit
//                }
//            }
//        }


    }

    private fun checkPermission(){
        val missingPermission = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }
        if(missingPermission.isNotEmpty()){
            permissionLauncher.launch(missingPermission.toTypedArray())
        }else{
            makeCall()
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions ->
        val allGranted = permissions.all {
            it.value == true
        }
        if(allGranted == true){
            makeCall()
        }else{
            Toast.makeText(requireContext(), "Permissions not granted", Toast.LENGTH_SHORT).show()
        }

    }

    private fun makeCall() {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$number")
        }
        startActivity(intent)
    }


    private fun updateNumber() {
        val currentNumber = number.toString()
        binding.tvNumber.text = currentNumber

    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}