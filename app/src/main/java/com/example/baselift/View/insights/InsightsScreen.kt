package com.example.baselift.View.insights

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.baselift.Model.local.entity.PhotoLogEntity
import com.example.baselift.Model.local.entity.UserEntity
import com.example.baselift.Model.local.entity.WeightLogEntity
import com.example.baselift.View.theme.*
import com.example.baselift.ViewModel.onboarding.OnboardingViewModel
import com.example.baselift.ViewModel.progress.ProgressViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun InsightsScreen(
    onboardingViewModel: OnboardingViewModel,
    progressViewModel: ProgressViewModel,
    onRecalibrate: () -> Unit
) {
    val uiState by onboardingViewModel.uiState.collectAsStateWithLifecycle()
    val weightLogs by progressViewModel.weightLogs.collectAsStateWithLifecycle()
    val photoLogs by progressViewModel.photoLogs.collectAsStateWithLifecycle()
    val user by progressViewModel.user.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var selectedUrisForLogging by remember { mutableStateOf<List<Uri>?>(null) }
    var clickedPhoto by remember { mutableStateOf<PhotoLogEntity?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedUrisForLogging = uris
        }
    }

    val profilePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            progressViewModel.updateProfilePhoto(context, uri)
        }
    }

    var showNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf(user?.name ?: "") }

    LaunchedEffect(user) {
        user?.let { currentUser ->
            nameInput = currentUser.name ?: ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        // cabeçalho de perfil
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // fotografia de perfil
            Box(
                modifier = Modifier.size(105.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(2.dp, NeonGreen, CircleShape)
                        .background(DeepCharcoal)
                        .clickable {
                            profilePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (!user?.profilePhotoUri.isNullOrEmpty()) {
                        AsyncImage(
                            model = user?.profilePhotoUri,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "No Profile Photo",
                            tint = MediumGrey,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // ícone da câmara
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(NeonGreen)
                        .clickable {
                            profilePhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = PureBlack,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // nome do utilizador
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { showNameDialog = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = (user?.name ?: "SEM NOME").uppercase(),
                    color = CrystalWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Name",
                    tint = MediumGrey,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // métricas base
        Text("Baseline Metrics", color = CrystalWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(label = "GENDER", value = uiState.gender, unit = "", icon = Icons.Default.Person, modifier = Modifier.weight(1f))
            MetricCard(label = "AGE", value = uiState.age.toString(), unit = "YRS", icon = Icons.Default.DateRange, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            MetricCard(label = "HEIGHT", value = uiState.height.toString(), unit = uiState.preferredHeightUnit, icon = Icons.Default.Height, modifier = Modifier.weight(1f))
            MetricCard(
                label = "CURRENT WEIGHT", 
                value = uiState.weight.toString(), 
                unit = uiState.preferredWeightUnit, 
                icon = Icons.Default.FitnessCenter,
                modifier = Modifier.weight(1f),
                borderColor = NeonGreen,
                valueColor = NeonGreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        MetricCard(label = "LIFESTYLE", value = uiState.activityLevel.uppercase(), unit = "", icon = Icons.Default.DirectionsRun, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        // botão de recalibrar
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

        // secção de IMC
        val bmiColor = getBmiColor(uiState.bmi)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
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
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        // tendência de peso
        WeightTrendSection(
            weightLogs = weightLogs, 
            targetWeight = user?.targetWeight,
            onSetTargetWeight = { progressViewModel.setTargetWeight(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // registo de peso
        LogWeightSection { weight, timestamp ->
            progressViewModel.addWeightLog(weight, timestamp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // histórico
        TechnicalHistoryLedger(weightLogs, user, onDelete = { progressViewModel.deleteWeightLog(it) })

        Spacer(modifier = Modifier.height(48.dp))

        // diário visual
        VisualDiarySection(
            photoLogs = photoLogs,
            onPhotoClick = { clickedPhoto = it }
        ) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        Spacer(modifier = Modifier.height(100.dp)) // espaço para navegação inferior
    }

    // diálogo para adicionar foto
    if (selectedUrisForLogging != null) {
        var photoTimestamp by remember { mutableStateOf(System.currentTimeMillis()) }
        val photoCalendar = Calendar.getInstance()
        val photoDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        Dialog(onDismissRequest = { selectedUrisForLogging = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PureBlack)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "ASSOCIATE DATE TO PHOTOS",
                            color = NeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Select the date for the ${selectedUrisForLogging?.size ?: 0} selected progress photos:",
                            color = CrystalWhite,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // caixa de seleção de data
                        OutlinedTextField(
                            value = photoDateFormat.format(Date(photoTimestamp)),
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = MediumGrey)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val dpd = DatePickerDialog(
                                        context,
                                        { _, year, month, dayOfMonth ->
                                            val cal = Calendar.getInstance()
                                            cal.set(year, month, dayOfMonth)
                                            if (cal.timeInMillis <= System.currentTimeMillis()) {
                                                photoTimestamp = cal.timeInMillis
                                            }
                                        },
                                        photoCalendar.get(Calendar.YEAR),
                                        photoCalendar.get(Calendar.MONTH),
                                        photoCalendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                    dpd.datePicker.maxDate = System.currentTimeMillis()
                                    dpd.show()
                                },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color.White.copy(alpha = 0.15f),
                                disabledContainerColor = PureBlack,
                                disabledTextColor = CrystalWhite
                            ),
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { selectedUrisForLogging = null }) {
                                Text("CANCEL", color = MediumGrey)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    selectedUrisForLogging?.let { uris ->
                                        progressViewModel.addPhotoLogs(context, uris, photoTimestamp)
                                    }
                                    selectedUrisForLogging = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("SAVE PHOTOS", color = PureBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // diálogo de detalhe da foto
    clickedPhoto?.let { photo ->
        PhotoDetailDialog(
            photoLog = photo,
            weightLogs = weightLogs,
            onDismiss = { clickedPhoto = null },
            onDelete = {
                progressViewModel.deletePhotoLog(photo)
                clickedPhoto = null
            }
        )
    }

    if (showNameDialog) {
        Dialog(onDismissRequest = { showNameDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PureBlack)
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EDITAR NOME",
                            color = NeonGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Nome", color = MediumGrey) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGreen,
                                unfocusedBorderColor = PureBlack,
                                focusedContainerColor = PureBlack,
                                unfocusedContainerColor = PureBlack,
                                focusedTextColor = CrystalWhite,
                                unfocusedTextColor = CrystalWhite
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { showNameDialog = false }) {
                                Text("CANCELAR", color = MediumGrey)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    progressViewModel.updateProfileName(nameInput.ifBlank { null })
                                    showNameDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("GRAVAR", color = PureBlack, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeightTrendSection(weightLogs: List<WeightLogEntity>, targetWeight: Float?, onSetTargetWeight: (Float?) -> Unit) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("7D", "30D", "1Y", "ALL")
    
    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInput by remember { mutableStateOf(targetWeight?.toString() ?: "") }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Weight Trend", color = CrystalWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (selectedFilter == filter) NeonGreen else DarkSurface)
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        filter, 
                        color = if (selectedFilter == filter) PureBlack else CrystalWhite, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // caixa do gráfico
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
    ) {
        val now = System.currentTimeMillis()
        val filteredLogs = when (selectedFilter) {
            "7D" -> weightLogs.filter { now - it.timestamp <= 7L * 24 * 60 * 60 * 1000 }
            "30D" -> weightLogs.filter { now - it.timestamp <= 30L * 24 * 60 * 60 * 1000 }
            "1Y" -> weightLogs.filter { now - it.timestamp <= 365L * 24 * 60 * 60 * 1000 }
            else -> weightLogs
        }
        val validLogs = if (filteredLogs.isEmpty()) weightLogs.takeLast(1) else filteredLogs

        CustomWeightChart(validLogs, targetWeight)
        
        if (validLogs.isNotEmpty()) {
            val latestLog = validLogs.last()
            val previousLog = if (validLogs.size > 1) validLogs[validLogs.size - 2] else latestLog
            val delta = latestLog.weightValue - previousLog.weightValue
            val sign = if (delta > 0) "+" else ""
            
            Column(modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)) {
                Text("LATEST", color = CrystalWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
                Text("${sign}${String.format(Locale.US, "%.1f", delta)} KG", color = NeonGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // botões de adicionar e remover objetivo
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        if (targetWeight != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, SoftCoral, RoundedCornerShape(8.dp))
                    .clickable { onSetTargetWeight(null) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Goal", tint = SoftCoral, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("REMOVE GOAL", color = SoftCoral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ElectricBlue.copy(alpha = 0.2f))
                    .clickable { showGoalDialog = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("ADD GOAL +", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showGoalDialog) {
        Dialog(onDismissRequest = { showGoalDialog = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PureBlack)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Text("Set Target Goal", color = NeonGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { goalInput = it.replace("\n", "").replace("\r", "") },
                    label = { Text("Target Weight (KG)", color = MediumGrey) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = PureBlack,
                        focusedContainerColor = PureBlack,
                        unfocusedContainerColor = PureBlack,
                        focusedTextColor = CrystalWhite,
                        unfocusedTextColor = CrystalWhite
                    )
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { showGoalDialog = false }) {
                        Text("CANCEL", color = MediumGrey)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val w = goalInput.toFloatOrNull()
                            if (w != null && w > 0) {
                                onSetTargetWeight(w)
                            }
                            showGoalDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("SAVE", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomWeightChart(weightLogs: List<WeightLogEntity>, targetWeight: Float?) {
    if (weightLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No data available yet.", color = MediumGrey)
        }
        return
    }

    val minWeight = weightLogs.minOf { it.weightValue }
    val maxWeight = weightLogs.maxOf { it.weightValue }
    
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 28f
            isAntiAlias = true
        }
    }
    
    val dateTextPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.LTGRAY
            textSize = 26f
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    
    val targetPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#00E5FF") // ElectricBlue
            textSize = 24f
            isAntiAlias = true
        }
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedOffset by remember { mutableStateOf(Offset.Zero) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .padding(top = 16.dp, bottom = 16.dp, end = 24.dp, start = 8.dp)
        .pointerInput(weightLogs) {
            detectTapGestures { tapOffset ->
                // lógica para encontrar o ponto mais próximo
                val width = size.width
                val height = size.height
                
                val leftPadding = 80f
                val bottomPadding = 40f
                val topPadding = 40f
                
                val chartWidth = width - leftPadding
                val chartHeight = height - bottomPadding
                
                val yPadding = if (maxWeight == minWeight) 5f else (maxWeight - minWeight) * 0.2f
                var yMin = (minWeight - yPadding).coerceAtLeast(0f)
                var yMax = maxWeight + yPadding
                
                if (targetWeight != null) {
                    if (targetWeight < yMin) yMin = (targetWeight - yPadding).coerceAtLeast(0f)
                    if (targetWeight > yMax) yMax = targetWeight + yPadding
                }
                
                val yRange = yMax - yMin
                val stepX = if (weightLogs.size > 1) chartWidth / (weightLogs.size - 1) else chartWidth / 2f
                
                var closestIndex: Int? = null
                var minDistance = Float.MAX_VALUE
                var closestOffset = Offset.Zero
                
                weightLogs.forEachIndexed { index, log ->
                    val x = leftPadding + (index * stepX)
                    val y = topPadding + (chartHeight - topPadding) - ((log.weightValue - yMin) / yRange) * (chartHeight - topPadding)
                    val dist = kotlin.math.sqrt((tapOffset.x - x)*(tapOffset.x - x) + (tapOffset.y - y)*(tapOffset.y - y))
                    if (dist < 60f && dist < minDistance) { // 60f é o raio de toque
                        minDistance = dist
                        closestIndex = index
                        closestOffset = Offset(x, y)
                    }
                }
                
                if (closestIndex != null) {
                    selectedIndex = closestIndex
                    selectedOffset = closestOffset
                } else {
                    selectedIndex = null
                }
            }
        }
    ) {
        val width = size.width
        val height = size.height
        
        val leftPadding = 80f
        val bottomPadding = 40f
        val topPadding = 40f // espaço superior para datas
        
        val chartWidth = width - leftPadding
        val chartHeight = height - bottomPadding
        
        // desenhar eixos
        drawLine(color = MediumGrey, start = Offset(leftPadding, topPadding), end = Offset(leftPadding, chartHeight), strokeWidth = 2f)
        drawLine(color = MediumGrey, start = Offset(leftPadding, chartHeight), end = Offset(width, chartHeight), strokeWidth = 2f)
        
        // calcular intervalo y
        val yPadding = if (maxWeight == minWeight) 5f else (maxWeight - minWeight) * 0.2f
        var yMin = (minWeight - yPadding).coerceAtLeast(0f)
        var yMax = maxWeight + yPadding
        
        // garantir que o objetivo está visível
        if (targetWeight != null) {
            if (targetWeight < yMin) yMin = (targetWeight - yPadding).coerceAtLeast(0f)
            if (targetWeight > yMax) yMax = targetWeight + yPadding
        }
        
        val yRange = yMax - yMin
        val drawHeight = chartHeight - topPadding
        
        // linha de objetivo
        if (targetWeight != null && targetWeight in yMin..yMax) {
            val yPos = topPadding + drawHeight - ((targetWeight - yMin) / yRange) * drawHeight
            drawLine(
                color = ElectricBlue,
                start = Offset(leftPadding, yPos),
                end = Offset(width, yPos),
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f))
            )
            drawContext.canvas.nativeCanvas.drawText("$targetWeight", leftPadding + 10f, yPos - 10f, targetPaint)
        }
        
        // pontos de dados
        val stepX = if (weightLogs.size > 1) chartWidth / (weightLogs.size - 1) else chartWidth / 2f
        
        val path = Path()
        val points = mutableListOf<Offset>()
        
        weightLogs.forEachIndexed { index, log ->
            val x = leftPadding + (index * stepX)
            val y = topPadding + drawHeight - ((log.weightValue - yMin) / yRange) * drawHeight
            points.add(Offset(x, y))
            
            if (index == 0) path.moveTo(x, y)
            else path.lineTo(x, y)
        }
        
        // gradiente de preenchimento
        if (points.isNotEmpty()) {
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, chartHeight)
                lineTo(points.first().x, chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(NeonGreen.copy(alpha = 0.6f), Color.Transparent),
                    startY = topPadding,
                    endY = chartHeight
                )
            )
        }
        
        // contorno
        drawPath(
            path = path,
            color = NeonGreen,
            style = Stroke(width = 6f)
        )
        
        // pontos
        points.forEach { pt ->
            drawCircle(color = PureBlack, radius = 10f, center = pt)
            drawCircle(color = NeonGreen, radius = 10f, center = pt, style = Stroke(width = 4f))
        }
        
        // etiquetas para y
        drawContext.canvas.nativeCanvas.drawText(String.format(Locale.US, "%.1f", yMax), 0f, topPadding + 15f, textPaint)
        val yMid = (yMax + yMin) / 2
        val yMidPos = topPadding + drawHeight / 2
        drawContext.canvas.nativeCanvas.drawText(String.format(Locale.US, "%.1f", yMid), 0f, yMidPos + 10f, textPaint)
        drawContext.canvas.nativeCanvas.drawText(String.format(Locale.US, "%.1f", yMin), 0f, chartHeight, textPaint)

        // etiquetas para datas
        val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
        if (weightLogs.isNotEmpty()) {
            val startText = dateFormat.format(Date(weightLogs.first().timestamp))
            drawContext.canvas.nativeCanvas.drawText(startText, leftPadding, 25f, dateTextPaint.apply { textAlign = android.graphics.Paint.Align.LEFT })
            
            if (weightLogs.size > 2) {
                val midIndex = weightLogs.size / 2
                val midText = dateFormat.format(Date(weightLogs[midIndex].timestamp))
                val midX = leftPadding + (chartWidth / 2f)
                drawContext.canvas.nativeCanvas.drawText(midText, midX, 25f, dateTextPaint.apply { textAlign = android.graphics.Paint.Align.CENTER })
            }

            if (weightLogs.size > 1) {
                val endText = dateFormat.format(Date(weightLogs.last().timestamp))
                drawContext.canvas.nativeCanvas.drawText(endText, width, 25f, dateTextPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT })
            }
        }

        // desenhar tooltip
        selectedIndex?.let { index ->
            val log = weightLogs[index]
            val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date(log.timestamp))
            val textStr = "${log.weightValue} KG - $dateStr"
            
            val tooltipPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 32f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
            }
            val textBounds = android.graphics.Rect()
            tooltipPaint.getTextBounds(textStr, 0, textStr.length, textBounds)
            
            val tooltipWidth = textBounds.width() + 40f
            val tooltipHeight = textBounds.height() + 30f
            
            var tooltipX = selectedOffset.x
            // evitar sair do ecrã
            if (tooltipX - tooltipWidth/2 < 0) tooltipX = tooltipWidth/2
            if (tooltipX + tooltipWidth/2 > width) tooltipX = width - tooltipWidth/2
            
            val tooltipY = selectedOffset.y - tooltipHeight - 20f
            
            drawRoundRect(
                color = PureBlack.copy(alpha = 0.8f),
                topLeft = Offset(tooltipX - tooltipWidth/2, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = NeonGreen,
                topLeft = Offset(tooltipX - tooltipWidth/2, tooltipY),
                size = Size(tooltipWidth, tooltipHeight),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f)
            )
            
            drawContext.canvas.nativeCanvas.drawText(
                textStr,
                tooltipX,
                tooltipY + tooltipHeight/2 + textBounds.height()/2,
                tooltipPaint
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogWeightSection(onConfirmEntry: (Float, Long) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf("") }
    
    val calendar = Calendar.getInstance()
    var selectedTimestamp by remember { mutableStateOf(calendar.timeInMillis) }
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
    ) {
        if (!isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = true }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("LOG WEIGHT", color = CrystalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Expand", tint = MediumGrey)
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("NEW MANUAL ENTRY", color = CrystalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.Add, contentDescription = "Collapse", tint = MediumGrey, modifier = Modifier.clickable { isExpanded = false })
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("WEIGHT (KG)", color = MediumGrey, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it.replace("\n", "").replace("\r", "") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("00.0", color = MediumGrey) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = PureBlack,
                        focusedContainerColor = PureBlack,
                        unfocusedContainerColor = PureBlack,
                        focusedTextColor = CrystalWhite,
                        unfocusedTextColor = CrystalWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                
                Text("ENTRY DATE", color = MediumGrey, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = dateFormat.format(Date(selectedTimestamp)),
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Select Date", tint = MediumGrey)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val dpd = DatePickerDialog(
                                context,
                                { _, year, month, dayOfMonth ->
                                    val cal = Calendar.getInstance()
                                    cal.set(year, month, dayOfMonth)
                                    if (cal.timeInMillis <= System.currentTimeMillis()) {
                                        selectedTimestamp = cal.timeInMillis
                                    }
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            dpd.datePicker.maxDate = System.currentTimeMillis()
                            dpd.show()
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = PureBlack,
                        disabledContainerColor = PureBlack,
                        disabledTextColor = CrystalWhite
                    ),
                    enabled = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val w = weightInput.toFloatOrNull()
                        if (w != null && w > 0) {
                            onConfirmEntry(w, selectedTimestamp)
                            isExpanded = false
                            weightInput = ""
                            selectedTimestamp = System.currentTimeMillis()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("CONFIRM ENTRY", color = PureBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TechnicalHistoryLedger(weightLogs: List<WeightLogEntity>, user: UserEntity?, onDelete: (WeightLogEntity) -> Unit) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.US)
    val sortedLogs = weightLogs.sortedByDescending { it.timestamp }
    var showAllLogs by remember { mutableStateOf(false) }

    val isGainGoal = user?.goal?.contains("Gain", ignoreCase = true) ?: false

    var logToDelete by remember { mutableStateOf<WeightLogEntity?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("TECHNICAL HISTORY LEDGER", color = MediumGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Box(modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(NeonGreen.copy(alpha = 0.2f)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                Text("RAW_DATA_SYNCED", color = NeonGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (sortedLogs.isEmpty()) {
            Text("No entries yet.", color = MediumGrey, fontSize = 12.sp)
        } else {
            val displayLogs = if (showAllLogs) sortedLogs else sortedLogs.take(4)
            
            displayLogs.forEachIndexed { index, log ->
                val previousLogIndex = sortedLogs.indexOf(log) + 1
                val previousLog = if (previousLogIndex < sortedLogs.size) sortedLogs[previousLogIndex] else null
                val delta = if (previousLog != null) log.weightValue - previousLog.weightValue else 0f
                val deltaText = if (delta > 0) "+${String.format(Locale.US, "%.1f", delta)}" else String.format(Locale.US, "%.1f", delta)
                
                val deltaColor = if (delta > 0) {
                    if (isGainGoal) NeonGreen else SoftCoral
                } else if (delta < 0) {
                    if (isGainGoal) SoftCoral else NeonGreen
                } else {
                    MediumGrey
                }
                
                val alphaValue = if (!showAllLogs && index == 3) 0.4f else 1f

                var isPressed by remember { mutableStateOf(false) }
                val animatedBgAlpha by animateFloatAsState(targetValue = if (isPressed) 0.3f else 0f, animationSpec = tween(600))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(alphaValue)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    SoftCoral.copy(alpha = animatedBgAlpha),
                                    Color.Transparent
                                )
                            )
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    val success = tryAwaitRelease()
                                    isPressed = false
                                },
                                onLongPress = {
                                    isPressed = false
                                    logToDelete = log
                                }
                            )
                        }
                        .padding(vertical = 8.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(dateFormat.format(Date(log.timestamp)).uppercase(Locale.US), color = CrystalWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("ID: ${log.id}-${log.timestamp.toString().takeLast(4)}", color = MediumGrey, fontSize = 10.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${log.weightValue} KG", color = CrystalWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (previousLog != null) {
                            Text("$deltaText KG DELTA", color = deltaColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            if (sortedLogs.size > 4) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MediumGrey.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (showAllLogs) "LOAD LESS DATA" else "LOAD MORE DATA",
                    color = MediumGrey,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().clickable { showAllLogs = !showAllLogs },
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (logToDelete != null) {
        Dialog(onDismissRequest = { logToDelete = null }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(PureBlack)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Text("Delete Entry", color = SoftCoral, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Are you sure you want to remove this weight record? This action cannot be undone.", color = CrystalWhite, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { logToDelete = null }) {
                        Text("CANCEL", color = MediumGrey)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            logToDelete?.let { onDelete(it) }
                            logToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCoral),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("DELETE", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun VisualDiarySection(
    photoLogs: List<PhotoLogEntity>,
    onPhotoClick: (PhotoLogEntity) -> Unit,
    onAddPhoto: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.US)
    val sortedPhotos = remember(photoLogs) { photoLogs.sortedBy { it.timestamp } } // mais antigas à esquerda e recentes à direita
    val lazyListState = rememberLazyListState()

    // deslizar automaticamente para a última foto
    LaunchedEffect(sortedPhotos) {
        if (sortedPhotos.isNotEmpty()) {
            lazyListState.scrollToItem(sortedPhotos.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Visual Diary", color = CrystalWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Icon(Icons.Default.CameraAlt, contentDescription = "Add Photo", tint = NeonGreen, modifier = Modifier.clickable { onAddPhoto() })
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        if (sortedPhotos.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)).background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                Text("No photos added yet.", color = MediumGrey)
            }
        } else {
            val leftFadeAlpha by animateFloatAsState(
                targetValue = if (lazyListState.canScrollBackward) 0.6f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "leftFadeAlpha"
            )
            val rightFadeAlpha by animateFloatAsState(
                targetValue = if (lazyListState.canScrollForward) 0.6f else 0f,
                animationSpec = tween(durationMillis = 300),
                label = "rightFadeAlpha"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                LazyRow(
                    state = lazyListState,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(sortedPhotos) { index, photoLog ->
                        val isLatest = index == sortedPhotos.size - 1
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(12.dp))
                                .border(1.dp, if (isLatest) NeonGreen else Color.Transparent, RoundedCornerShape(12.dp))
                                .clickable { onPhotoClick(photoLog) }
                        ) {
                            AsyncImage(
                                model = Uri.parse(photoLog.photoUri),
                                contentDescription = "Progress Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            
                            if (isLatest) {
                                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).clip(RoundedCornerShape(4.dp)).background(NeonGreen).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text("LATEST", color = PureBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Text(
                                text = dateFormat.format(Date(photoLog.timestamp)).uppercase(Locale.US),
                                color = CrystalWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                            )
                        }
                    }
                }

                // máscara de desvanecimento esquerda
                if (leftFadeAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight()
                            .width(48.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(PureBlack.copy(alpha = leftFadeAlpha), Color.Transparent)
                                )
                            )
                    )
                }

                // máscara de desvanecimento direita
                if (rightFadeAlpha > 0f) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(48.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, PureBlack.copy(alpha = rightFadeAlpha))
                                )
                            )
                    )
                }
            }
        }
    }
}

fun isSameCalendarDay(t1: Long, t2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = t1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = t2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
           cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun PhotoDetailDialog(
    photoLog: PhotoLogEntity,
    weightLogs: List<WeightLogEntity>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.US)
    val associatedWeight = weightLogs.find { isSameCalendarDay(it.timestamp, photoLog.timestamp) }
    
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 5f)
        offset = if (scale > 1f) offset + panChange else Offset.Zero
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PureBlack)
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f))
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROGRESS PHOTO",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "CLOSE",
                            color = MediumGrey,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onDismiss() }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // imagem com zoom
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        if (scale > 1f) {
                                            scale = 1f
                                            offset = Offset.Zero
                                        } else {
                                            scale = 2.5f
                                        }
                                    }
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = Uri.parse(photoLog.photoUri),
                            contentDescription = "Zoomed Progress Photo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    scaleX = scale,
                                    scaleY = scale,
                                    translationX = offset.x,
                                    translationY = offset.y
                                )
                                .transformable(state = transformState)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // visualização da data
                    Text(
                        text = dateFormat.format(Date(photoLog.timestamp)).uppercase(Locale.US),
                        color = CrystalWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    // peso cruzado
                    if (associatedWeight != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonGreen.copy(alpha = 0.1f))
                                .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WEIGHT RECORDED: ${associatedWeight.weightValue} KG",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // fluxo de confirmação para eliminar
                    var showConfirmDelete by remember { mutableStateOf(false) }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    if (!showConfirmDelete) {
                        OutlinedButton(
                            onClick = { showConfirmDelete = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftCoral),
                            border = BorderStroke(1.dp, SoftCoral.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = SoftCoral,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("REMOVE PHOTO", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showConfirmDelete = false },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MediumGrey),
                                border = BorderStroke(1.dp, MediumGrey.copy(alpha = 0.3f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("CANCEL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                            Button(
                                onClick = {
                                    showConfirmDelete = false
                                    onDelete()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SoftCoral),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("CONFIRM DELETE", color = PureBlack, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// componentes visuais e de ajuda mantidos intactos
@Composable
fun MetricCard(
    label: String, 
    value: String, 
    unit: String, 
    modifier: Modifier = Modifier, 
    icon: ImageVector? = null,
    borderColor: Color = Color.White.copy(alpha = 0.15f),
    valueColor: Color = CrystalWhite
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = MediumGrey, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MediumGrey, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
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
