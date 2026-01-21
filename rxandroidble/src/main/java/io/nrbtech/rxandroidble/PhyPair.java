package io.nrbtech.rxandroidble;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Set;

/**
 * The interface used for results of {@link RxBleConnection#readPhy()} and {@link RxBleConnection#setPreferredPhy(Set, Set, RxBlePhyOption)}
 */
public interface PhyPair {

    /**
     * Returns the transmitter PHY.
     *
     * @return the transmitter PHY
     */
    @NonNull
    RxBlePhy getTxPhy();

    /**
     * Returns the receiver PHY.
     *
     * @return the receiver PHY
     */
    @NonNull
    RxBlePhy getRxPhy();

    int hashCode();

    boolean equals(@Nullable Object obj);
}
