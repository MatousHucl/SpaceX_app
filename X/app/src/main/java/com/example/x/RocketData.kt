package com.example.x

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Spinner
import com.example.x.ApiInterface
import com.example.x.AppDatabase
import com.example.x.BASE_URL
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.DatabaseDataSets.RocketDataEntity
import com.example.x.launches_DataClasses.LaunchesApiItem
import com.example.x.rocket_dataClasses.RocketDataApiItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RocketData : Fragment() {

    private lateinit var launchesListView: ListView
    private val dataList = ArrayList<String>()
    private lateinit var appDb : AppDatabase
    private lateinit var refreshButton: ImageButton
    private lateinit var filterSpinner: Spinner
    private val filterOptions = arrayOf("All","Falcon 1", "Falcon 9", "Falcon Heavy", "Starship")
    private var selectedFilter = "All"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appDb = AppDatabase.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_rocket_data, container, false)
        launchesListView = view.findViewById(R.id.listView)
        refreshButton = view.findViewById(R.id.refreshButton)
        filterSpinner = view.findViewById(R.id.filterSpinner)

        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)

        //Updating adapter
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.list_item, // Layout for list items
            R.id.textItem, // TextView in list item layout
            dataList // Data source
        )

        //set data from adapter to the listView
        launchesListView.adapter = adapter

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filterSpinner.adapter = adapterSpinner

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilter = filterOptions[position]
                filterUpdate(selectedFilter)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        filterUpdate(selectedFilter)

        refreshButton.setOnClickListener {
            // Implement the logic to refresh the data here
            GlobalScope.launch(Dispatchers.IO) {
                appDb.setDao().deleteAllLaunches()
                getRocketData()

            }
        }
        return view
    }


    private fun filterUpdate(selectedFilter: String) {
        //getting proper data from database
        GlobalScope.launch(Dispatchers.IO) {
            val data: List<RocketDataEntity> = when (selectedFilter) {
                "All" -> appDb.setDao().getAllRocket()
                "Falcon 1" -> appDb.setDao().getFalcon1()
                "Falcon 9" -> appDb.setDao().getFalcon9()
                "Falcon Heavy" -> appDb.setDao().getFalconHeavy()
                "Starship" -> appDb.setDao().getStarship()
                else -> emptyList()
            }

            //updating dataList
            withContext(Dispatchers.Main) {
                dataList.clear() // Clear existing data
                for (data2 in data) {
                    val rocketInfo = """
                        Name: ${data2.name}
                        Company: ${data2.company}
                        Cost per Launch: ${data2.costPerLaunch}
                        Diameter: ${data2.diameter}
                        Description: ${data2.description}
                       """.trimIndent()
                    dataList.add(rocketInfo)
                }
                // Notify the adapter that the data has changed
                (launchesListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            }
        }
    }


//getting data from API by refresh button
private fun getRocketData() {
    val retrofitBuilder = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL2)
        .build()
        .create(ApiInterface::class.java)

    val retrofitData = retrofitBuilder.getRockets()

    retrofitData.enqueue(object : Callback<List<RocketDataApiItem>> {
        override fun onResponse(call: Call<List<RocketDataApiItem>>, response: Response<List<RocketDataApiItem>>) {
            if (response.isSuccessful) {
                val rocketDataList = response.body() ?: emptyList()
                for (rocketDataItem in rocketDataList) {
                    val rocketDataEntity = RocketDataEntity(
                        name = rocketDataItem.name,
                        id = rocketDataItem.id,
                        company = rocketDataItem.company,
                        costPerLaunch = rocketDataItem.cost_per_launch,
                        diameter = rocketDataItem.diameter,
                        description = rocketDataItem.description ?: ""
                    )
                    println("$rocketDataEntity")

                    GlobalScope.launch(Dispatchers.IO) {
                        appDb.setDao().insertOrUpdateRocket(rocketDataEntity)
                    }
                }
                filterUpdate(selectedFilter)
            } else {
                println("API request failed with code: ${response.code()}")
            }
        }
        override fun onFailure(call: Call<List<RocketDataApiItem>>, t: Throwable) {
            println("API request failed with exception: ${t.message}")
        }
    })
}
}