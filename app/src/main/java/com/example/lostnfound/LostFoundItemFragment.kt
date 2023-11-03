package com.example.lostnfound

import android.widget.Toast
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.jvm.Throws

class LostFoundItemFragment : Fragment() {

    private lateinit var mItem: LostFoundItem
    private lateinit var mTitleField: EditText
    private lateinit var mDateButton: Button
    private lateinit var mTimeButton: Button
    private lateinit var mLocationField: EditText
    private lateinit var mFoundCheckbox: CheckBox
    private lateinit var mCommentField: EditText
    private lateinit var mPhotoButton: ImageButton
    private lateinit var mPhotoView: ImageView

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val DIALOG_DATE = "DialogDate"
        private const val DIALOG_TIME = "DialogTime"
        private const val REQUEST_DATE = 0
        private const val REQUEST_TIME = 1
        private const val SELECT_IMAGE_REQUEST = 2
        private const val TAKE_IMAGE_REQUEST = 3
        private const val TAKE_PHOTO = "Take Photo"
        private const val CHOOSE_PHOTO = "Choose Photo"
        private const val CANCEL = "Cancel"

        fun newInstance(itemId: UUID): LostFoundItemFragment {
            val args = Bundle()
            args.putSerializable(ARG_ITEM_ID, itemId)
            val fragment = LostFoundItemFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = requireArguments().getSerializable(ARG_ITEM_ID) as UUID?
        mItem = LostFoundItemLab.get(requireActivity()).getItem(itemId!!)!!
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_item, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_item -> {
                LostFoundItemLab.get(requireActivity()).removeItem(mItem)
                requireActivity().finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_lostfound_item, container, false)

        mTitleField = v.findViewById(R.id.item_title)
        mTitleField.setText(mItem.mTitle)
        mTitleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                mItem.mTitle = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        mDateButton = v.findViewById(R.id.lost_date)
        updateDate()
        mDateButton.setOnClickListener {
            val manager: FragmentManager? = fragmentManager
            val dialog: DatePickerFragment =
                DatePickerFragment.newInstance(mItem.mDate)
            dialog.setTargetFragment(this@LostFoundItemFragment, REQUEST_DATE)
            dialog.show(manager!!, DIALOG_DATE)
        }

        mTimeButton = v.findViewById(R.id.lost_time)
        updateTime()
        mTimeButton.setOnClickListener {
            val manager: FragmentManager? = fragmentManager
            val dialog: TimePickerFragment =
                TimePickerFragment.newInstance(mItem.mTime)
            dialog.setTargetFragment(this@LostFoundItemFragment, REQUEST_TIME)
            dialog.show(manager!!, DIALOG_TIME)
        }

        mLocationField = v.findViewById(R.id.lostLocation)
        mLocationField.setText(mItem.mLocation)
        mLocationField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                mItem.mLocation = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        mFoundCheckbox = v.findViewById(R.id.item_found)
        mFoundCheckbox.isChecked = mItem.mFound
        mFoundCheckbox.setOnCheckedChangeListener { _, isChecked ->
            mItem.mFound = isChecked
        }

        mCommentField = v.findViewById(R.id.comment)
        mCommentField.setText(mItem.mComment)
        mCommentField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence?,
                i: Int,
                i1: Int,
                i2: Int
            ) {
                mItem.mComment = charSequence.toString()
            }

            override fun afterTextChanged(editable: Editable?) {}
        })

        mPhotoView = v.findViewById(R.id.item_photo)
        val data: ByteArray? = mItem.mItemPhoto
        if (data != null) {
            val bitmap: Bitmap = PhotoUtils.getImage(data)
            mPhotoView.setImageBitmap(bitmap)
        }

        mPhotoButton = v.findViewById(R.id.btn_photo)
        mPhotoButton.setOnClickListener {
            val items = arrayOf<CharSequence>(TAKE_PHOTO, CHOOSE_PHOTO, CANCEL)
            val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
            builder.setTitle("Item Photo")
            builder.setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when (items[i]) {
                    TAKE_PHOTO -> takePhoto()
                    CHOOSE_PHOTO -> selectImage()
                    CANCEL -> dialogInterface.dismiss()
                }
            })
            builder.show()
        }
        val btnSave: Button = v.findViewById(R.id.btn_save)
        btnSave.setOnClickListener {
            saveDetails()
        }
        return v
    }

    private fun updateDate() {
        val df: DateFormat = SimpleDateFormat("E, MMMM dd, YYYY")
        mDateButton.text = df.format(mItem.mDate)
    }

    private fun updateTime() {
        val tf: DateFormat = SimpleDateFormat("hh:mm a")
        mTimeButton.text = tf.format(mItem.mTime)
    }

    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImage: Uri? = data?.data
                mPhotoView.setImageURI(selectedImage)
                try {
                    if (ActivityCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            SELECT_IMAGE_REQUEST
                        )
                    } else {
                        val intent = Intent()
                        intent.type = "image/*"
                        intent.action = Intent.ACTION_GET_CONTENT
                        startActivityForResult(
                            Intent.createChooser(intent, "Select item Photo"),
                            SELECT_IMAGE_REQUEST
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun saveImageToItem(item: LostFoundItem, bitmap: Bitmap) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        item.setPhotoData(byteArrayOutputStream.toByteArray())
    }

    @Throws(IOException::class)
    private fun convertUriToByteArray(contentResolver: ContentResolver, uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val image = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val outputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    private fun takePhoto() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    TAKE_IMAGE_REQUEST
                )
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(
                    Intent.createChooser(intent, "Take item Photo"),
                    TAKE_IMAGE_REQUEST
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun saveDetails() {
        val title = mTitleField.text.toString().trim()
        val location = mLocationField.text.toString().trim()
        val photoDrawable = mPhotoView.drawable
        if (photoDrawable is BitmapDrawable) {
            val bitmap = photoDrawable.bitmap
            if (bitmap != null) {
                saveImageToItem(mItem, bitmap)
            }
        }

        if (title.isEmpty()) {
            mTitleField.error = "Title cannot be empty"
            return
        }

        if (location.isEmpty()) {
            mLocationField.error = "Location cannot be empty"
            return
        }

        // Set the updated values to the mItem object
        mItem.mTitle = title
        mItem.mLocation = location
        mItem.mFound = mFoundCheckbox.isChecked
        mItem.mComment = mCommentField.text.toString()

        // Save the updated item
        LostFoundItemLab.get(requireActivity()).updateItem(mItem)

        // Show a toast message to indicate that the item is saved
        Toast.makeText(requireContext(), "Item saved successfully!", Toast.LENGTH_SHORT).show()

        requireActivity().finish()
    }


    override fun onPause() {
        super.onPause()
        LostFoundItemLab.get(requireActivity()).updateItem(mItem)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            REQUEST_DATE -> {
                val date = data?.getSerializableExtra(DatePickerFragment.EXTRA_DATE) as Date
                mItem.mDate = date
                updateDate()
            }

            REQUEST_TIME -> {
                val time = data?.getSerializableExtra(TimePickerFragment.EXTRA_TIME) as Date
                mItem.mTime = time
                updateTime()
            }

            TAKE_IMAGE_REQUEST -> {
                val bitmap = data?.extras?.get("data") as Bitmap
                mPhotoView.setImageBitmap(bitmap)
                mItem.mItemPhoto = PhotoUtils.getBytes(bitmap)
            }

            SELECT_IMAGE_REQUEST -> {
                val selectedImage: Uri? = data?.data
                selectedImage?.let {
                    try {
                        val byteArray =
                            convertUriToByteArray(requireActivity().contentResolver, selectedImage)
                        mItem.mItemPhoto = byteArray
                        mPhotoView.setImageBitmap(
                            BitmapFactory.decodeByteArray(
                                byteArray,
                                0,
                                byteArray.size
                            )
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}