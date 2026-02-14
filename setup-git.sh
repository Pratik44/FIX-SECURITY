#!/bin/bash
# Git Setup Script for FIX Security Platform

echo "Setting up Git repository..."

# Initialize git repository
git init

# Add remote repository
git remote add origin https://github.com/Pratik44/FIX-SECURITY.git

# Configure git user (update with your details)
git config user.name "Pratik"
git config user.email "pratik.deenbandhu456@gmail.com"

# Add all files
git add .

# Create initial commit
git commit -m "Initial commit: FIX Security Platform implementation

- FIX message parser and engine
- Security monitoring and anomaly detection
- Compliance rule engine
- REST API
- Docker infrastructure setup
- Comprehensive documentation"

echo ""
echo "Repository setup complete!"
echo ""
echo "To push to GitHub:"
echo "1. Ensure your SSH key is added to GitHub"
echo "2. Run: git push -u origin main"
echo ""
echo "Or if using HTTPS:"
echo "git remote set-url origin https://github.com/Pratik44/FIX-SECURITY.git"
echo "git push -u origin main"
