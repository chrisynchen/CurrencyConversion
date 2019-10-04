package com.chrischen.currencyconversion.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.adapter.MainAdapter
import com.chrischen.currencyconversion.dagger.DaggerMainComponent
import com.chrischen.currencyconversion.databinding.ActivityMainBinding
import com.chrischen.currencyconversion.viewmodel.MainViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        private const val ROW_COUNT = 3
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private val adapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val component = DaggerMainComponent.create()
        component.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.vm = viewModel

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, ROW_COUNT)
            adapter = this@MainActivity.adapter
        }

        viewModel.currencyRateItems.observe(this, Observer {
            adapter.setItems(it)
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onRefresh()
    }
}
