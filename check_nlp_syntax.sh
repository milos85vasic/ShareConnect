#!/bin/bash

echo "Checking NLP file syntax..."

# Check if files have basic Kotlin syntax
echo "Checking AdvancedSemanticEmbedding.kt basic syntax..."
if grep -q "class AdvancedSemanticEmbedding" Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/nlp/AdvancedSemanticEmbedding.kt; then
    echo "✓ AdvancedSemanticEmbedding class found"
else
    echo "✗ AdvancedSemanticEmbedding class not found"
fi

if grep -q "fun generateEmbedding" Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/nlp/AdvancedSemanticEmbedding.kt; then
    echo "✓ generateEmbedding method found"
else
    echo "✗ generateEmbedding method not found"
fi

if grep -q "class NlpModelManager" Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/nlp/NlpModelManager.kt; then
    echo "✓ NlpModelManager class found"
else
    echo "✗ NlpModelManager class not found"
fi

if grep -q "fun loadModel" Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/nlp/NlpModelManager.kt; then
    echo "✓ loadModel method found"
else
    echo "✗ loadModel method not found"
fi

# Check test files
echo "Checking test file imports..."
if grep -q "import java.io.File" Connectors/PlexConnect/PlexConnector/src/test/kotlin/com/shareconnect/plexconnect/nlp/AdvancedSemanticEmbeddingTest.kt; then
    echo "✓ File import found in test"
else
    echo "✗ File import missing in test"
fi

if grep -q "private fun FloatArray.average" Connectors/PlexConnect/PlexConnector/src/test/kotlin/com/shareconnect/plexconnect/nlp/AdvancedSemanticEmbeddingTest.kt; then
    echo "✓ FloatArray average extension found"
else
    echo "✗ FloatArray average extension missing"
fi

echo "Basic syntax check completed."