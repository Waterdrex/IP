#!/usr/bin/env bash
set -e

echo "=================================================================="
echo "  TCET Mumbai - Project Management System"
echo "  DevOps Multi-Remote Git Deployment Automation"
echo "=================================================================="

git status -s
echo ""

read -p "Enter commit message [Default: 'Automated build & sync']: " COMMIT_MSG
COMMIT_MSG=${COMMIT_MSG:-"Automated build & sync"}

echo "[*] Staging and committing..."
git add .
git commit -m "$COMMIT_MSG" || echo "No changes to commit."

echo "[*] Pushing to all configured push remotes..."
# Push to all configured remotes
for remote in $(git remote); do
    echo "[*] Pushing to remote: $remote ..."
    git push "$remote" --all
    git push "$remote" --tags
done

echo "[+] Multi-remote push completed successfully!"
