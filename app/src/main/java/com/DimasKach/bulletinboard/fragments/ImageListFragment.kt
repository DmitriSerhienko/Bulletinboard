package com.DimasKach.bulletinboard.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.databinding.ListImageFragmentBinding
import com.DimasKach.bulletinboard.utils.ImageManager
import com.DimasKach.bulletinboard.utils.ImagePicker
import com.DimasKach.bulletinboard.utils.ItemTouchMoveCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ImageListFragment(private val fragCloseInterface: FragmentCloseInterface, private val newList: ArrayList<String>?): Fragment() {
    lateinit var binding: ListImageFragmentBinding
    val adapter = SelectImageRvAdapter()
    val dragCallback = ItemTouchMoveCallback(adapter)
    val touchHelper = ItemTouchHelper(dragCallback)
    private var job: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ListImageFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()
        touchHelper.attachToRecyclerView(binding.rcViewSelectImage)
        binding.rcViewSelectImage.layoutManager = LinearLayoutManager(activity)
        binding.rcViewSelectImage.adapter = adapter
        if(newList != null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                val bitmapList = ImageManager.imageResize(newList)
                adapter.updateAdapter(bitmapList, true)
            }
        }
    }

    fun upDateAdapterFromEdit(bitmapList : List<Bitmap>){
        adapter.updateAdapter(bitmapList, true)
    }

    override fun onDetach() {
        super.onDetach()
        fragCloseInterface.onFragClose(adapter.mainArray)
        job?.cancel()
    }

    private fun setUpToolBar(){
        binding.tb.inflateMenu(R.menu.menu_choose_image)
        val deleteItem = binding.tb.menu.findItem(R.id.id_delete_image)
        val addItem = binding.tb.menu.findItem(R.id.id_add_image)
        binding.tb.setNavigationOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        deleteItem.setOnMenuItemClickListener {
            adapter.updateAdapter(ArrayList(), true )
            true
        }
        addItem.setOnMenuItemClickListener {
            val imageCount = ImagePicker.MAX_IMAGE_COUNT - adapter.mainArray.size
            ImagePicker.getImages(activity as AppCompatActivity, imageCount , ImagePicker.REQUEST_CODE_IMAGES)
            true
        }
    }

    fun upDateAdapter( newList: ArrayList<String>){
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(newList)
            adapter.updateAdapter(bitmapList, false)
        }

    }
    fun setSingleImage(uri: String, pos: Int){
        job = CoroutineScope(Dispatchers.Main).launch {
            val bitmapList = ImageManager.imageResize(listOf(uri))
            adapter.mainArray[pos] = bitmapList[0]
            adapter.notifyDataSetChanged()
        }

    }
}