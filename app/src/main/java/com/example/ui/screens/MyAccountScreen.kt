package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BusinessCenter
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.model.ContractorProfile
import com.example.model.KaamConstants
import com.example.model.UserSession
import com.example.model.WorkerProfile
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
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.util.IntentUtils

@Composable
fun MyAccountScreen(
  session: UserSession,
  myWorkerProfile: WorkerProfile?,
  myContractorProfile: ContractorProfile?,
  isDutyOnline: Boolean,
  onLoginSubmit: (fullName: String, phone: String) -> Unit,
  onLogout: () -> Unit,
  onEditMyCityClick: () -> Unit,
  onOpenWorkerRegister: (editExisting: Boolean) -> Unit,
  onOpenContractorRegister: (editExisting: Boolean) -> Unit,
  onToggleWorkerAvailable: (Boolean) -> Unit,
  onToggleWorkerOnline: (Boolean) -> Unit,
  onToggleContractorHiring: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current

  LazyColumn(
    modifier = modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    if (!session.isLoggedIn) {
      item {
        MobileLoginCard(onSubmit = onLoginSubmit)
      }
    } else {
      item {
        Surface(
          color = NavyDeep,
          contentColor = Color.White,
          shape = RoundedCornerShape(18.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.Person,
                  contentDescription = null,
                  modifier = Modifier.size(28.dp),
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = session.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                  )
                  Text(
                    text = "+91 ${session.phone}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFCBD5E1),
                  )
                }
              }

              TextButton(
                onClick = onLogout,
                colors = ButtonDefaults.textButtonColors(contentColor = AmberContainer),
                modifier = Modifier.testTag("account_logout_button"),
              ) {
                Icon(
                  imageVector = Icons.AutoMirrored.Rounded.Logout,
                  contentDescription = "Logout",
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Logout")
              }
            }

            HorizontalDivider(color = Color(0xFF334155))

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Rounded.LocationOn,
                  contentDescription = null,
                  tint = AmberPrimary,
                  modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "My City: ${session.preferredCity}, ${session.preferredState}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = Color.White,
                )
              }
              OutlinedButton(
                onClick = onEditMyCityClick,
                border = BorderStroke(1.dp, Color(0xFF64748B)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.heightIn(min = 38.dp),
              ) {
                Text("Change", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }
      }
    }

    // Worker Profile Sync Card
    item {
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.Engineering,
                contentDescription = null,
                tint = AmberPrimary,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "My Worker Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }
            if (myWorkerProfile != null) {
              StatusPill(
                text = if (myWorkerProfile.isAvailable) "Available for Work" else "Busy / On Site",
                containerColor =
                  if (myWorkerProfile.isAvailable) EmeraldContainer else CrimsonContainer,
                contentColor =
                  if (myWorkerProfile.isAvailable) OnEmeraldContainer else OnCrimsonContainer,
              )
            }
          }

          if (myWorkerProfile != null) {
            Text(
              text =
                "${myWorkerProfile.skill} • ₹${myWorkerProfile.dailyRate}/day • ${myWorkerProfile.city}, ${myWorkerProfile.state}",
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("Available for Work", style = MaterialTheme.typography.titleSmall)
              Switch(
                checked = myWorkerProfile.isAvailable,
                onCheckedChange = onToggleWorkerAvailable,
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldSuccess,
                  ),
              )
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text("Live Duty Online", style = MaterialTheme.typography.titleSmall)
              Switch(
                checked = isDutyOnline,
                onCheckedChange = onToggleWorkerOnline,
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldSuccess,
                  ),
              )
            }

            OutlinedButton(
              onClick = { onOpenWorkerRegister(true) },
              modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Edit Worker Profile")
            }
          } else {
            Text(
              text =
                "Create your Worker Profile so contractors across India can find and call you directly.",
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )
            Button(
              onClick = { onOpenWorkerRegister(false) },
              colors = ButtonDefaults.buttonColors(containerColor = AmberPrimary),
              modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
              Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("+ Register Worker Profile")
            }
          }
        }
      }
    }

    // Contractor Profile Sync Card
    item {
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Rounded.BusinessCenter,
                contentDescription = null,
                tint = NavyDeep,
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "My Contractor Profile",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
              )
            }
            if (myContractorProfile != null) {
              StatusPill(
                text = if (myContractorProfile.isHiring) "Hiring ON" else "Hiring OFF",
                containerColor =
                  if (myContractorProfile.isHiring) EmeraldContainer else SlateSurfaceVariant,
                contentColor =
                  if (myContractorProfile.isHiring) OnEmeraldContainer else SlateTextMuted,
              )
            }
          }

          if (myContractorProfile != null) {
            Text(
              text =
                buildString {
                  if (myContractorProfile.companyName.isNotBlank()) {
                    append("${myContractorProfile.companyName} • ")
                  }
                  append("${myContractorProfile.city}, ${myContractorProfile.state}")
                },
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text("Hiring ON (Need Workers)", style = MaterialTheme.typography.titleSmall)
                Text(
                  text = "Turn OFF to hide your phone number from the directory",
                  style = MaterialTheme.typography.bodySmall,
                  color = SlateTextMuted,
                )
              }
              Switch(
                checked = myContractorProfile.isHiring,
                onCheckedChange = onToggleContractorHiring,
                colors =
                  SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = EmeraldSuccess,
                  ),
              )
            }

            OutlinedButton(
              onClick = { onOpenContractorRegister(true) },
              modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
            ) {
              Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text("Edit Contractor Profile")
            }
          } else {
            Text(
              text =
                "Register as a Contractor to hire welders, fitters, masons, and helpers directly.",
              style = MaterialTheme.typography.bodyMedium,
              color = SlateTextSecondary,
            )
            Button(
              onClick = { onOpenContractorRegister(false) },
              colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
              modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
            ) {
              Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
              Spacer(modifier = Modifier.width(6.dp))
              Text("+ Register Contractor Profile")
            }
          }
        }
      }
    }

    // Organizer / On-the-spot registration card
    item {
      Surface(
        color = AmberContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          Text(
            text = "On-the-Spot Registration (Organizer Mode)",
            style = MaterialTheme.typography.titleMedium,
            color = OnAmberContainer,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "Help fellow workers or site contractors join Kaam Connect with their mobile number.",
            style = MaterialTheme.typography.bodySmall,
            color = OnAmberContainer,
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Button(
              onClick = { onOpenWorkerRegister(false) },
              colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
              modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            ) {
              Text("+ Register Profile", style = MaterialTheme.typography.labelMedium)
            }
            OutlinedButton(
              onClick = { onOpenContractorRegister(false) },
              border = BorderStroke(1.dp, OnAmberContainer),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = OnAmberContainer),
              modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            ) {
              Text("+ Contractor", style = MaterialTheme.typography.labelMedium)
            }
          }
        }
      }
    }

    // Viral Share App Card
    item {
      Surface(
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            text = "Share Kaam Connect",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text =
              "Share with workers and contractors on WhatsApp. Link included: ${KaamConstants.PUBLIC_APP_URL}",
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextSecondary,
          )
          Button(
            onClick = { IntentUtils.shareApp(context) },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSuccess),
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("account_share_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Share,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share App on WhatsApp", style = MaterialTheme.typography.labelLarge)
          }
        }
      }
    }
  }
}

@Composable
private fun MobileLoginCard(
  onSubmit: (fullName: String, phone: String) -> Unit,
) {
  var fullName by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  Surface(
    color = Color.White,
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, SlateBorder),
    shadowElevation = 2.dp,
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(18.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = "Join / Login with Mobile Number",
        style = MaterialTheme.typography.headlineSmall,
      )
      Text(
        text =
          "No email or password needed. Your Worker Profile, Contractor Profile, and Live Bookings sync automatically by your mobile number.",
        style = MaterialTheme.typography.bodyMedium,
        color = SlateTextSecondary,
      )

      OutlinedTextField(
        value = fullName,
        onValueChange = {
          fullName = it
          errorText = null
        },
        label = { Text("Full Name *") },
        placeholder = { Text("e.g. Vikram Kumar") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("account_login_name"),
      )

      OutlinedTextField(
        value = phone,
        onValueChange = { v ->
          phone = v.filter { it.isDigit() }.take(10)
          errorText = null
        },
        label = { Text("10-Digit Indian Mobile Number *") },
        prefix = { Text("+91 ") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("account_login_phone"),
      )

      if (errorText != null) {
        Text(
          text = errorText!!,
          style = MaterialTheme.typography.bodySmall,
          color = CrimsonBusy,
        )
      }

      Spacer(modifier = Modifier.height(2.dp))

      Button(
        onClick = {
          when {
            fullName.trim().length < 2 -> errorText = "Please enter your Full Name."
            phone.length != 10 -> errorText = "Please enter a valid 10-digit mobile number."
            else -> onSubmit(fullName.trim(), phone)
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = NavyDeep),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("account_login_submit"),
      ) {
        Text("Join / Login Now", style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}
