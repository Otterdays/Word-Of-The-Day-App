package com.example.wordofday.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wordofday.data.model.Category
import com.example.wordofday.ui.theme.AppSpacing

// [TRACE: DOCS/ROADMAP.md] — §9a grouped searchable interest picker
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestPicker(
    selected: Set<Category>,
    onToggle: (Category) -> Unit,
    maxSelection: Int,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    onSearchChange: ((String) -> Unit)? = null,
) {
    val query = searchQuery.trim().lowercase()
    val sections = remember(query) {
        Category.InterestSections.mapNotNull { (group, items) ->
            val filtered = if (query.isEmpty()) {
                items
            } else {
                items.filter {
                    it.displayLabel.lowercase().contains(query) ||
                        group.title.lowercase().contains(query)
                }
            }
            if (filtered.isEmpty()) null else group to filtered
        }
    }

    Column(modifier) {
        if (onSearchChange != null) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search ${Category.entries.size} interests…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
            )
            Spacer(Modifier.height(8.dp))
        }
        Text(
            text = "${selected.size} / $maxSelection selected",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(6.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            sections.forEach { (group, items) ->
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.chipGap),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.chipGap),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    items.forEach { cat ->
                        val maxReached = cat !in selected && selected.size >= maxSelection
                        FilterChip(
                            selected = cat in selected,
                            onClick = { if (!maxReached || cat in selected) onToggle(cat) },
                            enabled = !maxReached || cat in selected,
                            label = { Text(cat.displayLabel, maxLines = 1) },
                            colors = FilterChipDefaults.filterChipColors(),
                        )
                    }
                }
            }
        }
    }
}
