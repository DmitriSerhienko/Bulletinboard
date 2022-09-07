package com.DimasKach.bulletinboard.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.activity.EditAdsAct
import com.DimasKach.bulletinboard.model.Ad
import com.DimasKach.bulletinboard.databinding.AdListItemBinding
import com.google.firebase.auth.FirebaseAuth

class AdsRcAdapter(val act: MainActivity ): RecyclerView.Adapter<AdsRcAdapter.AdHolder>() {
    val adArray = ArrayList<Ad>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdHolder {
        val binding = AdListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdHolder(binding, act)
    }

    override fun onBindViewHolder(holder: AdHolder, position: Int) {
        holder.setData(adArray[position])
    }

    override fun getItemCount(): Int {
        return adArray.size
    }

    fun upDateAdapter(newList: List<Ad>){
        adArray.clear()
        adArray.addAll(newList)
        notifyDataSetChanged()

    }

    class AdHolder(val binding: AdListItemBinding, val act: MainActivity): RecyclerView.ViewHolder(binding.root) {
        fun setData(ad: Ad) = with(binding) {
            tvDescription.text = ad.description
            tvTitle.text = ad.title
            tvPrice.text = ad.price
            showEditPanel(isOwner(ad))
            ibEditAd.setOnClickListener(onClickEdit(ad))
        }
        private fun onClickEdit(ad: Ad): View.OnClickListener{
            return View.OnClickListener {
                val editIntent = Intent(act, EditAdsAct::class.java).apply {
                    putExtra(MainActivity.EDIT_STATE, true)
                    putExtra(MainActivity.ADS_DATA, ad)
                }
                act.startActivity(editIntent)
            }
        }

        private fun isOwner(ad: Ad): Boolean{
            return ad.uid == act.mAuth.uid
        }

        private fun showEditPanel(isOwner: Boolean){
            if (isOwner){
                binding.editPanel.visibility = View.VISIBLE
            } else {
                binding.editPanel.visibility = View.GONE
            }
        }
    }

}