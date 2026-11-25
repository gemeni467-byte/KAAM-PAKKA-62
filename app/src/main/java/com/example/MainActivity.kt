package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.MainTab
import com.example.ui.components.ChangeMyCityDialog
import com.example.ui.components.ContractorRegistrationDialog
import com.example.ui.components.JoinLoginDialog
import com.example.ui.components.KaamTopHeader
import com.example.ui.components.WorkerRegistrationDialog
import com.example.ui.screens.ContractorsListScreen
import com.example.ui.screens.LiveDutyScreen
import com.example.ui.screens.MyAccountScreen
import com.example.ui.screens.WorkersListScreen
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.KaamConnectTheme
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OnCrimsonContainer
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateTextSecondary
import com.example.util.IntentUtils
import com.example.viewmodel.KaamConnectViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
    )
    setContent {
      KaamConnectTheme {
        KaamConnectApp()
      }
    }
  }
}

@Composable
fun KaamConnectApp(
  viewModel: KaamConnectViewModel = viewModel(),
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  val session by viewModel.session.collectAsStateWithLifecycle()
  val filters by viewModel.filters.collectAsStateWithLifecycle()
  val isBusy by viewModel.isBusy.collectAsStateWithLifecycle()
  val isDutyOnline by viewModel.isDutyOnline.collectAsStateWithLifecycle()
  val syncError by viewModel.syncError.collectAsStateWithLifecycle()

  val filteredWorkers by viewModel.filteredWorkers.collectAsStateWithLifecycle()
  val workersLoaded by viewModel.workersLoaded.collectAsStateWithLifecycle()
  val myWorkerProfile by viewModel.myWorkerProfile.collectAsStateWithLifecycle()

  val filteredContractors by viewModel.filteredContractors.collectAsStateWithLifecycle()
  val contractorsLoaded by viewModel.contractorsLoaded.collectAsStateWithLifecycle()
  val myContractorProfile by viewModel.myContractorProfile.collectAsStateWithLifecycle()

  val visibleLiveRequests by viewModel.visibleLiveRequests.collectAsStateWithLifecycle()
  val myPostedRequests by viewModel.myPostedRequests.collectAsStateWithLifecycle()
  val liveRequestsLoaded by viewModel.liveRequestsLoaded.collectAsStateWithLifecycle()

  var currentTab by rememberSaveable { mutableStateOf(MainTab.LIVE_DUTY) }
  var showLoginDialog by remember { mutableStateOf(false) }
  var showWorkerDialog by remember { mutableStateOf(false) }
  var editOwnWorker by remember { mutableStateOf(false) }
  var showContractorDialog by remember { mutableStateOf(false) }
  var editOwnContractor by remember { mutableStateOf(false) }
  var showChangeCityDialog by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    viewModel.messages.collect { message ->
      snackbarHostState.showSnackbar(message)
    }
  }

  BackHandler(enabled = currentTab != MainTab.LIVE_DUTY) {
    currentTab = MainTab.LIVE_DUTY
  }

  BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
    val useNavigationRail = maxWidth >= 600.dp

    Scaffold(
      containerColor = SlateBackground,
      snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
      contentWindowInsets = WindowInsets(0, 0, 0, 0),
      topBar = {
        KaamTopHeader(
          session = session,
          onShareClick = { IntentUtils.shareApp(context) },
          onLoginClick = {
            if (session.isLoggedIn) {
              currentTab = MainTab.MY_ACCOUNT
            } else {
              showLoginDialog = true
            }
          },
        )
      },
      bottomBar = {
        if (!useNavigationRail) {
          KaamBottomBar(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
          )
        }
      },
    ) { innerPadding ->
      Row(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
      ) {
        if (useNavigationRail) {
          KaamNavRail(
            currentTab = currentTab,
            onTabSelected = { currentTab = it },
          )
        }

        Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          if (syncError != null) {
            Surface(
              color = CrimsonContainer,
              contentColor = OnCrimsonContainer,
              modifier = Modifier.fillMaxWidth(),
            ) {
              Text(
                text = "Cloud Sync Notice: $syncError",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
              )
            }
          }

          Box(
            modifier = Modifier.weight(1f).widthIn(max = 780.dp).fillMaxWidth(),
          ) {
            when (currentTab) {
              MainTab.LIVE_DUTY ->
                LiveDutyScreen(
                  visibleRequests = visibleLiveRequests,
                  myPostedRequests = myPostedRequests,
                  session = session,
                  isDutyOnline = isDutyOnline,
                  filters = filters,
                  isLoaded = liveRequestsLoaded,
                  isBusy = isBusy,
                  onToggleDutyOnline = viewModel::toggleDutyOnline,
                  onScopeChange = viewModel::setLocationScope,
                  onAcceptRequest = viewModel::acceptLiveRequest,
                  onSkipRequest = viewModel::skipLiveRequest,
                  onReportRequest = viewModel::reportLiveRequest,
                  onCreateRequest = viewModel::createLiveRequest,
                  onCloseRequest = viewModel::closeLiveRequest,
                  onRequireLoginClick = { showLoginDialog = true },
                  onRegisterWorkerClick = {
                    editOwnWorker = false
                    showWorkerDialog = true
                  },
                )

              MainTab.WORKERS ->
                WorkersListScreen(
                  workers = filteredWorkers,
                  myWorkerProfile = myWorkerProfile,
                  session = session,
                  filters = filters,
                  isLoaded = workersLoaded,
                  onRegisterClick = {
                    editOwnWorker = false
                    showWorkerDialog = true
                  },
                  onToggleMyAvailability = viewModel::toggleMyWorkerAvailability,
                  onFilterScopeChange = viewModel::setLocationScope,
                  onFilterStateChange = viewModel::setFilterState,
                  onFilterCityChange = viewModel::setFilterCity,
                  onSkillFilterChange = viewModel::setWorkerSkillFilter,
                  onOnlyAvailableChange = viewModel::setOnlyAvailableWorkers,
                  onEditMyCityClick = { showChangeCityDialog = true },
                )

              MainTab.CONTRACTORS ->
                ContractorsListScreen(
                  contractors = filteredContractors,
                  myContractorProfile = myContractorProfile,
                  session = session,
                  filters = filters,
                  isLoaded = contractorsLoaded,
                  onRegisterClick = {
                    editOwnContractor = false
                    showContractorDialog = true
                  },
                  onToggleMyHiring = viewModel::toggleMyContractorHiring,
                  onFilterScopeChange = viewModel::setLocationScope,
                  onFilterStateChange = viewModel::setFilterState,
                  onFilterCityChange = viewModel::setFilterCity,
                  onTradeFilterChange = viewModel::setContractorTradeFilter,
                  onOnlyHiringChange = viewModel::setOnlyHiringContractors,
                  onEditMyCityClick = { showChangeCityDialog = true },
                )

              MainTab.MY_ACCOUNT ->
                MyAccountScreen(
                  session = session,
                  myWorkerProfile = myWorkerProfile,
                  myContractorProfile = myContractorProfile,
                  isDutyOnline = isDutyOnline,
                  onLoginSubmit = { name, phone -> viewModel.loginWithMobile(name, phone) },
                  onLogout = viewModel::logout,
                  onEditMyCityClick = { showChangeCityDialog = true },
                  onOpenWorkerRegister = { editExisting ->
                    editOwnWorker = editExisting
                    showWorkerDialog = true
                  },
                  onOpenContractorRegister = { editExisting ->
                    editOwnContractor = editExisting
                    showContractorDialog = true
                  },
                  onToggleWorkerAvailable = viewModel::toggleMyWorkerAvailability,
                  onToggleWorkerOnline = viewModel::toggleDutyOnline,
                  onToggleContractorHiring = viewModel::toggleMyContractorHiring,
                )
            }
          }
        }
      }
    }
  }

  if (showLoginDialog) {
    JoinLoginDialog(
      initialSession = session,
      onDismiss = { showLoginDialog = false },
      onSubmit = { fullName, phone ->
        viewModel.loginWithMobile(fullName, phone) {
          showLoginDialog = false
        }
      },
    )
  }

  if (showChangeCityDialog) {
    ChangeMyCityDialog(
      initialState = session.preferredState,
      initialCity = session.preferredCity,
      onDismiss = { showChangeCityDialog = false },
      onSave = { state, city -> viewModel.updatePreferredLocation(state, city) },
    )
  }

  if (showWorkerDialog) {
    WorkerRegistrationDialog(
      initialProfile = if (editOwnWorker) myWorkerProfile else null,
      session = session,
      isBusy = isBusy,
      onDismiss = { showWorkerDialog = false },
      onSave = { profile ->
        viewModel.saveWorkerProfile(profile) {
          showWorkerDialog = false
        }
      },
    )
  }

  if (showContractorDialog) {
    ContractorRegistrationDialog(
      initialProfile = if (editOwnContractor) myContractorProfile else null,
      session = session,
      isBusy = isBusy,
      onDismiss = { showContractorDialog = false },
      onSave = { profile ->
        viewModel.saveContractorProfile(profile) {
          showContractorDialog = false
        }
      },
    )
  }
}

@Composable
private fun KaamBottomBar(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 6.dp,
    windowInsets = WindowInsets.navigationBars,
  ) {
    MainTab.entries.forEach { tab ->
      val selected = currentTab == tab
      val icon =
        when (tab) {
          MainTab.LIVE_DUTY -> if (selected) Icons.Rounded.Bolt else Icons.Outlined.Bolt
          MainTab.WORKERS ->
            if (selected) Icons.Rounded.Engineering else Icons.Outlined.Engineering
          MainTab.CONTRACTORS ->
            if (selected) Icons.Rounded.BusinessCenter else Icons.Outlined.BusinessCenter
          MainTab.MY_ACCOUNT -> if (selected) Icons.Rounded.Person else Icons.Outlined.Person
        }

      NavigationBarItem(
        selected = selected,
        onClick = { onTabSelected(tab) },
        icon = { Icon(imageVector = icon, contentDescription = tab.label) },
        label = {
          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        },
        colors =
          NavigationBarItemDefaults.colors(
            selectedIconColor = NavyDeep,
            selectedTextColor = NavyDeep,
            indicatorColor = AmberContainer,
            unselectedIconColor = SlateTextSecondary,
            unselectedTextColor = SlateTextSecondary,
          ),
        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
      )
    }
  }
}

@Composable
private fun KaamNavRail(
  currentTab: MainTab,
  onTabSelected: (MainTab) -> Unit,
) {
  NavigationRail(
    containerColor = Color.White,
    modifier = Modifier.fillMaxHeight(),
  ) {
    MainTab.entries.forEach { tab ->
      val selected = currentTab == tab
      val icon =
        when (tab) {
          MainTab.LIVE_DUTY -> if (selected) Icons.Rounded.Bolt else Icons.Outlined.Bolt
          MainTab.WORKERS ->
            if (selected) Icons.Rounded.Engineering else Icons.Outlined.Engineering
          MainTab.CONTRACTORS ->
            if (selected) Icons.Rounded.BusinessCenter else Icons.Outlined.BusinessCenter
          MainTab.MY_ACCOUNT -> if (selected) Icons.Rounded.Person else Icons.Outlined.Person
        }

      NavigationRailItem(
        selected = selected,
        onClick = { onTabSelected(tab) },
        icon = { Icon(imageVector = icon, contentDescription = tab.label) },
        label = {
          Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
          )
        },
        colors =
          NavigationRailItemDefaults.colors(
            selectedIconColor = NavyDeep,
            selectedTextColor = AmberPrimary,
            indicatorColor = AmberContainer,
          ),
      )
    }
  }
}
