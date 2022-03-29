package io.nrbtech.rxandroidble.exceptions

import spock.lang.Specification

import static io.nrbtech.rxandroidble.exceptions.BleScanException.BLUETOOTH_DISABLED

/**
 * Tests BleScanException
 */
class BleScanExceptionTest extends Specification {

    BleScanException objectUnderTest

    def "toString should include message"() {

        when:
        objectUnderTest = new BleScanException(BLUETOOTH_DISABLED)

        then:
        assert objectUnderTest.toString() ==
                "io.nrbtech.rxandroidble.exceptions.BleScanException: Bluetooth disabled (code 1)"
    }
}
