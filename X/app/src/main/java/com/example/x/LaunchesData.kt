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
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.launches_DataClasses.LaunchesApiItem
import com.example.x.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LaunchesData : Fragment() {

    private lateinit var launchesListView: ListView
    private val dataList = ArrayList<String>()
    private lateinit var appDb : AppDatabase
    private lateinit var refreshButton: ImageButton
    private lateinit var filterSpinner: Spinner
    private val filterOptions = arrayOf("All", "Success", "Failure")
    private var selectedFilter = "All"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appDb = AppDatabase.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_launches_data, container, false)
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
                refreshLaunchesData()

            }
        }
        return view
    }


    private fun filterUpdate(selectedFilter: String) {
        //getting proper data from database
        GlobalScope.launch(Dispatchers.IO) {
            val data: List<LaunchesDataEntity> = when (selectedFilter) {
                "All" -> appDb.setDao().getAllLaunches()
                "Success" -> appDb.setDao().getSuccessfulLaunches()
                "Failure" -> appDb.setDao().getFailedLaunches()
                else -> emptyList()
            }

            //updating dataList
            withContext(Dispatchers.Main) {
                dataList.clear() // Clear existing data
                for (data2 in data) {
                    val launchInfo = """
                        Name: ${data2.name}
                        Flight Number: ${data2.flight_number}
                        Date UTC: ${data2.date_utc}
                        Success: ${data2.success}
                        Details: ${data2.details}
                    """.trimIndent()
                    dataList.add(launchInfo)
                }
                // Notify the adapter that the data has changed
                (launchesListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
            }
        }
    }


//getting data from API by refresh button
    private fun refreshLaunchesData(){

//retrofit set up
        val retrofitBuilder = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build().create(ApiInterface::class.java)
        val retrofitData = retrofitBuilder.getLaunches()

        retrofitData.enqueue(object : Callback<List<LaunchesApiItem>?> {
            override fun onResponse(call: Call<List<LaunchesApiItem>?>, response: Response<List<LaunchesApiItem>?>) {
                val responseBody = response.body()!!
                for (myData in responseBody) {
//Adding to listView
                    val details = myData.details ?: ""
                    val launchesDataEntity = LaunchesDataEntity(
                        flight_number = myData.flight_number,
                        idLaunch = myData.id,
                        name = myData.name,
                        date_utc = myData.date_utc,
                        success = myData.success,
                        details = details
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        appDb.setDao().insertOrUpdateLaunch(launchesDataEntity)
                    }
                }
                filterUpdate(selectedFilter)
            }


            override fun onFailure(call: Call<List<LaunchesApiItem>?>, t: Throwable) {
                println("API request failed with exception: ${t.message}")
            }
        })
    }
}