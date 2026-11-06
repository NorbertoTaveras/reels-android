#!/usr/bin/env bash
set -euo pipefail

repository=$(gh api "repos/$R8_ARCHIVE_REPOSITORY")
if ! jq -e --arg owner "$GITHUB_REPOSITORY_OWNER" --arg expected "$R8_ARCHIVE_REPOSITORY" --arg source "$GITHUB_REPOSITORY" '
  .private and .visibility == "private" and
  (.owner.login | ascii_downcase) == ($owner | ascii_downcase) and
  (.full_name | ascii_downcase) == ($expected | ascii_downcase) and
  (.full_name | ascii_downcase) != ($source | ascii_downcase)
' <<< "$repository" >/dev/null; then
  echo '::error::The R8 mapping destination must be a separate private repository owned by the Reels repository owner.'
  exit 1
fi

mapping=Reels/app/build/outputs/mapping/productionRelease/mapping.txt
checksum="$RUNNER_TEMP/Reels-$RELEASE_VERSION.apk.sha256"
if [[ ! -s "$mapping" || ! -s "$checksum" ]]; then
  echo '::error::The Production Release mapping or signed APK checksum is missing.'
  exit 1
fi
read -r apk_sha _ < "$checksum"
if [[ ! "$apk_sha" =~ ^[0-9a-f]{64}$ ]]; then
  echo '::error::The signed APK checksum is invalid.'
  exit 1
fi

path="reels/v$RELEASE_VERSION/$GITHUB_SHA/$GITHUB_RUN_ID-$GITHUB_RUN_ATTEMPT/$apk_sha.mapping.txt.gz"
gzip -n -c "$mapping" | base64 | tr -d '\n' |
  jq -Rs --arg message "Archive Reels $RELEASE_VERSION R8 mapping" '{message: $message, content: .}' |
  gh api --method PUT "repos/$R8_ARCHIVE_REPOSITORY/contents/$path" --input - >/dev/null
echo 'R8 mapping archived in the configured private repository.'
