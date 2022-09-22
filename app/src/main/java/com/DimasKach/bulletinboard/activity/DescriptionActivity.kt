package com.DimasKach.bulletinboard.activity

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.DimasKach.bulletinboard.adapters.ImageAdapter
import com.DimasKach.bulletinboard.databinding.ActivityDescriptionBinding
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.utils.ImageManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply{
            viewPager2.adapter = adapter
        }
        getIntentFromMainAct()
    }

    private fun getIntentFromMainAct(){
        val ad = intent.getSerializableExtra("AD") as Ad
        updateUI(ad)
    }

    private fun updateUI(ad: Ad){
        fillImageArray(ad)
        fillTextViews(ad)
    }

    private fun fillTextViews(ad: Ad) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvPrice.text = ad.tel
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSent.text = isWithSent(ad.withSent.toBoolean())

    }

    private fun isWithSent(withSent: Boolean): String{
        return if(withSent) "Так" else "Ні"
    }

    private fun fillImageArray(ad: Ad){
        val listUris = listOf(ad.mainImage, ad.image2, ad.image3)
        CoroutineScope(Dispatchers.Main).launch {
            val bitMapList = ImageManager.getBitMapFromUri(listUris)
            adapter.update(bitMapList as ArrayList<Bitmap>)
        }
    }



}