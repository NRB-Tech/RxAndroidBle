package io.nrbtech.rxandroidble.internal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import bleshadow.javax.inject.Scope;

@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface DeviceScope {

}