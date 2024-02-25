package com.model.weather.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.*
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.model.weather.R
import com.model.weather.model.APIResponse
import com.model.weatherapplication.models.DataList
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.main_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
        private val TAG = "LocationProvider"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var cityName: TextView
    private lateinit var todayTemp: TextView
    private lateinit var loadingView: ImageView
    private lateinit var linearLayout: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var mainLayout: LinearLayoutCompat
    private lateinit var slidePanel: SlidingUpPanelLayout
    val dayMap: LinkedHashMap<String, ArrayList<Double>> = LinkedHashMap()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.main_fragment, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        view.rv.layoutManager = LinearLayoutManager(context)
        weatherAdapter = WeatherAdapter(requireContext(), dayMap);
        view.rv.adapter = weatherAdapter
        todayTemp = view.findViewById(R.id.avrg_tmp)
        cityName = view.findViewById(R.id.city_name)
        loadingView = view.findViewById(R.id.loading_view)
        linearLayout = view.findViewById(R.id.error_layout)
        retryButton = view.findViewById(R.id.button_retry)
        mainLayout = view.findViewById(R.id.main)
        slidePanel = view.findViewById(R.id.sliding_layout)
        slidePanel.shadowHeight = 0
        slidePanel.isTouchEnabled = false
        slidePanel.anchorPoint = 0.5f
        slidePanel.minimumHeight = 0
        slidePanel.panelHeight = 100

        val a = AnimationUtils.loadAnimation(context, R.anim.progress_anim)
        a.duration = 1000
        loadingView.startAnimation(a)
        if (!checkPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions()
            }
        } else {
            getLastLocation()
        }

        retryButton.setOnClickListener {
            getLastLocation()
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }


    fun getData(lat: Double, lon: Double) {

        viewModel.getTemp(lat, lon, resources.getString(R.string.api_key), "metric").observe(
                viewLifecycleOwner,
                Observer {
                    when (it.status) {
                        APIResponse.Status.LOADING -> {
                            loadingView.visibility = View.VISIBLE
                            linearLayout.visibility = View.GONE
                            mainLayout.visibility = View.GONE
                        }
                        APIResponse.Status.SUCCESS -> {
                            loadingView.clearAnimation()
                            loadingView.visibility = View.GONE
                            linearLayout.visibility = View.GONE
                            mainLayout.visibility = View.VISIBLE
                            cityName.text = it.data!!.city!!.name
                            setData(it.data!!.list!!)
                        }
                        APIResponse.Status.ERROR -> {
                            loadingView.clearAnimation()
                            loadingView.visibility = View.GONE
                            linearLayout.visibility = View.VISIBLE
                            mainLayout.visibility = View.GONE
                        }
                    }
                })
    }

    private fun setData(list: List<DataList>) {
        dayMap.clear()
        list.forEach {
            if (dayMap.containsKey(getDay(it.dt!!))) {
                dayMap[getDay(it.dt)]!!.add(it.main!!.temp!!)
            } else {
                val temps: ArrayList<Double> = ArrayList<Double>()
                temps.add(it.main!!.temp!!)
                dayMap[getDay(it.dt)] = temps
            }
        }
        val indexes: List<String> = ArrayList<String>(dayMap.keys)
        // val calendar = Calendar.getInstance()
        todayTemp.text = resources.getString(R.string.deg, dayMap[indexes[0]]!!.average().roundToInt().toString())
        weatherAdapter.notifyDataSetChanged()
        slidePanel.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
    }


    private fun getDay(timeStamp: Long): String {
        val calendar = Calendar.getInstance()
        val tz = TimeZone.getDefault()
        calendar.timeInMillis = timeStamp * 1000
        calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.timeInMillis))
        val sdf = SimpleDateFormat("EEEE")
        val currenTimeZone = calendar.time as Date
        return sdf.format(currenTimeZone)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient?.lastLocation!!.addOnCompleteListener() { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                Log.e("lat lon ---- ", "${lastLocation!!.latitude} --- ${lastLocation!!.longitude}")
                getData(lastLocation!!.latitude, lastLocation!!.longitude)
            } else {
                Log.w(MainFragment.TAG, "getLastLocation:exception", task.exception)
                showMessage("No location detected. Make sure location is enabled on the device.")
            }
        }
    }

    private fun showMessage(string: String) {
        Toast.makeText(requireActivity(), string, Toast.LENGTH_LONG).show()
    }

    private fun showSnackbar(
            mainTextStringId: String, actionStringId: String,
            listener: View.OnClickListener
    ) {
        Toast.makeText(requireActivity(), mainTextStringId, Toast.LENGTH_LONG).show()
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                MainFragment.REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (shouldProvideRationale) {
            Log.i(
                    MainFragment.TAG,
                    "Displaying permission rationale to provide additional context."
            )
            showSnackbar("Location permission is needed for core functionality", "Okay",
                    View.OnClickListener {
                        startLocationPermissionRequest()
                    })
        } else {
            Log.i(MainFragment.TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(MainFragment.TAG, "onRequestPermissionResult")
        if (requestCode == MainFragment.REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                    Log.i(MainFragment.TAG, "User interaction was cancelled.")
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission granted.
                    getLastLocation()
                }
                else -> {
                    showSnackbar("Permission was denied", "Settings",
                            View.OnClickListener {
                                // Build intent that displays the App settings screen.
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                val uri = Uri.fromParts(
                                        "package",
                                        Build.DISPLAY, null
                                )
                                intent.data = uri
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            }
                    )
                }
            }
        }
    }


}