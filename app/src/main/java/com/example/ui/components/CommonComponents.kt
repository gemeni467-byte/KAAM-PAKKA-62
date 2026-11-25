package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Chat
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Engineering
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.KaamConstants
import com.example.model.LocationScope
import com.example.model.UserSession
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyMuted
import com.example.ui.theme.NavySurface
import com.example.ui.theme.OnAmberContainer
import com.example.ui.theme.OnEmeraldContainer
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateSurfaceVariant
import com.example.ui.theme.SlateTextMuted
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.WhatsAppGreen

@Composable
fun KaamTopHeader(
  session: UserSession,
  onShareClick: () -> Unit,
  onLoginClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = NavyDeep,
    contentColor = Color.White,
    shadowElevation = 4.dp,
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .windowInsetsPadding(WindowInsets.statusBars)
          .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.weight(1f),
        ) {
          Box(
            modifier =
              Modifier.size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(AmberBright),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.Rounded.Engineering,
              contentDescription = "Kaam Connect Logo",
              tint = NavyDeep,
              modifier = Modifier.size(22.dp),
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Kaam Connect",
              style = MaterialTheme.typography.titleLarge,
              color = Color.White,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
            Text(
              text = "Direct Work, Direct Contact — No Middleman",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFCBD5E1),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
          IconButton(
            onClick = onShareClick,
            modifier =
              Modifier.size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NavySurface)
                .testTag("header_share_button"),
          ) {
            Icon(
              imageVector = Icons.Rounded.Share,
              contentDescription = "Share App",
              tint = AmberBright,
              modifier = Modifier.size(20.dp),
            )
          }

          Surface(
            onClick = onLoginClick,
            color = if (session.isLoggedIn) NavySurface else AmberBright,
            contentColor = if (session.isLoggedIn) Color.White else NavyDeep,
            shape = RoundedCornerShape(10.dp),
            border = if (session.isLoggedIn) BorderStroke(1.dp, NavyMuted) else null,
            modifier = Modifier.heightIn(min = 44.dp).testTag("header_login_button"),
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = "Account",
                modifier = Modifier.size(16.dp),
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text =
                  if (session.isLoggedIn) {
                    session.fullName.substringBefore(" ").take(12)
                  } else {
                    "Join / Login"
                  },
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun MyCityAllIndiaToggle(
  scope: LocationScope,
  preferredCity: String,
  onScopeChange: (LocationScope) -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = SlateSurfaceVariant,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = modifier.fillMaxWidth(),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth().padding(4.dp),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      val isMyCity = scope == LocationScope.MY_CITY
      Surface(
        onClick = { onScopeChange(LocationScope.MY_CITY) },
        color = if (isMyCity) NavyDeep else Color.Transparent,
        contentColor = if (isMyCity) Color.White else SlateTextSecondary,
        shape = RoundedCornerShape(9.dp),
        modifier = Modifier.weight(1f).heightIn(min = 42.dp).testTag("scope_my_city"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "My City ($preferredCity)",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
      }

      val isAllIndia = scope == LocationScope.ALL_INDIA
      Surface(
        onClick = { onScopeChange(LocationScope.ALL_INDIA) },
        color = if (isAllIndia) NavyDeep else Color.Transparent,
        contentColor = if (isAllIndia) Color.White else SlateTextSecondary,
        shape = RoundedCornerShape(9.dp),
        modifier = Modifier.weight(1f).heightIn(min = 42.dp).testTag("scope_all_india"),
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Icon(
            imageVector = Icons.Rounded.Public,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "All India",
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
          )
        }
      }
    }
  }
}

@Composable
fun DirectoryFilterPanel(
  locationScope: LocationScope,
  preferredCity: String,
  selectedState: String,
  cityQuery: String,
  selectedTrade: String,
  toggleLabel: String?,
  toggleChecked: Boolean,
  onScopeChange: (LocationScope) -> Unit,
  onStateChange: (String) -> Unit,
  onCityChange: (String) -> Unit,
  onTradeChange: (String) -> Unit,
  onToggleChange: ((Boolean) -> Unit)?,
  onEditMyCityClick: () -> Unit,
) {
  var showFiltersExpanded by remember { mutableStateOf(false) }

  Surface(
    color = Color.White,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      MyCityAllIndiaToggle(
        scope = locationScope,
        preferredCity = preferredCity,
        onScopeChange = onScopeChange,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        OutlinedButton(
          onClick = { showFiltersExpanded = !showFiltersExpanded },
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.heightIn(min = 40.dp).testTag("toggle_filters_button"),
        ) {
          Icon(
            imageVector = Icons.Rounded.FilterList,
            contentDescription = "Filters",
            modifier = Modifier.size(16.dp),
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text =
              if (showFiltersExpanded) "Hide Filters"
              else "Filters: ${if (selectedTrade == "All Trades") "All Trades" else selectedTrade}",
            style = MaterialTheme.typography.labelMedium,
          )
        }

        if (locationScope == LocationScope.MY_CITY) {
          OutlinedButton(
            onClick = onEditMyCityClick,
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.heightIn(min = 40.dp),
          ) {
            Text(
              text = "Change My City",
              style = MaterialTheme.typography.labelMedium,
            )
          }
        }
      }

      AnimatedVisibility(visible = showFiltersExpanded) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          if (locationScope == LocationScope.ALL_INDIA) {
            DropdownSelectorField(
              label = "Filter by State",
              selected = selectedState,
              options = listOf("All States") + KaamConstants.INDIA_STATES,
              onSelect = onStateChange,
              testTag = "filter_state_dropdown",
            )
            OutlinedTextField(
              value = cityQuery,
              onValueChange = onCityChange,
              label = { Text("Filter by City / Site Area") },
              placeholder = { Text("e.g. Rourkela, Patna, Surat") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("filter_city_input"),
            )
          }

          SkillDropdownWithOther(
            label = "Filter by Skill / Trade",
            selectedValue = selectedTrade,
            options = listOf("All Trades") + KaamConstants.SKILLS_LIST,
            onValueChange = onTradeChange,
            testTagPrefix = "filter_trade",
          )
        }
      }

      if (toggleLabel != null && onToggleChange != null) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(SlateSurfaceVariant)
              .clickable { onToggleChange(!toggleChecked) }
              .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(
            text = toggleLabel,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
          )
          Switch(
            checked = toggleChecked,
            onCheckedChange = onToggleChange,
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
}

@Composable
fun DropdownSelectorField(
  label: String,
  selected: String,
  options: List<String>,
  onSelect: (String) -> Unit,
  modifier: Modifier = Modifier,
  testTag: String = "dropdown_field",
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxWidth()) {
    OutlinedTextField(
      value = selected,
      onValueChange = {},
      readOnly = true,
      label = { Text(label) },
      trailingIcon = {
        Icon(
          imageVector = Icons.Rounded.ArrowDropDown,
          contentDescription = "Select $label",
        )
      },
      modifier = Modifier.fillMaxWidth(),
    )
    Box(
      modifier =
        Modifier.matchParentSize()
          .clickable { expanded = true }
          .testTag(testTag)
    )
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
      modifier = Modifier.heightIn(max = 320.dp),
    ) {
      options.forEach { option ->
        DropdownMenuItem(
          text = { Text(option) },
          onClick = {
            onSelect(option)
            expanded = false
          },
          trailingIcon = {
            if (option == selected) {
              Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = EmeraldSuccess,
              )
            }
          },
        )
      }
    }
  }
}

@Composable
fun SkillDropdownWithOther(
  label: String,
  selectedValue: String,
  options: List<String> = KaamConstants.SKILLS_LIST,
  onValueChange: (String) -> Unit,
  testTagPrefix: String = "skill",
) {
  val isPreset = selectedValue in options && selectedValue != KaamConstants.OTHER_SKILL_OPTION
  var isOtherSelected by remember(selectedValue) {
    mutableStateOf(
      selectedValue == KaamConstants.OTHER_SKILL_OPTION ||
        (selectedValue.isNotBlank() && !isPreset)
    )
  }
  var customText by remember(selectedValue) {
    mutableStateOf(
      if (selectedValue != KaamConstants.OTHER_SKILL_OPTION && !isPreset) selectedValue else ""
    )
  }

  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    DropdownSelectorField(
      label = label,
      selected = if (isOtherSelected) KaamConstants.OTHER_SKILL_OPTION else selectedValue,
      options = options,
      onSelect = { chosen ->
        if (chosen == KaamConstants.OTHER_SKILL_OPTION) {
          isOtherSelected = true
          onValueChange(customText)
        } else {
          isOtherSelected = false
          customText = ""
          onValueChange(chosen)
        }
      },
      testTag = "${testTagPrefix}_dropdown",
    )

    AnimatedVisibility(visible = isOtherSelected) {
      OutlinedTextField(
        value = customText,
        onValueChange = { typed ->
          customText = typed
          onValueChange(typed)
        },
        label = { Text("Write Your Own Skill / Trade") },
        placeholder = { Text("e.g. Crane Operator, Scaffolder, Grinder") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("${testTagPrefix}_other_input"),
      )
    }
  }
}

@Composable
fun StateAndCitySelector(
  stateValue: String,
  cityValue: String,
  onStateChange: (String) -> Unit,
  onCityChange: (String) -> Unit,
  testTagPrefix: String = "location",
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    DropdownSelectorField(
      label = "State",
      selected = stateValue.ifBlank { KaamConstants.DEFAULT_STATE },
      options = KaamConstants.INDIA_STATES,
      onSelect = onStateChange,
      testTag = "${testTagPrefix}_state_dropdown",
    )

    OutlinedTextField(
      value = cityValue,
      onValueChange = onCityChange,
      label = { Text("City / Site Area") },
      placeholder = { Text("e.g. Rourkela, Kansbahal, Patna") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth().testTag("${testTagPrefix}_city_input"),
    )

    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
      KaamConstants.QUICK_CITIES.forEach { quickCity ->
        val selected = cityValue.equals(quickCity, ignoreCase = true)
        FilterChip(
          selected = selected,
          onClick = { onCityChange(quickCity) },
          label = { Text(quickCity, style = MaterialTheme.typography.labelMedium) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = AmberContainer,
              selectedLabelColor = OnAmberContainer,
            ),
        )
      }
    }
  }
}

@Composable
fun StatusPill(
  text: String,
  containerColor: Color,
  contentColor: Color,
  dotColor: Color? = null,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = containerColor,
    contentColor = contentColor,
    shape = RoundedCornerShape(50),
    modifier = modifier,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      if (dotColor != null) {
        Box(
          modifier = Modifier.size(7.dp).clip(CircleShape).background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
      )
    }
  }
}

@Composable
fun DirectContactButtons(
  onCallClick: () -> Unit,
  onWhatsAppClick: () -> Unit,
  highlighted: Boolean = true,
  modifier: Modifier = Modifier,
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
  ) {
    Button(
      onClick = onCallClick,
      colors =
        ButtonDefaults.buttonColors(
          containerColor = if (highlighted) NavyDeep else SlateTextMuted,
          contentColor = Color.White,
        ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("call_now_button"),
    ) {
      Icon(
        imageVector = Icons.Rounded.Call,
        contentDescription = "Call Now",
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text("Call Now", style = MaterialTheme.typography.labelLarge)
    }

    Button(
      onClick = onWhatsAppClick,
      colors =
        ButtonDefaults.buttonColors(
          containerColor = if (highlighted) WhatsAppGreen else SlateTextMuted,
          contentColor = Color.White,
        ),
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("whatsapp_button"),
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Rounded.Chat,
        contentDescription = "WhatsApp",
        modifier = Modifier.size(18.dp),
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text("WhatsApp", style = MaterialTheme.typography.labelLarge)
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FacilityBadgesRow(facilities: List<String>) {
  if (facilities.isEmpty()) return
  FlowRow(
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp),
  ) {
    facilities.forEach { facility ->
      StatusPill(
        text = facility,
        containerColor = EmeraldContainer,
        contentColor = OnEmeraldContainer,
      )
    }
  }
}

@Composable
fun EmptyStateCard(
  icon: ImageVector,
  title: String,
  subtitle: String,
  ctaLabel: String,
  onCtaClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(18.dp),
    border = BorderStroke(1.dp, SlateBorder),
    modifier = modifier.fillMaxWidth(),
  ) {
    Column(
      modifier = Modifier.fillMaxWidth().padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Box(
        modifier =
          Modifier.size(56.dp)
            .clip(CircleShape)
            .background(AmberContainer),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = AmberPrimary,
          modifier = Modifier.size(28.dp),
        )
      }
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = SlateTextSecondary,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Button(
        onClick = onCtaClick,
        colors =
          ButtonDefaults.buttonColors(
            containerColor = AmberPrimary,
            contentColor = Color.White,
          ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.heightIn(min = 48.dp).testTag("empty_state_cta"),
      ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text(ctaLabel, style = MaterialTheme.typography.labelLarge)
      }
    }
  }
}
