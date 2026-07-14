# FlashLink Android Source Notice

This repository contains the corresponding source for the FlashLink Android
release. It is derived from ClashMetaForAndroid and is distributed under the
GNU General Public License, version 3.0. See `LICENSE` for the full terms.

Release builds intentionally do not contain signing material, local build
configuration, account credentials, subscription URLs, or production secrets.

## Build the release source

Use JDK 21 and Android SDK 35. Create a local `local.properties` file with the
Android SDK path and the FlashLink application configuration:

```properties
sdk.dir=/path/to/Android/Sdk
custom.application.id=cc.flashlink.vpn
remove.suffix=true
```

For a signed distribution build, create an untracked `signing.properties` file
and provide your own keystore. The official FlashLink signing key is not part
of this repository.

```bash
./gradlew :app:assembleAlphaRelease
./gradlew :app:bundleAlphaRelease
```
