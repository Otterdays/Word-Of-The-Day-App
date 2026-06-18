package com.example.wordofday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.wordofday.data.content.WordContentQuality
import com.example.wordofday.data.model.ContentSource
import com.example.wordofday.data.model.WordEntry
import com.example.wordofday.ui.theme.AppSpacing

// [TRACE: DOCS/ROADMAP.md] — shared rich word detail cards
@Composable
fun WordDetailContent(
    word: WordEntry,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        DetailCard(label = "Definition", body = word.definition)
        val synonyms = WordContentQuality.cleanedSynonyms(word)
        if (synonyms.isNotEmpty()) {
            Spacer(modifier = Modifier.height(AppSpacing.detailGap))
            DetailCard(
                label = "Synonyms",
                body = synonyms.joinToString(", "),
            )
        }
        if (word.usageNote.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.detailGap))
            DetailCard(label = "Usage tip", body = word.usageNote)
        }
        if (word.example.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.detailGap))
            DetailCard(label = "Example", body = "\"${word.example}\"", italic = true)
        }
        if (word.etymology.isNotBlank()) {
            Spacer(modifier = Modifier.height(AppSpacing.detailGap))
            DetailCard(label = "Etymology", body = word.etymology, subdued = true)
        }
        if (word.source != ContentSource.CURATED) {
            Spacer(modifier = Modifier.height(AppSpacing.detailGap))
            DetailCard(
                label = "Source",
                body = "${word.source.displayLabel} — ${word.source.attribution}",
                subdued = true,
            )
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
        shape = RoundedCornerShape(AppSpacing.cardRadius),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(AppSpacing.cardPadding)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.5.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
                color = if (subdued) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                lineHeight = 24.sp,
            )
        }
    }
}
