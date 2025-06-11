package com.example.contacts

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.contacts.databinding.ActivityMainBinding
import com.example.contacts.repository.ContactRepository
import com.example.contacts.room.ContactDatabase
import com.example.contacts.viewmodel.ContactVMFactory
import com.example.contacts.viewmodel.ContactViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var toolbarTitle :  TextView

    private lateinit var contactViewModel : ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        toolbarTitle = binding.toolbarTitle
        setSupportActionBar(binding.toolbar)

        val database = ContactDatabase.getDatabase(this)
        val contactRepository = ContactRepository(database)
        contactViewModel = ViewModelProvider(this, ContactVMFactory(contactRepository))[ContactViewModel::class]



        binding.bottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.contacts -> {
                    findNavController(R.id.fragmentContainerView).navigate(R.id.contactsFragment)
                }

                R.id.dialler -> {
                    findNavController(R.id.fragmentContainerView).navigate(R.id.diallerFragment)
                }
            }
            true
        }
    }
}