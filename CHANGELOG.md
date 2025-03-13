Change Log
==========

## Version 2.2.5 - 13 March 25

* Upgraded dependencies

## Version 2.2.4 - 1 July 24

* Fixed issue with publishing

## Version 2.2.3 – 30 June 24

* Upgraded dependencies and gradle

## Version 2.2.2 – 30 October 23

* Upgraded to Gradle 8.1.2
* Upgraded dependencies

## Version 2.2.1 – 15 May 23

* Upgraded to Gradle 8
* Upgraded dependencies

## Version 2.2.0 – 1 April 22

* Changed `ScanResult` property types to enums where appropriate
* Improved scan result details in sample apps

## Version 2.1.0 – 31 March 22

* Added additional info to scan results when available
* Added scan result details in sample apps
* Removed `IsConnectableStatus` enum – changed relevant properties to nullable `Boolean`s to match other added properties

## Version 2.0.0 – 29 March 22
* Switched to RxJava 3, thanks @z3ntu, @Drjacky (https://github.com/NRB-Tech/RxAndroidBle/pull/5)
* Added `isConnectable` to scan result, thanks @MartinSadovy, @BeBetterBee (https://github.com/NRB-Tech/RxAndroidBle/pull/4)
* Added `maxSdkVersion` to `ACCESS_COARSE_LOCATION` and `ACCESS_FINE_LOCATION` permissions
* Updated GATT status code URLs, thanks @ariccio

## Version 1.2.1
* Resolving issues with repository – no code changes

## Version 1.2.0
* Fork of [Polidea/RxAndroidBle](https://github.com/Polidea/RxAndroidBle)
* Integrated PRs for Gradle 7 support, unit tests without Robolectric, Android 12 permissions, and getting connected peripherals
