# Placeholder Models for Development

This directory contains placeholder TensorFlow Lite models for development and testing.

## Production Models
In production, replace these with actual trained models:

1. `multilingual_bert.tflite` - Multilingual BERT model (768 dimensions)
2. `english_bert.tflite` - English-only BERT model
3. `chinese_bert.tflite` - Chinese BERT model
4. `japanese_bert.tflite` - Japanese BERT model
5. `korean_bert.tflite` - Korean BERT model
6. `arabic_bert.tflite` - Arabic BERT model
7. `hindi_bert.tflite` - Hindi BERT model
8. `fallback_bert.tflite` - Lightweight fallback model

## Model Specifications
- Input: Tokenized text sequences (max 512 tokens)
- Output: 768-dimensional embedding vectors
- Size: ~15-25MB per model
- Format: TensorFlow Lite (.tflite)

## Download Instructions
Actual models should be downloaded from:
- Internal model repository (private)
- Hugging Face model hub (converted to TFLite)
- Custom trained models for media content

## Implementation Notes
The NlpModelManager will automatically:
1. Check for model existence in assets
2. Download required models on first use
3. Cache models for offline access
4. Fall back to lightweight model if primary fails