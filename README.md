Intellij DUB Plugin
===================

Support for [DUB](https://code.dlang.org/) within IntelliJ IDEA. Requires [IntelliJ D Language](https://github.com/intellij-dlanguage/intellij-dlanguage) to be installed.

| Branch | Status |
| :--- | :--- |
| Master | [![Build & Verify](https://github.com/intellij-dlanguage/intellij-dub/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/intellij-dlanguage/intellij-dub/actions/workflows/gradle.yml) |
| Develop | [![Build & Verify](https://github.com/intellij-dlanguage/intellij-dub/actions/workflows/gradle.yml/badge.svg?branch=develop)](https://github.com/intellij-dlanguage/intellij-dub/actions/workflows/gradle.yml) |

## Supported versions of IntelliJ

| Plugin Version | IntelliJ Versions | release notes                                              |
| :--- | :--- |:-----------------------------------------------------------|
| 0.7.0 | 2021.2.* - 2022.1.* | support for IntelliJ _2021.2.*_ - _2022.1.*_               |
| 0.6.1 | 2020.3.* - 2021.2.* | support for IntelliJ _2020.3.*_ - _2021.2.*_               |
| 0.5.0 | 2020.2.* | support for IntelliJ 2020.2                                |
| 0.4.0 | 2019.3.* - 2020.1.* | support for IntelliJ 2020.1                                |
| 0.3.1 | 2019.2.* - 2019.3.* | updated for IntelliJ 2019.3.*                              |
| 0.3.0 | 2019.2.* | updated for IntelliJ 2019.2                                |
| 0.2.0 | 2019.1.* | updated for IntelliJ 2019.1                                |
| 0.1.2 | 2018.3.* - 2019.1.* | minor tweaks for IntelliJ 2018.3                           |
| 0.1.1 | 2017.3.* - 2018.1.* | Initial release. Supports IntelliJ _2017.3.*_ - _2018.1.*_ |

## Installation

Assuming you already have the [D Language](https://github.com/intellij-dlanguage/intellij-dlanguage) plugin installed, go to Preferences > Plugins > Browse Repositories and search For "DUB" then click install and restart IntelliJ

You can also download the plugin directly from the [Jetbrains plugin repository](http://plugins.jetbrains.com/plugin/10416-dub) and then in IntelliJ go to Preferences > Plugins > Install plugin from disk and choose the jar you downloaded
