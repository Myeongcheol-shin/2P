package com.shino72.location.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.shino72.location.MainActivity
import com.shino72.location.R
import com.shino72.location.databinding.ActivitySearchBinding
import com.shino72.location.service.data.kakao.Place
import com.shino72.location.utils.Status
import com.shino72.location.utils.data.Location
import com.shino72.location.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    var placeData : List<Place>? = null
    private lateinit var binding: ActivitySearchBinding
    var listAdapter : ArrayAdapter<String>? = null
    private val searchViewModel : SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        binding.closeBtn.setOnClickListener {
            val finish_intent = Intent(this, MainActivity::class.java)
            finish_intent.putExtra(getString(R.string.search_key), Location(false))
            setResult(9001, finish_intent)
            finish()
        }

        binding.lineLv.setOnItemClickListener { parent, view, position, id ->
            val finish_intent = Intent(this, MainActivity::class.java)
            finish_intent.putExtra(getString(R.string.search_key), Location(true, placeData!![position].place_name,placeData!![position].x, placeData!![position].y))
            setResult(9001, finish_intent)
            finish()
        }

        val searchViewListener : SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query != "")
                    {
                        searchViewModel.getPlace(query!!)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            }

        binding.placeSv.setOnQueryTextListener(searchViewListener)
        binding.placeSv.isSubmitButtonEnabled = true

        listAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
        )

        searchViewModel.place.observe(this) {state ->
            when(state)
            {
                is Status.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.placeSv.isEnabled = false
                    binding.resultCv.visibility = View.GONE
                }
                is Status.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.placeSv.isEnabled = true
                    binding.resultCv.visibility = View.VISIBLE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is Status.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.resultCv.visibility = View.VISIBLE
                    binding.placeSv.isEnabled = true

                    placeData = state.data?.documents
                    listAdapter?.clear()
                    val placeList = mutableListOf<String>()
                    state.data?.documents?.forEach {
                        placeList.add(it.place_name)
                        listAdapter?.add(it.place_name)
                    }
                    listAdapter?.notifyDataSetChanged()
                }
            }
        }
        binding.lineLv.adapter = listAdapter
    }
}