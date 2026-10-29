@echo off
setlocal enabledelayedexpansion
title Multi-Remote Git Push Utility
echo ==================================================================
echo   TCET Mumbai - Project Management System
echo   DevOps Multi-Remote Git Deployment Automation
echo ==================================================================
echo.

:: Check git status
git status --short
echo.

set /p COMMIT_MSG="Enter commit message (press Enter for default 'Automated build & sync'): "
if "!COMMIT_MSG!"=="" set COMMIT_MSG=Automated build & sync

echo [*] Staging changes...
git add .

echo [*] Committing changes...
git commit -m "!COMMIT_MSG!"

echo.
echo [*] Pushing to all configured remotes...
git push --all
git push --tags

echo.
echo [+] Multi-remote deployment push completed successfully!
pause
