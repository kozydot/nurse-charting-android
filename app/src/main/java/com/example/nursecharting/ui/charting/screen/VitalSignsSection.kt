package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nursecharting.data.local.entity.VitalSign
import com.example.nursecharting.ui.theme.DarkGrey
import com.example.nursecharting.ui.theme.PrimaryBlue
import com.example.nursecharting.utils.toFormattedString

@Composable
fun VitalSignsSection(
    vitalSigns: List<VitalSign>,
    onDeleteVitalSign: (VitalSign) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Vital Signs History",
            style = MaterialTheme.typography.titleLarge,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (vitalSigns.isEmpty()) {
            Text(
                text = "No vital signs recorded.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 8.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vitalSigns.sortedByDescending { it.timestamp }, key = { it.id }) { vitalSignEntry ->
                    VitalSignHistoryCard(
                        vitalSignEntry = vitalSignEntry,
                        onDelete = { onDeleteVitalSign(vitalSignEntry) }
                    )
                }
            }
        }
    }
}

@Composable
fun VitalSignHistoryCard(
    vitalSignEntry: VitalSign,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recorded: ${vitalSignEntry.timestamp.toFormattedString()}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Vital Sign Entry",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    VitalSignItem("BP", "${vitalSignEntry.systolicBP}/${vitalSignEntry.diastolicBP} mmHg")
                    Spacer(modifier = Modifier.height(8.dp))
                    VitalSignItem("RR", "${vitalSignEntry.respiratoryRate} breaths/min")
                    Spacer(modifier = Modifier.height(8.dp))
                    VitalSignItem("SpO2", "${vitalSignEntry.spO2}%")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    VitalSignItem("HR", "${vitalSignEntry.heartRate} bpm")
                    Spacer(modifier = Modifier.height(8.dp))
                    VitalSignItem("Temp", "${vitalSignEntry.temperature}${vitalSignEntry.temperatureUnit}")
                    Spacer(modifier = Modifier.height(8.dp))
                    vitalSignEntry.painScore?.takeIf { it.isNotBlank() }?.let {
                        VitalSignItem("Pain", it)
                    } ?: VitalSignItem("Pain", "N/A")
                }
            }
        }
    }
}

@Composable
fun VitalSignItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.Start) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}