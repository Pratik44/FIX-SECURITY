# Git Setup Script for FIX Security Platform (PowerShell)

Write-Host "Setting up Git repository..." -ForegroundColor Green

# Initialize git repository
git init

# Add remote repository
git remote add origin https://github.com/Pratik44/FIX-SECURITY.git

# Configure git user
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

Write-Host ""
Write-Host "Repository setup complete!" -ForegroundColor Green
Write-Host ""
Write-Host "To push to GitHub:" -ForegroundColor Yellow
Write-Host "1. Ensure your SSH key is added to GitHub"
Write-Host "2. Run: git push -u origin main"
Write-Host ""
Write-Host "Or if using HTTPS:" -ForegroundColor Yellow
Write-Host "git remote set-url origin https://github.com/Pratik44/FIX-SECURITY.git"
Write-Host "git push -u origin main"
