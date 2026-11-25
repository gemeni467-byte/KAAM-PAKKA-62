package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.ContractorProfile
import com.example.model.KaamConstants
import com.example.model.UserSession
import com.example.model.WorkerProfile
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextSecondary

private data class WorkerFormState(
  val id: String = "",
  val name: String = "",
  val phone: String = "",
  val skill: String = KaamConstants.SKILLS_LIST.first(),
  val experienceText: String = "2",
  val dailyRateText: String = "750",
  val registrationType: String = "Individual Worker",
  val teamSizeText: String = "5",
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val outstationReady: Boolean = true,
  val isAvailable: Boolean = true,
)

private data class ContractorFormState(
  val id: String = "",
  val name: String = "",
  val companyName: String = "",
  val phone: String = "",
  val selectedTrades: Set<String> = setOf("MIG Welder"),
  val includeOtherTrade: Boolean = false,
  val customOtherTrade: String = "",
  val state: String = KaamConstants.DEFAULT_STATE,
  val city: String = KaamConstants.DEFAULT_CITY,
  val facilities: Set<String> = setOf("Free Stay"),
  val isHiring: Boolean = true,
)

@Composable
fun JoinLoginDialog(
  initialSession: UserSession,
  onDismiss: () -> Unit,
  onSubmit: (fullName: String, phone: String) -> Unit,
) {
  var fullName by remember { mutableStateOf(initialSession.fullName) }
  var phone by remember { mutableStateOf(initialSession.phone) }
  var errorText by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Join / Login with Mobile Number",
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
          text = "Enter your Full Name and 10-digit Indian mobile number to sync your profiles and bookings.",
          style = MaterialTheme.typography.bodyMedium,
          color = SlateTextSecondary,
        )
        OutlinedTextField(
          value = fullName,
          onValueChange = {
            fullName = it
            errorText = null
          },
          label = { Text("Full Name") },
          placeholder = { Text("e.g. Rajesh Kumar") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("login_name_input"),
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { input ->
            val digits = input.filter { it.isDigit() }.take(10)
            phone = digits
            errorText = null
          },
          label = { Text("10-Digit Mobile Number") },
          placeholder = { Text("e.g. 9876543210") },
          prefix = { Text("+91 ") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("login_phone_input"),
        )
        if (errorText != null) {
          Text(
            text = errorText!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (fullName.trim().length < 2) {
            errorText = "Please enter your full name."
          } else if (phone.length != 10) {
            errorText = "Mobile number must be exactly 10 digits."
          } else {
            onSubmit(fullName.trim(), phone)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
        modifier = Modifier.testTag("login_submit_button"),
      ) {
        Text("Continue")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
  )
}

@Composable
fun ChangeMyCityDialog(
  initialState: String,
  initialCity: String,
  onDismiss: () -> Unit,
  onSave: (String, String) -> Unit,
) {
  var state by remember { mutableStateOf(initialState) }
  var city by remember { mutableStateOf(initialCity) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Set My City / Work Area", style = MaterialTheme.typography.titleLarge) },
    text = {
      StateAndCitySelector(
        stateValue = state,
        cityValue = city,
        onStateChange = { state = it },
        onCityChange = { city = it },
        testTagPrefix = "my_city_dialog",
      )
    },
    confirmButton = {
      Button(
        onClick = {
          if (city.isNotBlank()) {
            onSave(state, city.trim())
            onDismiss()
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
      ) {
        Text("Save City")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    },
  )
}

@Composable
fun WorkerRegistrationDialog(
  initialProfile: WorkerProfile?,
  session: UserSession,
  isBusy: Boolean,
  onDismiss: () -> Unit,
  onSave: (WorkerProfile) -> Unit,
) {
  var form by remember {
    mutableStateOf(
      if (initialProfile != null) {
        WorkerFormState(
          id = initialProfile.id,
          name = initialProfile.name,
          phone = initialProfile.phone,
          skill = initialProfile.skill,
          experienceText = initialProfile.experienceYears.toString(),
          dailyRateText = initialProfile.dailyRate.toString(),
          registrationType = initialProfile.registrationType,
          teamSizeText = initialProfile.teamSize.coerceAtLeast(2).toString(),
          state = initialProfile.state,
          city = initialProfile.city,
          outstationReady = initialProfile.outstationReady,
          isAvailable = initialProfile.isAvailable,
        )
      } else {
        WorkerFormState(
          name = session.fullName,
          phone = session.phone,
          state = session.preferredState,
          city = session.preferredCity,
        )
      }
    )
  }
  var validationError by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Register Worker Profile",
              style = MaterialTheme.typography.headlineSmall,
            )
            Text(
              text = "Register yourself or another worker on the spot",
              style = MaterialTheme.typography.bodySmall,
              color = SlateTextSecondary,
            )
          }
          TextButton(
            onClick = {
              form =
                WorkerFormState(
                  state = form.state,
                  city = form.city,
                )
              validationError = null
            }
          ) {
            Text("New Person")
          }
        }

        OutlinedTextField(
          value = form.name,
          onValueChange = { v -> form = form.copy(name = v) },
          label = { Text("Full Name *") },
          placeholder = { Text("e.g. Suresh Verma") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("worker_form_name"),
        )

        OutlinedTextField(
          value = form.phone,
          onValueChange = { v ->
            val digits = v.filter { it.isDigit() }.take(10)
            form = form.copy(phone = digits)
          },
          label = { Text("10-Digit Mobile Number *") },
          prefix = { Text("+91 ") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("worker_form_phone"),
        )

        SkillDropdownWithOther(
          label = "Primary Skill / Trade *",
          selectedValue = form.skill,
          onValueChange = { v -> form = form.copy(skill = v) },
          testTagPrefix = "worker_form_skill",
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedTextField(
            value = form.experienceText,
            onValueChange = { v ->
              form = form.copy(experienceText = v.filter { it.isDigit() }.take(2))
            },
            label = { Text("Experience (Yrs)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("worker_form_experience"),
          )
          OutlinedTextField(
            value = form.dailyRateText,
            onValueChange = { v ->
              form = form.copy(dailyRateText = v.filter { it.isDigit() }.take(5))
            },
            label = { Text("Daily Rate (₹/day)") },
            prefix = { Text("₹") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f).testTag("worker_form_rate"),
          )
        }

        Text(
          text = "Registration Type",
          style = MaterialTheme.typography.titleSmall,
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          val types = listOf("Individual Worker", "Team Leader (With Squad)")
          types.forEach { typeLabel ->
            val selected = form.registrationType == typeLabel
            FilterChip(
              selected = selected,
              onClick = { form = form.copy(registrationType = typeLabel) },
              label = { Text(typeLabel, style = MaterialTheme.typography.labelMedium) },
              colors =
                FilterChipDefaults.filterChipColors(
                  selectedContainerColor = AmberContainer,
                  selectedLabelColor = OnAmberContainer,
                ),
              modifier = Modifier.weight(1f),
            )
          }
        }

        AnimatedVisibility(
          visible = form.registrationType.contains("Team Leader", ignoreCase = true)
        ) {
          OutlinedTextField(
            value = form.teamSizeText,
            onValueChange = { v ->
              form = form.copy(teamSizeText = v.filter { it.isDigit() }.take(3))
            },
            label = { Text("Team Size (Workers in Squad)") },
            placeholder = { Text("e.g. 5, 10, 20") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("worker_form_team_size"),
          )
        }

        StateAndCitySelector(
          stateValue = form.state,
          cityValue = form.city,
          onStateChange = { v -> form = form.copy(state = v) },
          onCityChange = { v -> form = form.copy(city = v) },
          testTagPrefix = "worker_form",
        )

        Surface(
          color = SlateSurfaceVariant,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                text = "Ready to work in other cities/states",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
              )
              Switch(
                checked = form.outstationReady,
                onCheckedChange = { v -> form = form.copy(outstationReady = v) },
              )
            }
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text =
                    if (form.isAvailable) "Available for Work"
                    else "Currently Working / Busy",
                  style = MaterialTheme.typography.titleSmall,
                  color =
                    if (form.isAvailable) EmeraldSuccess
                    else MaterialTheme.colorScheme.error,
                )
              }
              Switch(
                checked = form.isAvailable,
                onCheckedChange = { v -> form = form.copy(isAvailable = v) },
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldSuccess,
                  ),
              )
            }
          }
        }

        if (validationError != null) {
          Text(
            text = validationError!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
          ) {
            Text("Cancel")
          }
          Button(
            enabled = !isBusy,
            onClick = {
              when {
                form.name.isBlank() -> validationError = "Please enter Full Name."
                form.phone.length != 10 ->
                  validationError = "Please enter a valid 10-digit mobile number."
                form.skill.isBlank() ->
                  validationError = "Please select or write your Skill / Trade."
                else -> {
                  val isLeader =
                    form.registrationType.contains("Team Leader", ignoreCase = true)
                  val parsedTeamSize =
                    if (isLeader) (form.teamSizeText.toIntOrNull() ?: 2).coerceAtLeast(2)
                    else 1
                  onSave(
                    WorkerProfile(
                      id = form.id,
                      name = form.name.trim(),
                      phone = form.phone,
                      skill = form.skill.trim(),
                      experienceYears = form.experienceText.toIntOrNull() ?: 0,
                      dailyRate = form.dailyRateText.toIntOrNull() ?: 0,
                      registrationType = form.registrationType,
                      teamSize = parsedTeamSize,
                      state = form.state,
                      city = form.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
                      outstationReady = form.outstationReady,
                      isAvailable = form.isAvailable,
                      isOnline = initialProfile?.isOnline ?: false,
                    )
                  )
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
            modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("worker_form_save"),
          ) {
            Text(if (isBusy) "Saving..." else "Save Worker")
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ContractorRegistrationDialog(
  initialProfile: ContractorProfile?,
  session: UserSession,
  isBusy: Boolean,
  onDismiss: () -> Unit,
  onSave: (ContractorProfile) -> Unit,
) {
  var form by remember {
    mutableStateOf(
      if (initialProfile != null) {
        val standardTrades =
          KaamConstants.SKILLS_LIST.filter { it != KaamConstants.OTHER_SKILL_OPTION }.toSet()
        val presetSelected = initialProfile.tradesNeeded.filter { it in standardTrades }.toSet()
        val customTrades = initialProfile.tradesNeeded.filter { it !in standardTrades }
        ContractorFormState(
          id = initialProfile.id,
          name = initialProfile.name,
          companyName = initialProfile.companyName,
          phone = initialProfile.phone,
          selectedTrades = presetSelected,
          includeOtherTrade = customTrades.isNotEmpty(),
          customOtherTrade = customTrades.joinToString(", "),
          state = initialProfile.state,
          city = initialProfile.city,
          facilities = initialProfile.facilities.toSet(),
          isHiring = initialProfile.isHiring,
        )
      } else {
        ContractorFormState(
          name = session.fullName,
          phone = session.phone,
          state = session.preferredState,
          city = session.preferredCity,
        )
      }
    )
  }
  var validationError by remember { mutableStateOf<String?>(null) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = Color.White,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Register Contractor Profile",
              style = MaterialTheme.typography.headlineSmall,
            )
            Text(
              text = "Register yourself or another contractor on the spot",
              style = MaterialTheme.typography.bodySmall,
              color = SlateTextSecondary,
            )
          }
          TextButton(
            onClick = {
              form =
                ContractorFormState(
                  state = form.state,
                  city = form.city,
                )
              validationError = null
            }
          ) {
            Text("New Person")
          }
        }

        OutlinedTextField(
          value = form.name,
          onValueChange = { v -> form = form.copy(name = v) },
          label = { Text("Contractor Name *") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("contractor_form_name"),
        )

        OutlinedTextField(
          value = form.companyName,
          onValueChange = { v -> form = form.copy(companyName = v) },
          label = { Text("Company / Firm Name (Optional)") },
          placeholder = { Text("e.g. Maa Steel Fabrication") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("contractor_form_company"),
        )

        OutlinedTextField(
          value = form.phone,
          onValueChange = { v ->
            val digits = v.filter { it.isDigit() }.take(10)
            form = form.copy(phone = digits)
          },
          label = { Text("10-Digit Mobile Number *") },
          prefix = { Text("+91 ") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("contractor_form_phone"),
        )

        Text(
          text = "Trades / Workers Usually Hired *",
          style = MaterialTheme.typography.titleSmall,
        )

        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          KaamConstants.SKILLS_LIST.forEach { skillOption ->
            val isOther = skillOption == KaamConstants.OTHER_SKILL_OPTION
            val checked =
              if (isOther) form.includeOtherTrade else skillOption in form.selectedTrades
            Row(
              modifier =
                Modifier.fillMaxWidth()
                  .clickable {
                    form =
                      if (isOther) {
                        form.copy(includeOtherTrade = !form.includeOtherTrade)
                      } else {
                        val next =
                          if (checked) form.selectedTrades - skillOption
                          else form.selectedTrades + skillOption
                        form.copy(selectedTrades = next)
                      }
                  }
                  .padding(vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = checked,
                onCheckedChange = { nowChecked ->
                  form =
                    if (isOther) {
                      form.copy(includeOtherTrade = nowChecked)
                    } else {
                      val next =
                        if (nowChecked) form.selectedTrades + skillOption
                        else form.selectedTrades - skillOption
                      form.copy(selectedTrades = next)
                    }
                },
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = skillOption, style = MaterialTheme.typography.bodyMedium)
            }
          }
        }

        AnimatedVisibility(visible = form.includeOtherTrade) {
          OutlinedTextField(
            value = form.customOtherTrade,
            onValueChange = { v -> form = form.copy(customOtherTrade = v) },
            label = { Text("Write Custom Trade(s)") },
            placeholder = { Text("e.g. Scaffolder, Grinder") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("contractor_form_other_trade"),
          )
        }

        StateAndCitySelector(
          stateValue = form.state,
          cityValue = form.city,
          onStateChange = { v -> form = form.copy(state = v) },
          onCityChange = { v -> form = form.copy(city = v) },
          testTagPrefix = "contractor_form",
        )

        Text(
          text = "Facilities Offered",
          style = MaterialTheme.typography.titleSmall,
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          KaamConstants.FACILITIES_LIST.forEach { facility ->
            val selected = facility in form.facilities
            FilterChip(
              selected = selected,
              onClick = {
                val next =
                  if (selected) form.facilities - facility
                  else form.facilities + facility
                form = form.copy(facilities = next)
              },
              label = { Text(facility) },
            )
          }
        }

        Surface(
          color = SlateSurfaceVariant,
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, SlateBorder),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text =
                  if (form.isHiring) "Hiring ON (Need Workers)"
                  else "Hiring OFF (No Vacancy)",
                style = MaterialTheme.typography.titleSmall,
                color = if (form.isHiring) EmeraldSuccess else SlateTextSecondary,
              )
              Text(
                text = "Turn OFF to hide your phone number and block unwanted calls.",
                style = MaterialTheme.typography.bodySmall,
                color = SlateTextSecondary,
              )
            }
            Switch(
              checked = form.isHiring,
              onCheckedChange = { v -> form = form.copy(isHiring = v) },
              colors =
                SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = EmeraldSuccess,
                ),
            )
          }
        }

        if (validationError != null) {
          Text(
            text = validationError!!,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
          ) {
            Text("Cancel")
          }
          Button(
            enabled = !isBusy,
            onClick = {
              val allTrades = buildList {
                addAll(form.selectedTrades)
                if (form.includeOtherTrade && form.customOtherTrade.isNotBlank()) {
                  addAll(
                    form.customOtherTrade
                      .split(",")
                      .map { it.trim() }
                      .filter { it.isNotBlank() }
                  )
                }
              }
              when {
                form.name.isBlank() -> validationError = "Please enter Contractor Name."
                form.phone.length != 10 ->
                  validationError = "Please enter a valid 10-digit mobile number."
                allTrades.isEmpty() ->
                  validationError = "Select at least one trade you hire."
                else -> {
                  onSave(
                    ContractorProfile(
                      id = form.id,
                      name = form.name.trim(),
                      companyName = form.companyName.trim(),
                      phone = form.phone,
                      tradesNeeded = allTrades,
                      state = form.state,
                      city = form.city.trim().ifBlank { KaamConstants.DEFAULT_CITY },
                      facilities = form.facilities.toList(),
                      isHiring = form.isHiring,
                    )
                  )
                }
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
            modifier =
              Modifier.weight(1f).heightIn(min = 48.dp).testTag("contractor_form_save"),
          ) {
            Text(if (isBusy) "Saving..." else "Save Contractor")
          }
        }
      }
    }
  }
}
