package com.DimasKach.bulletinboard.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
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
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fbTel.setOnClickListener { call() }
        binding.fbMail.setOnClickListener {sendEmail()  }

    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply{
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
        imageChangeCounter()
    }

    private fun getIntentFromMainAct(){
        ad = intent.getSerializableExtra("AD") as Ad
        if (ad != null) updateUI(ad!!)
    }

    private fun updateUI(ad: Ad){
        ImageManager.fillImageArray(ad, adapter)
        fillTextViews(ad)
    }

    private fun fillTextViews(ad: Ad) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvEmail.text = ad.email
        tvPrice.text = ad.tel
        tvCountry.text = ad.country
        tvCity.text = ad.city
        tvIndex.text = ad.index
        tvWithSent.text = isWithSent(ad.withSent.toBoolean())

    }

    private fun isWithSent(withSent: Boolean): String{
        return if(withSent) "Так" else "Ні"
    }

    private fun call(){
        val callUri = "tel:${ad?.tel}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail(){
        val isSendEmail = Intent(Intent.ACTION_SEND)
        isSendEmail.type = "message/rfc822"
        isSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Оголошення")
            putExtra(Intent.EXTRA_TEXT, "Мене цікавить Ваше оголошення!")
        }
        try {
            startActivity(Intent.createChooser(isSendEmail, "Відкрити за допомогою:"))
        } catch (e: ActivityNotFoundException) {

        }
    }

    private fun imageChangeCounter(){
        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position + 1}/${binding.viewPager.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }



}