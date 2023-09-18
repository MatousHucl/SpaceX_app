package com.example.x.company_dataClasses

data class CompanyApi(
    val headquarters: Headquarters,
    val links: Links,
    val name: String,
    val founder: String,
    val founded: Int,
    val employees: Int,
    val vehicles: Int,
    val launch_sites: Int,
    val test_sites: Int,
    val ceo: String,
    val cto: String,
    val coo: String,
    val cto_propulsion: String,
    val valuation: Long,
    val summary: String,
    val id: String
)