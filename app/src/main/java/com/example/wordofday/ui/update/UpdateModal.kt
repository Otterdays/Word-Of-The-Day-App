package com.example.wordofday.ui.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wordofday.data.release.AppRelease

// [TRACE: DOCS/EDITIONS_ROADMAP.md] — shown when versionCode advances past last seen
@Composable
fun UpdateModal(
    releases: List<AppRelease>,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (releases.isEmpty()) return

    val primary = releases.first()
    val olderCount = releases.size - 1

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = "What's new",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = primary.editionName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Version ${primary.versionName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                primary.highlights.forEach { line ->
                    Text(
                        text = "• $line",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                if (olderCount > 0) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "+ $olderCount earlier release${if (olderCount == 1) "" else "s"} also included",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Got it")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}
