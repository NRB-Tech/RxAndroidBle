Change Log
==========

## Version 2.4.1 - 22 June 26

* Dependency updates

## Version 2.4.0 - 21 January 26

* Merged upstream fixes from Polidea/RxAndroidBle:
  * Fixed RxBleAdapterStateObservable memory leak (polidea@7f11356)
  * Fixed maximal supported MTU on Android 13+ (515 instead of 517) (polidea@90d729e)
  * Fixed CharacteristicLongWriteOperation defaults for Android 13+ buffer limits (polidea@42285c3)
  * Prepared LoggerUtil for new BluetoothGattCallback methods (polidea@dbb55da)
  * Added `setLegacy` method to ScanSettings for Bluetooth 5.0 extended advertising (polidea@4371b78)
  * Added PHY read and update functionality for Bluetooth 5.0 (polidea@e9e45cc)

## Version 2.3.0 - 21 January 26

* Upgraded to Android Gradle Plugin 9.0.0 and Gradle 9.1.0
* Migrated to AGP 9's new DSL and built-in Kotlin support
* Replaced unmaintained groovy-android-gradle-plugin with manual Groovy compilation for Spock tests
* Updated Shadow plugin to 9.3.0 for Gradle 9 compatibility
* Enabled Gradle configuration cache

## Version 2.2.6 - 7 November 25

* Upgraded dependencies

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
