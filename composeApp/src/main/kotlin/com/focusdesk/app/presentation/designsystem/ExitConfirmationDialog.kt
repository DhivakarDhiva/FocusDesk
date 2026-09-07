package com.focusdesk.app.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.focusdesk.core.platform.HapticFeedbackType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExitConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmExit: () -> Unit
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .width(310.dp)
                .shadow(16.dp, RoundedCornerShape(26.dp), ambientColor = Color.Black.copy(alpha = 0.15f))
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFF7F8F5).copy(alpha = 0.98f))
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Exit FocusDesk?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Are you sure you want to close the app?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = themeColors.textSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFFE2E5DF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.textPrimary
                        )
                    }

                    // Exit Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFFE2E5DF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Warning)
                                onConfirmExit()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Exit",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD94D4D)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndSessionConfirmationDialog(
    onDismissRequest: () -> Unit,
    onConfirmEnd: () -> Unit
) {
    val themeColors = LocalFocusDeskColors.current
    val hapticEngine = LocalHapticEngine.current

    BasicAlertDialog(
        onDismissRequest = onDismissRequest
    ) {
        Box(
            modifier = Modifier
                .width(310.dp)
                .shadow(16.dp, RoundedCornerShape(26.dp), ambientColor = Color.Black.copy(alpha = 0.15f))
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFFF7F8F5).copy(alpha = 0.98f))
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Leave Focus Session?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.textPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "A focus session is currently in progress. Do you want to end it early?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    color = themeColors.textSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Keep Focusing Button
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFFE2E5DF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Light)
                                onDismissRequest()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keep Focusing",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = themeColors.textPrimary
                        )
                    }

                    // End Session Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clip(RoundedCornerShape(23.dp))
                            .background(Color(0xFFE2E5DF))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                hapticEngine.perform(HapticFeedbackType.Warning)
                                onConfirmEnd()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "End",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD94D4D)
                        )
                    }
                }
            }
        }
    }
}
