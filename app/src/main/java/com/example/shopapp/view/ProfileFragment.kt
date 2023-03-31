package com.example.shopapp.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentProfileBinding
import com.example.shopapp.databinding.LogInProfileBinding
import com.example.shopapp.databinding.RegisterProfileBinding
import com.example.shopapp.databinding.StartProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addStartProfile()
    }

    private fun addStartProfile() {

        val addStartProfileBinding = StartProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        addStartProfileBinding.registerButtonStartProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            addRegisterProfile()
        }

        addStartProfileBinding.logInButtonStartProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            addLogInProfile()
        }

        binding.placeForProfile.addView(addStartProfileBinding.root)
    }

    private fun addLogInProfile() {
        val addLogInProfileBinding = LogInProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        addLogInProfileBinding.registerButtonLogInProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            addRegisterProfile()
        }

        binding.placeForProfile.addView(addLogInProfileBinding.root)
    }

    private fun addRegisterProfile() {
        val addRegisterProfileBinding = RegisterProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        addRegisterProfileBinding.passwordEdittextRegisterProfile
            .addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0 != null) {
                    if(p0.isNotEmpty()) {
                        addRegisterProfileBinding.repeatPasswordLayoutRegisterProfile.isEnabled = true
                    } else {
                        addRegisterProfileBinding.repeatPasswordEdittextRegisterProfile
                            .setText("")

                        addRegisterProfileBinding.repeatPasswordLayoutRegisterProfile
                            .isEnabled = false
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        addRegisterProfileBinding.repeatPasswordLayoutRegisterProfile.isEnabled = false

        addRegisterProfileBinding.logInButtonRegisterProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            addLogInProfile()
        }

        binding.placeForProfile.addView(addRegisterProfileBinding.root)
    }

    private fun addProfile() {

    }
}