package com.social.timer.ui.dialog

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.social.timer.ui.theme.outfitFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTimingDialog(
    onDismissRequest: () -> Unit, onConfirm: (String, String, Long) -> Unit
) {
    val context = LocalContext.current
    var timeInMinutes by remember { mutableStateOf("") }

    val items =
        listOf(
            "YouTube",
            "Instagram",
            "Threads",
            "X",
            "Zee5",
            "JioHotstar",
            "SonyLiv",
            "Facebook",
            "LinkedIn",
            "Others"
        )
    var showDateTimeDialog by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var selectedPayType by remember { mutableStateOf(items.first()) }
    val paidByIndex = items.indexOf("YouTube")
    selectedPayType = items[paidByIndex]
    var socialTimeInMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(UInt) {
        socialTimeInMillis = System.currentTimeMillis()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest, title = {
            Text(
                text = "Timing", style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
        }, text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Add timing information:", style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = "Social Channel Name:",
                    modifier = Modifier.padding(top = 24.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.padding(top = 16.dp),
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }) {
                    TextField(
                        value = selectedPayType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier.menuAnchor(
                            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true
                        ),
                        textStyle = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded, onDismissRequest = { expanded = false }) {
                        items.forEach { item ->
                            DropdownMenuItem(text = {
                                Text(
                                    item, style = TextStyle(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 16.sp
                                    )
                                )
                            }, onClick = {
                                selectedPayType = item
                                expanded = false
                            })
                        }
                    }
                }
                Button(
                    onClick = {
                        showDateTimeDialog = true
                    }, shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF7771F), contentColor = Color.White
                    ), modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        "SELECT DATE",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 10.dp),
                        style = TextStyle(
                            fontFamily = outfitFontFamily, fontWeight = FontWeight.Bold
                        )
                    )
                }
                Text(
                    text = "Social Channel Time In Minutes:",
                    modifier = Modifier.padding(top = 16.dp),
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
                OutlinedTextField(
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    value = timeInMinutes,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
                            timeInMinutes = newValue
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),

                    label = {
                        Text(
                            "Timing in minutes",
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (showDateTimeDialog) {
                    DateTimePickerDialog(
                        onDismiss = {
                            showDateTimeDialog = false
                        },
                        onConfirm = { millis ->
                            showDateTimeDialog = false
                            socialTimeInMillis = millis
                            Toast.makeText(
                                context,
                                "Date and Time has been selected!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        },

        confirmButton = {
            TextButton(onClick = {
                if (timeInMinutes.isEmpty()) {
                    Toast.makeText(
                        context, "Please provide the contact number!", Toast.LENGTH_SHORT
                    ).show()
                } else if (timeInMinutes.isEmpty()) {
                    Toast.makeText(
                        context, "Please provide time in minutes!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onConfirm(selectedPayType, timeInMinutes, socialTimeInMillis)
                }
            }) {
                Text("Yes")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("No")
            }
        })
}