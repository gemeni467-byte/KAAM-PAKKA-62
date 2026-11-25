package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FirebaseProvider
import com.example.data.KaamConnectRepository
import com.example.data.SessionStore
import com.example.model.ContractorProfile
import com.example.model.KaamConstants
import com.example.model.LiveRequest
import com.example.model.LocationScope
import com.example.model.UserSession
import com.example.model.WorkerProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class KaamFilterState(
  val locationScope: LocationScope = LocationScope.ALL_INDIA,
  val filterState: String = "All States",
  val filterCity: String = "",
  val workerSkillFilter: String = "All Trades",
  val onlyAvailableWorkers: Boolean = false,
  val contractorTradeFilter: String = "All Trades",
  val onlyHiringContractors: Boolean = false,
)

class KaamConnectViewModel(application: Application) : AndroidViewModel(application) {

  private val sessionStore = SessionStore(application)
  private val repository = KaamConnectRepository(FirebaseProvider.getFirestore(application))

  private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
  val messages: SharedFlow<String> = _messages.asSharedFlow()

  private val _filters = MutableStateFlow(KaamFilterState())
  val filters: StateFlow<KaamFilterState> = _filters.asStateFlow()

  private val _isBusy = MutableStateFlow(false)
  val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

  private val _localDutyOnline = MutableStateFlow(false)

  private val _workersLoaded = MutableStateFlow(false)
  val workersLoaded: StateFlow<Boolean> = _workersLoaded.asStateFlow()

  private val _contractorsLoaded = MutableStateFlow(false)
  val contractorsLoaded: StateFlow<Boolean> = _contractorsLoaded.asStateFlow()

  private val _liveRequestsLoaded = MutableStateFlow(false)
  val liveRequestsLoaded: StateFlow<Boolean> = _liveRequestsLoaded.asStateFlow()

  private val _syncError = MutableStateFlow<String?>(null)
  val syncError: StateFlow<String?> = _syncError.asStateFlow()

  val session: StateFlow<UserSession> =
    sessionStore.sessionFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = UserSession(),
    )

  private val skippedIds: StateFlow<Set<String>> =
    sessionStore.skippedRequestIdsFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptySet(),
    )

  private val reportedIds: StateFlow<Set<String>> =
    sessionStore.reportedRequestIdsFlow.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000),
      initialValue = emptySet(),
    )

  val allWorkers: StateFlow<List<WorkerProfile>> =
    repository
      .observeWorkers()
      .catch { err ->
        _syncError.value = err.message ?: "Unable to load workers from cloud."
        _workersLoaded.value = true
      }
      .combine(MutableStateFlow(Unit)) { list, _ ->
        _workersLoaded.value = true
        _syncError.value = null
        list
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
      )

  val allContractors: StateFlow<List<ContractorProfile>> =
    repository
      .observeContractors()
      .catch { err ->
        _syncError.value = err.message ?: "Unable to load contractors from cloud."
        _contractorsLoaded.value = true
      }
      .combine(MutableStateFlow(Unit)) { list, _ ->
        _contractorsLoaded.value = true
        _syncError.value = null
        list
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
      )

  val allLiveRequests: StateFlow<List<LiveRequest>> =
    repository
      .observeLiveRequests()
      .catch { err ->
        _syncError.value = err.message ?: "Unable to load live duty requests."
        _liveRequestsLoaded.value = true
      }
      .combine(MutableStateFlow(Unit)) { list, _ ->
        _liveRequestsLoaded.value = true
        _syncError.value = null
        list
      }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList(),
      )

  val myWorkerProfile: StateFlow<WorkerProfile?> =
    combine(allWorkers, session) { workers, user ->
        if (user.phone.isBlank()) null else workers.firstOrNull { it.phone == user.phone }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  val myContractorProfile: StateFlow<ContractorProfile?> =
    combine(allContractors, session) { contractors, user ->
        if (user.phone.isBlank()) null else contractors.firstOrNull { it.phone == user.phone }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

  val isDutyOnline: StateFlow<Boolean> =
    combine(myWorkerProfile, _localDutyOnline) { profile, local ->
        profile?.isOnline ?: local
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

  val filteredWorkers: StateFlow<List<WorkerProfile>> =
    combine(allWorkers, filters, session) { workers, f, user ->
        workers.filter { w ->
          val matchesScope =
            when (f.locationScope) {
              LocationScope.MY_CITY ->
                w.city.equals(user.preferredCity, ignoreCase = true) ||
                  w.outstationReady && w.state.equals(user.preferredState, ignoreCase = true)
              LocationScope.ALL_INDIA -> {
                val stateMatch =
                  f.filterState == "All States" ||
                    w.state.equals(f.filterState, ignoreCase = true)
                val cityMatch =
                  f.filterCity.isBlank() ||
                    w.city.contains(f.filterCity.trim(), ignoreCase = true)
                stateMatch && cityMatch
              }
            }
          val matchesSkill =
            f.workerSkillFilter == "All Trades" ||
              w.skill.contains(f.workerSkillFilter.trim(), ignoreCase = true)
          val matchesAvailability = !f.onlyAvailableWorkers || w.isAvailable
          matchesScope && matchesSkill && matchesAvailability
        }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val filteredContractors: StateFlow<List<ContractorProfile>> =
    combine(allContractors, filters, session) { contractors, f, user ->
        contractors.filter { c ->
          val matchesScope =
            when (f.locationScope) {
              LocationScope.MY_CITY -> c.city.equals(user.preferredCity, ignoreCase = true)
              LocationScope.ALL_INDIA -> {
                val stateMatch =
                  f.filterState == "All States" ||
                    c.state.equals(f.filterState, ignoreCase = true)
                val cityMatch =
                  f.filterCity.isBlank() ||
                    c.city.contains(f.filterCity.trim(), ignoreCase = true)
                stateMatch && cityMatch
              }
            }
          val matchesTrade =
            f.contractorTradeFilter == "All Trades" ||
              c.tradesNeeded.any {
                it.contains(f.contractorTradeFilter.trim(), ignoreCase = true)
              }
          val matchesHiring = !f.onlyHiringContractors || c.isHiring
          matchesScope && matchesTrade && matchesHiring
        }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val visibleLiveRequests: StateFlow<List<LiveRequest>> =
    combine(allLiveRequests, filters, session, skippedIds, reportedIds) {
        requests,
        f,
        user,
        skipped,
        reported ->
        val cutoffMs = System.currentTimeMillis() - KaamConstants.SIX_HOURS_MS
        requests.filter { req ->
          val isRecent = req.createdAtMs <= 0L || req.createdAtMs >= cutoffMs
          val notClosed = !req.isClosed
          val notSkippedOrReported = req.id !in skipped && req.id !in reported
          val notReportedByMe =
            user.phone.isBlank() || user.phone !in req.reportedByPhones
          val alreadyAcceptedByMe =
            user.phone.isNotBlank() && req.acceptedWorkers.any { it.phone == user.phone }

          val matchesScope =
            when (f.locationScope) {
              LocationScope.MY_CITY -> req.city.equals(user.preferredCity, ignoreCase = true)
              LocationScope.ALL_INDIA -> {
                val stateMatch =
                  f.filterState == "All States" ||
                    req.state.equals(f.filterState, ignoreCase = true)
                val cityMatch =
                  f.filterCity.isBlank() ||
                    req.city.contains(f.filterCity.trim(), ignoreCase = true)
                stateMatch && cityMatch
              }
            }
          (alreadyAcceptedByMe && notClosed) ||
            (isRecent && notClosed && notSkippedOrReported && notReportedByMe && matchesScope)
        }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  val myPostedRequests: StateFlow<List<LiveRequest>> =
    combine(allLiveRequests, session) { requests, user ->
        if (user.phone.isBlank()) {
          emptyList()
        } else {
          requests.filter { it.contractorPhone == user.phone }
        }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

  fun setLocationScope(scope: LocationScope) {
    _filters.update { it.copy(locationScope = scope) }
  }

  fun setFilterState(state: String) {
    _filters.update { it.copy(filterState = state) }
  }

  fun setFilterCity(city: String) {
    _filters.update { it.copy(filterCity = city) }
  }

  fun setWorkerSkillFilter(skill: String) {
    _filters.update { it.copy(workerSkillFilter = skill) }
  }

  fun setOnlyAvailableWorkers(onlyAvailable: Boolean) {
    _filters.update { it.copy(onlyAvailableWorkers = onlyAvailable) }
  }

  fun setContractorTradeFilter(trade: String) {
    _filters.update { it.copy(contractorTradeFilter = trade) }
  }

  fun setOnlyHiringContractors(onlyHiring: Boolean) {
    _filters.update { it.copy(onlyHiringContractors = onlyHiring) }
  }

  fun loginWithMobile(fullName: String, phone: String, onSuccess: () -> Unit = {}) {
    val cleanPhone = phone.filter { it.isDigit() }
    if (fullName.trim().length < 2) {
      _messages.tryEmit("Please enter your full name.")
      return
    }
    if (cleanPhone.length != 10) {
      _messages.tryEmit("Please enter a valid 10-digit Indian mobile number.")
      return
    }
    viewModelScope.launch {
      sessionStore.saveLogin(fullName.trim(), cleanPhone)
      _messages.tryEmit("Welcome, ${fullName.trim()}! Synced with Cloud.")
      onSuccess()
    }
  }

  fun logout() {
    viewModelScope.launch {
      sessionStore.logout()
      _localDutyOnline.value = false
      _messages.tryEmit("Logged out successfully.")
    }
  }

  fun updatePreferredLocation(state: String, city: String) {
    viewModelScope.launch {
      sessionStore.savePreferredLocation(state, city)
      _messages.tryEmit("My City updated to $city, $state.")
    }
  }

  fun saveWorkerProfile(profile: WorkerProfile, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isBusy.value = true
      val result = repository.saveWorkerProfile(profile)
      _isBusy.value = false
      result
        .onSuccess {
          // If no user is currently logged in, log them in with this registration for instant sync
          if (!session.value.isLoggedIn) {
            sessionStore.saveLogin(profile.name, profile.phone)
          }
          _messages.tryEmit("Worker profile saved to Cloud!")
          onSuccess()
        }
        .onFailure { err ->
          _messages.tryEmit(err.message ?: "Failed to save worker profile.")
        }
    }
  }

  fun toggleMyWorkerAvailability(isAvailable: Boolean) {
    val profile = myWorkerProfile.value ?: return
    viewModelScope.launch {
      repository
        .toggleWorkerAvailability(profile.id, isAvailable)
        .onSuccess {
          val label = if (isAvailable) "Available for Work" else "Busy / On Site"
          _messages.tryEmit("Status updated: $label")
        }
        .onFailure { err -> _messages.tryEmit(err.message ?: "Failed to update status.") }
    }
  }

  fun toggleDutyOnline(isOnline: Boolean) {
    _localDutyOnline.value = isOnline
    val profile = myWorkerProfile.value
    if (profile != null) {
      viewModelScope.launch {
        repository
          .toggleWorkerOnline(profile.id, isOnline)
          .onSuccess {
            _messages.tryEmit(if (isOnline) "Duty is now ON (Online)" else "Duty is now OFF")
          }
          .onFailure { err -> _messages.tryEmit(err.message ?: "Could not update Duty status.") }
      }
    } else {
      _messages.tryEmit(
        if (isOnline) "Duty ON — Ready for live requests!" else "Duty OFF"
      )
    }
  }

  fun saveContractorProfile(profile: ContractorProfile, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isBusy.value = true
      val result = repository.saveContractorProfile(profile)
      _isBusy.value = false
      result
        .onSuccess {
          if (!session.value.isLoggedIn) {
            sessionStore.saveLogin(profile.name, profile.phone)
          }
          _messages.tryEmit("Contractor profile saved to Cloud!")
          onSuccess()
        }
        .onFailure { err ->
          _messages.tryEmit(err.message ?: "Failed to save contractor profile.")
        }
    }
  }

  fun toggleMyContractorHiring(isHiring: Boolean) {
    val profile = myContractorProfile.value ?: return
    viewModelScope.launch {
      repository
        .toggleContractorHiring(profile.id, isHiring)
        .onSuccess {
          val label = if (isHiring) "Hiring ON" else "Hiring OFF (Calls Protected)"
          _messages.tryEmit("Contractor status: $label")
        }
        .onFailure { err -> _messages.tryEmit(err.message ?: "Failed to update hiring status.") }
    }
  }

  fun createLiveRequest(request: LiveRequest, onSuccess: () -> Unit) {
    viewModelScope.launch {
      _isBusy.value = true
      val result = repository.createLiveRequest(request)
      _isBusy.value = false
      result
        .onSuccess {
          if (!session.value.isLoggedIn) {
            sessionStore.saveLogin(request.contractorName, request.contractorPhone)
          }
          _messages.tryEmit("Instant request sent live to workers!")
          onSuccess()
        }
        .onFailure { err ->
          _messages.tryEmit(err.message ?: "Failed to send instant request.")
        }
    }
  }

  fun acceptLiveRequest(request: LiveRequest) {
    val user = session.value
    if (!user.isLoggedIn) {
      _messages.tryEmit("Please login with your 10-digit mobile number first.")
      return
    }
    if (request.contractorPhone == user.phone) {
      _messages.tryEmit("This is your own request.")
      return
    }
    val workerSkill =
      myWorkerProfile.value?.skill?.takeIf { it.isNotBlank() }
        ?: request.skill.ifBlank { "Helper / Labour" }

    viewModelScope.launch {
      _isBusy.value = true
      val result =
        repository.acceptLiveRequest(
          requestId = request.id,
          workerName = user.fullName,
          workerPhone = user.phone,
          workerSkill = workerSkill,
        )
      _isBusy.value = false
      result
        .onSuccess {
          _messages.tryEmit("Job Confirmed! Call or WhatsApp the contractor now.")
        }
        .onFailure { err ->
          _messages.tryEmit(err.message ?: "Unable to accept job.")
        }
    }
  }

  fun skipLiveRequest(requestId: String) {
    viewModelScope.launch {
      sessionStore.markRequestSkipped(requestId)
      _messages.tryEmit("Request skipped.")
    }
  }

  fun reportLiveRequest(requestId: String) {
    viewModelScope.launch {
      sessionStore.markRequestReported(requestId)
      repository.reportLiveRequest(requestId, session.value.phone)
      _messages.tryEmit("Request reported and hidden.")
    }
  }

  fun closeLiveRequest(requestId: String) {
    viewModelScope.launch {
      repository
        .closeLiveRequest(requestId)
        .onSuccess { _messages.tryEmit("Live request closed.") }
        .onFailure { err -> _messages.tryEmit(err.message ?: "Failed to close request.") }
    }
  }
}
