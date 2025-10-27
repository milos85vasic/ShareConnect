#!/usr/bin/env python3
"""
Comprehensive test for the ShareConnect website
"""

import os
import re

def test_file_exists(filename):
    """Test if a file exists"""
    return os.path.exists(filename)

def test_css_variables(filename):
    """Test if CSS uses the blue theme"""
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Check for blue theme colors
    blue_colors = ["#268AF8", "#3EC9D6", "#1e5f99"]
    for color in blue_colors:
        if color not in content:
            return False
    
    return True

def test_consumer_messaging(filename):
    """Test if file uses consumer-focused messaging"""
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Check for technical terms that should be avoided
    technical_terms = ["comprehensive Android application ecosystem", "seamlessly connects your media discovery"]
    for term in technical_terms:
        if term in content:
            return False
    
    # Check for consumer-friendly terms
    consumer_terms = ["one tap", "instantly", "easily", "simple", "works with", "automatically", "directly"]
    found_terms = sum(1 for term in consumer_terms if term.lower() in content.lower())
    
    return found_terms >= 1  # At least 1 consumer-friendly term

def test_mobile_responsive(filename):
    """Test if CSS has mobile responsive features"""
    with open(filename, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Check for mobile responsive features
    responsive_features = [
        "@media",
        "max-width",
        "flex-wrap"
    ]
    
    return all(feature in content for feature in responsive_features)

def main():
    """Run comprehensive website tests"""
    
    print("🧪 Running comprehensive website tests...\n")
    
    # Test files
    test_files = [
        "index.html",
        "products.html", 
        "styles.css",
        "qbitconnect.html",
        "plexconnect.html",
        "jdownloaderconnect.html"
    ]
    
    results = {}
    
    for filename in test_files:
        print(f"📄 Testing {filename}...")
        
        if not test_file_exists(filename):
            print(f"   ❌ File not found: {filename}")
            results[filename] = False
            continue
        
        file_results = []
        
        # Test file existence
        file_results.append(("File exists", True))
        
        # Test CSS for blue theme (only for CSS file)
        if filename == "styles.css":
            file_results.append(("Blue theme", test_css_variables(filename)))
        
        # Test consumer messaging (only for HTML files)
        if filename.endswith(".html"):
            file_results.append(("Consumer messaging", test_consumer_messaging(filename)))
        
        # Test mobile responsive (only for CSS file)
        if filename == "styles.css":
            file_results.append(("Mobile responsive", test_mobile_responsive(filename)))
        
        # Print results
        all_passed = all(result[1] for result in file_results)
        for test_name, passed in file_results:
            status = "✅" if passed else "❌"
            print(f"   {status} {test_name}")
        
        results[filename] = all_passed
        print()
    
    # Summary
    print("📊 Test Summary:")
    passed_count = sum(1 for result in results.values() if result)
    total_count = len(results)
    
    print(f"   Passed: {passed_count}/{total_count}")
    
    if passed_count == total_count:
        print("\n🎉 All tests passed! The website is ready for production.")
    else:
        print("\n⚠️  Some tests failed. Please review the issues above.")

if __name__ == "__main__":
    main()