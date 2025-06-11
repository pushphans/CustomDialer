package com.example.contacts.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.contacts.MainActivity
import com.example.contacts.R
import com.example.contacts.adapter.LogAdapter
import com.example.contacts.bottomsheet.DiallerSheetFragment
import com.example.contacts.contactlogs.Logs
import com.example.contacts.databinding.FragmentDiallerBinding
import com.example.contacts.viewmodel.ContactViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiallerFragment : Fragment(R.layout.fragment_dialler) {

    private var _binding: FragmentDiallerBinding? = null
    private val binding get() = _binding!!

    private val readLogPermission = Manifest.permission.READ_CALL_LOG
    private val writePermission = Manifest.permission.WRITE_CALL_LOG
    private val permissions = listOf(readLogPermission, writePermission)

    private lateinit var adapter: LogAdapter
    private val viewModel: ContactViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiallerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnDialpad.setOnClickListener {
            val sheet = DiallerSheetFragment()
            sheet.show(parentFragmentManager, "DiallerSheet")
        }

        adapter = LogAdapter(
            onClick = {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:${it.number}")
                }
                startActivity(intent)
            },
            longClick = {
                val dialog = AlertDialog.Builder(requireContext())
                dialog.setTitle("Delete this call log")
                dialog.setMessage("Are you sure you want to delete this call log?")
                dialog.setIcon(R.drawable.baseline_block_24)
                dialog.setPositiveButton("Yes") { _, _ ->
                    deleteLog(it.number)
                }
                dialog.setNegativeButton("No") { _, _ ->
                    Toast.makeText(requireContext(), "Cancelled", Toast.LENGTH_SHORT).show()
                }
                dialog.setNeutralButton("Save") { _, _ ->
                    val action = DiallerFragmentDirections.actionDiallerFragmentToSaveLogsFragment(it)
                    findNavController().navigate(action)
                }
                dialog.create().apply {
                    setCancelable(false)
                    show()
                }
            }
        )

        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        checkPermissions()
    }

    private fun checkPermissions() {
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isNotEmpty()) {
            permissionLauncher.launch(missingPermissions.toTypedArray())
        } else {
            loadLogs()
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
        val allGranted = perms.all { it.value }

        if (allGranted) {
            loadLogs()
        } else {
            Toast.makeText(requireContext(), "Permission Denied", Toast.LENGTH_SHORT).show()
            adapter.submitList(emptyList())
        }
    }

    private fun loadLogs() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            val logs = fetchCallLogs()
            adapter.submitList(logs)
        }
    }

    @SuppressLint("Recycle")
    private suspend fun fetchCallLogs(): List<Logs> {
        val callLogs = mutableListOf<Logs>()
        val seenNumbers = mutableSetOf<String>()

        val cursor = requireContext().contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            arrayOf(CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME, CallLog.Calls.TYPE),
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndex(CallLog.Calls._ID)
            val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)

            while (it.moveToNext()) {
                val id = if (idIndex > -1) it.getString(idIndex) else ""
                val rawNumber = if (numberIndex > -1) it.getString(numberIndex) else ""
                val number = rawNumber.replace("+91", "").replace("\\s".toRegex(), "")

                if (seenNumbers.contains(number)) continue
                seenNumbers.add(number)

                val dbContact = viewModel.getContactByNumberSuspending(number)
                val name = dbContact?.name ?: "Unknown Number"

                val callTypeInt = if (typeIndex > -1) it.getInt(typeIndex) else -1
                val iconRes = when (callTypeInt) {
                    CallLog.Calls.INCOMING_TYPE -> R.drawable.baseline_call_received_24
                    CallLog.Calls.OUTGOING_TYPE -> R.drawable.baseline_call_made_24
                    CallLog.Calls.MISSED_TYPE -> R.drawable.baseline_call_missed_24
                    CallLog.Calls.REJECTED_TYPE, CallLog.Calls.BLOCKED_TYPE -> R.drawable.baseline_block_24
                    else -> R.drawable.baseline_circle_24
                }

                callLogs.add(Logs(id, name, number, iconRes))
            }
        }

        return callLogs
    }

    @SuppressLint("MissingPermission")
    private fun deleteLog(number: String) {
        try {
            val selection = "${CallLog.Calls.NUMBER} LIKE ?"
            val args = arrayOf("%$number%")

            val rowsDeleted = requireContext().contentResolver.delete(
                CallLog.Calls.CONTENT_URI,
                selection,
                args
            )

            if (rowsDeleted > 0) {
                Toast.makeText(requireContext(), "Log deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "No matching log found", Toast.LENGTH_SHORT).show()
            }

            loadLogs()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to delete: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).toolbarTitle.text = "Dialler"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
