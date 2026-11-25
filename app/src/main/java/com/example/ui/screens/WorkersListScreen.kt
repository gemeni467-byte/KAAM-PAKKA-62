package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.UserSession
import com.example.model.WorkerProfile
import com.example.ui.components.DirectContactButtons
import com.example.ui.components.DirectoryFilterPanel
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.StatusPill
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonBusy
import com.example.ui.theme.CrimsonContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnCrimsonContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextSecondary
import com.example.util.IntentUtils
import com.example.viewmodel.KaamFilterState

@Composable
fun WorkersListScreen(
  workers: List<WorkerProfile>,
  myWorkerProfile: WorkerProfile?,
  session: UserSession,
  filters: KaamFilterState,
  isLoaded: Boolean,
  onRegisterClick: () -> Unit,
  onToggleMyAvailability: (Boolean) -> Unit,
  onFilterScopeChange: (com.example.model.LocationScope) -> Unit,
  onFilterStateChange: (String) -> Unit,
  onFilterCityChange: (String) -> Unit,
  onSkillFilterChange: (String) -> Unit,
  onOnlyAvailableChange: (Boolean) -> Unit,
  onEditMyCityClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Skilled Workers (${workers.size})",
            style = MaterialTheme.typography.headlineSmall,
          )
          Text(
            text = "Call or WhatsApp workers directly — zero middleman",
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextSecondary,
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
          onClick = onRegisterClick,
          colors =
            ButtonDefaults.buttonColors(
              containerColor = AmberPrimary,
              contentColor = Color.White,
            ),
          shape = RoundedCornerShape(12.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
          modifier = Modifier.heightIn(min = 48.dp).testTag("workers_register_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Register Profile", style = MaterialTheme.typography.labelLarge)
        }
      }
    }

    if (myWorkerProfile != null) {
      item {
        Surface(
          color = if (myWorkerProfile.isAvailable) EmeraldContainer else CrimsonContainer,
          shape = RoundedCornerShape(14.dp),
          border =
            BorderStroke(
              1.dp,
              if (myWorkerProfile.isAvailable) EmeraldSuccess else CrimsonBusy,
            ),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text =
                  if (myWorkerProfile.isAvailable) "Your Status: Available for Work"
                  else "Your Status: Busy / On Site",
                style = MaterialTheme.typography.titleSmall,
                color =
                  if (myWorkerProfile.isAvailable) OnEmeraldContainer else OnCrimsonContainer,
              )
              Text(
                text = "Tap switch to update contractors in real time",
                style = MaterialTheme.typography.bodySmall,
                color =
                  if (myWorkerProfile.isAvailable) OnEmeraldContainer else OnCrimsonContainer,
              )
            }
            Switch(
              checked = myWorkerProfile.isAvailable,
              onCheckedChange = onToggleMyAvailability,
              colors =
                SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = EmeraldSuccess,
                ),
            )
          }
        }
      }
    }

    item {
      DirectoryFilterPanel(
        locationScope = filters.locationScope,
        preferredCity = session.preferredCity,
        selectedState = filters.filterState,
        cityQuery = filters.filterCity,
        selectedTrade = filters.workerSkillFilter,
        toggleLabel = "Show Only Available Workers",
        toggleChecked = filters.onlyAvailableWorkers,
        onScopeChange = onFilterScopeChange,
        onStateChange = onFilterStateChange,
        onCityChange = onFilterCityChange,
        onTradeChange = onSkillFilterChange,
        onToggleChange = onOnlyAvailableChange,
        onEditMyCityClick = onEditMyCityClick,
      )
    }

    if (!isLoaded) {
      item {
        Column(
          modifier = Modifier.fillMaxWidth().padding(32.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          CircularProgressIndicator(color = NavyDeep)
          Text(
            text = "Loading workers from Cloud Firestore...",
            style = MaterialTheme.typography.bodyMedium,
            color = SlateTextSecondary,
          )
        }
      }
    } else if (workers.isEmpty()) {
      item {
        EmptyStateCard(
          icon = Icons.Rounded.Engineering,
          title = "No Workers Found",
          subtitle =
            "No registered workers match your current filters yet. Be the first or register a worker on the spot!",
          ctaLabel = "+ Register Profile",
          onCtaClick = onRegisterClick,
        )
      }
    } else {
      items(workers, key = { it.id }) { worker ->
        WorkerCard(
          worker = worker,
          onCallClick = { IntentUtils.dialPhone(context, worker.phone) },
          onWhatsAppClick = {
            IntentUtils.openWhatsApp(
              context,
              worker.phone,
              "Hi ${worker.name}, I saw your ${worker.skill} profile on Kaam Connect. Is work available?",
            )
          },
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkerCard(
  worker: WorkerProfile,
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = if (worker.isAvailable) 1.5.dp else 1.dp,
        color = if (worker.isAvailable) EmeraldSuccess.copy(alpha = 0.45f) else SlateBorder,
      ),
    shadowElevation = if (worker.isAvailable) 2.dp else 0.dp,
    modifier = Modifier.fillMaxWidth().testTag("worker_card_${worker.id}"),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = worker.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Spacer(modifier = Modifier.size(4.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
          ) {
            StatusPill(
              text = worker.skill,
              containerColor = AmberContainer,
              contentColor = OnAmberContainer,
            )
            if (worker.isTeamLeader) {
              Surface(
                color = SlateSurfaceVariant,
                shape = RoundedCornerShape(50),
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                ) {
                  Icon(
                    imageVector = Icons.Rounded.Groups,
                    contentDescription = null,
                    tint = NavyDeep,
                    modifier = Modifier.size(14.dp),
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Squad of ${worker.teamSize}",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyDeep,
                  )
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        StatusPill(
          text = if (worker.isAvailable) "Available for Work" else "Busy / On Site",
          containerColor = if (worker.isAvailable) EmeraldContainer else CrimsonContainer,
          contentColor = if (worker.isAvailable) OnEmeraldContainer else OnCrimsonContainer,
          dotColor = if (worker.isAvailable) EmeraldSuccess else CrimsonBusy,
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Rounded.WorkHistory,
            contentDescription = null,
            tint = SlateTextSecondary,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${worker.experienceYears} yrs exp",
            style = MaterialTheme.typography.bodyMedium,
            color = SlateTextSecondary,
          )
        }

        Text(
          text = if (worker.dailyRate > 0) "₹${worker.dailyRate}/day" else "Rate negotiable",
          style = MaterialTheme.typography.titleSmall,
          color = NavyDeep,
          fontWeight = FontWeight.Bold,
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f),
        ) {
          Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            tint = AmberPrimary,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "${worker.city}, ${worker.state}",
            style = MaterialTheme.typography.bodyMedium,
            color = SlateTextSecondary,
          )
        }

        if (worker.outstationReady) {
          Surface(
            color = SlateSurfaceVariant,
            shape = RoundedCornerShape(50),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.Public,
                contentDescription = null,
                tint = NavyDeep,
                modifier = Modifier.size(13.dp),
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Outstation Ready",
                style = MaterialTheme.typography.labelSmall,
                color = NavyDeep,
              )
            }
          }
        }
      }

      HorizontalDivider(color = SlateBorder)

      DirectContactButtons(
        onCallClick = onCallClick,
        onWhatsAppClick = onWhatsAppClick,
        highlighted = worker.isAvailable,
      )
    }
  }
}
