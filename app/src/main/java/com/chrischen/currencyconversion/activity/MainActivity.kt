package com.chrischen.currencyconversion.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chrischen.currencyconversion.R
import com.chrischen.currencyconversion.dagger.DaggerMainComponent
import com.chrischen.currencyconversion.viewmodel.MainViewModel
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val component = DaggerMainComponent.create()
        component.inject(this)
    }

    override fun onStart() {
        super.onStart()
        viewModel.onRefresh()
    }
}
