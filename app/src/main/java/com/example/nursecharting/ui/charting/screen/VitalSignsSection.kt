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
import com.example.nursecharting.utils.toFormattedString // Corrected import

@Composable
fun VitalSignsSection(
    vitalSigns: List<VitalSign>,
    onDeleteVitalSign: (VitalSign) -> Unit, // Added delete callback
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Vital Signs History", // Changed title
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
                        onDelete = { onDeleteVitalSign(vitalSignEntry) } // Pass delete action
                    )
                }
            }
        }
    }
}

@Composable
fun VitalSignHistoryCard(
    vitalSignEntry: VitalSign,
    onDelete: () -> Unit // Callback for delete action
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
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Adjusted padding

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    VitalSignDisplayItem("BP", "${vitalSignEntry.systolicBP}/${vitalSignEntry.diastolicBP} mmHg")
                    VitalSignDisplayItem("RR", "${vitalSignEntry.respiratoryRate} breaths/min")
                    VitalSignDisplayItem("SpO2", "${vitalSignEntry.spO2}%")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    VitalSignDisplayItem("HR", "${vitalSignEntry.heartRate} bpm")
                    VitalSignDisplayItem("Temp", "${vitalSignEntry.temperature}${vitalSignEntry.temperatureUnit}")
                    vitalSignEntry.painScore?.takeIf { it.isNotBlank() }?.let {
                        VitalSignDisplayItem("Pain", it)
                    }
                }
            }
        }
    }
}

@Composable
fun ColumnScope.VitalSignDisplayItem(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = DarkGrey
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}