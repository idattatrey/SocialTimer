package com.social.timer.ui.dialog

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import java.text.SimpleDateFormat
import java.util.*

private enum class PickerStep { DATE, TIME }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: (millis: Long) -> Unit,
    initialMillis: Long = System.currentTimeMillis()
) {
    var step by remember { mutableStateOf(PickerStep.DATE) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    val initialCalendar = remember {
        Calendar.getInstance().apply { timeInMillis = initialMillis }
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialCalendar.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCalendar.get(Calendar.MINUTE),
        is24Hour = false
    )

    // Combines the currently chosen date + time into one epoch-millis value
    fun combinedMillis(): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = datePickerState.selectedDateMillis ?: initialMillis
        cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
        cal.set(Calendar.MINUTE, timePickerState.minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        ),
        confirmButton = {
            TextButton(onClick = { onConfirm(combinedMillis()) }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {

                // Tab row to switch between Date and Time steps
                TabRow(selectedTabIndex = step.ordinal) {
                    Tab(
                        selected = step == PickerStep.DATE,
                        onClick = { step = PickerStep.DATE },
                        text = { Text("Date") },
                        icon = {
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Date")
                        }
                    )
                    Tab(
                        selected = step == PickerStep.TIME,
                        onClick = { step = PickerStep.TIME },
                        text = { Text("Time") },
                        icon = {
                            Icon(Icons.Default.Schedule, contentDescription = "Time")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Preview of the currently selected date + time
                val previewFormat = remember { SimpleDateFormat("EEE, d MMM yyyy  •  hh:mm a", Locale.getDefault()) }
                Text(
                    text = previewFormat.format(Date(combinedMillis())),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                when (step) {
                    PickerStep.DATE -> {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = false,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    PickerStep.TIME -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            TimePicker(state = timePickerState)
                        }
                    }
                }
            }
        }
    )
}

/**
 * Example screen showing how to trigger the dialog and display the result.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerDemoScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMillis by remember { mutableStateOf<Long?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { showDialog = true }) {
            Text("Pick date & time")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedMillis?.let { millis ->
            val format = remember { SimpleDateFormat("EEEE, d MMMM yyyy 'at' hh:mm a", Locale.getDefault()) }
            Text("Selected: ${format.format(Date(millis))}")
        }
    }

    if (showDialog) {
        DateTimePickerDialog(
            onDismiss = { showDialog = false },
            onConfirm = { millis ->
                selectedMillis = millis
                showDialog = false
            }
        )
    }
}
