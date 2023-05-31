package com.example.financialmanager.presentation.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.financialmanager.R
import java.io.File
import java.util.*
import com.example.financialmanager.databinding.FragmentCreateTransactionBinding
import com.example.financialmanager.domain.enums.TransactionType
import com.example.financialmanager.presentation.viewmodels.TransactionsFragmentViewModel
import com.example.financialmanager.utils.createImageFileFromUri
import com.example.financialmanager.utils.parseDouble
import com.example.financialmanager.utils.snack
import com.example.financialmanager.utils.transformIntoDatePicker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateTransactionFragment : Fragment() {
    companion object {
        private val transactionType = listOf("Income", "Expense")
        private val IMAGES_COPY_ALBUM = "FinanceManagerImageCopies"
    }

    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding: FragmentCreateTransactionBinding get() = _binding!!
    private val viewModel: TransactionsFragmentViewModel by viewModels()

    private var imageFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transactionTypeAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_autocomplete_layout,
            transactionType
        )

        with(binding) {
            etTransactionType.setAdapter(transactionTypeAdapter)

            // transform TextInputEditText into DatePicker
            etWhen.transformIntoDatePicker(
                requireContext(),
                "dd/MM/yyyy",
                Date(),
            )

            btnSelectImage.setOnClickListener {
                openGallery()
            }

            btnSaveTransaction.setOnClickListener {
                binding.apply {
                    val title = binding.etTitle.text.toString()
                    val amount = parseDouble(binding.etAmount.text.toString())
                    val transactionType = binding.etTransactionType.text.toString()
                    val date = binding.etWhen.text.toString()

                    when {
                        title.isEmpty() -> {
                            this.etTitle.error = "Title must not be empty"
                        }
                        amount.isNaN() -> {
                            this.etAmount.error = "Amount must not be empty"
                        }
                        transactionType.isEmpty() -> {
                            this.etTransactionType.error = "Transaction type must not be empty"
                        }
                        date.isEmpty() -> {
                            this.etWhen.error = "Date must not be empty"
                        }
                        else -> {
                            viewModel.insertTransaction(
                                name = title,
                                amount = amount,
                                type = TransactionType.valueOf(transactionType),
                                imagePath = imageFile?.absolutePath,
                            ).run {
                                binding.root.snack(
                                    string = R.string.successfully_saved
                                )

                                val fragment = TransactionsFragment()
                                parentFragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container_view, fragment)
                                    .commit()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getGalleryImageResultLauncher.launch(gallery)
    }

    private var getGalleryImageResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult( ActivityResultContracts.StartActivityForResult()

        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                if (data != null) {
                    val imageUri = data.data
                    imageFile = createImageFileFromUri(requireContext(), imageUri!!, IMAGES_COPY_ALBUM)
                    if (imageFile == null) {
                        Toast.makeText(requireContext(), "img is null", Toast.LENGTH_SHORT)
                    }
                    else {
                        Glide.with(requireContext())
                            .load(imageFile)
                            .centerCrop()
                            .listener(object: RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }
                            })
                            .into(binding.ivPreview)
                    }
                }
            }
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}