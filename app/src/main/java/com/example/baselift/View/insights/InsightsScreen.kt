package com.example.baselift.View.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.baselift.Model.local.entity.UserEntity
import com.example.baselift.View.theme.*
import com.example.baselift.ViewModel.onboarding.OnboardingViewModel

@Composable
fun InsightsScreen(
    viewModel: OnboardingViewModel, // mesmo ViewModel para aceder aos dados do utilizador
    onRecalibrate: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        // Top Logo
        Text(
            text = "BASELIFT",
            color = NeonGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Baseline Metrics Section
        Text("Baseline Metrics", color = CrystalWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(label = "GENDER", value = uiState.gender, unit = "", modifier = Modifier.weight(1f))
            MetricCard(label = "AGE", value = uiState.age.toString(), unit = "YRS", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(label = "HEIGHT", value = uiState.height.toString(), unit = uiState.preferredHeightUnit, modifier = Modifier.weight(1f))
            MetricCard(
                label = "CURRENT WEIGHT", 
                value = uiState.weight.toString(), 
                unit = uiState.preferredWeightUnit, 
                modifier = Modifier.weight(1f),
                borderColor = NeonGreen,
                valueColor = NeonGreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        MetricCard(label = "LIFESTYLE", value = uiState.activityLevel.uppercase(), unit = "", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        // Recalibrate Button
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .clickable { onRecalibrate() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Refresh, contentDescription = "Recalibrate", tint = NeonGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("RECALIBRATE BASELINE", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // BMI Section
        val bmiColor = getBmiColor(uiState.bmi)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("BODY MASS INDEX", color = CrystalWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(bmiColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(getBmiCategory(uiState.bmi), color = bmiColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(uiState.bmi.toString(), color = bmiColor, fontSize = 48.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Custom BMI Bar
        BmiBar(uiState.bmi)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Your current BMI indicates ${getBmiCategory(uiState.bmi)} levels for your height and weight metrics.",
            color = MediumGrey,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurface)
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Default.Info, contentDescription = "Info", tint = ElectricBlue, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Note: BMI is not an absolute health indicator. It does not evaluate body composition, muscle mass, or localized fat.",
                color = MediumGrey,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // As secções "Weight Trend", "Log Weight" e "Visual Diary" serão adicionadas na Phase 5.
        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }
}

@Composable
fun MetricCard(
    label: String, 
    value: String, 
    unit: String, 
    modifier: Modifier = Modifier, 
    borderColor: Color = Color.Transparent,
    valueColor: Color = CrystalWhite
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(label, color = MediumGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(unit, color = MediumGrey, fontSize = 12.sp, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
fun BmiBar(bmi: Float) {
    Column(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val maxBmiRange = 40f
            val fraction = (bmi / maxBmiRange).coerceIn(0f, 1f)
            val offsetDp = maxWidth * fraction
            
            Icon(
                Icons.Default.ArrowDropDown, 
                contentDescription = "Your BMI", 
                tint = CrystalWhite, 
                modifier = Modifier.offset(x = offsetDp - 12.dp)
            )
        }
        
        Row(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))) {
            Box(modifier = Modifier.weight(18.5f).fillMaxHeight().background(ElectricBlue))
            Box(modifier = Modifier.weight(6.5f).fillMaxHeight().background(NeonGreen))
            Box(modifier = Modifier.weight(5f).fillMaxHeight().background(SunYellow))
            Box(modifier = Modifier.weight(10f).fillMaxHeight().background(SoftCoral))
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(18.5f), contentAlignment = Alignment.TopEnd) {
                Text("18.5", color = MediumGrey, fontSize = 10.sp, modifier = Modifier.offset(x = 10.dp))
            }
            Box(modifier = Modifier.weight(6.5f), contentAlignment = Alignment.TopEnd) {
                Text("25", color = MediumGrey, fontSize = 10.sp, modifier = Modifier.offset(x = 6.dp))
            }
            Box(modifier = Modifier.weight(5f), contentAlignment = Alignment.TopEnd) {
                Text("30", color = MediumGrey, fontSize = 10.sp, modifier = Modifier.offset(x = 6.dp))
            }
            Box(modifier = Modifier.weight(10f))
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(18.5f), contentAlignment = Alignment.Center) {
                Text("UNDER", color = ElectricBlue, fontSize = 10.sp)
            }
            Box(modifier = Modifier.weight(6.5f), contentAlignment = Alignment.Center) {
                Text("NORMAL", color = NeonGreen, fontSize = 10.sp)
            }
            Box(modifier = Modifier.weight(5f), contentAlignment = Alignment.Center) {
                Text("OVER", color = SunYellow, fontSize = 10.sp)
            }
            Box(modifier = Modifier.weight(10f), contentAlignment = Alignment.Center) {
                Text("OBESE", color = SoftCoral, fontSize = 10.sp)
            }
        }
    }
}

fun getBmiCategory(bmi: Float): String {
    return when {
        bmi < 18.5f -> "UNDERWEIGHT"
        bmi < 25f -> "HEALTHY RANGE"
        bmi < 30f -> "OVERWEIGHT"
        else -> "OBESE"
    }
}

fun getBmiColor(bmi: Float): Color {
    return when {
        bmi < 18.5f -> ElectricBlue
        bmi < 25f -> NeonGreen
        bmi < 30f -> SunYellow
        else -> SoftCoral
    }
}
