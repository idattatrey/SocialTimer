package com.social.timer.ui.composibles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.social.timer.R

@Composable
fun AnimatedPreLoader() {
    val preLoaderLottieComposition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(
            R.raw.no_data
        )
    )

    val preLoaderProgress by animateLottieCompositionAsState(
        preLoaderLottieComposition,
        iterations = 1,
        isPlaying = true
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .align(Alignment.Center),
            composition = preLoaderLottieComposition,
            progress = { preLoaderProgress } // Lambda used to avoid recomposition
        )
    }
}