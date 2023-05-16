package com.example.shopapp.view

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isEmpty
import androidx.core.view.marginStart
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.*
import com.example.shopapp.R
import com.example.shopapp.databinding.*
import com.example.shopapp.viewModel.AccountViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: AccountViewModel

    private val tag: String = "ProfileFragment:"

    private var ds: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(requireActivity())[AccountViewModel::class.java]
        ds = resources.displayMetrics.density

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.getFragmentNum().collect {
                    when(it) {
                        0 -> addStartProfile()
                        1 -> addRegisterProfile()
                        2 -> addLogInProfile()
                        3 -> addProfile()
                        else -> Log.i(tag, "Oops, an unexpected fragmentNum: $it")
                    }
                }
            }
        }

        binding.backButtonProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            binding.backButtonProfile.visibility = View.GONE
            val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
            params.marginStart = ceil(16 * ds).toInt()
            binding.wasteidProfileTitleTv.layoutParams = params

            viewModel.setFragmentNum(0)
        }
    }

    private fun addStartProfile() {
        val addStartProfileBinding = StartProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        addStartProfileBinding.registerButtonStartProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            viewModel.setFragmentNum(1)
        }

        addStartProfileBinding.logInButtonStartProfile.setOnClickListener{
            binding.placeForProfile.removeAllViews()

            viewModel.setFragmentNum(2)
        }

        binding.placeForProfile.addView(addStartProfileBinding.root)
    }

    private fun addLogInProfile() {
        val logInProfileBinding = LogInProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        val passwordEditText = logInProfileBinding.passwordEdittextLogInProfile
        val emailEditText = logInProfileBinding.emailEdittextLogInProfile

        emailEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (text.isNotEmpty() && passwordEditText.text.toString().isNotEmpty())  {
                    if(!logInProfileBinding.logInButtonLogInProfile.isEnabled) {
                        logInProfileBinding.logInButtonLogInProfile.isEnabled = true
                    }
                } else {
                    if(logInProfileBinding.logInButtonLogInProfile.isEnabled) {
                        logInProfileBinding.logInButtonLogInProfile.isEnabled = false
                    }
                }
            }
        }
        passwordEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if(text.isNotEmpty() && emailEditText.text.toString().isNotEmpty()) {
                    if(!logInProfileBinding.logInButtonLogInProfile.isEnabled) {
                        logInProfileBinding.logInButtonLogInProfile.isEnabled = true
                    }
                } else {
                    if(logInProfileBinding.logInButtonLogInProfile.isEnabled) {
                        logInProfileBinding.logInButtonLogInProfile.isEnabled = false
                    }
                }
            }
        }

        logInProfileBinding.logInButtonLogInProfile.isEnabled = false
        logInProfileBinding.logInButtonLogInProfile.setOnClickListener{

            viewModel.signInWithEmailAndPassword(emailEditText.text.toString(),
                passwordEditText.text.toString(),
            logInProfileBinding.emailLayoutLogInProfile, logInProfileBinding.passwordLayoutLogInProfile)
        }

        binding.backButtonProfile.visibility = View.VISIBLE
        val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
        params.marginStart = ceil(64 * ds).toInt()
        binding.wasteidProfileTitleTv.layoutParams = params

        binding.placeForProfile.addView(logInProfileBinding.root)
    }

    private fun addRegisterProfile() {
        val registerProfileBinding = RegisterProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        val passwordEditText = registerProfileBinding.passwordEdittextRegisterProfile
        val emailEditText = registerProfileBinding.emailEdittextRegisterProfile
        val repeatPasswordEditText = registerProfileBinding.repeatPasswordEdittextRegisterProfile
        registerProfileBinding.repeatPasswordLayoutRegisterProfile.isEnabled = false

        emailEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                if (registerProfileBinding.emailLayoutRegisterProfile.error != null) {
                    registerProfileBinding.emailLayoutRegisterProfile.error = null
                }

                if (text.isNotEmpty() && repeatPasswordEditText.text.toString().isNotEmpty()) {
                    if(!registerProfileBinding.registerButtonRegisterProfile.isEnabled) {
                        registerProfileBinding.registerButtonRegisterProfile.isEnabled = true
                    }
                } else {
                    if(registerProfileBinding.registerButtonRegisterProfile.isEnabled) {
                        registerProfileBinding.registerButtonRegisterProfile.isEnabled = false
                    }
                }
            }
        }
        passwordEditText.doOnTextChanged { text, _, _, _ ->
            if(text != null) {
                if (registerProfileBinding.passwordLayoutRegisterProfile.error != null) {
                    registerProfileBinding.passwordLayoutRegisterProfile.error = null
                }

                if(text.isNotEmpty()) {
                    registerProfileBinding.repeatPasswordLayoutRegisterProfile.isEnabled = true
                } else {
                    registerProfileBinding.repeatPasswordEdittextRegisterProfile
                        .setText("")

                    registerProfileBinding.repeatPasswordLayoutRegisterProfile
                        .isEnabled = false
                }
            }
        }
        repeatPasswordEditText.doOnTextChanged { text, _, _, _ ->
            if (text != null) {
                val repeatPasswordLayout = registerProfileBinding.repeatPasswordLayoutRegisterProfile

                if (text.toString() != passwordEditText.text.toString()) {
                    registerProfileBinding.repeatPasswordLayoutRegisterProfile.error = "Passwords don't match"
                } else {
                    repeatPasswordLayout.error = null
                }

                if (text.isNotEmpty() && emailEditText.text.toString().isNotEmpty()) {
                    if(!registerProfileBinding.registerButtonRegisterProfile.isEnabled) {
                        registerProfileBinding.registerButtonRegisterProfile.isEnabled = true
                    }
                } else if(registerProfileBinding.registerButtonRegisterProfile.isEnabled) {
                    registerProfileBinding.registerButtonRegisterProfile.isEnabled = false
                }
            }
        }

        registerProfileBinding.registerButtonRegisterProfile.isEnabled = false
        registerProfileBinding.registerButtonRegisterProfile.setOnClickListener{

            viewModel.createUserWithEmailAndPassword(emailEditText.text.toString(),
            passwordEditText.text.toString(),
            registerProfileBinding.emailLayoutRegisterProfile,
                registerProfileBinding.passwordLayoutRegisterProfile,
            registerProfileBinding.repeatPasswordLayoutRegisterProfile)
        }

        binding.backButtonProfile.visibility = View.VISIBLE
        val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
        params.marginStart = ceil(64 * ds).toInt()
        binding.wasteidProfileTitleTv.layoutParams = params

        binding.placeForProfile.addView(registerProfileBinding.root)
    }

    private fun addProfile() {
        binding.placeForProfile.removeAllViews()

        val profileBinding = ProfileBinding.inflate(layoutInflater,
            binding.placeForProfile, false)

        var startMargin = 36 * ds
        val layoutMargin = 176 * ds
        val layoutWidth = 152 * ds
        val layoutHeight = 200 * ds

        for(i in 1 .. 3) {
            val shoppingListCardBinding = ShoppingListCardBinding.inflate(layoutInflater,
                profileBinding.placeForShoppingLists, false)

            val params = ConstraintLayout.LayoutParams(
                ceil(layoutWidth).toInt(), ceil(layoutHeight).toInt()
            )
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            params.marginStart = ceil(startMargin).toInt()
            if(i == 3) {
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.marginEnd = ceil(36 * ds).toInt()
            }
            shoppingListCardBinding.shoppingListCardLayout.layoutParams = params

            profileBinding.placeForShoppingLists.addView(shoppingListCardBinding.root)

            startMargin += layoutMargin
        }

        binding.backButtonProfile.visibility = View.GONE
        val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
        params.marginStart = ceil(16 * ds).toInt()
        binding.wasteidProfileTitleTv.layoutParams = params

        binding.placeForProfile.addView(profileBinding.root)
    }

    override fun onPause() {
        super.onPause()

    }
}