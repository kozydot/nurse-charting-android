package com.example.nursecharting.ui.charting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nursecharting.data.local.entity.Patient

@Composable
fun PersistentPatientHeader(
    patient: Patient?,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit // Callback for edit action
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp // Add some elevation to distinguish it
    ) {
        if (patient != null) {
            BoxWithConstraints {
                val isWideScreen = maxWidth >= 600.dp

                if (isWideScreen) {
                    // Layout for Tablet/Wider Screens
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp), // Adjusted padding slightly for balance
                        verticalAlignment = Alignment.CenterVertically // Align all items in the row vertically
                    ) {
                        // Primary Patient Information
                        Column(
                            modifier = Modifier.weight(1f), // Takes up available space
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = patient.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacer
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "DOB: ${patient.dateOfBirth}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "ID: ${patient.patientId}", // MRN is usually patientId
                                    style = MaterialTheme.typography.bodyMedium, // Consistent style
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Age/Gender can be added here if available in Patient model and needed
                            }
                        }

                        // Spacer to ensure secondary info is pushed to the right,
                        // but primary info already has weight(1f)
                        // Spacer(modifier = Modifier.width(16.dp)) // Optional: add some space before secondary info

                        // Secondary Information Block (Room + Edit icon)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Room: ${patient.roomNumber}",
                                style = MaterialTheme.typography.bodyMedium, // Consistent style
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(48.dp) // Ensure 48dp touch target
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Patient Room Information",
                                    tint = MaterialTheme.colorScheme.primary // Optional: theme color
                                )
                            }
                        }
                    }
                } else {
                    // Layout for Phone/Narrower Screens
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Primary Patient Information
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = patient.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                             Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "DOB: ${patient.dateOfBirth}",
                                    style = MaterialTheme.typography.bodyMedium,
                                     maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "ID: ${patient.patientId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                     maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                // Age/Gender can be added here if available
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp)) // Space between primary and secondary info

                        // Secondary Information Block (Room + Edit icon) - Aligned to the start
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start // Align to the start of the row
                        ) {
                            Text(
                                text = "Room: ${patient.roomNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(48.dp) // Ensure 48dp touch target
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = "Edit Patient Room Information",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Placeholder or loading state if patient data is not yet available
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Loading patient details...", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}