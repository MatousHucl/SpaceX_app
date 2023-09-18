package com.example.x

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.x.AppDatabase
import com.example.x.DatabaseDataSets.CompanyDataEntity
import com.example.x.DatabaseDataSets.LaunchesDataEntity
import com.example.x.DatabaseDataSets.RocketDataEntity
import com.example.x.company_dataClasses.CompanyApi
import com.example.x.launches_DataClasses.LaunchesApiItem
import com.example.x.R
import com.example.x.RocketData
import com.example.x.rocket_dataClasses.RocketDataApiItem
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.spacexdata.com/v5/"
const val BASE_URL2 = "https://api.spacexdata.com/v4/"

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appDb: AppDatabase
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawer_layout)

        appDb = AppDatabase.getInstance(this)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CompanyData()).commit()
            navigationView.setCheckedItem(R.id.CompanyData)
        }
        getRocketData()
        getCompanyData()
        getLaunchData()
    }

//DrawerView navigation
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.CompanyData -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CompanyData()).commit()

            R.id.RocketData -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RocketData()).commit()

            R.id.LaunchesData -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LaunchesData()).commit()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getLaunchData() {
        val retrofitBuilder =
            Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL)
                .build().create(ApiInterface::class.java)
        val retrofitData = retrofitBuilder.getLaunches()

        retrofitData.enqueue(object : Callback<List<LaunchesApiItem>?> {
            override fun onResponse(
                call: Call<List<LaunchesApiItem>?>,
                response: Response<List<LaunchesApiItem>?>
            ) {
                val responseBody = response.body()!!
                println("$responseBody")
                for (myData in responseBody) {
//Adding to database
                    val launchesDataEntity = LaunchesDataEntity(
                        flight_number = myData.flight_number,
                        idLaunch = myData.id,
                        name = myData.name,
                        date_utc = myData.date_utc,
                        success = myData.success,
                        details = myData.details ?: ""
                    )
                    GlobalScope.launch(Dispatchers.IO) {
                        appDb.setDao().insertOrUpdateLaunch(launchesDataEntity)
                    }
                }
            }
            override fun onFailure(call: Call<List<LaunchesApiItem>?>, t: Throwable) {
                println("API request failed with exception: ${t.message}")
            }
        })
    }

    private fun getCompanyData() {
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
                    appDb.setDao().insertOrUpdateCompany(companyDataEntity)
                }
            }

            override fun onFailure(call: Call<CompanyApi>, t: Throwable) {
                println("API request failed with exception: ${t.message}")
            }
        })
    }

    private fun getRocketData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL2)
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getRockets()

        retrofitData.enqueue(object : Callback<List<RocketDataApiItem>> {
            override fun onResponse(
                call: Call<List<RocketDataApiItem>>,
                response: Response<List<RocketDataApiItem>>
            ) {
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
