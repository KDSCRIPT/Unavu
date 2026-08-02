#!/bin/sh
set -e
pr_json=$(curl -s -H "Authorization: Bearer $GITHUB_TOKEN" \
  "https://api.github.com/repos/$REPO/pulls?state=closed&sort=updated&direction=desc&base=main&per_page=5")
tag=$(echo "$pr_json" | jq -r '[.[] | select(.merged_at != null)][0].body' | grep -oP 'Image tag:\s*\K\w+')
if [ -z "$tag" ]; then echo "Could not resolve tag" && exit 1; fi
echo "tag=$tag" >> "$GITHUB_OUTPUT"