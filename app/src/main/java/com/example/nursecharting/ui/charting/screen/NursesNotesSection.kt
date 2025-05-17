package com.example.nursecharting.ui.charting.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width // Added import
import androidx.compose.foundation.layout.heightIn // Added import
import androidx.compose.foundation.layout.size // Added import
import androidx.compose.foundation.layout.widthIn // Added import
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions // Added import
import androidx.compose.foundation.shape.RoundedCornerShape // Added import
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.nursecharting.data.local.entity.NurseNote
import com.example.nursecharting.ui.theme.Teal // To be added in Color.kt
import com.example.nursecharting.utils.toFormattedString // Use centralized util

@Composable
fun NursesNotesSection(
    patientId: String,
    nurseNotes: List<NurseNote>,
    onAddNote: (NurseNote) -> Unit,
    onDeleteNote: (NurseNote) -> Unit, // Added delete callback
    modifier: Modifier = Modifier
) {
    var noteText by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = "Nurse's Notes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        // Note Input Field
        OutlinedTextField(
            value = noteText,
            onValueChange = { noteText = it },
            placeholder = { Text("Enter note") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .heightIn(min = 120.dp), // Changed minHeight to heightIn
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions( // Use constructor
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default
            ),
            singleLine = false,
            shape = MaterialTheme.shapes.small
        )

        // Add Entry Button
        Button(
            onClick = {
                if (noteText.isNotBlank()) {
                    val newNote = NurseNote(
                        patientId = patientId,
                        timestamp = System.currentTimeMillis(),
                        noteText = noteText
                    )
                    onAddNote(newNote)
                    noteText = "" // Clear field after adding
                }
            },
            enabled = noteText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Teal, // #009688
                contentColor = Color.White,
                disabledContainerColor = Teal.copy(alpha = 0.5f) // Indicate disabled state
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            // Icon removed as per request
            // Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing)) // Spacer for icon removed
            Text(
                text = "Add Entry", // Text changed
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )
        }

        // Display existing notes
        if (nurseNotes.isNotEmpty()) {
            Text(
                text = "Existing Notes:", // Simple header for the list
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .heightIn(max = 300.dp), // Limit height if many notes
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nurseNotes, key = { it.id }) { note ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f).padding(end = 8.dp)) {
                                Text(
                                    text = "Time: ${note.timestamp.toFormattedString()}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.noteText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(
                                onClick = { onDeleteNote(note) },
                                modifier = Modifier.size(40.dp) // Ensure tappable area
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete Note",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No nurse's notes recorded yet for this patient.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
            )
        }
    }
}