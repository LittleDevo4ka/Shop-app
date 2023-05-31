package com.example.shopapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopapp.R
import com.example.shopapp.databinding.FragmentQuickAddBinding
import com.example.shopapp.model.dataClasses.OnItemClickListener
import com.example.shopapp.model.dataClasses.Product
import com.example.shopapp.model.dataClasses.ShoppingList
import com.example.shopapp.viewModel.CatalogViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class QuickAddFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentQuickAddBinding
    private lateinit var viewModel: CatalogViewModel
    private var density: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentQuickAddBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[CatalogViewModel::class.java]
        density = resources.displayMetrics.density

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButtonQuickAdd.setOnClickListener {
            findNavController().navigate(R.id.action_quickAddFragment_to_productFragment)
        }

        binding.createShoppingListButton.setOnClickListener {
            val createDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Enter a new file name")
                .setView(R.layout.create_shopping_list_dialog)
                .create()
            createDialog.show()

            val createButton: Button? = createDialog.findViewById(R.id.create_list_tb_dialog)
            val cancelButton: Button? = createDialog.findViewById(R.id.cancel_tb_dialog)
            val textLayout: TextInputLayout? = createDialog.findViewById(R.id.create_list_text_layout_dialog)

            createDialog.setOnDismissListener {
                viewModel.getShoppingLists()
            }

            createButton?.setOnClickListener {
                val newNameStr = textLayout?.editText?.text.toString()
                if (newNameStr.isNotEmpty()) {
                    viewModel.createShoppingList(newNameStr, textLayout, createDialog)
                }
            }

            cancelButton?.setOnClickListener {
                createDialog.dismiss()
            }
        }

        val adapterList: MutableList<ShoppingList> = mutableListOf()
        val tempProduct = viewModel.stateProduct.value
        val myAdapter = if (tempProduct != null) {
            CompactShoppingLists(adapterList, requireContext(), viewModel.storageRef,
                density, this, tempProduct)
        } else {
            CompactShoppingLists(adapterList, requireContext(), viewModel.storageRef,
                density, this, Product()
            )
        }

        binding.compactShoppingListsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.compactShoppingListsRecyclerView.adapter = myAdapter

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

        viewModel.getShoppingLists()
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            if (!email.isNullOrEmpty()) {
                viewModel.getRealtimeDatabase().child("users").child(currentUser.uid)
                    .addChildEventListener(childEventListener)
            }
        }
    }

    override fun onItemClick(id: Int) {

    }

    override fun onItemClick(id: String) {

    }

    override fun onItemClick(isChecked: Boolean, shoppingList: ShoppingList) {
        if (isChecked) {
            val tempProduct = viewModel.stateProduct.value
            if (tempProduct != null) {
                viewModel.addItemIntoShoppingList(shoppingList, tempProduct)
            }
        } else {
            val tempProduct = viewModel.stateProduct.value
            if (tempProduct != null) {
                viewModel.deleteItemFromShoppingList(shoppingList, tempProduct)
            }
        }
    }

    private val childEventListener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            viewModel.getShoppingLists()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            viewModel.getShoppingLists()
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            viewModel.getShoppingLists()
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            viewModel.getShoppingLists()
        }

        override fun onCancelled(error: DatabaseError) {
            println("Error, something went wrong")
        }

    }
}