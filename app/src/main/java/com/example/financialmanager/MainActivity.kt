package com.example.financialmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.example.financialmanager.databinding.ActivityMainBinding
import com.example.financialmanager.presentation.fragments.WelcomeFragment
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.add

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<WelcomeFragment>(binding.fragmentContainerView.id)
            }
        }
    }
}