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
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Shield
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
import com.example.model.ContractorProfile
import com.example.model.LocationScope
import com.example.model.UserSession
import com.example.ui.components.DirectContactButtons
import com.example.ui.components.DirectoryFilterPanel
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.FacilityBadgesRow
import com.example.ui.components.StatusPill
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.util.IntentUtils
import com.example.viewmodel.KaamFilterState

@Composable
fun ContractorsListScreen(
  contractors: List<ContractorProfile>,
  myContractorProfile: ContractorProfile?,
  session: UserSession,
  filters: KaamFilterState,
  isLoaded: Boolean,
  onRegisterClick: () -> Unit,
  onToggleMyHiring: (Boolean) -> Unit,
  onFilterScopeChange: (LocationScope) -> Unit,
  onFilterStateChange: (String) -> Unit,
  onFilterCityChange: (String) -> Unit,
  onTradeFilterChange: (String) -> Unit,
  onOnlyHiringChange: (Boolean) -> Unit,
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
            text = "Contractors (${contractors.size})",
            style = MaterialTheme.typography.headlineSmall,
          )
          Text(
            text = "Verified site contractors & fabricators across India",
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
          modifier = Modifier.heightIn(min = 48.dp).testTag("contractors_register_button"),
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

    if (myContractorProfile != null) {
      item {
        Surface(
          color = if (myContractorProfile.isHiring) EmeraldContainer else SlateSurfaceVariant,
          shape = RoundedCornerShape(14.dp),
          border =
            BorderStroke(
              1.dp,
              if (myContractorProfile.isHiring) EmeraldSuccess else SlateBorder,
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
                  if (myContractorProfile.isHiring) "Your Hiring Status: Hiring ON"
                  else "Your Hiring Status: Hiring OFF (Calls Hidden)",
                style = MaterialTheme.typography.titleSmall,
                color =
                  if (myContractorProfile.isHiring) OnEmeraldContainer else SlateTextSecondary,
              )
              Text(
                text = "Turn OFF anytime when you have no vacancy to stop unwanted calls",
                style = MaterialTheme.typography.bodySmall,
                color =
                  if (myContractorProfile.isHiring) OnEmeraldContainer else SlateTextSecondary,
              )
            }
            Switch(
              checked = myContractorProfile.isHiring,
              onCheckedChange = onToggleMyHiring,
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
        selectedTrade = filters.contractorTradeFilter,
        toggleLabel = "Show Only Actively Hiring",
        toggleChecked = filters.onlyHiringContractors,
        onScopeChange = onFilterScopeChange,
        onStateChange = onFilterStateChange,
        onCityChange = onFilterCityChange,
        onTradeChange = onTradeFilterChange,
        onToggleChange = onOnlyHiringChange,
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
            text = "Loading contractors from Cloud Firestore...",
            style = MaterialTheme.typography.bodyMedium,
            color = SlateTextSecondary,
          )
        }
      }
    } else if (contractors.isEmpty()) {
      item {
        EmptyStateCard(
          icon = Icons.Rounded.BusinessCenter,
          title = "No Contractors Found",
          subtitle =
            "No contractors match your current filters yet. Register yourself or a contractor on the spot!",
          ctaLabel = "+ Register Profile",
          onCtaClick = onRegisterClick,
        )
      }
    } else {
      items(contractors, key = { it.id }) { contractor ->
        ContractorCard(
          contractor = contractor,
          onCallClick = { IntentUtils.dialPhone(context, contractor.phone) },
          onWhatsAppClick = {
            IntentUtils.openWhatsApp(
              context,
              contractor.phone,
              "Hi ${contractor.name}, I saw your hiring profile on Kaam Connect. Is work available?",
            )
          },
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContractorCard(
  contractor: ContractorProfile,
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = if (contractor.isHiring) 1.5.dp else 1.dp,
        color = if (contractor.isHiring) EmeraldSuccess.copy(alpha = 0.45f) else SlateBorder,
      ),
    shadowElevation = if (contractor.isHiring) 2.dp else 0.dp,
    modifier = Modifier.fillMaxWidth().testTag("contractor_card_${contractor.id}"),
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
            text = contractor.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          if (contractor.companyName.isNotBlank()) {
            Text(
              text = contractor.companyName,
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
        StatusPill(
          text = if (contractor.isHiring) "Hiring ON" else "Hiring OFF",
          containerColor = if (contractor.isHiring) EmeraldContainer else SlateSurfaceVariant,
          contentColor = if (contractor.isHiring) OnEmeraldContainer else SlateTextMuted,
          dotColor = if (contractor.isHiring) EmeraldSuccess else SlateTextMuted,
        )
      }

      if (contractor.tradesNeeded.isNotEmpty()) {
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          contractor.tradesNeeded.forEach { trade ->
            StatusPill(
              text = trade,
              containerColor = AmberContainer,
              contentColor = OnAmberContainer,
            )
          }
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.LocationOn,
          contentDescription = null,
          tint = AmberPrimary,
          modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${contractor.city}, ${contractor.state}",
          style = MaterialTheme.typography.bodyMedium,
          color = SlateTextSecondary,
        )
      }

      FacilityBadgesRow(facilities = contractor.facilities)

      HorizontalDivider(color = SlateBorder)

      if (contractor.isHiring) {
        Text(
          text = "Direct Mobile: +91 ${contractor.phone}",
          style = MaterialTheme.typography.labelMedium,
          color = NavyDeep,
        )
        DirectContactButtons(
          onCallClick = onCallClick,
          onWhatsAppClick = onWhatsAppClick,
          highlighted = true,
        )
      } else {
        Surface(
          color = SlateSurfaceVariant,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("hiring_off_notice_${contractor.id}"),
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.Shield,
              contentDescription = null,
              tint = SlateTextMuted,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Hiring OFF — Phone number & call buttons hidden to prevent spam calls.",
              style = MaterialTheme.typography.bodySmall,
              color = SlateTextSecondary,
            )
          }
        }
      }
    }
  }
}
