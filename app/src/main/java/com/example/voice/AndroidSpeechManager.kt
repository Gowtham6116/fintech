package com.example.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class VoiceInputState {
  object Idle : VoiceInputState()
  object Listening : VoiceInputState()
  object Processing : VoiceInputState()
  data class Success(val recognizedText: String) : VoiceInputState()
  data class Error(val errorMessage: String, val isUnavailable: Boolean = false) : VoiceInputState()
}

class AndroidSpeechManager(private val context: Context) {
  private val tag = "FinGuideSpeech"
  private var speechRecognizer: SpeechRecognizer? = null

  private val _state = MutableStateFlow<VoiceInputState>(VoiceInputState.Idle)
  val state: StateFlow<VoiceInputState> = _state.asStateFlow()

  private val _liveTranscript = MutableStateFlow("")
  val liveTranscript: StateFlow<String> = _liveTranscript.asStateFlow()

  fun isSpeechAvailable(): Boolean {
    return SpeechRecognizer.isRecognitionAvailable(context)
  }

  fun startListening(languageCode: String = "en") {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
      _state.value = VoiceInputState.Error(
        "Voice input is unavailable on this device. Please enter your expense manually or type below.",
        isUnavailable = true
      )
      return
    }

    stopListening()

    val langTag = when (languageCode) {
      "ta" -> "ta-IN"
      "hi" -> "hi-IN"
      "te" -> "te-IN"
      "kn" -> "kn-IN"
      "ml" -> "ml-IN"
      "bn" -> "bn-IN"
      "mr" -> "mr-IN"
      "gu" -> "gu-IN"
      "pa" -> "pa-IN"
      "or" -> "or-IN"
      "as" -> "as-IN"
      "ur" -> "ur-IN"
      "ne" -> "ne-NP"
      "kok" -> "kok-IN"
      else -> "en-IN"
    }

    try {
      speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
          override fun onReadyForSpeech(params: Bundle?) {
            _state.value = VoiceInputState.Listening
            _liveTranscript.value = ""
          }

          override fun onBeginningOfSpeech() {
            _state.value = VoiceInputState.Listening
          }

          override fun onRmsChanged(rmsdB: Float) {}

          override fun onBufferReceived(buffer: ByteArray?) {}

          override fun onEndOfSpeech() {
            _state.value = VoiceInputState.Processing
          }

          override fun onError(error: Int) {
            Log.w(tag, "SpeechRecognizer error: $error")
            val msg = when (error) {
              SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly or enter manually."
              SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected in time. Tap microphone to try again."
              SpeechRecognizer.ERROR_AUDIO -> "Audio recording issue. Please check microphone."
              SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
              SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network issue during speech recognition."
              else -> "Speech recognition ended. You can enter manually or retry."
            }
            _state.value = VoiceInputState.Error(msg, isUnavailable = false)
          }

          override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val resultText = matches?.firstOrNull()?.trim() ?: ""
            if (resultText.isNotBlank()) {
              _liveTranscript.value = resultText
              _state.value = VoiceInputState.Success(resultText)
            } else {
              _state.value = VoiceInputState.Error("No clear speech recognized. Please enter manually.")
            }
          }

          override fun onPartialResults(partialResults: Bundle?) {
            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            val text = matches?.firstOrNull()?.trim() ?: ""
            if (text.isNotBlank()) {
              _liveTranscript.value = text
            }
          }

          override fun onEvent(eventType: Int, params: Bundle?) {}
        })
      }

      val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, langTag)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, langTag)
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
      }

      speechRecognizer?.startListening(intent)
      _state.value = VoiceInputState.Listening
    } catch (e: Exception) {
      Log.e(tag, "Failed to start speech recognizer: ${e.message}")
      _state.value = VoiceInputState.Error(
        "Voice input is unavailable on this device. Please enter your expense manually or type below.",
        isUnavailable = true
      )
    }
  }

  fun stopListening() {
    try {
      speechRecognizer?.stopListening()
      speechRecognizer?.destroy()
      speechRecognizer = null
    } catch (_: Exception) {}
  }

  fun reset() {
    stopListening()
    _state.value = VoiceInputState.Idle
    _liveTranscript.value = ""
  }
}
