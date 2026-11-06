# Reels CI and releases

- Require **Build and lint** in the `develop` branch ruleset. Android CI builds same-repository PRs from the owner and Dependabot. Direct pushes to `develop` do not trigger Android CI.
- To refresh the dependency graph, run **Dependency Security** from `develop` in Actions. Enable the dependency graph and Dependabot alerts in the repository security settings.
- Push `release/MAJOR.MINOR.PATCH` to start a production build. After Android CI passes, approve the protected `production` job. Review and smoke-test the signed APK in the GitHub draft before publishing it.
- Configure `RELEASE_CERT_SHA256` as an Actions repository variable. Keep `TMDB_READ_TOKEN`, `RELEASE_KEYSTORE_BASE64`, `RELEASE_STORE_PASSWORD`, `RELEASE_KEY_ALIAS`, and `RELEASE_KEY_PASSWORD` in the `production` environment secrets.

## Private R8 mapping archive

1. Create a separate, initialized **private** repository under the Reels repository owner. Keep it private.
2. In Reels, set the Actions repository variable `R8_ARCHIVE_REPOSITORY` to the private repository's `owner/repo`.
3. Create a fine-grained token scoped only to that private repository with **Contents: Read and write**, and add it to the Reels `production` environment as the `R8_ARCHIVE_TOKEN` secret.

With both settings absent, releases can proceed without archiving. With both configured, an archive failure stops the draft upload. Each compressed mapping is stored under `reels/vVERSION/COMMIT/RUN-ATTEMPT/APK_SHA256.mapping.txt.gz` in the private repository. Match the APK checksum to choose the correct mapping when decoding a crash trace. The workflow never attaches the mapping to the public release.
