package com.DimasKach.bulletinboard.fragments

import android.app.Activity
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.get
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.activity.EditAdsAct
import com.DimasKach.bulletinboard.databinding.ListImageFragmentBinding
import com.DimasKach.bulletinboard.dialoghelper.ProgressDialog
import com.DimasKach.bulletinboard.utils.AdapterCallback
import com.DimasKach.bulletinboard.utils.ImageManager
import com.DimasKach.bulletinboard.utils.ImagePicker
import com.DimasKach.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(
    private val fragCloseInterface: FragmentCloseInterface,
    private val newList: ArrayList<String>?,
) : BaseAdsFrag(), AdapterCallback {
    val adapter = SelectImageRvAdapter(this)
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null
    private var addItem: MenuItem? = null
    lateinit var binding: ListImageFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ListImageFragmentBinding.inflate(layoutInflater)
        adView = binding.adView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        binding.apply {
            touchHelper.attachToRecyclerView(rcViewSelectImage)
            rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
            rcViewSelectImage.adapter = adapter
            if (newList != null) resizeSelectedImage(newList, true)
        }


    }

    override fun onItemDelete() {
        addItem?.isVisible = true
    }

    fun upDateAdapterFromEdit(bitmapList: List<Bitmap>) {
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    override fun onClose() {
        super.onClose()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this@ImageListFragment)?.commit()

    }

    private fun resizeSelectedImage(newList: ArrayList<String>, needClear: Boolean) {
        job = CoroutineScope(Dispatchers.Main).launch {
            val dialog = ProgressDialog.createProgressDialog(activity as Activity)
            val bitmapList = ImageManager.imageResize(newList)
            dialog.dismiss()
            adapter.updateAdapter(bitmapList, needClear)
            if (adapter.mainArray.size > 2) addItem?.isVisible = false
        }
    }

    private fun setUpToolBar() {

        binding.apply {
            tb.inflateMenu(R.menu.menu_choose_image)
            val deleteItem = tb.menu.findItem(R.id.id_delete_image)
            addItem = tb.menu.findItem(R.id.id_add_image)
            tb.setNavigationOnClickListener {
                showInterAd()
            }
            deleteItem.setOnMenuItemClickListener {
                adapter.updateAdapter(ArrayList(), true)
                addItem?.isVisible = true
                true
            }
            addItem?.setOnMenuItemClickListener {
                val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
                ImagePicker.launcher(activity as EditAdsAct, (activity as EditAdsAct).launcherMultiselectImage, imageCount )
                true
            }
        }
    }

    fun upDateAdapter(newList: ArrayList<String>) {
        resizeSelectedImage(newList, false)
    }

    fun setSingleImage(uri: String, pos: Int) {
        var pBar = binding.rcViewSelectImage[pos].findViewById<ProgressBar>(R.id.pBar)
        job = CoroutineScope(Dispatchers.Main).launch {
            pBar.visibility = View.VISIBLE
            val bitmapList = ImageManager.imageResize(listOf(uri))
            pBar.visibility = View.GONE
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyDataSetChanged()
        }
    }

}