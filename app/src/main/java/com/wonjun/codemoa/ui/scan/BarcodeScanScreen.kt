package com.wonjun.codemoa.ui.scan

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CompoundBarcodeView
import com.wonjun.codemoa.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScanScreen(
    navController: NavController,
    onBarcodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    var isFlashOn by remember { mutableStateOf(false) }
    var barcodeView: CompoundBarcodeView? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            !cameraPermissionState.status.isGranted -> {
                // 카메라 권한 요청
                CameraPermissionContent(
                    onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                )
            }
            else -> {
                // 바코드 스캔 화면
                AndroidView(
                    factory = { context ->
                        CompoundBarcodeView(context).apply {
                            val callback = object : BarcodeCallback {
                                override fun barcodeResult(result: BarcodeResult) {
                                    result.text?.let { barcodeData ->
                                        onBarcodeScanned(barcodeData)
                                        navController.navigateUp()
                                    }
                                }
                            }
                            decodeContinuous(callback)
                            barcodeView = this
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        barcodeView = view
                    }
                )

                // 오버레이 UI
                ScanOverlay(
                    isFlashOn = isFlashOn,
                    onFlashToggle = {
                        isFlashOn = !isFlashOn
                        barcodeView?.setTorch(isFlashOn)
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            barcodeView?.pause()
        }
    }
}

@Composable
private fun CameraPermissionContent(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "카메라 권한이 필요합니다",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "바코드를 스캔하려면 카메라 접근 권한이 필요합니다.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("권한 허용")
        }
    }
}

@Composable
private fun ScanOverlay(
    isFlashOn: Boolean,
    onFlashToggle: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 상단 툴바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.action_back)
                )
            }

            IconButton(
                onClick = onFlashToggle,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "플래시"
                )
            }
        }

        // 스캔 가이드
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // 어두운 오버레이
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            )

            // 스캔 영역 (투명한 사각형)
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent)
            ) {
                // 모서리 가이드라인
                ScanCorners()
            }
        }

        // 하단 안내 텍스트
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "바코드를 스캔 영역에 맞춰주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ScanCorners() {
    val cornerSize = 20.dp
    val cornerThickness = 3.dp
    val cornerColor = Color.White

    Box(modifier = Modifier.fillMaxSize()) {
        // 좌상단
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(cornerSize)
        ) {
            // 세로선
            Box(
                modifier = Modifier
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
            )
            // 가로선
            Box(
                modifier = Modifier
                    .height(cornerThickness)
                    .width(cornerSize)
                    .background(cornerColor)
            )
        }

        // 우상단
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(cornerSize)
        ) {
            // 세로선
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
            )
            // 가로선
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .height(cornerThickness)
                    .width(cornerSize)
                    .background(cornerColor)
            )
        }

        // 좌하단
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(cornerSize)
        ) {
            // 세로선
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
            )
            // 가로선
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .height(cornerThickness)
                    .width(cornerSize)
                    .background(cornerColor)
            )
        }

        // 우하단
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(cornerSize)
        ) {
            // 세로선
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(cornerThickness)
                    .height(cornerSize)
                    .background(cornerColor)
            )
            // 가로선
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .height(cornerThickness)
                    .width(cornerSize)
                    .background(cornerColor)
            )
        }
    }
}