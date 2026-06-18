package com.example.wordofday.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wordofday.data.model.WordEntry

// [TRACE: DOCS/ROADMAP.md] — shared rich word detail cards
@Composable
fun WordDetailContent(
    word: WordEntry,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        DetailCard(label = "Definition", body = word.definition)
        if (word.synonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard(
                label = "Synonyms",
                body = word.synonyms.joinToString(", "),
            )
        }
        if (word.usageNote.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard(label = "Usage tip", body = word.usageNote)
        }
        if (word.example.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard(label = "Example", body = "\"${word.example}\"", italic = true)
        }
        if (word.etymology.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            DetailCard(label = "Etymology", body = word.etymology, subdued = true)
        }
    }
}

@Composable
private fun DetailCard(
    label: String,
    body: String,
    italic: Boolean = false,
    subdued: Boolean = false,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                color = if (subdued) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                lineHeight = 26.sp,
            )
        }
    }
}
