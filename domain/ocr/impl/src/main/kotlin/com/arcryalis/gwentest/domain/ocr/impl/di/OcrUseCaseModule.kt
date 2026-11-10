package com.arcryalis.gwentest.domain.ocr.impl.di

import com.arcryalis.gwentest.domain.ocr.RecogniseTextUseCase
import com.arcryalis.gwentest.domain.ocr.impl.RecogniseTextUseCaseImpl
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface OcrUseCaseModule {

    @Binds
    fun bindRecogniseTextUseCase(recogniseTextUseCase: RecogniseTextUseCaseImpl): RecogniseTextUseCase
}

@InstallIn(SingletonComponent::class)
@Module
internal object OcrRecognizerModule {

    @Provides
    @Singleton
    fun provideTextRecognizer(): TextRecognizer =
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
}
