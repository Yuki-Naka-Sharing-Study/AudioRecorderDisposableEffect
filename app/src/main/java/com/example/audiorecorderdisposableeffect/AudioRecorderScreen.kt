package com.example.audiorecorderdisposableeffect

import androidx.compose.ui.graphics.Color
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun AudioRecorderScreen() {
    val context = LocalContext.current
    val isRecording = remember { mutableStateOf(false) }
    val hasRecorded = remember { mutableStateOf(false) }

    val audioFile = remember {
        File(
            context.getExternalFilesDir(null),
            "recorded_audio_${System.currentTimeMillis()}.3gp"
        )
    }

    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = when {
                isRecording.value -> "録音中..."
                hasRecorded.value -> "録音完了（再生可能）"
                else -> "録音停止中"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isRecording.value = !isRecording.value
                if (!isRecording.value) {
                    hasRecorded.value = true
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording.value) Color.Red else Color.Green
            )
        ) {
            Text(if (isRecording.value) "録音停止" else "録音開始")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hasRecorded.value) {
            Button(onClick = {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(audioFile.absolutePath)
                    prepare()
                    start()
                }
            }) {
                Text("録音を再生する")
            }
        }
    }

    // DisposableEffect：録音中だけ起動
    if (isRecording.value) {
        DisposableEffect(Unit) {
            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(audioFile.absolutePath)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                prepare()
                start()
            }

            onDispose {
                mediaRecorder?.apply {
                    stop()
                    release()
                }
                mediaRecorder = null
                Log.d("AudioRecorder", "🎤 録音停止: ${audioFile.absolutePath}")
            }
        }
    }
}
