package com.example.wordofday.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.wordofday.data.model.WordHistoryEntry
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// [TRACE: DOCS/ROADMAP.md] — §10a calendar history view
@Composable
fun HistoryCalendar(
    history: List<WordHistoryEntry>,
    onOpenWord: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var month by remember { mutableStateOf(YearMonth.now()) }
    val byDate = remember(history) { history.associateBy { it.date } }
    val today = remember { LocalDate.now() }

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { month = month.minusMonths(1) }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
            }
            Text(
                text = "${month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${month.year}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(
                onClick = { month = month.plusMonths(1) },
                enabled = month.isBefore(YearMonth.from(today)),
            ) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
            }
        }

        Row(Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEach { dow ->
                Text(
                    text = dow.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        val firstDay = month.atDay(1)
        val leadingBlanks = firstDay.dayOfWeek.ordinal
        val daysInMonth = month.lengthOfMonth()
        val cells = leadingBlanks + daysInMonth
        val rows = (cells + 6) / 7

        for (row in 0 until rows) {
            Row(Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val index = row * 7 + col
                    val dayNumber = index - leadingBlanks + 1
                    if (dayNumber !in 1..daysInMonth) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(dayNumber)
                        val entry = byDate[date]
                        CalendarDayCell(
                            day = dayNumber,
                            isToday = date == today,
                            hasWord = entry != null,
                            wordLabel = entry?.word,
                            onClick = { entry?.let { onOpenWord(it.wordKey) } },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        SpacerHint()
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    hasWord: Boolean,
    wordLabel: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(MaterialTheme.shapes.small)
            .then(if (hasWord) Modifier.clickable(onClick = onClick) else Modifier)
            .background(
                when {
                    hasWord -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                    isToday -> MaterialTheme.colorScheme.surfaceContainerHighest
                    else -> MaterialTheme.colorScheme.surface
                },
            )
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
        )
        if (hasWord) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 4.dp, vertical = 1.dp),
            ) {
                Text(
                    text = wordLabel?.take(3).orEmpty(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
private fun SpacerHint() {
    Text(
        text = "Tap a highlighted day to reopen that word.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 12.dp),
    )
}
