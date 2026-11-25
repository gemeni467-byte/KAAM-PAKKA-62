package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.AcceptedWorker
import com.example.model.KaamConstants
import com.example.model.LiveRequest
import com.example.model.LocationScope
import com.example.model.UserSession
import com.example.ui.components.DirectContactButtons
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.FacilityBadgesRow
import com.example.ui.components.MyCityAllIndiaToggle
import com.example.ui.components.SkillDropdownWithOther
import com.example.ui.components.StateAndCitySelector
import com.example.ui.components.StatusPill
import com.example.ui.theme.AmberBright
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
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.WhatsAppGreen
import com.example.util.IntentUtils
import com.example.viewmodel.KaamFilterState

private enum class LiveDutyMode {
  WORKER,
  CONTRACTOR,
}

private data class InstantHireFormState(
  val contractorName: String = "",
  val contractorPhone: String = "",
  val skill: String = KaamConstants.SKILLS_LIST.first(),
  val requiredCountText: String = "2",
  val dailyWageText: String = "800",
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val siteAddress: String = "",
  val facilities: Set<String> = setOf("Free Stay"),
)

@Composable
fun LiveDutyScreen(
  visibleRequests: List<LiveRequest>,
  myPostedRequests: List<LiveRequest>,
  session: UserSession,
  isDutyOnline: Boolean,
  filters: KaamFilterState,
  isLoaded: Boolean,
  isBusy: Boolean,
  onToggleDutyOnline: (Boolean) -> Unit,
  onScopeChange: (LocationScope) -> Unit,
  onAcceptRequest: (LiveRequest) -> Unit,
  onSkipRequest: (String) -> Unit,
  onReportRequest: (String) -> Unit,
  onCreateRequest: (LiveRequest, () -> Unit) -> Unit,
  onCloseRequest: (String) -> Unit,
  onRequireLoginClick: () -> Unit,
  onRegisterWorkerClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var mode by rememberSaveable { mutableStateOf(LiveDutyMode.WORKER) }
  var showHireForm by rememberSaveable { mutableStateOf(true) }
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    item {
      LiveDutyModeSwitcher(
        mode = mode,
        liveJobCount = visibleRequests.size,
        myBookingsCount = myPostedRequests.count { !it.isClosed },
        onModeChange = { mode = it },
      )
    }

    if (mode == LiveDutyMode.WORKER) {
      item {
        DutyOnlineBannerCard(
          isDutyOnline = isDutyOnline,
          isLoggedIn = session.isLoggedIn,
          onToggle = { desired ->
            if (!session.isLoggedIn) {
              onRequireLoginClick()
            } else {
              onToggleDutyOnline(desired)
            }
          },
        )
      }

      item {
        MyCityAllIndiaToggle(
          scope = filters.locationScope,
          preferredCity = session.preferredCity,
          onScopeChange = onScopeChange,
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
              text = "Syncing live job requests...",
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )
          }
        }
      } else if (visibleRequests.isEmpty()) {
        item {
          EmptyStateCard(
            icon = Icons.Rounded.Bolt,
            title = "No Active Live Requests Right Now",
            subtitle =
              "Live requests appear here in real time for 6 hours. Keep Duty ON or register your worker profile so contractors can call you directly.",
            ctaLabel = "+ Register Profile",
            onCtaClick = onRegisterWorkerClick,
          )
        }
      } else {
        items(visibleRequests, key = { it.id }) { request ->
          WorkerLiveRequestCard(
            request = request,
            session = session,
            isBusy = isBusy,
            onAcceptClick = {
              if (!session.isLoggedIn) {
                onRequireLoginClick()
              } else {
                onAcceptRequest(request)
              }
            },
            onSkipClick = { onSkipRequest(request.id) },
            onReportClick = { onReportRequest(request.id) },
            onCallClick = { IntentUtils.dialPhone(context, request.contractorPhone) },
            onWhatsAppClick = {
              IntentUtils.openWhatsApp(
                context,
                request.contractorPhone,
                "Hi ${request.contractorName}, I confirmed your ${request.skill} job on Kaam Connect.",
              )
            },
            onQuickChatClick = { msg ->
              IntentUtils.openWhatsApp(context, request.contractorPhone, msg)
            },
          )
        }
      }
    } else {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Hire Workers Instantly",
              style = MaterialTheme.typography.headlineSmall,
            )
            Text(
              text = "Broadcast live requirements to workers on Duty",
              style = MaterialTheme.typography.bodySmall,
              color = SlateTextSecondary,
            )
          }
          OutlinedButton(
            onClick = { showHireForm = !showHireForm },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.heightIn(min = 42.dp),
          ) {
            Text(if (showHireForm) "Hide Form" else "+ New Request")
          }
        }
      }

      if (showHireForm) {
        item {
          InstantHireFormCard(
            session = session,
            isBusy = isBusy,
            onSubmit = { request, resetCallback ->
              onCreateRequest(request) {
                resetCallback()
              }
            },
          )
        }
      }

      if (myPostedRequests.isNotEmpty()) {
        item {
          Text(
            text = "Your Live Bookings (${myPostedRequests.size})",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 4.dp),
          )
        }

        items(myPostedRequests, key = { it.id }) { booking ->
          ContractorBookingStatusCard(
            request = booking,
            onCloseRequest = { onCloseRequest(booking.id) },
            onCallWorker = { worker -> IntentUtils.dialPhone(context, worker.phone) },
            onWhatsAppWorker = { worker ->
              IntentUtils.openWhatsApp(
                context,
                worker.phone,
                "Hi ${worker.name}, your booking for ${booking.skill} at ${booking.city} is confirmed.",
              )
            },
            onQuickChatWorker = { worker, msg ->
              IntentUtils.openWhatsApp(context, worker.phone, msg)
            },
          )
        }
      } else {
        item {
          EmptyStateCard(
            icon = Icons.Rounded.BusinessCenter,
            title = "No Live Bookings Posted Yet",
            subtitle =
              "Use the form above to send an instant booking request. Workers confirm in real time with zero middleman.",
            ctaLabel = "Post Instant Request",
            onCtaClick = { showHireForm = true },
          )
        }
      }
    }
  }
}

@Composable
private fun LiveDutyModeSwitcher(
  mode: LiveDutyMode,
  liveJobCount: Int,
  myBookingsCount: Int,
  onModeChange: (LiveDutyMode) -> Unit,
) {
  Surface(
    color = SlateSurfaceVariant,
    shape = RoundedCornerShape(14.dp),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      val workerSelected = mode == LiveDutyMode.WORKER
      Surface(
        onClick = { onModeChange(LiveDutyMode.WORKER) },
        color = if (workerSelected) NavyDeep else Color.Transparent,
        contentColor = if (workerSelected) Color.White else SlateTextSecondary,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f).heightIn(min = 46.dp).testTag("live_mode_worker"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Engineering,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Worker Duty ($liveJobCount)",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }

      val contractorSelected = mode == LiveDutyMode.CONTRACTOR
      Surface(
        onClick = { onModeChange(LiveDutyMode.CONTRACTOR) },
        color = if (contractorSelected) AmberPrimary else Color.Transparent,
        contentColor = if (contractorSelected) Color.White else SlateTextSecondary,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.weight(1f).heightIn(min = 46.dp).testTag("live_mode_contractor"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Bolt,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Hire Now ($myBookingsCount)",
            style = MaterialTheme.typography.labelLarge,
          )
        }
      }
    }
  }
}

@Composable
private fun DutyOnlineBannerCard(
  isDutyOnline: Boolean,
  isLoggedIn: Boolean,
  onToggle: (Boolean) -> Unit,
) {
  Surface(
    color = if (isDutyOnline) EmeraldContainer else Color.White,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = 1.5.dp,
        color = if (isDutyOnline) EmeraldSuccess else SlateBorder,
      ),
    modifier = Modifier.fillMaxWidth().testTag("duty_online_card"),
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f),
      ) {
        Box(
          modifier =
            Modifier.size(12.dp)
              .clip(CircleShape)
              .background(if (isDutyOnline) EmeraldSuccess else SlateTextMuted)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = if (isDutyOnline) "Duty ON (Online)" else "Duty OFF",
            style = MaterialTheme.typography.titleMedium,
            color = if (isDutyOnline) OnEmeraldContainer else NavyDeep,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text =
              when {
                !isLoggedIn -> "Tap switch to join with mobile number"
                isDutyOnline -> "Receiving real-time instant bookings"
                else -> "Turn ON to accept instant jobs"
              },
            style = MaterialTheme.typography.bodySmall,
            color = if (isDutyOnline) OnEmeraldContainer else SlateTextSecondary,
          )
        }
      }
      Switch(
        checked = isDutyOnline,
        onCheckedChange = onToggle,
        colors =
          SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = EmeraldSuccess,
          ),
        modifier = Modifier.testTag("duty_online_switch"),
      )
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkerLiveRequestCard(
  request: LiveRequest,
  session: UserSession,
  isBusy: Boolean,
  onAcceptClick: () -> Unit,
  onSkipClick: () -> Unit,
  onReportClick: () -> Unit,
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  onQuickChatClick: (String) -> Unit,
) {
  val isOwnRequest =
    session.phone.isNotBlank() && request.contractorPhone == session.phone
  val acceptedByMe =
    session.phone.isNotBlank() && request.acceptedWorkers.any { it.phone == session.phone }

  Surface(
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = if (acceptedByMe) 2.dp else 1.dp,
        color = if (acceptedByMe) EmeraldSuccess else SlateBorder,
      ),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth().testTag("live_request_card_${request.id}"),
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
          StatusPill(
            text = request.skill,
            containerColor = AmberContainer,
            contentColor = OnAmberContainer,
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = request.contractorName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "₹${request.dailyWage}/day",
            style = MaterialTheme.typography.headlineSmall,
            color = NavyDeep,
          )
          StatusPill(
            text = "${request.confirmedCount} / ${request.requiredCount} Filled",
            containerColor =
              if (request.isFilled) CrimsonContainer else EmeraldContainer,
            contentColor =
              if (request.isFilled) OnCrimsonContainer else OnEmeraldContainer,
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Icon(
          imageVector = Icons.Rounded.LocationOn,
          contentDescription = null,
          tint = AmberPrimary,
          modifier = Modifier.size(16.dp),
        )
        Text(
          text =
            buildString {
              append("${request.city}, ${request.state}")
              if (request.siteAddress.isNotBlank()) {
                append(" • ${request.siteAddress}")
              }
            },
          style = MaterialTheme.typography.bodyMedium,
          color = SlateTextSecondary,
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.Schedule,
          contentDescription = null,
          tint = SlateTextMuted,
          modifier = Modifier.size(14.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Posted ${IntentUtils.formatRelativeTime(request.createdAtMs)}",
          style = MaterialTheme.typography.bodySmall,
          color = SlateTextMuted,
        )
      }

      FacilityBadgesRow(facilities = request.facilities)

      HorizontalDivider(color = SlateBorder)

      if (acceptedByMe) {
        Surface(
          color = EmeraldContainer,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Rounded.CheckCircle,
              contentDescription = null,
              tint = EmeraldSuccess,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Confirmed! Contact ${request.contractorName} directly below:",
              style = MaterialTheme.typography.titleSmall,
              color = OnEmeraldContainer,
            )
          }
        }

        DirectContactButtons(
          onCallClick = onCallClick,
          onWhatsAppClick = onWhatsAppClick,
          highlighted = true,
        )

        Text(
          text = "1-Tap Quick Chat on WhatsApp:",
          style = MaterialTheme.typography.labelMedium,
          color = SlateTextSecondary,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          KaamConstants.QUICK_CHAT_MESSAGES.forEach { msg ->
            OutlinedButton(
              onClick = { onQuickChatClick(msg) },
              shape = RoundedCornerShape(50),
              contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
              modifier = Modifier.heightIn(min = 38.dp),
            ) {
              Text(msg, style = MaterialTheme.typography.labelSmall)
            }
          }
        }
      } else {
        if (isOwnRequest) {
          Surface(
            color = SlateSurfaceVariant,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Text(
              text = "This is your own request",
              style = MaterialTheme.typography.labelLarge,
              color = SlateTextSecondary,
              modifier = Modifier.padding(12.dp),
            )
          }
        } else {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Button(
              onClick = onAcceptClick,
              enabled = !request.isFilled && !isBusy,
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = EmeraldSuccess,
                  contentColor = Color.White,
                ),
              shape = RoundedCornerShape(12.dp),
              modifier =
                Modifier.weight(1.6f)
                  .heightIn(min = 48.dp)
                  .testTag("accept_job_${request.id}"),
            ) {
              Text(
                text = if (request.isFilled) "All Slots Filled" else "Accept Job",
                style = MaterialTheme.typography.labelLarge,
              )
            }

            OutlinedButton(
              onClick = onSkipClick,
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp),
              modifier = Modifier.heightIn(min = 48.dp).testTag("skip_job_${request.id}"),
            ) {
              Text("Skip", style = MaterialTheme.typography.labelMedium)
            }

            OutlinedButton(
              onClick = onReportClick,
              shape = RoundedCornerShape(12.dp),
              contentPadding = PaddingValues(horizontal = 10.dp),
              modifier = Modifier.heightIn(min = 48.dp).testTag("report_job_${request.id}"),
            ) {
              Icon(
                imageVector = Icons.Rounded.Flag,
                contentDescription = "Report",
                tint = CrimsonBusy,
                modifier = Modifier.size(16.dp),
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InstantHireFormCard(
  session: UserSession,
  isBusy: Boolean,
  onSubmit: (LiveRequest, () -> Unit) -> Unit,
) {
  var form by remember(session.phone, session.fullName) {
    mutableStateOf(
      InstantHireFormState(
        contractorName = session.fullName,
        contractorPhone = session.phone,
        state = session.preferredState,
        city = session.preferredCity,
      )
    )
  }
  var errorText by remember { mutableStateOf<String?>(null) }

  Surface(
    color = Color.White,
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, SlateBorder),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Rounded.Bolt,
          contentDescription = null,
          tint = AmberBright,
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Instant Live Booking Form",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
        )
      }

      OutlinedTextField(
        value = form.contractorName,
        onValueChange = { v ->
          form = form.copy(contractorName = v)
          errorText = null
        },
        label = { Text("Contractor Name *") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("hire_form_name"),
      )

      OutlinedTextField(
        value = form.contractorPhone,
        onValueChange = { v ->
          form = form.copy(contractorPhone = v.filter { it.isDigit() }.take(10))
          errorText = null
        },
        label = { Text("10-Digit Mobile Number *") },
        prefix = { Text("+91 ") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("hire_form_phone"),
      )

      SkillDropdownWithOther(
        label = "Skill Needed *",
        selectedValue = form.skill,
        onValueChange = { v ->
          form = form.copy(skill = v)
          errorText = null
        },
        testTagPrefix = "hire_form_skill",
      )

      Text(
        text = "Workers Needed *",
        style = MaterialTheme.typography.titleSmall,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        KaamConstants.QUICK_WORKERS_COUNT_CHIPS.forEach { count ->
          val label = if (count == 1) "1 (Solo)" else "$count"
          val selected = form.requiredCountText == count.toString()
          FilterChip(
            selected = selected,
            onClick = { form = form.copy(requiredCountText = count.toString()) },
            label = { Text(label) },
            colors =
              FilterChipDefaults.filterChipColors(
                selectedContainerColor = AmberContainer,
                selectedLabelColor = OnAmberContainer,
              ),
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
      ) {
        OutlinedTextField(
          value = form.requiredCountText,
          onValueChange = { v ->
            form = form.copy(requiredCountText = v.filter { it.isDigit() }.take(3))
          },
          label = { Text("Custom Count") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(1f).testTag("hire_form_count"),
        )
        OutlinedTextField(
          value = form.dailyWageText,
          onValueChange = { v ->
            form = form.copy(dailyWageText = v.filter { it.isDigit() }.take(5))
          },
          label = { Text("Daily Wage (₹/day) *") },
          prefix = { Text("₹") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(1f).testTag("hire_form_wage"),
        )
      }

      StateAndCitySelector(
        stateValue = form.state,
        cityValue = form.city,
        onStateChange = { v -> form = form.copy(state = v) },
        onCityChange = { v -> form = form.copy(city = v) },
        testTagPrefix = "hire_form",
      )

      OutlinedTextField(
        value = form.siteAddress,
        onValueChange = { v -> form = form.copy(siteAddress = v) },
        label = { Text("Site Address / Plant Gate") },
        placeholder = { Text("e.g. RSP Gate No. 2, Sector 5") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("hire_form_address"),
      )

      Text(
        text = "Facilities Provided",
        style = MaterialTheme.typography.titleSmall,
      )
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        KaamConstants.FACILITIES_LIST.forEach { facility ->
          val selected = facility in form.facilities
          FilterChip(
            selected = selected,
            onClick = {
              val next =
                if (selected) form.facilities - facility else form.facilities + facility
              form = form.copy(facilities = next)
            },
            label = { Text(facility) },
          )
        }
      }

      if (errorText != null) {
        Text(
          text = errorText!!,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.error,
        )
      }

      Button(
        enabled = !isBusy,
        onClick = {
          val count = form.requiredCountText.toIntOrNull() ?: 0
          val wage = form.dailyWageText.toIntOrNull() ?: 0
          when {
            form.contractorName.isBlank() -> errorText = "Enter Contractor Name."
            form.contractorPhone.length != 10 ->
              errorText = "Enter a valid 10-digit mobile number."
            form.skill.isBlank() -> errorText = "Select or write the Skill needed."
            count <= 0 -> errorText = "Enter how many workers you need."
            wage <= 0 -> errorText = "Enter a valid daily wage."
            else -> {
              onSubmit(
                LiveRequest(
                  contractorName = form.contractorName.trim(),
                  contractorPhone = form.contractorPhone,
                  skill = form.skill.trim(),
                  requiredCount = count,
                  dailyWage = wage,
                  state = form.state,
                  city = form.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
                  siteAddress = form.siteAddress.trim(),
                  facilities = form.facilities.toList(),
                )
              ) {
                form =
                  form.copy(
                    siteAddress = "",
                  )
              }
            }
          }
        },
        colors =
          ButtonDefaults.buttonColors(
            containerColor = AmberPrimary,
            contentColor = Color.White,
          ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("send_instant_request"),
      ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = if (isBusy) "Sending..." else "Send Instant Request",
          style = MaterialTheme.typography.labelLarge,
        )
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContractorBookingStatusCard(
  request: LiveRequest,
  onCloseRequest: () -> Unit,
  onCallWorker: (AcceptedWorker) -> Unit,
  onWhatsAppWorker: (AcceptedWorker) -> Unit,
  onQuickChatWorker: (AcceptedWorker, String) -> Unit,
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border =
      BorderStroke(
        width = 1.dp,
        color = if (request.isClosed) SlateBorder else AmberPrimary,
      ),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "${request.skill} • ₹${request.dailyWage}/day",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "${request.city}, ${request.state}",
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextSecondary,
          )
        }
        StatusPill(
          text =
            if (request.isClosed) "Closed"
            else "${request.confirmedCount} / ${request.requiredCount} Workers Confirmed",
          containerColor =
            if (request.isClosed) SlateSurfaceVariant
            else if (request.confirmedCount > 0) EmeraldContainer
            else AmberContainer,
          contentColor =
            if (request.isClosed) SlateTextMuted
            else if (request.confirmedCount > 0) OnEmeraldContainer
            else OnAmberContainer,
        )
      }

      if (request.acceptedWorkers.isEmpty()) {
        Text(
          text = "Waiting for workers to accept...",
          style = MaterialTheme.typography.bodySmall,
          color = SlateTextMuted,
        )
      } else {
        request.acceptedWorkers.forEach { worker ->
          Surface(
            color = SlateSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Column(
              modifier = Modifier.padding(10.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Column {
                  Text(
                    text = worker.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                  )
                  Text(
                    text = "${worker.skill} • +91 ${worker.phone}",
                    style = MaterialTheme.typography.bodySmall,
                    color = SlateTextSecondary,
                  )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  Button(
                    onClick = { onCallWorker(worker) },
                    colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.heightIn(min = 40.dp),
                  ) {
                    Icon(
                      imageVector = Icons.Rounded.Call,
                      contentDescription = "Call Worker",
                      modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", style = MaterialTheme.typography.labelMedium)
                  }
                  Button(
                    onClick = { onWhatsAppWorker(worker) },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.heightIn(min = 40.dp),
                  ) {
                    Icon(
                      imageVector = Icons.AutoMirrored.Rounded.Chat,
                      contentDescription = "WhatsApp Worker",
                      modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Chat", style = MaterialTheme.typography.labelMedium)
                  }
                }
              }

              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
              ) {
                listOf(
                  "Please report at site by 8 AM.",
                  "Share your current location.",
                ).forEach { msg ->
                  OutlinedButton(
                    onClick = { onQuickChatWorker(worker, msg) },
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.heightIn(min = 34.dp),
                  ) {
                    Text(msg, style = MaterialTheme.typography.labelSmall)
                  }
                }
              }
            }
          }
        }
      }

      if (!request.isClosed) {
        TextButton(
          onClick = onCloseRequest,
          modifier = Modifier.align(Alignment.End),
        ) {
          Icon(
            imageVector = Icons.Rounded.Close,
            contentDescription = null,
            tint = CrimsonBusy,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Close Request", color = CrimsonBusy)
        }
      }
    }
  }
}
