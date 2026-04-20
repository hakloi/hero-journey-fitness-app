package com.example.fitnessapp.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.fitnessapp.R
import com.example.fitnessapp.ui.theme.AppTextStyles
import com.example.fitnessapp.view_models.PetersWarmupViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.PoseLandmark
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

private enum class JumpingJackState {
    OPEN,
    CLOSED
}

@Composable
fun PetersWarmupScreen(
    navController: NavController,
    viewModel: PetersWarmupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasCameraPermission by remember { mutableStateOf(false) }
    var reps by remember { mutableIntStateOf(0) }
    var phaseText by remember { mutableStateOf("Get ready") }
    var jackState by remember { mutableStateOf(JumpingJackState.CLOSED) }
    var openFrames by remember { mutableIntStateOf(0) }
    var closedFrames by remember { mutableIntStateOf(0) }

    val missionBodyTextStyle = AppTextStyles.missionBody()
    val smallMenuButtonTextStyle = AppTextStyles.smallMenuButton()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    val poseDetector = remember {
        val options = PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build()
        PoseDetection.getClient(options)
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    DisposableEffect(Unit) {
        onDispose {
            poseDetector.close()
            cameraExecutor.shutdown()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = androidx.camera.core.Preview.Builder()
                        .build()
                        .also { it.surfaceProvider = previewView.surfaceProvider }
                    val mainExecutor = ContextCompat.getMainExecutor(ctx)

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                analyzeJumpingJackFrame(
                                    imageProxy = imageProxy,
                                    poseDetector = poseDetector,
                                    mainExecutor = mainExecutor,
                                    onPoseDetected = { pose ->
                                        val open = isJumpingJackOpen(pose)
                                        val closed = isJumpingJackClosed(pose)

                                        if (open) {
                                            openFrames += 1
                                        } else {
                                            openFrames = 0
                                        }

                                        if (closed) {
                                            closedFrames += 1
                                        } else {
                                            closedFrames = 0
                                        }

                                        if (jackState == JumpingJackState.CLOSED && openFrames >= 3) {
                                            jackState = JumpingJackState.OPEN
                                            phaseText = "Open"
                                            closedFrames = 0
                                        } else if (jackState == JumpingJackState.OPEN && closedFrames >= 3) {
                                            jackState = JumpingJackState.CLOSED
                                            reps += 1
                                            phaseText = "Closed"
                                            openFrames = 0
                                        }
                                    },
                                    onNoPose = {
                                        phaseText = "Move into frame"
                                        openFrames = 0
                                        closedFrames = 0
                                    }
                                )
                            }
                        }

                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_FRONT_CAMERA,
                        preview,
                        imageAnalyzer
                    )

                    previewView
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Camera permission is required",
                    color = Color.White,
                    style = missionBodyTextStyle
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.55f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Peter's Warmup: Jumping Jacks",
                        color = Color.White,
                        style = missionBodyTextStyle
                    )
                    Text(
                        text = "Reps: $reps",
                        color = Color.White,
                        style = missionBodyTextStyle
                    )
                    Text(
                        text = "State: $phaseText",
                        color = Color.White,
                        style = missionBodyTextStyle
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth()
                                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.saveSession(reps)
                        reps = 0
                    },
                    modifier = Modifier
                ) {
                    Text(
                        text = stringResource(R.string.save_button),
                        style = smallMenuButtonTextStyle
                    )
                }
                Button(
                    onClick = { reps = 0 },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.reset_btn),
                        style = smallMenuButtonTextStyle
                    )
                }
                Button(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.return_button),
                        style = smallMenuButtonTextStyle
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun analyzeJumpingJackFrame(
    imageProxy: ImageProxy,
    poseDetector: com.google.mlkit.vision.pose.PoseDetector,
    mainExecutor: java.util.concurrent.Executor,
    onPoseDetected: (Pose) -> Unit,
    onNoPose: () -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val inputImage = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees
    )

    poseDetector.process(inputImage)
        .addOnSuccessListener(mainExecutor) { pose ->
            if (pose.allPoseLandmarks.isEmpty()) {
                onNoPose()
            } else {
                onPoseDetected(pose)
            }
        }
        .addOnFailureListener(mainExecutor) {
            onNoPose()
        }
        .addOnCompleteListener(mainExecutor) {
            imageProxy.close()
        }
}

private fun isJumpingJackOpen(pose: Pose): Boolean {
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position ?: return false
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position ?: return false
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position ?: return false
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position ?: return false
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position ?: return false
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position ?: return false
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position ?: return false
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position ?: return false

    val wristsUp = leftWrist.y < leftShoulder.y && rightWrist.y < rightShoulder.y
    val anklesDistance = abs(leftAnkle.x - rightAnkle.x)
    val hipsDistance = abs(leftHip.x - rightHip.x).coerceAtLeast(1f)
    val legsApart = anklesDistance > hipsDistance * 1.45f

    return wristsUp && legsApart
}

private fun isJumpingJackClosed(pose: Pose): Boolean {
    val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)?.position ?: return false
    val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)?.position ?: return false
    val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)?.position ?: return false
    val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)?.position ?: return false
    val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)?.position ?: return false
    val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)?.position ?: return false
    val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)?.position ?: return false
    val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)?.position ?: return false

    val wristsDown = leftWrist.y > leftShoulder.y && rightWrist.y > rightShoulder.y
    val anklesDistance = abs(leftAnkle.x - rightAnkle.x)
    val hipsDistance = abs(leftHip.x - rightHip.x).coerceAtLeast(1f)
    val legsTogether = anklesDistance < hipsDistance * 1.15f

    return wristsDown && legsTogether
}
