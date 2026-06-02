package com.example.baselift.View.nutrition.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baselift.View.theme.ElectricBlue
import com.example.baselift.View.theme.MediumGrey
import com.example.baselift.View.theme.NeonGreen
import com.example.baselift.View.theme.PureBlack
import com.example.baselift.View.onboarding.MacroAdjustBox

// secção para adicionar registos rápidos
@Composable
fun QuickLogSection(
    onAddEntry: (calories: Int, protein: Int, carbs: Int, fats: Int, isCaloriesOnly: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCaloriesOnly by remember { mutableStateOf(true) }
    var kcalInput by remember { mutableStateOf("") }
    var proteinInput by remember { mutableStateOf("") }
    var carbsInput by remember { mutableStateOf("") }
    var fatsInput by remember { mutableStateOf("") }

    // função para atualizar a caixa das calorias no modo "Calories Only"
    fun updateCalories(delta: Int) {
        val current = kcalInput.toIntOrNull() ?: 0
        kcalInput = (current + delta).coerceAtLeast(0).toString()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF131313), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
            
            // abas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick\nLog",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.width(32.dp))
                
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TabItem(
                        title = "CALORIES\nONLY",
                        isSelected = isCaloriesOnly,
                        onClick = { isCaloriesOnly = true }
                    )
                    TabItem(
                        title = "DETAILED\nMACROS",
                        isSelected = !isCaloriesOnly,
                        onClick = { isCaloriesOnly = false }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // inputs
            if (isCaloriesOnly) {
                MacroAdjustBox(
                    label = "TOTAL CALORIES",
                    unit = "kcal",
                    value = kcalInput,
                    onValueChange = { kcalInput = it.filter { char -> char.isDigit() } },
                    onAdjust = { updateCalories(it) },
                    accentColor = NeonGreen,
                    showBorder = false
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MacroInputField(
                        label = "KCAL",
                        value = kcalInput,
                        onValueChange = { kcalInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f)
                    )
                    MacroInputField(
                        label = "PROTEIN (g)",
                        value = proteinInput,
                        onValueChange = { proteinInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MacroInputField(
                        label = "CARBS (g)",
                        value = carbsInput,
                        onValueChange = { carbsInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f)
                    )
                    MacroInputField(
                        label = "FATS (g)",
                        value = fatsInput,
                        onValueChange = { fatsInput = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // botão adicionar
            Button(
                onClick = {
                    val kcal = kcalInput.toIntOrNull() ?: 0
                    val p = if (isCaloriesOnly) 0 else proteinInput.toIntOrNull() ?: 0
                    val c = if (isCaloriesOnly) 0 else carbsInput.toIntOrNull() ?: 0
                    val f = if (isCaloriesOnly) 0 else fatsInput.toIntOrNull() ?: 0
                    
                    if (kcal > 0 || (!isCaloriesOnly && (p > 0 || c > 0 || f > 0))) {
                        onAddEntry(kcal, p, c, f, isCaloriesOnly)
                        kcalInput = ""
                        proteinInput = ""
                        carbsInput = ""
                        fatsInput = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text(text = "+ ADD ENTRY", color = PureBlack, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
    }
}

@Composable
fun TabItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.White else MediumGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .background(NeonGreen)
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
fun MacroInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = label, color = if (enabled) Color.White else MediumGrey, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty() && enabled) {
                Text("0", color = MediumGrey, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            if (value.isEmpty() && !enabled) {
                Text("0", color = MediumGrey.copy(alpha = 0.3f), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                textStyle = TextStyle(
                    color = if (enabled) Color.White else Color.Transparent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(NeonGreen),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
