package com.niedzwiecki.przemyslguide.ui.placeDetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.WindowManager
import com.niedzwiecki.przemyslguide.R
import com.niedzwiecki.przemyslguide.data.model.PlaceOfInterest
import com.niedzwiecki.przemyslguide.databinding.ActivityPlaceDetailsBinding
import com.niedzwiecki.przemyslguide.ui.base.BaseActivity
import com.niedzwiecki.przemyslguide.ui.base.Navigator
import com.niedzwiecki.przemyslguide.ui.base.ViewModel
import com.niedzwiecki.przemyslguide.ui.gallery.GalleryPagerAdapter
import com.niedzwiecki.przemyslguide.ui.main.MainActivity
import com.niedzwiecki.przemyslguide.ui.maps.MapsActivity
import com.niedzwiecki.przemyslguide.util.Utils.convertDpToPixel
import com.squareup.picasso.Picasso

/**
 * Created by niedzwiedz on 10.07.17.
 */

class PlaceDetailsActivity : BaseActivity() {

    private var place: PlaceOfInterest? = null
    private lateinit var placeAdapter: GalleryPagerAdapter

    override fun beforeViews() {
        super.beforeViews()
        setDataBindingEnabled(true)
    }

    override fun afterViews() {
        super.afterViews()
        init()
        setStaticMap()
        toolbar.setTitle(" ")
        setDataToViewModel()
    }

    override fun afterViews(savedInstanceState: Bundle?) {
        super.afterViews(savedInstanceState)
        init()
        setDataToViewModel()
        if (savedInstanceState != null) {
            restoreData(savedInstanceState)
        }
    }

    private fun init() {
        viewDataBinding.viewModel = getViewModel()
        setScreenFlags()
        fetchExtraData()
        placeAdapter = GalleryPagerAdapter(this)
        placeAdapter.setItems(place?.images)
        viewDataBinding.viewPager.adapter = placeAdapter
        fixPagerScroll()
    }

    private fun fixPagerScroll() {
        viewDataBinding.viewPager.setOnTouchListener { view, motionEvent ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            false
        }

        viewDataBinding.viewPager.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                viewDataBinding.viewPager.parent.requestDisallowInterceptTouchEvent(true)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setStaticMap() {
        Picasso.with(this)
                .load("https://maps.googleapis.com/maps/api/staticmap?center=" + place?.lat +
                        "," + place?.lon +
                        "&zoom=14&size=800x450&maptype=roadmap&markers=color:blue%7Clabel:S%7C" + place?.lat +
                        "," + place?.lon + "&key=AIzaSyDthXCUL8-qPlt8F21aQRuMDGSt8WLveB8")
                .resize(convertDpToPixel(400f, this), convertDpToPixel(250f, this))
                .centerCrop()
                .into(viewDataBinding.staticMap)
    }

    private fun fetchExtraData() {
        if (intent.hasExtra(MainActivity.INTEREST_PLACE_KEY)) {
            place = intent.extras!!.getParcelable(MainActivity.INTEREST_PLACE_KEY)
        }
    }

    private fun restoreData(savedInstanceState: Bundle) {
        place = savedInstanceState.getParcelable(INTEREST_PLACE_KEY)
    }

    private fun setScreenFlags() {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    private fun setDataToViewModel() {
        getViewModel().setData(place)
    }

    override fun contentId(): Int {
        return R.layout.activity_place_details
    }

    override fun getViewDataBinding(): ActivityPlaceDetailsBinding {
        return (super.getViewDataBinding() as? ActivityPlaceDetailsBinding)!!
    }

    override fun createViewModel(): ViewModel {
        return PlaceDetailsViewModel(dataManager)
    }

    override fun getViewModel(): PlaceDetailsViewModel {
        return super.getViewModel() as PlaceDetailsViewModel
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(INTEREST_PLACE_KEY, place)
    }

    override fun moveForward(options: Navigator.Options?, vararg data: Any?) {
        super.moveForward(options, *data)
        when (options) {
            Navigator.Options.START_ACTIVITY_WITH_INTENT ->
                startMapActivity(data[0] as PlaceOfInterest)
            Navigator.Options.START_GOOGLE_STREET ->
                openGoogleStreet(data[0] as Float, data[1] as Float)
            Navigator.Options.OPEN_NAVIGATION_MAP ->
                openNavigation(data[0] as Float, data[1] as Float)
        }
    }

    private fun openGoogleStreet(lat: Float, lon: Float) {
        val gmmIntentUri = Uri.parse("google.streetview:cbll=" + lat + "," + lon)
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        startActivity(mapIntent)
    }

    fun openNavigation(lat: Float, lon: Float) {
        val gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon + "&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.`package` = "com.google.android.apps.maps"
        startActivity(mapIntent)
    }

    private fun startMapActivity(place: PlaceOfInterest) {
        val intent = Intent(MapsActivity.getStartIntent(baseContext, place))
        startActivity(intent)
    }

    companion object {

        private val INTEREST_PLACE_KEY = "interestPlaceKey"

        fun getStartIntent(context: Context, place: PlaceOfInterest): Intent {
            val intent = Intent(context, PlaceDetailsActivity::class.java)
            intent.putExtra(MainActivity.INTEREST_PLACE_KEY, place)
            return intent
        }
    }
}
