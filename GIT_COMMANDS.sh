#!/bin/bash
# Git Commands Reference for Groq Integration Project
# Use these commands to manage your GitHub repository

# ============================================================================
# INITIAL SETUP (One-time)
# ============================================================================

# Check if Git is installed
git --version

# Configure Git (if first time)
git config --global user.name "Your Name"
git config --global user.email "your-email@example.com"

# Navigate to project directory
cd "D:\Akshay\AI projects\groq-integration"

# ============================================================================
# FIRST TIME: Initialize Repository & Push to GitHub
# ============================================================================

# Step 1: Initialize Git
git init

# Step 2: Add all files
git add .

# Step 3: Create first commit
git commit -m "Initial commit: Groq AI integration with Spring Boot"

# Step 4: Rename branch to main
git branch -M main

# Step 5: Add remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/groq-integration.git

# Step 6: Verify remote is added
git remote -v

# Step 7: Push to GitHub
git push -u origin main

# ============================================================================
# ONGOING: Working with Your Repository
# ============================================================================

# Check status of files
git status

# Add specific file
git add src/main/java/com/akshay/groq/controller/GroqController.java

# Add all changes
git add .

# Create a commit with message
git commit -m "Add feature: multi-turn conversation support"

# Better commit message (with description)
git commit -m "Add feature: multi-turn conversation support

- Implement chatWithHistory() method in service
- Add ChatHistoryRequest DTO
- Update controller with /with-history endpoint
- Add tests for history-based conversations"

# Push changes to GitHub
git push origin main

# ============================================================================
# VIEWING HISTORY
# ============================================================================

# View commit history
git log

# View compact commit history
git log --oneline

# View last 5 commits
git log -5

# View commit with changes
git log -p

# See who changed what
git blame src/main/java/com/akshay/groq/service/GroqService.java

# ============================================================================
# BRANCHING (For multiple features)
# ============================================================================

# Create new branch
git branch feature/database-integration

# Switch to branch
git checkout feature/database-integration

# Create and switch to new branch (shorthand)
git checkout -b feature/jwt-auth

# List all branches
git branch -a

# Push branch to GitHub
git push origin feature/database-integration

# Delete local branch
git branch -d feature/database-integration

# Delete remote branch
git push origin --delete feature/database-integration

# ============================================================================
# UNDOING CHANGES
# ============================================================================

# Discard changes in working directory
git checkout -- src/main/java/SomeFile.java

# Unstage file (before commit)
git reset HEAD src/main/java/SomeFile.java

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes)
git reset --hard HEAD~1

# Amend last commit
git commit --amend -m "Better commit message"

# ============================================================================
# MERGING & PULLING
# ============================================================================

# Fetch latest from GitHub without merging
git fetch origin

# Pull latest from GitHub (fetch + merge)
git pull origin main

# Merge branch into main
git checkout main
git merge feature/new-feature

# ============================================================================
# TYPICAL WORKFLOW
# ============================================================================

# 1. Start working on new feature
git checkout -b feature/add-database

# 2. Make changes to files...

# 3. Check status
git status

# 4. Stage changes
git add .

# 5. Commit
git commit -m "Add database integration"

# 6. Push to GitHub
git push origin feature/add-database

# 7. Create Pull Request on GitHub (web interface)

# 8. Once merged, switch back to main
git checkout main

# 9. Pull latest
git pull origin main

# ============================================================================
# TROUBLESHOOTING
# ============================================================================

# See changes not yet committed
git diff

# See staged changes
git diff --cached

# See changes in a specific file
git diff src/main/java/SomeFile.java

# See which commits are in your branch but not in main
git log main..feature/my-feature

# Reset remote URL (if you copied wrong URL)
git remote set-url origin https://github.com/YOUR_USERNAME/groq-integration.git

# View current remote URL
git remote get-url origin

# ============================================================================
# TAGS (For releases)
# ============================================================================

# Create a tag (version)
git tag v1.0.0

# Push tag to GitHub
git push origin v1.0.0

# Push all tags
git push origin --tags

# ============================================================================
# CLEAN UP
# ============================================================================

# Remove untracked files (be careful!)
git clean -fd

# Remove ignored files
git clean -fdx

# Compact repository
git gc

# ============================================================================
# SHORTCUTS (Add to your shell profile)
# ============================================================================

# Add these to ~/.bashrc or ~/.zshrc for quick access:

alias gs='git status'
alias ga='git add'
alias gc='git commit -m'
alias gp='git push'
alias gl='git log --oneline'
alias gb='git branch'
alias gco='git checkout'

# Usage:
# gs              → git status
# ga .            → git add .
# gc "message"    → git commit -m "message"
# gp              → git push

# ============================================================================
# COMMON ERRORS & SOLUTIONS
# ============================================================================

# ERROR: Permission denied (publickey)
# SOLUTION: Generate SSH key
ssh-keygen -t rsa -b 4096 -C "your-email@example.com"
# Then add public key to GitHub settings

# ERROR: "fatal: not a git repository"
# SOLUTION: You're not in the right directory or git init wasn't run
git init

# ERROR: "fatal: The current branch main has no upstream branch"
# SOLUTION: Use full command first time
git push -u origin main

# ERROR: "Your branch is ahead of 'origin/main' by X commits"
# SOLUTION: Push your commits
git push origin main

# ============================================================================
# GIT BEST PRACTICES
# ============================================================================

# 1. Commit often with meaningful messages
# 2. Push regularly to avoid losing work
# 3. Create branches for features
# 4. Use descriptive commit messages
# 5. Don't commit large binary files
# 6. Review changes before committing (git diff)
# 7. Keep .gitignore updated
# 8. Pull before pushing
# 9. Use tags for releases
# 10. Document your process

# ============================================================================
# EXAMPLE WORKFLOW FOR THIS PROJECT
# ============================================================================

# Setting up for first time
cd "D:\Akshay\AI projects\groq-integration"
git init
git add .
git commit -m "Initial commit: Groq AI integration with Spring Boot"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/groq-integration.git
git push -u origin main

# Making updates
git checkout -b feature/add-jwt-auth
# ... make changes ...
git add .
git commit -m "Add JWT authentication

- Implement JwtTokenProvider
- Add security filter
- Create auth endpoints"
git push origin feature/add-jwt-auth
# (Create pull request on GitHub)
# (After review, merge on GitHub)
git checkout main
git pull origin main

# Fixing a bug
git checkout -b bugfix/fix-null-pointer
# ... fix the bug ...
git add .
git commit -m "Fix NPE in ChatCompletionResponse.getResponseText()"
git push origin bugfix/fix-null-pointer
# (Create and merge pull request)

# Releasing version
git tag v1.0.0 -m "Version 1.0.0 - Initial release"
git push origin v1.0.0

# ============================================================================
# FOR WINDOWS (PowerShell) SPECIFIC COMMANDS
# ============================================================================

# Set environment variable for API key
$env:GROQ_API_KEY="your-api-key"

# Verify it's set
echo $env:GROQ_API_KEY

# View recent commits
git log --oneline -10

# Create batch commit
git add .; git commit -m "Update multiple files"; git push origin main

# ============================================================================
# ADVANCED: Git Configuration
# ============================================================================

# Configure global gitignore
git config --global core.excludesfile ~/.gitignore_global

# Set default editor
git config --global core.editor "vim"

# Set merge tool
git config --global merge.tool meld

# List all configuration
git config --list

# ============================================================================
# USEFUL GIT ALIASES (Add to ~/.gitconfig)
# ============================================================================

[alias]
    st = status
    co = checkout
    br = branch
    ci = commit
    unstage = reset HEAD --
    last = log -1 HEAD
    visual = log --graph --oneline --all
    amend = commit --amend --no-edit

# Usage:
git st      → git status
git co main → git checkout main

# ============================================================================
# NEED HELP?
# ============================================================================

# Get help for any git command
git help <command>
git help commit
git help push

# Check git status
git status

# See recent commands
history | grep git

# ============================================================================
# SUMMARY
# ============================================================================

# Most common commands you'll use:
git status           # Check what changed
git add .            # Stage all changes
git commit -m "msg"  # Create commit
git push origin main # Push to GitHub
git pull origin main # Get latest from GitHub

# That's 95% of what you need! 🎉

