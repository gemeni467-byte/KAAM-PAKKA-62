package com.example.model

object KaamConstants {
  const val PUBLIC_APP_URL = "https://kaam-connect-2.ai.studio"
  const val OTHER_SKILL_OPTION = "Other (Write Your Own)"
  const val DEFAULT_STATE = "Odisha"
  const val DEFAULT_CITY = "Rourkela"
  const val SIX_HOURS_MS = 6L * 60L * 60L * 1000L

  val INDIA_STATES =
    listOf(
      "Odisha",
      "Bihar",
      "Jharkhand",
      "Uttar Pradesh",
      "Chhattisgarh",
      "West Bengal",
      "Maharashtra",
      "Gujarat",
      "Delhi NCR",
      "Tamil Nadu",
      "Karnataka",
      "Telangana",
      "Rajasthan",
      "Madhya Pradesh",
      "Other",
    )

  val QUICK_CITIES =
    listOf(
      "Rourkela",
      "Kansbahal",
      "Bihar Sharif",
      "Patna",
      "Jamshedpur",
      "Bokaro",
      "Raipur",
      "Angul",
      "Jharsuguda",
      "Surat",
      "Mumbai",
      "Pune",
    )

  val SKILLS_LIST =
    listOf(
      "MIG Welder",
      "Arc Welder",
      "Pipe / Structure Fitter",
      "Gas Cutter",
      "Rigger",
      "Mason (Rajmistri)",
      "Electrician",
      "Plumber",
      "Carpenter",
      "Painter",
      "Helper / Labour",
      OTHER_SKILL_OPTION,
    )

  val FACILITIES_LIST = listOf("Free Stay", "Free Food", "Travel Allowance")

  val QUICK_WORKERS_COUNT_CHIPS = listOf(1, 2, 5, 10, 20)

  val QUICK_CHAT_MESSAGES =
    listOf(
      "When does the work start?",
      "Are food and stay provided?",
      "I can reach the site tomorrow.",
      "Please share the exact site location.",
    )
}

enum class MainTab(val label: String) {
  LIVE_DUTY("Live Duty"),
  WORKERS("Workers List"),
  CONTRACTORS("Contractors List"),
  MY_ACCOUNT("My Account"),
}

enum class LocationScope {
  MY_CITY,
  ALL_INDIA,
}

data class UserSession(
  val fullName: String = "",
  val phone: String = "",
  val preferredState: String = KaamConstants.DEFAULT_STATE,
  val preferredCity: String = KaamConstants.DEFAULT_CITY,
) {
  val isLoggedIn: Boolean
    get() = fullName.isNotBlank() && phone.length == 10
}

data class WorkerProfile(
  val id: String = "",
  val name: String = "",
  val phone: String = "",
  val skill: String = "",
  val experienceYears: Int = 0,
  val dailyRate: Int = 0,
  val registrationType: String = "Individual Worker",
  val teamSize: Int = 1,
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val outstationReady: Boolean = false,
  val isAvailable: Boolean = true,
  val isOnline: Boolean = false,
  val createdAtMs: Long = 0L,
) {
  val isTeamLeader: Boolean
    get() = registrationType.contains("Team Leader", ignoreCase = true) || teamSize > 1
}

data class ContractorProfile(
  val id: String = "",
  val name: String = "",
  val companyName: String = "",
  val phone: String = "",
  val tradesNeeded: List<String> = emptyList(),
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val facilities: List<String> = emptyList(),
  val isHiring: Boolean = true,
  val createdAtMs: Long = 0L,
)

data class AcceptedWorker(
  val name: String = "",
  val phone: String = "",
  val skill: String = "",
  val acceptedAtMs: Long = 0L,
)

data class LiveRequest(
  val id: String = "",
  val contractorName: String = "",
  val contractorPhone: String = "",
  val skill: String = "",
  val requiredCount: Int = 1,
  val confirmedCount: Int = 0,
  val dailyWage: Int = 0,
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val siteAddress: String = "",
  val facilities: List<String> = emptyList(),
  val status: String = "active",
  val acceptedWorkers: List<AcceptedWorker> = emptyList(),
  val reportedByPhones: List<String> = emptyList(),
  val createdAtMs: Long = 0L,
) {
  val isFilled: Boolean
    get() = confirmedCount >= requiredCount

  val isClosed: Boolean
    get() = status.equals("closed", ignoreCase = true)
}
