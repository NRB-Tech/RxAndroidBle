package io.nrbtech.rxandroidble.internal.operations;


import androidx.annotation.RestrictTo;

import io.nrbtech.rxandroidble.internal.Priority;
import io.nrbtech.rxandroidble.internal.serialization.QueueReleaseInterface;

import io.reactivex.rxjava3.core.Observable;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface Operation<T> extends Comparable<Operation<?>> {

    Observable<T> run(QueueReleaseInterface queueReleaseInterface);

    Priority definedPriority();
}
