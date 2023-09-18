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
import com.example.x.AppDatabase
import com.example.x.DatabaseDataSets.CompanyDataEntity
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.company_dataClasses.CompanyApi
import com.example.x.launches_DataClasses.LaunchesApiItem
import com.example.x.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class CompanyData : Fragment() {

    private lateinit var companyListView: ListView
    private val dataList = ArrayList<String>()
    private lateinit var appDb: AppDatabase
    private lateinit var refreshButton: ImageButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        appDb = AppDatabase.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_company_data, container, false)
        companyListView = view.findViewById(R.id.listView)
        refreshButton = view.findViewById(R.id.refreshButton)

        //Updating adapter
        val adapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.list_item, // Layout for list items
            R.id.textItem, // TextView in list item layout
            dataList // Data source
        )

        //set data from adapter to the listView
        companyListView.adapter = adapter

        getCompanyData()

        refreshButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                appDb.setDao().deleteCompanyData()
                delay(500)
                refreshCompanyData()
            }
        }
        return view
    }

//getting data from database
    private fun getCompanyData() {
        GlobalScope.launch(Dispatchers.IO) {
            val companyDataList = appDb.setDao().getAllCompany()
            val formattedDataList = mutableListOf<String>()

            for (companyData in companyDataList) {
                val formattedEntry = buildString {
                    append("CEO: ${companyData.ceo}\n")
                    append("COO: ${companyData.coo}\n")
                    append("Employees: ${companyData.employees}\n")
                    append("Founded: ${companyData.founded}\n")
                    append("Adress: ${companyData.headquarters.address}, ${companyData.headquarters.city}, ${companyData.headquarters.state}\n")
                    append("Summary: ${companyData.summary}\n")
                }
                formattedDataList.add(formattedEntry)
            }
            withContext(Dispatchers.Main) {
                dataList.clear()
                dataList.addAll(formattedDataList)
                (companyListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()

            }
        }
    }


//getting data from API by refresh button
    private fun refreshCompanyData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL2)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getCompany()

        retrofitData.enqueue(object : Callback<CompanyApi> {
            override fun onResponse(call: Call<CompanyApi>, response: Response<CompanyApi>) {
                val responseBody = response.body()!!
                println("$responseBody")

                val companyDataEntity = CompanyDataEntity(
                    id = responseBody.id,
                    ceo = responseBody.ceo,
                    coo = responseBody.coo,
                    employees = responseBody.employees,
                    founded = responseBody.founded,
                    headquarters = responseBody.headquarters,
                    summary = responseBody.summary ?: ""
                )
                println("$companyDataEntity")

                GlobalScope.launch(Dispatchers.IO) {
                    appDb.setDao().insertCompanyData(companyDataEntity)

                    withContext(Dispatchers.Main) {
                        getCompanyData()
                        (companyListView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                    }
                }
                getCompanyData()

            }

            override fun onFailure(call: Call<CompanyApi>, t: Throwable) {
                println("API request failed with exception: ${t.message}")
            }
        })
    }
}