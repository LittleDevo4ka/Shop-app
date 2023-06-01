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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.*
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.model.dataClasses.ShoppingList
import com.example.shopapp.viewModel.AccountViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.ceil

class ProfileFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var viewModel: AccountViewModel

    private val tag: String = "ProfileFragment:"

    private var ds: Float = 0f

    private var mainShoppingList: ShoppingList? = null

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

        val adapterList: MutableList<ShoppingList> = mutableListOf()
        val myAdapter = BigShoppingLists(adapterList, requireContext(), viewModel.storageRef, ds, this)

        profileBinding.recyclerViewShoppingListsProfile.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        profileBinding.recyclerViewShoppingListsProfile.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateShoppingLists.collect {
                    adapterList.clear()

                    if (it != null) {
                        adapterList.addAll(it)
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
        }


        binding.backButtonProfile.visibility = View.GONE
        val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
        params.marginStart = ceil(16 * ds).toInt()
        binding.wasteidProfileTitleTv.layoutParams = params

        binding.placeForProfile.addView(profileBinding.root)
        viewModel.getShoppingLists()
    }

    private fun addShoppingListLayout() {
        val tempShoppingList = mainShoppingList ?: return

        binding.placeForProfile.removeAllViews()
        binding.wasteidProfileTitleTv.text = tempShoppingList.name
        binding.backButtonProfile.visibility = View.VISIBLE
        val params = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
        params.marginStart = ceil(64 * ds).toInt()
        binding.wasteidProfileTitleTv.layoutParams = params

        val shoppingListBinding = ShoppingListLayoutBinding
            .inflate(layoutInflater, binding.placeForProfile, false)

        val adapterList: MutableList<Product> = mutableListOf()
        val myAdapter = ShoppingListProduct(adapterList, requireContext(), viewModel.storageRef, ds, this)

        shoppingListBinding.recyclerViewShoppingListContent.layoutManager =
            LinearLayoutManager(requireContext())
        shoppingListBinding.recyclerViewShoppingListContent.adapter = myAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.stateShoppingListProducts.collect {
                    adapterList.clear()

                    if (it != null) {
                        adapterList.addAll(it)
                    }
                    myAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.placeForProfile.addView(shoppingListBinding.root)
        viewModel.getAllProductsFromShoppingList(tempShoppingList)

        binding.backButtonProfile.setOnClickListener {
            binding.wasteidProfileTitleTv.text = "Profile"
            binding.backButtonProfile.visibility = View.GONE
            val tempParams = (binding.wasteidProfileTitleTv.layoutParams as ConstraintLayout.LayoutParams)
            tempParams.marginStart = ceil(16 * ds).toInt()
            binding.wasteidProfileTitleTv.layoutParams = tempParams

            addProfile()
        }

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                viewModel.getRealtimeDatabase().child("users").child(currentUser.uid)
                    .addChildEventListener(childEventListener)
            }
        }
    }

    private val childEventListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val tempShoppingList = mainShoppingList
            if (tempShoppingList != null) {
                viewModel.getAllProductsFromShoppingList(tempShoppingList)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            val tempShoppingList = mainShoppingList
            if (tempShoppingList != null) {
                viewModel.getAllProductsFromShoppingList(tempShoppingList)
            }
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            val tempShoppingList = mainShoppingList
            if (tempShoppingList != null) {
                viewModel.getAllProductsFromShoppingList(tempShoppingList)
            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            val tempShoppingList = mainShoppingList
            if (tempShoppingList != null) {
                viewModel.getAllProductsFromShoppingList(tempShoppingList)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error, something went wrong")
        }
    }

    override fun onItemClick(id: Int) {
    }

    override fun onItemClick(id: String) {
    }

    override fun onItemClick(isChecked: Boolean, shoppingList: ShoppingList) {
    }

    override fun onItemClick(shoppingList: ShoppingList) {
        mainShoppingList = shoppingList
        addShoppingListLayout()
    }

    override fun deleteProductFromShoppingList(product: Product) {
        val tempShoppingList = mainShoppingList
        if (tempShoppingList != null) {
            viewModel.deleteItemFromShoppingList(tempShoppingList, product)
        }
    }
}