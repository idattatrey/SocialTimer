package com.social.timer.ui.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.social.timer.ui.theme.outfitFontFamily

@Composable
fun GenericDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    description: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title, style = TextStyle(
                    fontFamily = outfitFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
        },
        text = {
            Text(
                text = description, style = TextStyle(
                    fontFamily = outfitFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
        },

        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("No")
            }
        })
}