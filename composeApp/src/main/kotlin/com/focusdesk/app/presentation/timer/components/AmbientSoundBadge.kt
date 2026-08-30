package com.focusdesk.app.presentation.timer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.domain.model.Soundscape

@Composable
fun AmbientSoundBadge(
    soundscape: Soundscape,
    isPlaying: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (soundscape) {
        Soundscape.None -> Icons.AutoMirrored.Filled.VolumeOff
        Soundscape.Rain -> Icons.Default.WaterDrop
        Soundscape.Forest -> Icons.Default.Forest
        Soundscape.Cafe -> Icons.Default.LocalCafe
        Soundscape.Ocean -> Icons.Default.Waves
        Soundscape.WhiteNoise -> Icons.Default.Air
    }

    val tintColor = if (isPlaying && soundscape != Soundscape.None) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = soundscape.title,
                tint = tintColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = soundscape.title,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = tintColor
            )
        }
    }
}
